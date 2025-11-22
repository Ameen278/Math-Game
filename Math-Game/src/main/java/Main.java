import com.google.cloud.firestore.Firestore;

import firebase.AuthService;
import firebase.FirebaseInit;

import latexEngine.*;

import java.util.Scanner;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Firestore db = null;
        Scanner scanner = new Scanner(System.in);

        try {
            db = FirebaseInit.initialize();
            AuthService auth = new AuthService(db);

            while (true) {
                System.out.println("\n===========================");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.print("Choose an option: ");

                String choice = scanner.nextLine().trim();

                if (choice.equals("1")) { // REGISTER
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

                    if (success) {
                        boolean isAdmin = username.equals("admin");
                        main_menu(db, isAdmin);
                    }

                } else if (choice.equals("2")) { // LOGIN
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

                            boolean isAdmin = username.equals("admin");
                            main_menu(db, isAdmin);
                        }
                    } else {
                        System.out.println("Invalid username or password.");
                    }

                } else if (choice.equals("3")) { // EXIT
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
        }
    }

    // ================= MAIN MENU =================
    public static void main_menu(Firestore db, boolean isAdmin) {
        Scanner input = new Scanner(System.in);
        FormulaDb formulas = new FormulaDb(db);
        FormulaService formulaService = new FormulaService(db);

        while (true) {
            System.out.println("\n=== Main Menu ===");
            System.out.println("1. Solve a problem (choose formula)");

            if (isAdmin) {
                System.out.println("2. Add a new LaTeX formula (Admin)");
                System.out.println("3. Manage existing formulas (Admin)");
                System.out.println("4. Back to login menu");
            } else {
                System.out.println("2. Back to login menu");
            }

            System.out.print("Choose: ");
            byte choice = input.nextByte();
            input.nextLine(); // clear buffer

            try {
                if (choice == 1) { // PLAY: choose formula
                    List<String> list = formulas.listAll();
                    if (list.isEmpty()) {
                        System.out.println("No formulas found!");
                        continue;
                    }

                    System.out.println("\nAvailable formulas:");
                    for (int i = 0; i < list.size(); i++) {
                        String docId = list.get(i);
                        String title = formulas.getTitle(docId);
                        System.out.println((i + 1) + ". " + title + " (ID: " + docId + ")");
                    }

                    System.out.print("\nChoose formula number: ");
                    int pick = input.nextInt();
                    input.nextLine();

                    if (pick < 1 || pick > list.size()) {
                        System.out.println("Invalid choice!");
                        continue;
                    }

                    String docId = list.get(pick - 1);
                    String latex = formulas.getLatex(docId);

                    if (latex == null) {
                        System.out.println("Error: formula not found!");
                        continue;
                    }

                    System.out.println("Loaded LaTeX: " + latex);
                    formulaToImage.convertAndOpen(latex, "GeneratedFromDB.png", 35f);

                } else if (isAdmin && choice == 2) { // ADD formula
                    System.out.print("Enter formula title: ");
                    String title = input.nextLine();
                    System.out.print("Enter LaTeX code: ");
                    String latex = input.nextLine();

                    String id = formulaService.saveFormula(title, latex);
                    System.out.println("Formula saved! Document ID: " + id);

                } else if (isAdmin && choice == 3) { // MANAGE formulas
                    List<String> list = formulas.listAll();
                    if (list.isEmpty()) {
                        System.out.println("No formulas found!");
                        continue;
                    }

                    System.out.println("\nAvailable formulas:");
                    for (int i = 0; i < list.size(); i++) {
                        String docId = list.get(i);
                        String title = formulas.getTitle(docId);
                        String latex = formulas.getLatex(docId);
                        System.out.println((i + 1) + ". ID: " + docId + " | Title: " + title + " | LaTeX: " + latex);
                    }

                    System.out.print("\nChoose formula number to manage: ");
                    int pick = input.nextInt();
                    input.nextLine();

                    if (pick < 1 || pick > list.size()) {
                        System.out.println("Invalid choice!");
                        continue;
                    }

                    String docId = list.get(pick - 1);

                    System.out.println("\nWhat do you want to do?");
                    System.out.println("1. Edit title");
                    System.out.println("2. Edit LaTeX code");
                    System.out.println("3. Remove formula");
                    System.out.println("4. Change formula ID");
                    System.out.print("Choose: ");
                    int adminChoice = input.nextInt();
                    input.nextLine();

                    switch (adminChoice) {
                        case 1:
                            System.out.print("Enter new title: ");
                            String newTitle = input.nextLine();
                            formulaService.updateTitle(docId, newTitle, isAdmin);
                            System.out.println("Title updated!");
                            break;
                        case 2:
                            System.out.print("Enter new LaTeX code: ");
                            String newLatex = input.nextLine();
                            formulaService.updateLatex(docId, newLatex, isAdmin);
                            System.out.println("LaTeX code updated!");
                            break;
                        case 3:
                            formulaService.removeFormula(docId, isAdmin);
                            System.out.println("Formula removed!");
                            break;
                        case 4:
                            System.out.print("Enter new formula ID: ");
                            String newId = input.nextLine();
                            formulaService.changeFormulaId(docId, newId, isAdmin);
                            System.out.println("Formula ID changed!");
                            break;
                        default:
                            System.out.println("Invalid option!");
                            break;
                    }

                } else if ((isAdmin && choice == 4) || (!isAdmin && choice == 2)) {
                    return; // back to login menu

                } else {
                    System.out.println("Invalid option. Try again.");
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
