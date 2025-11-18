package models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class User {
    public String username;
    public String passwordHash;
    public Date createdAt;

    // Required empty constructor for Firestore
    public User() {}

    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.createdAt = new Date();
    }

    // Helper to convert to Map for saving
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("username", username);
        map.put("passwordHash", passwordHash);
        map.put("createdAt", createdAt);
        return map;
    }
}