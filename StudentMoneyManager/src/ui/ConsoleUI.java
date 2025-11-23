package ui;

import models.User;
import models.Transaction;
import services.UserService;
import services.TransactionService;
import services.AnalyticsService;
import java.util.Scanner;

public class ConsoleUI {
    private Scanner input;
    private UserService userService;
    private TransactionService transactionService;
    private AnalyticsService analyticsService;
    private User currentUser;

    public ConsoleUI() {
        this.input = new Scanner(System.in);
        this.userService = new UserService();
        this.transactionService = new TransactionService();
        this.analyticsService = new AnalyticsService(transactionService);
        this.currentUser = null;
    }

    public void start() {
        System.out.println("=== Student Money Manager ===");

        while (true) {
            if (currentUser == null) {
                showAuthMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private void showAuthMenu() {
        System.out.println("\n--- Welcome ---");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");

        String choice = input.nextLine();

        switch (choice) {
            case "1":
                registerUser();
                break;
            case "2":
                loginUser();
                break;
            case "3":
                System.out.println("Thank you for using Student Money Manager!");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid option! Please try again.");
        }
    }

    private void showMainMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("Welcome, " + currentUser.getUsername() + "!");
        System.out.println("1. Add Transaction");
        System.out.println("2. View Transactions");
        System.out.println("3. View Balance");
        System.out.println("4. View Analytics");
        System.out.println("5. Set Budget Limit");
        System.out.println("6. Profile Management");
        System.out.println("7. Logout");
        System.out.print("Choose an option: ");

        String choice = input.nextLine();

        switch (choice) {
            case "1":
                addTransaction();
                break;
            case "2":
                viewTransactions();
                break;
            case "3":
                viewBalance();
                break;
            case "4":
                viewAnalytics();
                break;
            case "5":
                setBudgetLimit();
                break;
            case "6":
                showProfileMenu();
                break;
            case "7":
                logout();
                break;
            default:
                System.out.println("Invalid option! Please try again.");
        }
    }

    private void registerUser() {
        System.out.println("\n--- User Registration ---");

        System.out.print("Username: ");
        String username = input.nextLine();

        System.out.print("Email: ");
        String email = input.nextLine();

        System.out.print("Password: ");
        String password = input.nextLine();

        try {
            User newUser = userService.registerUser(username, email, password);
            System.out.println("Registration successful! You can now login.");
        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private void loginUser() {
        System.out.println("\n--- User Login ---");

        System.out.print("Username: ");
        String username = input.nextLine();

        System.out.print("Password: ");
        String password = input.nextLine();

        try {
            User user = userService.loginUser(username, password);
            currentUser = user;
            System.out.println("Login successful! Welcome back, " + user.getUsername() + "!");
        } catch (Exception e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private void addTransaction() {
        System.out.println("\n--- Add Transaction ---");

        System.out.println("Transaction Type:");
        System.out.println("1. Income");
        System.out.println("2. Expense");
        System.out.print("Choose type: ");
        String typeChoice = input.nextLine();

        Transaction.TransactionType type;
        if (typeChoice.equals("1")) {
            type = Transaction.TransactionType.INCOME;
        } else if (typeChoice.equals("2")) {
            type = Transaction.TransactionType.EXPENSE;
        } else {
            System.out.println("Invalid choice!");
            return;
        }

        System.out.print("Amount: ");
        double amount;
        try {
            amount = Double.parseDouble(input.nextLine());
            if (amount <= 0) {
                System.out.println("Amount must be positive!");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format!");
            return;
        }

        System.out.println("Categories:");
        Transaction.Category[] categories = Transaction.Category.values();
        for (int i = 0; i < categories.length; i++) {
            System.out.println((i + 1) + ". " + categories[i]);
        }
        System.out.print("Choose category: ");

        int categoryIndex;
        try {
            categoryIndex = Integer.parseInt(input.nextLine()) - 1;
            if (categoryIndex < 0 || categoryIndex >= categories.length) {
                System.out.println("Invalid category choice!");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid category format!");
            return;
        }

        Transaction.Category category = categories[categoryIndex];

        System.out.print("Description: ");
        String description = input.nextLine();

        try {
            Transaction transaction = transactionService.addTransaction(
                    currentUser.getUserId(), amount, type, category, description, ""
            );
            System.out.println("Transaction added successfully!");
            System.out.println(transaction);
        } catch (Exception e) {
            System.out.println("Failed to add transaction: " + e.getMessage());
        }
    }

    private void viewTransactions() {
        System.out.println("\n--- Your Transactions ---");

        var transactions = transactionService.getUserTransactions(currentUser.getUserId());

        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        System.out.printf("%-5s %-10s %-15s %-10s %-20s%n",
                "No.", "Type", "Category", "Amount", "Description");
        System.out.println("------------------------------------------------------------");

        double totalIncome = 0;
        double totalExpenses = 0;

        for (int i = 0; i < transactions.size(); i++) {
            Transaction t = transactions.get(i);
            String type = t.isIncome() ? "INCOME" : "EXPENSE";
            String amountStr = String.format("Rs%.2f", t.getAmount());

            if (t.isIncome()) totalIncome += t.getAmount();
            else totalExpenses += t.getAmount();

            System.out.printf("%-5d %-10s %-15s %-10s %-20s%n",
                    (i + 1), type, t.getCategory(), amountStr, t.getDescription());
        }

        System.out.println("------------------------------------------------------------");
        System.out.printf("Total Income: Rs%.2f | Total Expenses: Rs%.2f | Balance: Rs%.2f%n",
                totalIncome, totalExpenses, (totalIncome - totalExpenses));
    }

    private void viewBalance() {
        System.out.println("\n--- Account Balance ---");

        double balance = transactionService.calculateBalance(currentUser.getUserId());
        double totalIncome = transactionService.calculateTotalIncome(currentUser.getUserId());
        double totalExpenses = transactionService.calculateTotalExpenses(currentUser.getUserId());

        System.out.printf("Current Balance: Rs%.2f%n", balance);
        System.out.printf("Total Income: Rs%.2f%n", totalIncome);
        System.out.printf("Total Expenses: Rs%.2f%n", totalExpenses);

        if (currentUser.getBudgetLimit() > 0) {
            double budgetUsage = (totalExpenses / currentUser.getBudgetLimit()) * 100;
            System.out.printf("Monthly Budget: Rs%.2f (%.1f%% used)%n",
                    currentUser.getBudgetLimit(), budgetUsage);
        }
    }

    private void viewAnalytics() {
        System.out.println("\n--- Financial Analytics ---");

        var spendingByCategory = transactionService.getSpendingByCategory(currentUser.getUserId());

        if (spendingByCategory.isEmpty()) {
            System.out.println("No expense data available for analytics.");
            return;
        }

        System.out.println("Spending by Category:");
        System.out.printf("%-20s %-10s%n", "Category", "Amount");
        System.out.println("--------------------------------");

        spendingByCategory.forEach((category, amount) -> {
            System.out.printf("Rs-20s Rs%-10.2f%n", category, amount);
        });

        System.out.println("\nFinancial Health: " + analyticsService.getFinancialHealth(currentUser.getUserId()));
    }

    private void setBudgetLimit() {
        System.out.println("\n--- Set Budget Limit ---");
        System.out.printf("Current budget limit: Rs%.2f%n", currentUser.getBudgetLimit());

        System.out.print("Enter new budget limit (0 to remove limit): ");
        try {
            double newLimit = Double.parseDouble(input.nextLine());
            if (newLimit < 0) {
                System.out.println("Budget limit cannot be negative!");
                return;
            }

            userService.updateUserBudget(currentUser.getUserId(), newLimit);
            System.out.println("Budget limit updated successfully!");
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format!");
        } catch (Exception e) {
            System.out.println("Failed to update budget: " + e.getMessage());
        }
    }

    private void showProfileMenu() {
        System.out.println("\n--- Profile Management ---");
        System.out.println("1. View Your Profile");
        System.out.println("2. Update Your Profile");
        System.out.println("3. Change Password");
        System.out.println("4. Jump Back to Main Menu");
        System.out.print("Choose an option: ");

        String choice = input.nextLine();

        switch (choice) {
            case "1":
                viewProfile();
                break;
            case "2":
                updateProfile();
                break;
            case "3":
                changePassword();
                break;
            case "4":
                return;
            default:
                System.out.println("Invalid option!");
        }
    }

    private void viewProfile() {
        System.out.println("\n--- Your Profile ---");
        System.out.println("Username: " + currentUser.getUsername());
        System.out.println("Email: " + currentUser.getEmail());
        System.out.printf("Budget Limit: Rs%.2f%n", currentUser.getBudgetLimit());
        System.out.println("Account Created: " + currentUser.getCreatedAt());
        System.out.println("Status: " + (currentUser.isActive() ? "Active" : "Inactive"));
    }

    private void updateProfile() {
        System.out.println("\n--- Update Profile ---");

        System.out.print("New Username (leave empty to keep current): ");
        String newUsername = input.nextLine();

        System.out.print("New Email (leave empty to keep current): ");
        String newEmail = input.nextLine();

        try {
            userService.updateUserProfile(currentUser.getUserId(), newEmail, newUsername);
            currentUser = userService.getUserById(currentUser.getUserId());
            System.out.println("Profile updated successfully!");
        } catch (Exception e) {
            System.out.println("Failed to update profile: " + e.getMessage());
        }
    }

    private void changePassword() {
        System.out.println("\n--- Change Your Password ---");

        System.out.print("Your Current Password: ");
        String currentPassword = input.nextLine();

        System.out.print("Your New Password: ");
        String newPassword = input.nextLine();

        System.out.print("Confirm your New Password: ");
        String confirmPassword = input.nextLine();

        if (!newPassword.equals(confirmPassword)) {
            System.out.println("New passwords do not match!");
            return;
        }

        try {
            userService.changePassword(currentUser.getUserId(), currentPassword, newPassword);
            System.out.println("Password changed successfully!");
        } catch (Exception e) {
            System.out.println("Failed to change password: " + e.getMessage());
        }
    }

    private void logout() {
        System.out.println("Logging out... Goodbye,Have a nice day ahead " + currentUser.getUsername() + "!");
        currentUser = null;
    }
}