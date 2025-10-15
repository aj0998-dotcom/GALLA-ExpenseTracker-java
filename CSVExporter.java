import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import javax.swing.JOptionPane;

public class CSVExporter {
    public static void export(List<Transaction> transactions) {
        try (FileWriter writer = new FileWriter("expenses.csv")) {
            writer.write("Date,Category,Amount,Type\n");
            for (Transaction t : transactions) {
                writer.write(t.getDate() + "," + t.getCategory() + "," + t.getAmount() + "," + (t.isIncome() ? "Income" : "Expense") + "\n");
            }
            JOptionPane.showMessageDialog(null, "Exported to expenses.csv");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Export failed");
        }
    }
}