public class cli {

    public void loginMenu()
    {
        clearScreen();
    System.out.println("  ███╗   ███╗ █████╗ ████████╗██╗  ██╗     ██████╗  █████╗ ███╗   ███╗███████╗");
    System.out.println("  ████╗ ████║██╔══██╗╚══██╔══╝██║  ██║    ██╔════╝ ██╔══██╗████╗ ████║██╔════╝");
    System.out.println("  ██╔████╔██║███████║   ██║   ███████║    ██║  ███╗███████║██╔████╔██║██████╗");
    System.out.println("  ██║╚██╔╝██║██╔══██║   ██║   ██╔══██║    ██║   ██║██╔══██║██║╚██╔╝██║██════╝");
    System.out.println("  ██║ ╚═╝ ██║██║  ██║   ██║   ██║  ██║    ╚██████╔╝██║  ██║██║ ╚═╝ ██║███████╗");
    System.out.println("  ╚═╝     ╚═╝╚═╝  ╚═╝   ╚═╝   ╚═╝  ╚═╝     ╚═════╝ ╚═╝  ╚═╝╚═╝     ╚═╝╚══════╝");
    System.out.println("");
    System.out.println("                                 Math Game  ");
    System.out.println("");
    System.out.println("                    1) Register     2) Login     3) Exit");
    }

    public void mainMenu(String username, long points, int solved, boolean isAdmin)
    {
        clearScreen();
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.printf("║  Welcome, : "+ username);

        for(int i=0; i<(45-("║  Welcome, : "+ username).length()); i++)
            System.out.print(" ");
        System.out.println("║");

        System.out.printf("║  Points: "+ points+"|  Problems Solved:"+ solved);

        for(int i=0; i<(45-("║  Points: "+ points+"|  Problems Solved:"+ solved).length()); i++)
            System.out.print(" ");
        System.out.println("║");

        System.out.println("╚════════════════════════════════════════════╝");

        System.out.println("\n=== Main Menu ===");
        System.out.println("1. Solve a problem");

        if (isAdmin) {
            System.out.println("2. Add new formula (Admin)");
            System.out.println("3. Manage formulas (Admin)");
            System.out.println("4. Logout");
            System.out.println("5. Leaderboard");
        } else {
            System.out.println("2. Logout");
            System.out.println("3. Leaderboard");
        }
    }

    public void editingMenu(String currentTitle)
    {
        clearScreen();
        System.out.println("\nEditing: " + (currentTitle != null ? currentTitle : "Untitled"));
        System.out.println("1. Edit title");
        System.out.println("2. Edit LaTeX");
        System.out.println("3. Edit points");
        System.out.println("4. Edit difficulty");
        System.out.println("5. Edit correct answer");
        System.out.println("6. Delete formula");
        System.out.println("7. Change formula ID");
        System.out.print("\nChoose action: ");
    }

    public static void clearScreen() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            
            if (os.contains("windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (Exception e) {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
    }
    
}