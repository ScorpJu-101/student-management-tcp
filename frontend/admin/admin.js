// WebSocket connection
let ws = null;

// Connect to WebSocket Gateway
function connectWebSocket() {
    ws = new WebSocket('ws://localhost:8080');
    
    ws.onopen = () => {
        console.log('Connected to WebSocket Gateway');
        showStatus('Connected to server', 'success');
    };
    
    ws.onclose = () => {
        console.log('Disconnected from WebSocket Gateway');
        showStatus('Disconnected from server. Reconnecting...', 'error');
        setTimeout(connectWebSocket, 3000);
    };
    
    ws.onerror = (error) => {
        console.error('WebSocket error:', error);
        showStatus('Connection error', 'error');
    };
    
    ws.onmessage = (event) => {
        console.log('Received:', event.data);
        handleResponse(JSON.parse(event.data));
    };
}

// Send request to server
function sendRequest(action, data = {}) {
    if (ws && ws.readyState === WebSocket.OPEN) {
        const request = { action, data };
        ws.send(JSON.stringify(request));
        showStatus('Sending request...', 'info');
    } else {
        showStatus('Not connected to server', 'error');
    }
}

// Handle response from server
let currentAction = '';
function handleResponse(response) {
    if (response.success) {
        showStatus(response.message, 'success');
        
        // Handle different response types
        if (currentAction === 'LIST_STUDENTS') {
            displayStudents(response.data);
        } else if (currentAction === 'LIST_MODULES') {
            displayModules(response.data);
        } else if (currentAction === 'VIEW_STUDENT') {
            displayStudentDetails(response.data);
        } else if (currentAction === 'VIEW_MODULE') {
            displayModuleDetails(response.data);
        }
    } else {
        showStatus(response.message, 'error');
    }
}

// Student Management Functions
document.getElementById('student-form').addEventListener('submit', (e) => {
    e.preventDefault();
    addStudent();
});

function addStudent() {
    const data = {
        studentId: document.getElementById('studentId').value,
        name: document.getElementById('studentName').value,
        email: document.getElementById('studentEmail').value,
        password: document.getElementById('studentPassword').value
    };
    currentAction = 'ADD_STUDENT';
    sendRequest('ADD_STUDENT', data);
}

function updateStudent() {
    const data = {
        studentId: document.getElementById('studentId').value,
        name: document.getElementById('studentName').value,
        email: document.getElementById('studentEmail').value,
        password: document.getElementById('studentPassword').value
    };
    currentAction = 'EDIT_STUDENT';
    sendRequest('EDIT_STUDENT', data);
}

function viewStudent() {
    const studentId = prompt('Enter Student ID:');
    if (studentId) {
        currentAction = 'VIEW_STUDENT';
        sendRequest('VIEW_STUDENT', { studentId });
    }
}

function listStudents() {
    currentAction = 'LIST_STUDENTS';
    sendRequest('LIST_STUDENTS');
}

function clearStudentForm() {
    document.getElementById('student-form').reset();
}

function displayStudents(students) {
    const container = document.getElementById('students-result');
    if (!students || students.length === 0) {
        container.innerHTML = '<p>No students found.</p>';
        return;
    }
    
    let html = '<table><thead><tr><th>Student ID</th><th>Name</th><th>Email</th><th>Registered Modules</th><th>Actions</th></tr></thead><tbody>';
    students.forEach(student => {
        html += `<tr>
            <td>${student.studentId}</td>
            <td>${student.name}</td>
            <td>${student.email}</td>
            <td>${student.registeredModules ? student.registeredModules.join(', ') : 'None'}</td>
            <td><button class="btn-small" onclick="editStudent('${student.studentId}', '${student.name}', '${student.email}')">Edit</button></td>
        </tr>`;
    });
    html += '</tbody></table>';
    container.innerHTML = html;
}

function displayStudentDetails(student) {
    const container = document.getElementById('students-result');
    container.innerHTML = `
        <div class="detail-card">
            <h3>Student Details</h3>
            <p><strong>ID:</strong> ${student.studentId}</p>
            <p><strong>Name:</strong> ${student.name}</p>
            <p><strong>Email:</strong> ${student.email}</p>
            <p><strong>Registered Modules:</strong> ${student.registeredModules ? student.registeredModules.join(', ') : 'None'}</p>
        </div>
    `;
}

function editStudent(id, name, email) {
    document.getElementById('studentId').value = id;
    document.getElementById('studentName').value = name;
    document.getElementById('studentEmail').value = email;
}

// Module Management Functions
document.getElementById('module-form').addEventListener('submit', (e) => {
    e.preventDefault();
    addModule();
});

function addModule() {
    const data = {
        moduleCode: document.getElementById('moduleCode').value,
        moduleName: document.getElementById('moduleName').value,
        description: document.getElementById('moduleDescription').value,
        credits: parseInt(document.getElementById('moduleCredits').value)
    };
    currentAction = 'ADD_MODULE';
    sendRequest('ADD_MODULE', data);
}

function updateModule() {
    const data = {
        moduleCode: document.getElementById('moduleCode').value,
        moduleName: document.getElementById('moduleName').value,
        description: document.getElementById('moduleDescription').value,
        credits: parseInt(document.getElementById('moduleCredits').value)
    };
    currentAction = 'EDIT_MODULE';
    sendRequest('EDIT_MODULE', data);
}

function viewModule() {
    const moduleCode = prompt('Enter Module Code:');
    if (moduleCode) {
        currentAction = 'VIEW_MODULE';
        sendRequest('VIEW_MODULE', { moduleCode });
    }
}

function listModules() {
    currentAction = 'LIST_MODULES';
    sendRequest('LIST_MODULES');
}

function clearModuleForm() {
    document.getElementById('module-form').reset();
}

function displayModules(modules) {
    const container = document.getElementById('modules-result');
    if (!modules || modules.length === 0) {
        container.innerHTML = '<p>No modules found.</p>';
        return;
    }
    
    let html = '<table><thead><tr><th>Module Code</th><th>Module Name</th><th>Description</th><th>Credits</th><th>Actions</th></tr></thead><tbody>';
    modules.forEach(module => {
        html += `<tr>
            <td>${module.moduleCode}</td>
            <td>${module.moduleName}</td>
            <td>${module.description}</td>
            <td>${module.credits}</td>
            <td><button class="btn-small" onclick="editModule('${module.moduleCode}', '${module.moduleName}', '${module.description}', ${module.credits})">Edit</button></td>
        </tr>`;
    });
    html += '</tbody></table>';
    container.innerHTML = html;
}

function displayModuleDetails(module) {
    const container = document.getElementById('modules-result');
    container.innerHTML = `
        <div class="detail-card">
            <h3>Module Details</h3>
            <p><strong>Code:</strong> ${module.moduleCode}</p>
            <p><strong>Name:</strong> ${module.moduleName}</p>
            <p><strong>Description:</strong> ${module.description}</p>
            <p><strong>Credits:</strong> ${module.credits}</p>
        </div>
    `;
}

function editModule(code, name, description, credits) {
    document.getElementById('moduleCode').value = code;
    document.getElementById('moduleName').value = name;
    document.getElementById('moduleDescription').value = description;
    document.getElementById('moduleCredits').value = credits;
}

// Tab switching
function showTab(tabName) {
    const tabs = document.querySelectorAll('.tab-content');
    tabs.forEach(tab => tab.classList.remove('active'));
    
    const buttons = document.querySelectorAll('.tab-button');
    buttons.forEach(btn => btn.classList.remove('active'));
    
    document.getElementById(tabName + '-tab').classList.add('active');
    event.target.classList.add('active');
}

// Show status message
function showStatus(message, type) {
    const statusDiv = document.getElementById('status-message');
    statusDiv.textContent = message;
    statusDiv.className = 'status-message ' + type;
    statusDiv.style.display = 'block';
    
    setTimeout(() => {
        statusDiv.style.display = 'none';
    }, 5000);
}

// Initialize WebSocket connection on page load
window.addEventListener('load', connectWebSocket);
