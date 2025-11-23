package services;

import models.Transaction;
import utils.ValidationUtil;
import java.util.*;
import java.util.stream.Collectors;

public class TransactionService {
    private final Map<String, List<Transaction>> userTransactions;

    public TransactionService() {
        this.userTransactions = new HashMap<>();
    }

    public Transaction addTransaction(String userId, double amount,
                                      Transaction.TransactionType type,
                                      Transaction.Category category,
                                      String description, String location) {
        if (!ValidationUtil.isValidAmount(amount)) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Transaction transaction = new Transaction(userId, amount, type, category, description);
        userTransactions.computeIfAbsent(userId, k -> new ArrayList<>()).add(transaction);
        return transaction;
    }

    public List<Transaction> getUserTransactions(String userId) {
        return userTransactions.getOrDefault(userId, new ArrayList<>());
    }

    public double calculateBalance(String userId) {
        List<Transaction> transactions = getUserTransactions(userId);
        return transactions.stream()
                .mapToDouble(Transaction::getSignedAmount)
                .sum();
    }

    public double calculateTotalIncome(String userId) {
        List<Transaction> transactions = getUserTransactions(userId);
        return transactions.stream()
                .filter(Transaction::isIncome)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double calculateTotalExpenses(String userId) {
        List<Transaction> transactions = getUserTransactions(userId);
        return transactions.stream()
                .filter(Transaction::isExpense)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public Map<Transaction.Category, Double> getSpendingByCategory(String userId) {
        List<Transaction> transactions = getUserTransactions(userId);
        return transactions.stream()
                .filter(Transaction::isExpense)
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)
                ));
    }
}