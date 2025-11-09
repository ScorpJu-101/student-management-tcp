package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Student implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String studentId;
    private String name;
    private String email;
    private String password;
    private List<String> registeredModules;
    
    public Student() {
        this.registeredModules = new ArrayList<>();
    }
    
    public Student(String studentId, String name, String email, String password) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.registeredModules = new ArrayList<>();
    }
    
    // Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public List<String> getRegisteredModules() { return registeredModules; }
    public void setRegisteredModules(List<String> registeredModules) { 
        this.registeredModules = registeredModules; 
    }
    
    public void registerModule(String moduleCode) {
        if (!registeredModules.contains(moduleCode)) {
            registeredModules.add(moduleCode);
        }
    }
    
    @Override
    public String toString() {
        return "Student{" +
                "studentId='" + studentId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", registeredModules=" + registeredModules +
                '}';
    }
}
