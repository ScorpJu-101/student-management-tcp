@echo off
echo ============================================
echo   Compiling Student Management System
echo ============================================
echo.

cd src

echo [1/3] Compiling model classes...
javac models\Student.java models\Module.java models\Request.java models\Response.java

echo [2/3] Compiling server and gateway...
javac server\TCPServer.java
javac gateway\WebSocketGateway.java

echo [3/3] Compiling launcher...
javac Launcher.java

echo.
echo ============================================
echo   Compilation Complete!
echo ============================================
echo   All files compiled successfully.
echo   Run 'run.bat' to start the system.
echo ============================================
echo.

cd ..
