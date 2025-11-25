package latexEngine;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class FormulaService {
    private final Firestore db;

    public FormulaService(Firestore db) {
        this.db = db;
    }

    // ================= SAVE FORMULA =================
    public String saveFormula(String title, String latex) throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("latex", latex);
        data.put("createdAt", System.currentTimeMillis());

        DocumentReference doc = db.collection("latex1").document(); // Auto-ID
        doc.set(data).get();
        return doc.getId();
    }

    // ================= REMOVE FORMULA (ADMIN ONLY) =================
    public void removeFormula(String docId, boolean isAdmin) throws Exception {
        if (!isAdmin) throw new Exception("Only admin can remove formulas!");
        DocumentReference docRef = db.collection("latex1").document(docId);
        docRef.delete().get();
        System.out.println("Formula removed successfully: " + docId);
    }

    // ================= CHANGE FORMULA ID (ADMIN ONLY) =================
    public void changeFormulaId(String oldId, String newId, boolean isAdmin) throws Exception {
        if (!isAdmin) throw new Exception("Only admin can change formula ID!");

        DocumentReference oldDocRef = db.collection("latex1").document(oldId);
        DocumentSnapshot snapshot = oldDocRef.get().get();

        if (!snapshot.exists()) {
            throw new Exception("Old formula ID does not exist!");
        }

        Map<String, Object> data = snapshot.getData();
        if (data == null) throw new Exception("Formula data is empty!");

        // Create new document with new ID
        db.collection("latex1").document(newId).set(data).get();

        // Delete old document
        oldDocRef.delete().get();

        System.out.println("Formula ID changed from " + oldId + " to " + newId);
    }

    // ================= UPDATE TITLE (ADMIN ONLY) =================
    public void updateTitle(String docId, String newTitle, boolean isAdmin) throws Exception {
        if (!isAdmin) throw new Exception("Only admin can update formula title!");
        DocumentReference docRef = db.collection("latex1").document(docId);
        docRef.update("title", newTitle).get();
        System.out.println("Title updated successfully: " + docId);
    }

    // ================= UPDATE LATEX CODE (ADMIN ONLY) =================
    public void updateLatex(String docId, String newLatex, boolean isAdmin) throws Exception {
        if (!isAdmin) throw new Exception("Only admin can update LaTeX code!");
        DocumentReference docRef = db.collection("latex1").document(docId);
        docRef.update("latex", newLatex).get();
        System.out.println("LaTeX code updated successfully: " + docId);
    }
}
