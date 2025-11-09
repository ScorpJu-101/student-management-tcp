package gateway;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.*;
import models.*;

public class WebSocketGateway {
    private static final String TCP_SERVER_HOST = "localhost";
    private static final int TCP_SERVER_PORT = 8888;
    private static final int WEBSOCKET_PORT = 8080;
    private ExecutorService threadPool;
    
    public WebSocketGateway() {
        this.threadPool = Executors.newCachedThreadPool();
    }
    
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(WEBSOCKET_PORT)) {
            System.out.println("WebSocket Gateway started on port " + WEBSOCKET_PORT);
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                threadPool.execute(new WebSocketClientHandler(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    class WebSocketClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private OutputStream out;
        private boolean handshakeComplete = false;
        
        public WebSocketClientHandler(Socket socket) {
            this.socket = socket;
        }
        
        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = socket.getOutputStream();
                
                // Perform WebSocket handshake
                performHandshake();
                
                // Read and process messages
                while (true) {
                    String message = readWebSocketMessage();
                    if (message == null) break;
                    
                    System.out.println("Received: " + message);
                    String response = processMessage(message);
                    sendWebSocketMessage(response);
                }
            } catch (Exception e) {
                System.out.println("Client disconnected");
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        private void performHandshake() throws IOException {
            StringBuilder request = new StringBuilder();
            String line;
            String key = null;
            
            while (!(line = in.readLine()).isEmpty()) {
                request.append(line).append("\r\n");
                if (line.startsWith("Sec-WebSocket-Key:")) {
                    key = line.substring(19).trim();
                }
            }
            
            if (key != null) {
                String accept = generateAcceptKey(key);
                String response = "HTTP/1.1 101 Switching Protocols\r\n" +
                        "Upgrade: websocket\r\n" +
                        "Connection: Upgrade\r\n" +
                        "Sec-WebSocket-Accept: " + accept + "\r\n\r\n";
                out.write(response.getBytes(StandardCharsets.UTF_8));
                out.flush();
                handshakeComplete = true;
                System.out.println("WebSocket handshake complete");
            }
        }
        
        private String generateAcceptKey(String key) {
            try {
                String magic = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                byte[] digest = md.digest((key + magic).getBytes(StandardCharsets.UTF_8));
                return Base64.getEncoder().encodeToString(digest);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        
        private String readWebSocketMessage() throws IOException {
            InputStream in = socket.getInputStream();
            
            // Read first byte (FIN + opcode)
            int firstByte = in.read();
            if (firstByte == -1) return null;
            
            // Read second byte (MASK + payload length)
            int secondByte = in.read();
            if (secondByte == -1) return null;
            
            boolean masked = (secondByte & 0x80) != 0;
            int payloadLength = secondByte & 0x7F;
            
            // Read extended payload length if needed
            if (payloadLength == 126) {
                payloadLength = (in.read() << 8) | in.read();
            } else if (payloadLength == 127) {
                payloadLength = 0;
                for (int i = 0; i < 8; i++) {
                    payloadLength = (payloadLength << 8) | in.read();
                }
            }
            
            // Read mask key if present
            byte[] mask = new byte[4];
            if (masked) {
                for (int i = 0; i < 4; i++) {
                    mask[i] = (byte) in.read();
                }
            }
            
            // Read payload data
            byte[] payload = new byte[payloadLength];
            int bytesRead = 0;
            while (bytesRead < payloadLength) {
                int read = in.read(payload, bytesRead, payloadLength - bytesRead);
                if (read == -1) break;
                bytesRead += read;
            }
            
            // Unmask if needed
            if (masked) {
                for (int i = 0; i < payload.length; i++) {
                    payload[i] ^= mask[i % 4];
                }
            }
            
            return new String(payload, StandardCharsets.UTF_8);
        }
        
        private void sendWebSocketMessage(String message) throws IOException {
            byte[] rawData = message.getBytes(StandardCharsets.UTF_8);
            int frameSize = rawData.length;
            
            ByteArrayOutputStream frame = new ByteArrayOutputStream();
            frame.write(0x81); // Text frame
            
            if (frameSize <= 125) {
                frame.write(frameSize);
            } else if (frameSize <= 65535) {
                frame.write(126);
                frame.write((frameSize >> 8) & 0xFF);
                frame.write(frameSize & 0xFF);
            } else {
                frame.write(127);
                for (int i = 7; i >= 0; i--) {
                    frame.write((frameSize >> (8 * i)) & 0xFF);
                }
            }
            
            frame.write(rawData);
            out.write(frame.toByteArray());
            out.flush();
        }
        
        private String processMessage(String message) {
            try {
                // Parse JSON manually
                Map<String, Object> jsonRequest = parseJSON(message);
                
                // Create Request object for TCP server
                Request request = new Request();
                request.setAction((String) jsonRequest.get("action"));
                
                if (jsonRequest.containsKey("data") && jsonRequest.get("data") != null) {
                    Object dataObj = jsonRequest.get("data");
                    if (dataObj instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> data = (Map<String, Object>) dataObj;
                        request.setData(new HashMap<>(data));
                    }
                }
                
                // Send to TCP server and get response
                Response response = sendToTCPServer(request);
                
                // Convert response to JSON and return
                return toJSON(response);
                
            } catch (Exception e) {
                e.printStackTrace();
                return "{\"success\":false,\"message\":\"Error: " + e.getMessage() + "\"}";
            }
        }
        
        private Response sendToTCPServer(Request request) {
            try (Socket socket = new Socket(TCP_SERVER_HOST, TCP_SERVER_PORT);
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                
                out.writeObject(request);
                out.flush();
                
                Response response = (Response) in.readObject();
                return response;
                
            } catch (Exception e) {
                e.printStackTrace();
                return new Response(false, "Failed to connect to TCP server: " + e.getMessage());
            }
        }
    }
    
    // Simple JSON parser
    private static Map<String, Object> parseJSON(String json) {
        Map<String, Object> result = new HashMap<>();
        json = json.trim();
        
        // Handle empty object
        if (json.equals("{}")) {
            return result;
        }
        
        // Remove outer braces
        if (json.startsWith("{") && json.endsWith("}")) {
            json = json.substring(1, json.length() - 1);
        }
        
        // Handle whitespace-only content
        if (json.trim().isEmpty()) {
            return result;
        }
        
        // Split by commas not inside quotes or nested objects
        List<String> pairs = splitByComma(json);
        
        for (String pair : pairs) {
            if (pair == null || pair.trim().isEmpty()) {
                continue;
            }
            
            // Find the first colon that's not inside quotes or braces
            int colonIndex = findFirstColon(pair);
            if (colonIndex == -1) continue;
            
            String key = pair.substring(0, colonIndex).trim().replaceAll("\"", "");
            String value = pair.substring(colonIndex + 1).trim();
            
            if (value == null || value.isEmpty()) {
                continue;
            }
            
            // Parse the value
            result.put(key, parseValue(value));
        }
        return result;
    }
    
    private static List<String> splitByComma(String str) {
        List<String> result = new ArrayList<>();
        int braceDepth = 0;
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();
        
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            
            if (c == '"' && (i == 0 || str.charAt(i - 1) != '\\')) {
                inQuotes = !inQuotes;
                current.append(c);
            } else if (c == '{' && !inQuotes) {
                braceDepth++;
                current.append(c);
            } else if (c == '}' && !inQuotes) {
                braceDepth--;
                current.append(c);
            } else if (c == ',' && !inQuotes && braceDepth == 0) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        
        if (current.length() > 0) {
            result.add(current.toString());
        }
        
        return result;
    }
    
    private static int findFirstColon(String str) {
        boolean inQuotes = false;
        int braceDepth = 0;
        
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            
            if (c == '"' && (i == 0 || str.charAt(i - 1) != '\\')) {
                inQuotes = !inQuotes;
            } else if (c == '{' && !inQuotes) {
                braceDepth++;
            } else if (c == '}' && !inQuotes) {
                braceDepth--;
            } else if (c == ':' && !inQuotes && braceDepth == 0) {
                return i;
            }
        }
        
        return -1;
    }
    
    private static Object parseValue(String value) {
        value = value.trim();
        
        if (value.startsWith("{") && value.endsWith("}")) {
            // Nested object
            return parseJSON(value);
        } else if (value.startsWith("\"") && value.endsWith("\"")) {
            // String value
            return value.substring(1, value.length() - 1);
        } else if (value.equals("true")) {
            return Boolean.TRUE;
        } else if (value.equals("false")) {
            return Boolean.FALSE;
        } else if (value.equals("null")) {
            return null;
        } else if (value.matches("-?\\d+")) {
            // Integer
            return Integer.parseInt(value);
        } else if (value.matches("-?\\d*\\.\\d+")) {
            // Double
            return Double.parseDouble(value);
        } else {
            // Default to string
            return value;
        }
    }
    
    // Simple JSON serializer
    private static String toJSON(Response response) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"success\":").append(response.isSuccess()).append(",");
        sb.append("\"message\":\"").append(escapeJSON(response.getMessage())).append("\"");
        
        if (response.getData() != null) {
            sb.append(",\"data\":");
            sb.append(objectToJSON(response.getData()));
        }
        
        sb.append("}");
        return sb.toString();
    }
    
    private static String objectToJSON(Object obj) {
        if (obj == null) return "null";
        if (obj instanceof String) return "\"" + escapeJSON((String) obj) + "\"";
        if (obj instanceof Number || obj instanceof Boolean) return obj.toString();
        if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append(objectToJSON(list.get(i)));
            }
            sb.append("]");
            return sb.toString();
        }
        if (obj instanceof models.Student) {
            models.Student s = (models.Student) obj;
            return "{\"studentId\":\"" + s.getStudentId() + "\",\"name\":\"" + escapeJSON(s.getName()) + 
                   "\",\"email\":\"" + escapeJSON(s.getEmail()) + "\",\"registeredModules\":" + 
                   objectToJSON(s.getRegisteredModules()) + "}";
        }
        if (obj instanceof models.Module) {
            models.Module m = (models.Module) obj;
            return "{\"moduleCode\":\"" + m.getModuleCode() + "\",\"moduleName\":\"" + escapeJSON(m.getModuleName()) + 
                   "\",\"description\":\"" + escapeJSON(m.getDescription()) + "\",\"credits\":" + m.getCredits() + "}";
        }
        return "{}";
    }
    
    private static String escapeJSON(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
    
    public static void main(String[] args) {
        WebSocketGateway gateway = new WebSocketGateway();
        gateway.start();
    }
}
