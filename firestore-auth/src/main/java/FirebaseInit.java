import java.io.FileInputStream;
import java.io.InputStream;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

public class FirebaseInit {
    public static Firestore initialize() throws Exception {
        String path = "serviceAccountKey.json";

        InputStream serviceAccount;
        try {
            serviceAccount = new FileInputStream(path);
        } catch (Exception e) {
            throw new Exception("Missing 'serviceAccountKey.json' in project root! Download from Firebase Console.", e);
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
            System.out.println("Firebase connected successfully!");
        }

        return FirestoreClient.getFirestore();
    }
}