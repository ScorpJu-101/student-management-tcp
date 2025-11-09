# 📤 How to Upload to GitHub

## Step-by-Step Guide to Upload Your Project

### Step 1: Create a New Repository on GitHub

1. Go to [GitHub](https://github.com)
2. Click the **+** icon in the top right corner
3. Select **"New repository"**
4. Fill in the details:
   - **Repository name**: `student-management-tcp-system` (or your preferred name)
   - **Description**: "Student Management System using Java TCP Sockets, Multithreading, and WebSocket Gateway"
   - **Visibility**: Choose Public or Private
   - **DO NOT** initialize with README, .gitignore, or license (we'll add these)
5. Click **"Create repository"**

### Step 2: Initialize Git in Your Project

Open PowerShell in the `D:\Networking` folder and run:

```powershell
# Navigate to your project folder
cd D:\Networking

# Initialize git repository
git init

# Add all files
git add .

# Create first commit
git commit -m "Initial commit: Student Management System with TCP Sockets"
```

### Step 3: Connect to GitHub and Push

```powershell
# Add your GitHub repository as remote (replace YOUR_USERNAME and REPO_NAME)
git remote add origin https://github.com/YOUR_USERNAME/REPO_NAME.git

# Push to GitHub
git branch -M main
git push -u origin main
```

**Replace:**
- `YOUR_USERNAME` with your GitHub username
- `REPO_NAME` with the repository name you created

### Step 4: Verify Upload

1. Go to your GitHub repository URL
2. You should see all your files uploaded!

---

## 📋 Alternative: Using GitHub Desktop (Easier)

If you prefer a GUI:

1. **Download GitHub Desktop**: https://desktop.github.com/
2. **Install and sign in** with your GitHub account
3. Click **File → Add Local Repository**
4. Select the `D:\Networking` folder
5. Click **Initialize Git Repository** if prompted
6. Click **Publish Repository** in the top bar
7. Choose repository name and visibility
8. Click **Publish Repository**

---

## 🎯 Quick Commands (Copy & Paste)

### First Time Setup (if not already done):
```powershell
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"
```

### Upload Your Project:
```powershell
# Navigate to project
cd D:\Networking

# Initialize and commit
git init
git add .
git commit -m "Initial commit: TCP Socket Student Management System"

# Connect to GitHub (replace with your repo URL)
git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPO.git

# Push to GitHub
git branch -M main
git push -u origin main
```

---

## 📝 What Will Be Uploaded

Your entire project structure:
```
Networking/
├── src/
│   ├── models/          (Student, Module, Request, Response)
│   ├── server/          (TCPServer)
│   ├── gateway/         (WebSocketGateway)
│   └── Launcher.java
├── frontend/
│   ├── admin/           (Admin UI)
│   ├── student/         (Student UI)
│   └── styles.css
├── compile.bat
├── run.bat
├── README.md
├── QUICKSTART.md
├── PROJECT_SUMMARY.md
├── BUGFIXES.md
└── ARCHITECTURE.html
```

---

## ⚠️ Optional: Create .gitignore

Before uploading, create a `.gitignore` file to exclude compiled files:

```powershell
# Create .gitignore in D:\Networking
@"
# Compiled class files
*.class

# Test files
src/test/

# IDE files
.vscode/
.idea/
*.iml

# OS files
.DS_Store
Thumbs.db
"@ | Out-File -FilePath .gitignore -Encoding utf8
```

Then run:
```powershell
git add .gitignore
git commit -m "Add .gitignore"
```

---

## 🔑 Authentication Options

### Option 1: HTTPS (Recommended for beginners)
- GitHub will prompt for username and password
- Use a **Personal Access Token** instead of password
- Create token at: https://github.com/settings/tokens

### Option 2: SSH (More secure)
```powershell
# Generate SSH key
ssh-keygen -t ed25519 -C "your.email@example.com"

# Copy public key
cat ~/.ssh/id_ed25519.pub | clip

# Add to GitHub: Settings → SSH and GPG keys → New SSH key
# Then use SSH URL instead:
git remote add origin git@github.com:YOUR_USERNAME/YOUR_REPO.git
```

---

## 🆘 Troubleshooting

### Error: "fatal: not a git repository"
```powershell
git init
```

### Error: "remote origin already exists"
```powershell
git remote remove origin
git remote add origin YOUR_REPO_URL
```

### Error: "Permission denied"
- Make sure you're logged into the correct GitHub account
- Use a Personal Access Token instead of password
- Or set up SSH keys

### Want to update after making changes?
```powershell
git add .
git commit -m "Description of changes"
git push
```

---

## ✅ Done!

Your project is now on GitHub! 🎉

Share your repository URL with others:
`https://github.com/YOUR_USERNAME/YOUR_REPO`
