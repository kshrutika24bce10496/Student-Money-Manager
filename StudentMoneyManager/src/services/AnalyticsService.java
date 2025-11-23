package services;

public class AnalyticsService {
    private final TransactionService transactionService;

    public AnalyticsService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public String getFinancialHealth(String userId) {
        double balance = transactionService.calculateBalance(userId);
        if (balance > 1000) return "Excellent";
        if (balance > 0) return "Good";
        if (balance > -500) return "Needs Attention";
        return "Critical";
    }
}