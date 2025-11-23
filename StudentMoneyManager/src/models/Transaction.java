package models;

import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {
    private String transactionId;
    private String userId;
    private double amount;
    private TransactionType type;
    private Category category;
    private String description;
    private LocalDateTime date;
    private String location;

    public enum TransactionType {
        INCOME, EXPENSE
    }

    public enum Category {
        ALLOWANCE, PART_TIME_JOB, SCHOLARSHIP, GIFTS, OTHER_INCOME,
        FOOD, TRANSPORT, ENTERTAINMENT, STUDY_MATERIALS, RENT, UTILITIES, OTHER_EXPENSE
    }

    public Transaction(String userId, double amount, TransactionType type,
                       Category category, String description) {
        this.transactionId = UUID.randomUUID().toString();
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.description = description;
        this.date = LocalDateTime.now();
        this.location = "";
    }

    // Getters
    public String getTransactionId() { return transactionId; }
    public String getUserId() { return userId; }
    public double getAmount() { return amount; }
    public TransactionType getType() { return type; }
    public Category getCategory() { return category; }
    public String getDescription() { return description; }
    public LocalDateTime getDate() { return date; }
    public String getLocation() { return location; }

    // Setters
    public void setAmount(double amount) { this.amount = amount; }
    public void setCategory(Category category) { this.category = category; }
    public void setDescription(String description) { this.description = description; }
    public void setLocation(String location) { this.location = location; }

    public boolean isIncome() {
        return type == TransactionType.INCOME;
    }

    public boolean isExpense() {
        return type == TransactionType.EXPENSE;
    }

    public double getSignedAmount() {
        return isIncome() ? amount : -amount;
    }

    @Override
    public String toString() {
        return String.format("Transaction{type=%s, category=%s, amount=%.2f, description='%s'}",
                type, category, amount, description);
    }
}