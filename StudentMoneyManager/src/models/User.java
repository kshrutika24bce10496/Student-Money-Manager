package models;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {
    private String userId;
    private String username;
    private String email;
    private String passwordHash;
    private String salt;
    private LocalDateTime createdAt;
    private double budgetLimit;
    private boolean isActive;

    public User(String username, String email, String password) {
        this.userId = UUID.randomUUID().toString();
        this.username = username;
        this.email = email;
        this.salt = generateSalt();
        this.passwordHash = hashPassword(password, this.salt);
        this.createdAt = LocalDateTime.now();
        this.budgetLimit = 0.0;
        this.isActive = true;
    }

    // Constructor for loading from storage
    public User(String userId, String username, String email, String passwordHash,
                String salt, LocalDateTime createdAt, double budgetLimit, boolean isActive) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.createdAt = createdAt;
        this.budgetLimit = budgetLimit;
        this.isActive = isActive;
    }

    private String generateSalt() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private String hashPassword(String password, String salt) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            String combined = password + salt;
            byte[] hash = md.digest(combined.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public boolean verifyPassword(String password) {
        return this.passwordHash.equals(hashPassword(password, this.salt));
    }

    // Getters
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getSalt() { return salt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public double getBudgetLimit() { return budgetLimit; }
    public boolean isActive() { return isActive; }

    // Setters
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setBudgetLimit(double budgetLimit) {
        if (budgetLimit < 0) {
            throw new IllegalArgumentException("Budget limit cannot be negative");
        }
        this.budgetLimit = budgetLimit;
    }
    public void setActive(boolean active) { isActive = active; }

    @Override
    public String toString() {
        return String.format("User{username='%s', email='%s', budgetLimit=%.2f, active=%s}",
                username, email, budgetLimit, isActive);
    }
}