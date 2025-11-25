package latexEngine;

import com.google.cloud.firestore.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormulaDb {
    private final Firestore db;

    public FormulaDb(Firestore db) {
        this.db = db;
    }

    // List all formula IDs
    public List<String> listAll() throws Exception {
        var result = db.collection("latex1").get().get();
        List<String> ids = new ArrayList<>();
        result.forEach(doc -> ids.add(doc.getId()));
        return ids;
    }

    // Get LaTeX code
    public String getLatex(String docId) throws Exception {
        var doc = db.collection("latex1").document(docId).get().get();
        if (!doc.exists()) return null;
        return doc.getString("latex");
    }

    // Get title
    public String getTitle(String docId) throws Exception {
        var doc = db.collection("latex1").document(docId).get().get();
        if (!doc.exists()) return null;
        return doc.getString("title");
    }

    // Get points
    public int getPoints(String docId) throws Exception {
        var doc = db.collection("latex1").document(docId).get().get();
        if (!doc.exists()) return 10;
        Long points = doc.getLong("points");
        return points != null ? points.intValue() : 10;
    }

    // Get difficulty
    public String getDifficulty(String docId) throws Exception {
        var doc = db.collection("latex1").document(docId).get().get();
        if (!doc.exists()) return "medium";
        String diff = doc.getString("difficulty");
        return (diff != null && !diff.isEmpty()) ? diff : "medium";
    }

    // NEW: Get correct answer
    public String getCorrectAnswer(String docId) throws Exception {
        var doc = db.collection("latex1").document(docId).get().get();
        if (!doc.exists()) return null;
        return doc.getString("answer");
    }

    // Add a new formula (now includes answer)
    public String addFormula(String title, String latex, int points, String difficulty, String answer) throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("latex", latex);
        data.put("points", points);
        data.put("difficulty", difficulty.toLowerCase());
        data.put("answer", answer.trim());
        data.put("createdAt", System.currentTimeMillis());

        DocumentReference doc = db.collection("latex1").document();
        doc.set(data).get();
        System.out.println("Formula added! ID: " + doc.getId() + " | Answer: " + answer);
        return doc.getId();
    }

    // Update title
    public void updateTitle(String docId, String newTitle) throws Exception {
        db.collection("latex1").document(docId).update("title", newTitle).get();
    }

    // Update LaTeX
    public void updateLatex(String docId, String newLatex) throws Exception {
        db.collection("latex1").document(docId).update("latex", newLatex).get();
    }

    // Update points
    public void updatePoints(String docId, int newPoints) throws Exception {
        if (newPoints < 1) newPoints = 1;
        db.collection("latex1").document(docId).update("points", newPoints).get();
    }

    // Update difficulty
    public void updateDifficulty(String docId, String newDifficulty) throws Exception {
        String diff = newDifficulty.toLowerCase();
        if (!diff.equals("easy") && !diff.equals("medium") && !diff.equals("hard")) {
            diff = "medium";
        }
        db.collection("latex1").document(docId).update("difficulty", diff).get();
    }

    // NEW: Update correct answer
    public void updateAnswer(String docId, String newAnswer) throws Exception {
        if (newAnswer == null || newAnswer.trim().isEmpty()) {
            throw new IllegalArgumentException("Answer cannot be empty!");
        }
        db.collection("latex1").document(docId).update("answer", newAnswer.trim()).get();
    }

    // Remove formula
    public void removeFormula(String docId) throws Exception {
        db.collection("latex1").document(docId).delete().get();
        System.out.println("Formula removed: " + docId);
    }

    // Change document ID
    public void changeDocId(String oldId, String newId) throws Exception {
        DocumentReference oldDoc = db.collection("latex1").document(oldId);
        DocumentSnapshot snapshot = oldDoc.get().get();
        if (!snapshot.exists()) throw new Exception("Document not found!");

        Map<String, Object> data = snapshot.getData();
        db.collection("latex1").document(newId).set(data).get();
        oldDoc.delete().get();
        System.out.println("ID changed: " + oldId + " → " + newId);
    }
}