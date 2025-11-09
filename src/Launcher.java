import gateway.WebSocketGateway;
import server.TCPServer;

public class Launcher {
    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("  Student Management System - Starting...    ");
        System.out.println("==============================================");
        
        // Start TCP Server in a separate thread
        Thread tcpServerThread = new Thread(() -> {
            System.out.println("\n[1] Starting TCP Server...");
            TCPServer.main(null);
        });
        tcpServerThread.setDaemon(false);
        tcpServerThread.start();
        
        // Wait a bit for TCP server to initialize
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Start WebSocket Gateway in a separate thread
        Thread websocketThread = new Thread(() -> {
            System.out.println("\n[2] Starting WebSocket Gateway...");
            WebSocketGateway.main(null);
        });
        websocketThread.setDaemon(false);
        websocketThread.start();
        
        System.out.println("\n==============================================");
        System.out.println("  All services started successfully!         ");
        System.out.println("  TCP Server: localhost:8888                 ");
        System.out.println("  WebSocket Gateway: localhost:8080          ");
        System.out.println("==============================================");
        System.out.println("\nOpen your browser and navigate to:");
        System.out.println("  Admin UI:   file:///<path>/frontend/admin/index.html");
        System.out.println("  Student UI: file:///<path>/frontend/student/index.html");
        System.out.println("\nPress Ctrl+C to stop all services.");
        System.out.println("==============================================\n");
    }
}
