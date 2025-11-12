// WebSocket connection
let ws = null;
let currentStudent = null;

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
    console.log('handleResponse called. currentAction:', currentAction, 'response:', response);
    
    if (response.success) {
        showStatus(response.message, 'success');
        
        // Handle different response types based on message content
        if (response.message === 'Login successful') {
            handleLoginSuccess(response.data);
        } else if (response.message === 'Modules retrieved') {
            console.log('Calling displayModules with data:', response.data);
            displayModules(response.data);
        } else if (response.message === 'Registered modules retrieved') {
            displayRegisteredModules(response.data);
        } else if (response.message === 'Module registered successfully') {
            loadRegisteredModules();
            loadModules(); // Refresh available modules to show updated status
        }
    } else {
        showStatus(response.message, 'error');
    }
}

// Login functionality
document.getElementById('login-form').addEventListener('submit', (e) => {
    e.preventDefault();
    login();
});

function login() {
    const studentId = document.getElementById('loginStudentId').value;
    const password = document.getElementById('loginPassword').value;
    
    currentAction = 'STUDENT_LOGIN';
    sendRequest('STUDENT_LOGIN', { studentId, password });
}

function handleLoginSuccess(student) {
    currentStudent = student;
    
    // Debug: Log the student object received
    console.log('Login successful! Student object:', student);
    console.log('Registered modules:', student.registeredModules);
    
    // Hide login section, show dashboard
    document.getElementById('login-section').style.display = 'none';
    document.getElementById('dashboard-section').style.display = 'block';
    
    // Update student info
    document.getElementById('student-name').textContent = student.name;
    document.getElementById('logged-student-id').textContent = student.studentId;
    
    // Load modules
    loadModules();
    loadRegisteredModules();
}

function logout() {
    currentStudent = null;
    
    // Show login section, hide dashboard
    document.getElementById('login-section').style.display = 'block';
    document.getElementById('dashboard-section').style.display = 'none';
    
    // Clear form
    document.getElementById('login-form').reset();
    
    showStatus('Logged out successfully', 'info');
}

// Module functions
function loadModules() {
    currentAction = 'VIEW_MODULES';
    sendRequest('VIEW_MODULES');
}

function loadRegisteredModules() {
    if (!currentStudent) {
        showStatus('Please login first', 'error');
        return;
    }
    
    currentAction = 'VIEW_REGISTERED_MODULES';
    sendRequest('VIEW_REGISTERED_MODULES', { studentId: currentStudent.studentId });
}

function registerModule(moduleCode) {
    if (!currentStudent) {
        showStatus('Please login first', 'error');
        return;
    }
    
    if (confirm(`Register for module ${moduleCode}?`)) {
        currentAction = 'REGISTER_MODULE';
        sendRequest('REGISTER_MODULE', {
            studentId: currentStudent.studentId,
            moduleCode: moduleCode
        });
    }
}

function displayModules(modules) {
    const container = document.getElementById('modules-list');
    if (!modules || modules.length === 0) {
        container.innerHTML = '<p>No modules available.</p>';
        return;
    }
    
    // Debug: Check current student's registered modules
    console.log('Current Student:', currentStudent);
    console.log('Registered Modules:', currentStudent ? currentStudent.registeredModules : 'No student');
    
    let html = '<table><thead><tr><th>Code</th><th>Module Name</th><th>Description</th><th>Credits</th><th>Action</th></tr></thead><tbody>';
    modules.forEach(module => {
        // Check if module is registered (ensure registeredModules is an array)
        const registeredModules = currentStudent && currentStudent.registeredModules ? 
                                 (Array.isArray(currentStudent.registeredModules) ? 
                                  currentStudent.registeredModules : []) : [];
        const isRegistered = registeredModules.includes(module.moduleCode);
        
        console.log(`Module ${module.moduleCode}: isRegistered=${isRegistered}, registeredModules=`, registeredModules);
        
        html += `<tr>
            <td>${module.moduleCode}</td>
            <td>${module.moduleName}</td>
            <td>${module.description}</td>
            <td>${module.credits}</td>
            <td>
                ${isRegistered ? 
                    '<span class="badge-registered">Registered</span>' : 
                    `<button class="btn btn-primary btn-small" onclick="registerModule('${module.moduleCode}')">Register</button>`
                }
            </td>
        </tr>`;
    });
    html += '</tbody></table>';
    container.innerHTML = html;
}

function displayRegisteredModules(modules) {
    const container = document.getElementById('registered-modules-list');
    if (!modules || modules.length === 0) {
        container.innerHTML = '<p>You have not registered for any modules yet.</p>';
        return;
    }
    
    let html = '<table><thead><tr><th>Code</th><th>Module Name</th><th>Description</th><th>Credits</th></tr></thead><tbody>';
    modules.forEach(module => {
        html += `<tr>
            <td>${module.moduleCode}</td>
            <td>${module.moduleName}</td>
            <td>${module.description}</td>
            <td>${module.credits}</td>
        </tr>`;
    });
    html += '</tbody></table>';
    container.innerHTML = html;
    
    // Update the current student's registered modules list
    if (currentStudent) {
        currentStudent.registeredModules = modules.map(m => m.moduleCode);
    }
}

// Tab switching
function showTab(tabName) {
    const tabs = document.querySelectorAll('.tab-content');
    tabs.forEach(tab => tab.classList.remove('active'));
    
    const buttons = document.querySelectorAll('.tab-button');
    buttons.forEach(btn => btn.classList.remove('active'));
    
    document.getElementById(tabName + '-tab').classList.add('active');
    event.target.classList.add('active');
    
    // Load data when switching tabs
    if (tabName === 'modules') {
        loadModules();
    } else if (tabName === 'registered') {
        loadRegisteredModules();
    }
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
