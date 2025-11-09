package models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String action;
    private Map<String, Object> data;
    
    public Request() {
        this.data = new HashMap<>();
    }
    
    public Request(String action) {
        this.action = action;
        this.data = new HashMap<>();
    }
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }
    
    public void addData(String key, Object value) {
        this.data.put(key, value);
    }
    
    public Object getData(String key) {
        return this.data.get(key);
    }
}
