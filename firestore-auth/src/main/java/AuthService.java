import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import models.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthService {

    private final Firestore db;
    private final CollectionReference users;

    public AuthService(Firestore db) {
        this.db = db;
        this.users = db.collection("users");
    }

    public boolean register(String username, String password) throws Exception {
        // Check if username exists
        ApiFuture<QuerySnapshot> queryFuture = users.whereEqualTo("username", username).limit(1).get();
        List<QueryDocumentSnapshot> docs = queryFuture.get().getDocuments();

        if (!docs.isEmpty()) {
            return false; // Username taken
        }

        String hash = PasswordUtil.hashPassword(password);
        User user = new User(username, hash);

        users.add(user.toMap()).get(); // Save to Firestore
        return true;
    }

    public boolean login(String username, String password) throws Exception {
        ApiFuture<QuerySnapshot> queryFuture = users.whereEqualTo("username", username).limit(1).get();
        QuerySnapshot snapshot = queryFuture.get();

        if (snapshot.isEmpty()) {
            return false; // User not found
        }

        DocumentSnapshot doc = snapshot.getDocuments().get(0);
        String storedHash = doc.getString("passwordHash");

        return PasswordUtil.checkPassword(password, storedHash);
    }

    public Map<String, Object> getUserInfo(String username) throws Exception {
        ApiFuture<QuerySnapshot> queryFuture = users.whereEqualTo("username", username).limit(1).get();
        QuerySnapshot snapshot = queryFuture.get();

        if (snapshot.isEmpty()) return null;

        DocumentSnapshot doc = snapshot.getDocuments().get(0);
        Map<String, Object> data = new HashMap<>(doc.getData());
        data.put("id", doc.getId());
        data.remove("passwordHash"); // Never expose password
        return data;
    }
}