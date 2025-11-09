package models;

import java.io.Serializable;

public class Module implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String moduleCode;
    private String moduleName;
    private String description;
    private int credits;
    
    public Module() {}
    
    public Module(String moduleCode, String moduleName, String description, int credits) {
        this.moduleCode = moduleCode;
        this.moduleName = moduleName;
        this.description = description;
        this.credits = credits;
    }
    
    // Getters and Setters
    public String getModuleCode() { return moduleCode; }
    public void setModuleCode(String moduleCode) { this.moduleCode = moduleCode; }
    
    public String getModuleName() { return moduleName; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    
    @Override
    public String toString() {
        return "Module{" +
                "moduleCode='" + moduleCode + '\'' +
                ", moduleName='" + moduleName + '\'' +
                ", description='" + description + '\'' +
                ", credits=" + credits +
                '}';
    }
}
