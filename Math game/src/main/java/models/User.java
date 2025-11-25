package models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class User {
    public String username;
    public String passwordHash;
    public Date createdAt;
    public int points;
    public List<String> solvedFormulas;

    // Required empty constructor for Firestore
    public User() {
        this.solvedFormulas = new ArrayList<>();
        this.points = 0;
    }

    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.createdAt = new Date();
        this.points = 0;
        this.solvedFormulas = new ArrayList<>();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("username", username);
        map.put("passwordHash", passwordHash);
        map.put("createdAt", createdAt);
        map.put("points", points);
        map.put("solvedFormulas", solvedFormulas);
        return map;
    }
}