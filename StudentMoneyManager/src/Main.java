import ui.ConsoleUI;

public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 Starting Student Money Manager...");

        try {
            displayWelcomeMessage();
            ConsoleUI app = new ConsoleUI();
            app.start();

        } catch (Exception e) {
            System.err.println("❌ Error starting application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void displayWelcomeMessage() {
        System.out.println("\n==========================================");
        System.out.println("    WELCOME ");
        System.out.println("    STUDENT MONEY MANAGER v1.0");
        System.out.println("==========================================");
        System.out.println("📊 Features:");
        System.out.println("  1. User Registration & Login");
        System.out.println("  2. Income & Expense Tracking");
        System.out.println("  3. Budget Management");
        System.out.println("  4. Financial Analytics");
        System.out.println("  5. Profile Management");
        System.out.println("==========================================\n");
    }
}