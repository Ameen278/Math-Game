import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import java.util.concurrent.ExecutionException;

import java.util.*;

public  class leaderBoard
{
    public Map<String, Long> fetchLeaderboardMap() throws InterruptedException, ExecutionException {
        Firestore db = FirestoreClient.getFirestore();

        ApiFuture<QuerySnapshot> query = db.collection("users")
                .get(); // ممكن تضيف orderBy لو عايز من السيرفر

        List<QueryDocumentSnapshot> docs = query.get().getDocuments();

        Map<String, Long> map = new HashMap<>();
        for (DocumentSnapshot doc : docs) {
            String playerId = doc.getId(); // أو doc.getString("name") لو بتحب الاسم key
            Long score = doc.contains("points") ? doc.getLong("points") : 0L;
            map.put(playerId, score);
        }

        return map;
    }

    public void printTop10FromFirestore() throws Exception {
        Firestore db = FirestoreClient.getFirestore();

        ApiFuture<QuerySnapshot> query = db.collection("users")
                .orderBy("points", Query.Direction.DESCENDING)
                .limit(10)
                .get();

        List<QueryDocumentSnapshot> docs = query.get().getDocuments();

        LinkedHashMap<String, Long> top = new LinkedHashMap<>();
        for (DocumentSnapshot d : docs) {
            String name = d.getString("username"); // أو d.getId()
            Long score = d.getLong("points");
            top.put(name != null ? name : d.getId(), score != null ? score : 0L);
        }

        int rank = 1;
        System.out.println("\n╔════════════════════════════════════════════╗");
        for (Map.Entry<String, Long> e : top.entrySet()) {
            String line = ("║ " + rank + ". " + e.getKey() + " => " + e.getValue());
            System.out.print(line);

            for(int i=0; i<(45-line.length()); i++)
                System.out.print(" ");
            System.out.println("║");

            System.out.println("╠════════════════════════════════════════════╣");
            rank++;
        }
        System.out.println("║ Study hard to be in the list               ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.println("press ENTER to go back...");
    }
//46
}
