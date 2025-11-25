package firebase;

import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;

import java.util.ArrayList;
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
        DocumentReference docRef = users.document(username); // Use username as doc ID
        DocumentSnapshot snapshot = docRef.get().get();

        if (snapshot.exists()) {
            return false; // Already exists
        }

        String hash = PasswordUtil.hashPassword(password);

        Map<String, Object> data = new HashMap<>();
        data.put("username", username);
        data.put("passwordHash", hash);
        data.put("points", 0L);
        data.put("solvedCount", 0);
        data.put("solvedFormulas", new ArrayList<String>());

        docRef.set(data).get();
        return true;
    }

    public boolean login(String username, String password) throws Exception {
        DocumentSnapshot doc = users.document(username).get().get();
        if (!doc.exists()) return false;

        String storedHash = doc.getString("passwordHash");
        return PasswordUtil.checkPassword(password, storedHash);
    }

    // Get full user stats (points, solved count, list)
    public Map<String, Object> getUserStats(String username) throws Exception {
        DocumentSnapshot doc = users.document(username).get().get();
        if (!doc.exists()) return null;

        Map<String, Object> stats = new HashMap<>();
        Long points = doc.getLong("points");
        Long solvedCount = doc.getLong("solvedCount");
        List<String> solved = (List<String>) doc.get("solvedFormulas");

        stats.put("points", points != null ? points : 0L);
        stats.put("solvedCount", solvedCount != null ? solvedCount.intValue() : 0);
        stats.put("solvedFormulas", solved != null ? solved : new ArrayList<String>());

        return stats;
    }

    // MARK AS SOLVED + ADD POINTS (only if not already solved)
    public boolean markFormulaAsSolved(String username, String formulaId, int pointsToAdd) throws Exception {
        DocumentReference userRef = users.document(username);

        // Use transaction to prevent race conditions
        return db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(userRef).get();

            if (!snapshot.exists()) throw new Exception("User not found");

            List<String> solved = (List<String>) snapshot.get("solvedFormulas");
            if (solved == null) solved = new ArrayList<>();
            if (solved.contains(formulaId)) {
                return false; // Already solved
            }

            // Atomic updates
            transaction.update(userRef, "points", FieldValue.increment(pointsToAdd));
            transaction.update(userRef, "solvedCount", FieldValue.increment(1));
            transaction.update(userRef, "solvedFormulas", FieldValue.arrayUnion(formulaId));

            return true;
        }).get();
    }
}