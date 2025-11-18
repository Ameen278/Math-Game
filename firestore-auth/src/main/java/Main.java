import com.google.cloud.firestore.Firestore;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Firestore db = null;
        Scanner scanner = new Scanner(System.in);

        try {
            db = FirebaseInit.initialize();
            AuthService auth = new AuthService(db);

            while (true) {
                System.out.println("\n=== Firestore Auth System ===");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.print("Choose an option: ");

                String choice = scanner.nextLine().trim();

                if (choice.equals("1")) {
                    System.out.print("Enter username: ");
                    String username = scanner.nextLine().trim();
                    System.out.print("Enter password: ");
                    String password = scanner.nextLine();

                    if (username.isEmpty() || password.isEmpty()) {
                        System.out.println("Username and password cannot be empty!");
                        continue;
                    }

                    boolean success = auth.register(username, password);
                    System.out.println(success
                            ? "Registration successful!"
                            : "Username already taken. Try another.");

                } else if (choice.equals("2")) {
                    System.out.print("Enter username: ");
                    String username = scanner.nextLine().trim();
                    System.out.print("Enter password: ");
                    String password = scanner.nextLine();

                    if (username.isEmpty() || password.isEmpty()) {
                        System.out.println("Please fill both fields.");
                        continue;
                    }

                    boolean loggedIn = auth.login(username, password);
                    if (loggedIn) {
                        System.out.println("Login successful! Welcome, " + username + "!");
                        var info = auth.getUserInfo(username);
                        if (info != null) {
                            System.out.println("User ID: " + info.get("id"));
                            System.out.println("Created: " + info.get("createdAt"));
                        }
                    } else {
                        System.out.println("Invalid username or password.");
                    }

                } else if (choice.equals("3")) {
                    System.out.println("Goodbye!");
                    break;
                } else {
                    System.out.println("Invalid option. Try again.");
                }
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
            // Do NOT close Firestore here — it's managed by FirebaseApp
        }
    }
}