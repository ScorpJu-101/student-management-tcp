#Project Summary - Student Management System


###Complete File Structure

```
D:\Networking\
├── src/
│   ├── models/
│   │   ├── Student.java       ✅ Serializable student model
│   │   ├── Module.java        ✅ Serializable module model
│   │   ├── Request.java       ✅ Request wrapper for TCP
│   │   └── Response.java      ✅ Response wrapper for TCP
│   ├── server/
│   │   └── TCPServer.java     ✅ Multithreaded TCP server (port 8888)
│   ├── gateway/
│   │   └── WebSocketGateway.java  ✅ WebSocket gateway (port 8080)
│   └── Launcher.java          ✅ Main launcher class
├── frontend/
│   ├── admin/
│   │   ├── index.html         ✅ Admin dashboard UI
│   │   └── admin.js           ✅ Admin JavaScript logic
│   ├── student/
│   │   ├── index.html         ✅ Student portal UI
│   │   └── student.js         ✅ Student JavaScript logic
│   └── styles.css             ✅ Shared CSS styling
├── compile.bat                ✅ Compilation script
├── run.bat                    ✅ Run script
├── README.md                  ✅ Full documentation
└── QUICKSTART.md              ✅ Quick start guide
```

---

###TCP Sockets
- `ServerSocket` on port 8888
- Socket-based client connections
- Reliable connection-oriented communication

###Multithreading
- `ExecutorService` with cached thread pool
- Each client handled by separate `ClientHandler` thread
- Concurrent request processing
- Thread-safe data storage using `ConcurrentHashMap`

###Object Serialization
- All model classes implement `Serializable`
- `ObjectOutputStream` for sending objects
- `ObjectInputStream` for receiving objects
- Automatic serialization/deserialization

###WebSocket Protocol
- Custom WebSocket implementation (no external libraries)
- WebSocket handshake with SHA-1 and Base64
- Frame encoding/decoding
- Full-duplex communication with browser

---

##Communication Flow

```
┌─────────────┐         ┌──────────────────┐         ┌─────────────┐
│   Browser   │         │  WebSocket GW    │         │ TCP Server  │
│ (HTML/JS)   │◄───────►│  Port 8080       │◄───────►│ Port 8888   │
└─────────────┘         └──────────────────┘         └─────────────┘
   WebSocket              JSON ↔ Objects               ObjectStream
```

### Data Flow:
1. **Browser** sends JSON via WebSocket
2. **Gateway** converts JSON → Request object
3. **Gateway** sends Request to TCP server via socket
4. **TCP Server** processes request (multithreaded)
5. **TCP Server** sends Response back via socket
6. **Gateway** converts Response → JSON
7. **Browser** receives JSON and updates UI

---

##Supported Actions (13 Total)

### Student Operations (4):
1. `STUDENT_LOGIN` - Authenticate student
2. `VIEW_MODULES` - Get all modules
3. `REGISTER_MODULE` - Enroll in module
4. `VIEW_REGISTERED_MODULES` - Get enrolled modules

### Admin - Student Operations (4):
5. `ADD_STUDENT` - Create student
6. `EDIT_STUDENT` - Update student
7. `VIEW_STUDENT` - Get student details
8. `LIST_STUDENTS` - Get all students

### Admin - Module Operations (4):
9. `ADD_MODULE` - Create module
10. `EDIT_MODULE` - Update module
11. `VIEW_MODULE` - Get module details
12. `LIST_MODULES` - Get all modules

### Special (1):
13. `Launcher` - Starts both servers together

---



### Students (1):
```
ID: S001
Name: John Doe
Email: john@example.com
Password: password123
```



---


### Quick Method:
```powershell
# Compile
.\compile.bat

# Run
.\run.bat
```

### Manual Method:
```powershell
# Compile
cd src
javac models\*.java server\*.java gateway\*.java Launcher.java

# Run
java Launcher
```

---

---


This project demonstrates:
1. **Networking**: TCP sockets, WebSocket protocol
2. **Concurrency**: Multithreading, thread pools, synchronization
3. **Serialization**: Object serialization, custom serialization
4. **Protocols**: Request-response, client-server architecture
5. **Full-Stack**: Backend (Java) + Frontend (HTML/CSS/JS)
6. **Integration**: Bridging different protocols (WebSocket ↔ TCP)

---

