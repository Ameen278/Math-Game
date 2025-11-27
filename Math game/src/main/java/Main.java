import com.google.cloud.firestore.Firestore;
import firebase.AuthService;
import firebase.FirebaseInit;
import latexEngine.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    static cli cli = new cli();
    public static void main(String[] args) {
        Firestore db = null;
        Scanner scanner = new Scanner(System.in);

        try {
            db = FirebaseInit.initialize();
            AuthService auth = new AuthService(db);

            while (true) {
                cli.loginMenu();

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
                    System.out.println(success ? "Registration successful!" : "Username already taken.");

                    if (success) {
                        boolean isAdmin = username.equalsIgnoreCase("admin");
                        main_menu(db, isAdmin, username, auth);
                    }

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
                        boolean isAdmin = username.equalsIgnoreCase("admin");
                        main_menu(db, isAdmin, username, auth);
                    } else {
                        System.out.println("Invalid username or password.");
                    }

                } else if (choice.equals("3")) {
                    System.out.println("Thank you for using Math Practice App! Goodbye!");
                    break;
                } else {
                    System.out.println("Invalid option. Please try again.");
                }
            }

        } catch (Exception e) {
            System.err.println("Fatal Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    // ================= MAIN MENU =================
    public static void main_menu(Firestore db, boolean isAdmin, String username, AuthService auth) {
        Scanner input = new Scanner(System.in);
        FormulaDb formulas = new FormulaDb(db);

        while (true) {
            // Show user stats
            try {
                var stats = auth.getUserStats(username);
                long points = stats != null ? (long) stats.get("points") : 0L;
                int solved = stats != null ? (int) stats.get("solvedCount") : 0;

                cli.mainMenu( username, points, solved, isAdmin); // from class cli.java
            
            } catch (Exception e) {
                System.out.println("\nWelcome, " + username + "!");
            }
                

            System.out.print("\nYour choice: ");
            String choiceStr = input.nextLine().trim();
            if (choiceStr.isEmpty()) continue;

            int choice;
            try {
                choice = Integer.parseInt(choiceStr);
            } catch (Exception e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            try {
                if (choice == 1) {
                    // === SOLVE A PROBLEM ===
                    List<String> allIds = formulas.listAll();
                    if (allIds.isEmpty()) {
                        System.out.println("No formulas available yet!");
                        continue;
                    }

                    var stats = auth.getUserStats(username);
                    List<String> solvedIds = stats != null ? (List<String>) stats.get("solvedFormulas") : new ArrayList<>();

                    List<String> unsolved = new ArrayList<>();
                    for (String id : allIds) {
                        if (!solvedIds.contains(id)) unsolved.add(id);
                    }

                    if (unsolved.isEmpty()) {
                        System.out.println("You've solved ALL problems! You're a math master!");
                        continue;
                    }

                    System.out.println("\nAvailable unsolved problems:");
                    for (int i = 0; i < unsolved.size(); i++) {
                        String id = unsolved.get(i);
                        String title = formulas.getTitle(id);
                        int pts = formulas.getPoints(id);
                        String diff = formulas.getDifficulty(id);
                        System.out.printf(i + 1+" "+diff+" "+pts+" → "+title+"\n");
                    }

                    System.out.print("\nChoose problem number: ");
                    int pick;
                    try {
                        pick = Integer.parseInt(input.nextLine().trim());
                    } catch (Exception e) {
                        System.out.println("Invalid number!");
                        continue;
                    }
                    if (pick < 1 || pick > unsolved.size()) {
                        System.out.println("Invalid choice!");
                        continue;
                    }

                    String selectedId = unsolved.get(pick - 1);
                    String latex = formulas.getLatex(selectedId);
                    String title = formulas.getTitle(selectedId);
                    int points = formulas.getPoints(selectedId);
                    String correctAnswer = formulas.getCorrectAnswer(selectedId);

                    System.out.printf("\n=== %s ===\n", title != null ? title : "Problem");
                    System.out.println("Points: " + points);
                    System.out.println("LaTeX: " + latex);
                    formulaToImage.convertAndOpen(latex, "CurrentProblem.png", 40f);

                    System.out.print("\nYour answer: ");
                    String userAnswer = input.nextLine().trim();

                    if (correctAnswer == null || correctAnswer.isEmpty()) {
                        System.out.println("No correct answer set. Auto-awarding points...");
                        boolean awarded = auth.markFormulaAsSolved(username, selectedId, points);
                        if (awarded) {
                            long newPoints = (long) auth.getUserStats(username).get("points");
                            System.out.printf("+%d points! Total now: %d\n", points, newPoints);
                        }
                    } else if (userAnswer.equalsIgnoreCase(correctAnswer)) {
                        boolean awarded = auth.markFormulaAsSolved(username, selectedId, points);
                        if (awarded) {
                            long newPoints = (long) auth.getUserStats(username).get("points");
                            System.out.printf("CORRECT!"+"+"+points+"and you have Total pts : "+newPoints);
                        } else {
                            System.out.println("You already solved this one!");
                        }
                    } else {
                        System.out.println("Incorrect! Correct answer: " + correctAnswer);
                    }

                } else if (isAdmin && choice == 2) {
                    // === ADD NEW FORMULA ===
                    System.out.println("\n--- Add New Formula ---");
                    System.out.print("Title: ");
                    String title = input.nextLine().trim();
                    if (title.isEmpty()) { System.out.println("Title required!"); continue; }

                    System.out.print("LaTeX code: ");
                    String latex = input.nextLine().trim();
                    if (latex.isEmpty()) { System.out.println("LaTeX required!"); continue; }

                    System.out.print("Correct answer: ");
                    String answer = input.nextLine().trim();
                    if (answer.isEmpty()) { System.out.println("Answer required!"); continue; }

                    System.out.print("Points (default 10): ");
                    String ptsStr = input.nextLine().trim();
                    int points = 10;
                    try {
                        if (!ptsStr.isEmpty()) points = Integer.parseInt(ptsStr);
                        if (points < 1) points = 10;
                    } catch (Exception e) { points = 10; }

                    System.out.print("Difficulty (easy/medium/hard, default medium): ");
                    String diff = input.nextLine().trim().toLowerCase();
                    if (!diff.matches("easy|medium|hard")) diff = "medium";

                    String id = formulas.addFormula(title, latex, points, diff, answer);
                    System.out.println("Formula added successfully! ID: " + id);

                } else if (isAdmin && choice == 3) {
                    // === FULL ADMIN EDITING MENU ===
                    List<String> list = formulas.listAll();
                    if (list.isEmpty()) {
                        System.out.println("No formulas to manage.");
                        continue;
                    }

                    System.out.println("\n=== Manage Formulas (Admin) ===");
                    for (int i = 0; i < list.size(); i++) {
                        String id = list.get(i);
                        String title = formulas.getTitle(id);
                        int pts = formulas.getPoints(id);
                        String diff = formulas.getDifficulty(id);
                        String ans = formulas.getCorrectAnswer(id);
                        System.out.printf(i + 1+".diffculty:"+diff+" point: "+pts+" category: "+title+" answer: "+ ans+"\n");
                    }

                    System.out.print("\nSelect formula to edit: ");
                    int pick;
                    try {
                        pick = Integer.parseInt(input.nextLine().trim()) - 1;
                    } catch (Exception e) {
                        System.out.println("Invalid number!");
                        continue;
                    }
                    if (pick < 0 || pick >= list.size()) {
                        System.out.println("Invalid choice!");
                        continue;
                    }

                    String docId = list.get(pick);
                    String currentTitle = formulas.getTitle(docId);

                    cli.editingMenu( currentTitle); // from class cli.java

                    int action;
                    try {
                        action = Integer.parseInt(input.nextLine().trim());
                    } catch (Exception e) {
                        System.out.println("Invalid action!");
                        continue;
                    }

                    switch (action) {
                        case 1:
                            System.out.print("New title: ");
                            formulas.updateTitle(docId, input.nextLine().trim());
                            System.out.println("Title updated!");
                            break;
                        case 2:
                            System.out.print("New LaTeX: ");
                            formulas.updateLatex(docId, input.nextLine().trim());
                            System.out.println("LaTeX updated!");
                            break;
                        case 3:
                            System.out.print("New points: ");
                            try {
                                int p = Integer.parseInt(input.nextLine().trim());
                                formulas.updatePoints(docId, p > 0 ? p : 10);
                                System.out.println("Points updated!");
                            } catch (Exception e) {
                                System.out.println("Invalid number! Using default 10.");
                                formulas.updatePoints(docId, 10);
                            }
                            break;
                        case 4:
                            System.out.print("New difficulty (easy/medium/hard): ");
                            formulas.updateDifficulty(docId, input.nextLine().trim());
                            System.out.println("Difficulty updated!");
                            break;
                        case 5:
                            System.out.print("New correct answer: ");
                            String newAns = input.nextLine().trim();
                            if (newAns.isEmpty()) {
                                System.out.println("Answer cannot be empty!");
                            } else {
                                formulas.updateAnswer(docId, newAns);
                                System.out.println("Correct answer updated!");
                            }
                            break;
                        case 6:
                            System.out.print("Type (Y) to confirm permanent deletion: ");
                            if (input.nextLine().trim().equalsIgnoreCase("Y")) {
                                formulas.removeFormula(docId);
                                System.out.println("Formula deleted permanently.");
                            } else {
                                System.out.println("Deletion cancelled.");
                            }
                            break;
                        case 7:
                            System.out.print("New formula ID: ");
                            String newId = input.nextLine().trim();
                            if (!newId.isEmpty()) {
                                try {
                                    formulas.changeDocId(docId, newId);
                                    System.out.println("Formula ID changed successfully!");
                                } catch (Exception e) {
                                    System.out.println("Failed to change ID: " + e.getMessage());
                                }
                            } else {
                                System.out.println("ID cannot be empty!");
                            }
                            break;
                        default:
                            System.out.println("Invalid action!");
                    }

                } else if ((isAdmin && choice == 4) || (!isAdmin && choice == 2)) {
                    System.out.println("Logging out... See you soon, " + username + "!");
                    return;

                }else if ((isAdmin && choice == 5) || (!isAdmin && choice == 3)){
                    cli.clearScreen();
                    leaderBoard lb = new leaderBoard();
                    lb.printTop10FromFirestore();
                    String b = input.nextLine();
                } else {
                    System.out.println("Invalid menu option!");
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

}