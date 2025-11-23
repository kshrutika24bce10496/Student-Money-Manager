package services;

import models.User;
import utils.ValidationUtil;
import java.util.*;

public class UserService {
    private final Map<String, User> users;
    private final Map<String, String> usernameToUserId;
    private final Map<String, String> emailToUserId;

    public UserService() {
        this.users = new HashMap<>();
        this.usernameToUserId = new HashMap<>();
        this.emailToUserId = new HashMap<>();
    }

    public User registerUser(String username, String email, String password) {
        if (!ValidationUtil.isValidUsername(username)) {
            throw new IllegalArgumentException("Invalid username format. Use 3-20 letters, numbers, or underscores.");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        if (!ValidationUtil.isValidPassword(password)) {
            throw new IllegalArgumentException("Password must be at least 6 characters with both letters and numbers.");
        }

        if (usernameToUserId.containsKey(username.toLowerCase())) {
            throw new IllegalArgumentException("Username already exists.");
        }
        if (emailToUserId.containsKey(email.toLowerCase())) {
            throw new IllegalArgumentException("Email already registered.");
        }

        User newUser = new User(username, email, password);
        users.put(newUser.getUserId(), newUser);
        usernameToUserId.put(username.toLowerCase(), newUser.getUserId());
        emailToUserId.put(email.toLowerCase(), newUser.getUserId());

        System.out.println("User registered successfully: " + username);
        return newUser;
    }

    public User loginUser(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        String userId = usernameToUserId.get(username.toLowerCase());
        if (userId == null) {
            throw new IllegalArgumentException("User not found.");
        }

        User user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }

        if (!user.isActive()) {
            throw new IllegalArgumentException("Account is deactivated.");
        }

        if (!user.verifyPassword(password)) {
            throw new IllegalArgumentException("Invalid password.");
        }

        System.out.println("Login successful: " + user.getUsername());
        return user;
    }

    public void updateUserBudget(String userId, double budgetLimit) {
        User user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }

        if (budgetLimit < 0) {
            throw new IllegalArgumentException("Budget limit cannot be negative.");
        }

        user.setBudgetLimit(budgetLimit);
        System.out.println("Budget limit updated to: $" + budgetLimit);
    }

    public User getUserById(String userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }
        return user;
    }

    public User updateUserProfile(String userId, String newEmail, String newUsername) {
        User user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }

        boolean changesMade = false;

        if (newEmail != null && !newEmail.trim().isEmpty()) {
            if (!ValidationUtil.isValidEmail(newEmail)) {
                throw new IllegalArgumentException("Invalid email format.");
            }
            if (!newEmail.equalsIgnoreCase(user.getEmail()) && emailToUserId.containsKey(newEmail.toLowerCase())) {
                throw new IllegalArgumentException("Email already in use by another account.");
            }

            emailToUserId.remove(user.getEmail().toLowerCase());
            user.setEmail(newEmail);
            emailToUserId.put(newEmail.toLowerCase(), userId);
            changesMade = true;
            System.out.println("Email updated successfully.");
        }

        if (newUsername != null && !newUsername.trim().isEmpty()) {
            if (!ValidationUtil.isValidUsername(newUsername)) {
                throw new IllegalArgumentException("Invalid username format.");
            }
            if (!newUsername.equalsIgnoreCase(user.getUsername()) && usernameToUserId.containsKey(newUsername.toLowerCase())) {
                throw new IllegalArgumentException("Username already in use.");
            }

            usernameToUserId.remove(user.getUsername().toLowerCase());
            user.setUsername(newUsername);
            usernameToUserId.put(newUsername.toLowerCase(), userId);
            changesMade = true;
            System.out.println("Username updated successfully.");
        }

        if (!changesMade) {
            System.out.println("No changes made to profile.");
        }

        return user;
    }

    public void changePassword(String userId, String currentPassword, String newPassword) {
        User user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }

        if (!user.verifyPassword(currentPassword)) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        if (!ValidationUtil.isValidPassword(newPassword)) {
            throw new IllegalArgumentException("New password must be at least 6 characters with both letters and numbers.");
        }

        if (currentPassword.equals(newPassword)) {
            throw new IllegalArgumentException("New password must be different from current password.");
        }

        System.out.println("Password changed successfully.");
    }

    public boolean usernameExists(String username) {
        return usernameToUserId.containsKey(username.toLowerCase());
    }

    public boolean emailExists(String email) {
        return emailToUserId.containsKey(email.toLowerCase());
    }
}