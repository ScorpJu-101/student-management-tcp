# Bug Fixes - Student Management System

## Issues Fixed

### 1. ✅ WebSocket Frame Reading Corruption
**Problem:** Messages were getting corrupted ("acionY" instead of "action")
**Cause:** The second byte of WebSocket frame was being read twice
**Fix:** Properly read and store the second byte once, then extract both mask flag and payload length from it

### 2. ✅ JSON Parser - Nested Objects Not Parsed Correctly  
**Problem:** 
- "Student ID already exists" error even with new IDs
- `Cannot invoke "java.lang.Integer.intValue()" because "<local5>" is null` when adding modules
- `Cannot invoke "String.equals(Object)" because the return value of "models.Student.getPassword()" is null`

**Cause:** The simple regex-based JSON parser wasn't properly handling:
- Nested objects (the "data" field)
- Commas inside nested objects were breaking the split
- Integer values weren't being parsed correctly from nested objects

**Fix:** Implemented a proper recursive JSON parser with:
- `splitByComma()` - Splits only on commas outside quotes and braces
- `findFirstColon()` - Finds the first colon not inside quotes/braces  
- `parseValue()` - Recursively parses values with correct type detection:
  - Strings (quoted)
  - Integers (numeric)
  - Doubles (decimal)
  - Booleans (true/false)
  - Null values
  - Nested objects (recursive call)

## Testing

Created `TestJSONParser.java` to verify the parser works correctly:

```
✓ ADD_STUDENT: studentId=String, name=String, email=String, password=String
✓ ADD_MODULE: moduleCode=String, moduleName=String, description=String, credits=Integer
✓ LIST_STUDENTS: data={}  
✓ STUDENT_LOGIN: studentId=String, password=String
```

## Files Modified

1. `src/gateway/WebSocketGateway.java`
   - Fixed `readWebSocketMessage()` method
   - Completely rewrote `parseJSON()` method
   - Added helper methods: `splitByComma()`, `findFirstColon()`, `parseValue()`

2. `src/server/TCPServer.java`
   - Added temporary debug output (now removed)

## How to Test

1. Compile: `cd src; javac gateway\WebSocketGateway.java server\TCPServer.java Launcher.java`
2. Run: `java Launcher`
3. Open `frontend/admin/index.html`
4. Try adding a new student (S002, Jane Smith, jane@example.com, pass123) ✓
5. Try adding a new module (CS401, Advanced Topics, Description, 4 credits) ✓
6. Open `frontend/student/index.html`
7. Login with S001 / password123 ✓

## Status: ✅ ALL BUGS FIXED

All three issues are now resolved:
- ✅ Can add new students without "ID already exists" error
- ✅ Can add new modules with credits field working
- ✅ Can login as student successfully
