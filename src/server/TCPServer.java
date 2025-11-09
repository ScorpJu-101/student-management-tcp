package server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import models.Request;
import models.Response;
import models.Student;

public class TCPServer {
    private static final int PORT = 8888;
    private static final Map<String, Student> students = new ConcurrentHashMap<>();
    private static final Map<String, models.Module> modules = new ConcurrentHashMap<>();
    
    public static void main(String[] args) {
        initializeData();
        
        ExecutorService threadPool = Executors.newCachedThreadPool();
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("TCP Server started on port " + PORT);
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                threadPool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void initializeData() {
        // Initialize with some sample data
        modules.put("CS101", new models.Module("CS101", "Introduction to Programming", "Learn basic programming concepts", 3));
        modules.put("CS201", new models.Module("CS201", "Data Structures", "Study fundamental data structures", 4));
        modules.put("CS301", new models.Module("CS301", "Database Systems", "Relational databases and SQL", 3));
        
        Student sampleStudent = new Student("S001", "John Doe", "john@example.com", "password123");
        students.put("S001", sampleStudent);
    }
    
    static class ClientHandler implements Runnable {
        private Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        
        public ClientHandler(Socket socket) {
            this.socket = socket;
        }
        
        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
                
                while (true) {
                    Request request = (Request) in.readObject();
                    Response response = processRequest(request);
                    out.writeObject(response);
                    out.flush();
                }
            } catch (EOFException e) {
                System.out.println("Client disconnected");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        private Response processRequest(Request request) {
            String action = request.getAction();
            System.out.println("Processing action: " + action);
            
            try {
                switch (action) {
                    // Student operations
                    case "STUDENT_LOGIN":
                        return handleStudentLogin(request);
                    case "VIEW_MODULES":
                        return handleViewModules();
                    case "REGISTER_MODULE":
                        return handleRegisterModule(request);
                    case "VIEW_REGISTERED_MODULES":
                        return handleViewRegisteredModules(request);
                    
                    // Admin operations - Student management
                    case "ADD_STUDENT":
                        return handleAddStudent(request);
                    case "EDIT_STUDENT":
                        return handleEditStudent(request);
                    case "VIEW_STUDENT":
                        return handleViewStudent(request);
                    case "LIST_STUDENTS":
                        return handleListStudents();
                    
                    // Admin operations - Module management
                    case "ADD_MODULE":
                        return handleAddModule(request);
                    case "EDIT_MODULE":
                        return handleEditModule(request);
                    case "VIEW_MODULE":
                        return handleViewModule(request);
                    case "LIST_MODULES":
                        return handleListModules();
                    
                    default:
                        return new Response(false, "Unknown action: " + action);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return new Response(false, "Error: " + e.getMessage());
            }
        }
        
        // Student Operations
        private Response handleStudentLogin(Request request) {
            String studentId = (String) request.getData("studentId");
            String password = (String) request.getData("password");
            
            Student student = students.get(studentId);
            if (student != null && student.getPassword().equals(password)) {
                return new Response(true, "Login successful", student);
            }
            return new Response(false, "Invalid credentials");
        }
        
        private Response handleViewModules() {
            return new Response(true, "Modules retrieved", new ArrayList<>(modules.values()));
        }
        
        private Response handleRegisterModule(Request request) {
            String studentId = (String) request.getData("studentId");
            String moduleCode = (String) request.getData("moduleCode");
            
            Student student = students.get(studentId);
            if (student == null) {
                return new Response(false, "Student not found");
            }
            
            if (!modules.containsKey(moduleCode)) {
                return new Response(false, "Module not found");
            }
            
            student.registerModule(moduleCode);
            return new Response(true, "Module registered successfully");
        }
        
        private Response handleViewRegisteredModules(Request request) {
            String studentId = (String) request.getData("studentId");
            Student student = students.get(studentId);
            
            if (student == null) {
                return new Response(false, "Student not found");
            }
            
            List<models.Module> registeredModules = new ArrayList<>();
            for (String moduleCode : student.getRegisteredModules()) {
                models.Module module = modules.get(moduleCode);
                if (module != null) {
                    registeredModules.add(module);
                }
            }
            
            return new Response(true, "Registered modules retrieved", registeredModules);
        }
        
        // Admin Operations - Students
        private Response handleAddStudent(Request request) {
            String studentId = (String) request.getData("studentId");
            String name = (String) request.getData("name");
            String email = (String) request.getData("email");
            String password = (String) request.getData("password");
            
            if (students.containsKey(studentId)) {
                return new Response(false, "Student ID already exists");
            }
            
            Student student = new Student(studentId, name, email, password);
            students.put(studentId, student);
            return new Response(true, "Student added successfully", student);
        }
        
        private Response handleEditStudent(Request request) {
            String studentId = (String) request.getData("studentId");
            Student student = students.get(studentId);
            
            if (student == null) {
                return new Response(false, "Student not found");
            }
            
            String name = (String) request.getData("name");
            String email = (String) request.getData("email");
            String password = (String) request.getData("password");
            
            if (name != null) student.setName(name);
            if (email != null) student.setEmail(email);
            if (password != null) student.setPassword(password);
            
            return new Response(true, "Student updated successfully", student);
        }
        
        private Response handleViewStudent(Request request) {
            String studentId = (String) request.getData("studentId");
            Student student = students.get(studentId);
            
            if (student == null) {
                return new Response(false, "Student not found");
            }
            
            return new Response(true, "Student retrieved", student);
        }
        
        private Response handleListStudents() {
            return new Response(true, "Students retrieved", new ArrayList<>(students.values()));
        }
        
        // Admin Operations - Modules
        private Response handleAddModule(Request request) {
            String moduleCode = (String) request.getData("moduleCode");
            String moduleName = (String) request.getData("moduleName");
            String description = (String) request.getData("description");
            Integer credits = (Integer) request.getData("credits");
            
            if (modules.containsKey(moduleCode)) {
                return new Response(false, "Module code already exists");
            }
            
            models.Module module = new models.Module(moduleCode, moduleName, description, credits);
            modules.put(moduleCode, module);
            return new Response(true, "Module added successfully", module);
        }
        
        private Response handleEditModule(Request request) {
            String moduleCode = (String) request.getData("moduleCode");
            models.Module module = modules.get(moduleCode);
            
            if (module == null) {
                return new Response(false, "Module not found");
            }
            
            String moduleName = (String) request.getData("moduleName");
            String description = (String) request.getData("description");
            Integer credits = (Integer) request.getData("credits");
            
            if (moduleName != null) module.setModuleName(moduleName);
            if (description != null) module.setDescription(description);
            if (credits != null) module.setCredits(credits);
            
            return new Response(true, "Module updated successfully", module);
        }
        
        private Response handleViewModule(Request request) {
            String moduleCode = (String) request.getData("moduleCode");
            models.Module module = modules.get(moduleCode);
            
            if (module == null) {
                return new Response(false, "Module not found");
            }
            
            return new Response(true, "Module retrieved", module);
        }
        
        private Response handleListModules() {
            return new Response(true, "Modules retrieved", new ArrayList<>(modules.values()));
        }
    }
}
