import java.time.LocalDate;

public class Transaction {
    private String category;
    private double amount;
    private LocalDate date;
    private boolean isIncome;

    public Transaction(String category, double amount, LocalDate date, boolean isIncome) {
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.isIncome = isIncome;
    }

    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public LocalDate getDate() { return date; }
    public boolean isIncome() { return isIncome; }
}