# 🚀 Quick Start Guide

## Option 1: Using Batch Files (Easiest)

### Step 1: Compile
```powershell
.\compile.bat
```

### Step 2: Run
```powershell
.\run.bat
```

## Option 2: Manual Commands

### Step 1: Compile
```powershell
cd src
javac models\*.java server\*.java gateway\*.java Launcher.java
```

### Step 2: Run
```powershell
java Launcher
```

## Step 3: Open Frontend

Open in your browser:

**Admin Interface:**
```
file:///D:/Networking/frontend/admin/index.html
```

**Student Interface:**
```
file:///D:/Networking/frontend/student/index.html
```

---

## 🧪 Test Credentials

### Sample Student Login:
- **Student ID**: S001
- **Password**: password123

### Sample Modules (Pre-loaded):
- CS101 - Introduction to Programming (3 credits)
- CS201 - Data Structures (4 credits)
- CS301 - Database Systems (3 credits)

---

## 📝 Quick Test Scenario

### As Admin:
1. Open `frontend/admin/index.html`
2. Click "Students Management" tab
3. Click "List All Students" → See S001 (John Doe)
4. Add a new student: S002, Jane Smith, jane@example.com, pass123
5. Click "Modules Management" tab
6. Click "List All Modules" → See CS101, CS201, CS301
7. Add a new module: CS401, Advanced Topics, Advanced CS topics, 4

### As Student:
1. Open `frontend/student/index.html`
2. Login with S001 / password123
3. Click "Available Modules" → See all modules
4. Click "Register" on CS101
5. Click "My Modules" tab → See CS101 registered
6. Try registering for CS201 and CS301

---

## 🔍 Troubleshooting

### Server won't start?
- Check if ports 8888 and 8080 are available
- Close any other Java processes using these ports

### Frontend can't connect?
- Make sure the server is running first
- Check browser console (F12) for WebSocket errors
- Verify WebSocket is connecting to `ws://localhost:8080`

### Compilation errors?
- Make sure you're using Java 8 or higher
- Check that you're in the correct directory

---

## 🎯 System Architecture

```
Browser (WebSocket) ←→ WebSocketGateway:8080 ←→ TCPServer:8888 ←→ In-Memory Data
```

Enjoy! 🎓
