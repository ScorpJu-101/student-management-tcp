# Student Management System - TCP Socket Application

A complete client-server system built using Java TCP sockets with WebSocket gateway for browser communication.

## 🎯 Core Technologies

- **TCP Sockets**: Client-server communication
- **Multithreading**: Concurrent client handling using ExecutorService
- **Object Serialization**: Data transfer between client and server
- **WebSocket**: Browser-to-TCP gateway for frontend communication
- **HTML/CSS/JavaScript**: Interactive user interface

## 📁 Project Structure

```
Networking/
├── src/
│   ├── models/
│   │   ├── Student.java       # Student model with serialization
│   │   ├── Module.java        # Module model with serialization
│   │   ├── Request.java       # Request wrapper for TCP communication
│   │   └── Response.java      # Response wrapper for TCP communication
│   ├── server/
│   │   └── TCPServer.java     # Main TCP server with multithreading
│   ├── gateway/
│   │   └── WebSocketGateway.java  # WebSocket gateway for browser
│   └── Launcher.java          # Starts both TCP server and WebSocket gateway
└── frontend/
    ├── admin/
    │   ├── index.html         # Admin UI
    │   └── admin.js           # Admin JavaScript
    ├── student/
    │   ├── index.html         # Student UI
    │   └── student.js         # Student JavaScript
    └── styles.css             # Shared CSS styles
```

## 🚀 How to Compile and Run

### Step 1: Compile the Java Files

Open PowerShell in the project directory and run:

```powershell
# Navigate to the src directory
cd D:\Networking\src

# Compile all Java files
javac models\*.java server\*.java gateway\*.java Launcher.java
```

### Step 2: Run the Application

```powershell
# Run the Launcher (starts both TCP server and WebSocket gateway)
java Launcher
```

You should see output like:
```
==============================================
  Student Management System - Starting...    
==============================================

[1] Starting TCP Server...
TCP Server started on port 8888

[2] Starting WebSocket Gateway...
WebSocket Gateway started on port 8080
WebSocket Gateway is running on port 8080

==============================================
  All services started successfully!         
  TCP Server: localhost:8888                 
  WebSocket Gateway: localhost:8080          
==============================================
```

### Step 3: Open the Frontend

Open your browser and navigate to:

- **Admin UI**: `file:///D:/Networking/frontend/admin/index.html`
- **Student UI**: `file:///D:/Networking/frontend/student/index.html`


## 🧪 Test Data

The system comes with pre-loaded sample data:

### Students:
- **ID**: S001
- **Name**: John Doe
- **Email**: john@example.com
- **Password**: password123

### Modules:
- **CS101**: Introduction to Programming (3 credits)
- **CS201**: Data Structures (4 credits)
- **CS301**: Database Systems (3 credits)

## 🔧 Technical Details

### TCP Server (Port 8888)
- Uses `ServerSocket` for accepting connections
- Implements multithreading with `ExecutorService` (cached thread pool)
- Each client handled by separate `ClientHandler` thread
- Uses `ObjectInputStream` and `ObjectOutputStream` for serialized communication
- Supports concurrent student and admin operations

### WebSocket Gateway (Port 8080)
- Bridges browser (WebSocket) to TCP server (sockets)
- Performs WebSocket handshake
- Converts JSON (from browser) to Java objects (for TCP server)
- Converts Java objects back to JSON (for browser)
- Built-in JSON parser and serializer (no external dependencies)

