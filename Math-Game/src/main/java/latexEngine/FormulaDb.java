package latexEngine;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

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

    // Get LaTeX code for a document ID
    public String getLatex(String docId) throws Exception {
        var doc = db.collection("latex1").document(docId).get().get();
        if (!doc.exists()) return null;
        return doc.getString("latex");
    }

    // Get the title for a document ID
    public String getTitle(String docId) throws Exception {
        var doc = db.collection("latex1").document(docId).get().get();
        if (!doc.exists()) return null;
        return doc.getString("title");
    }

    // Add a new formula
    public void addFormula(String title, String latex) throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("latex", latex);
        data.put("createdAt", System.currentTimeMillis());

        DocumentReference doc = db.collection("latex1").document(); // Auto-ID
        doc.set(data).get();
        System.out.println("Formula added! Document ID: " + doc.getId());
    }

    // Update title for a document
    public void updateTitle(String docId, String newTitle) throws Exception {
        db.collection("latex1").document(docId).update("title", newTitle).get();
    }

    // Update LaTeX code for a document
    public void updateLatex(String docId, String newLatex) throws Exception {
        db.collection("latex1").document(docId).update("latex", newLatex).get();
    }

    // Remove a formula
    public void removeFormula(String docId) throws Exception {
        db.collection("latex1").document(docId).delete().get();
        System.out.println("Formula removed: " + docId);
    }

    // Optional: Change document ID by copying data to a new ID
    public void changeDocId(String oldId, String newId) throws Exception {
        DocumentReference oldDoc = db.collection("latex1").document(oldId);
        DocumentSnapshot snapshot = oldDoc.get().get();
        if (!snapshot.exists()) throw new Exception("Old document not found!");

        Map<String, Object> data = snapshot.getData();
        db.collection("latex1").document(newId).set(data).get();
        oldDoc.delete().get();
        System.out.println("Document ID changed from " + oldId + " to " + newId);
    }
}
