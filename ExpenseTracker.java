import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

public class ExpenseTracker extends JFrame {
    private java.util.List<Transaction> transactions = new ArrayList<>();
    private JTable table;
    private DefaultTableModel model;

    public ExpenseTracker() {
        setTitle("Expense Tracker");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{"Date", "Category", "Amount", "Type"}, 0);
        table = new JTable(model);

        // ✅ Row coloring logic
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String type = table.getValueAt(row, 3).toString(); // "Type" column

                if ("Income".equalsIgnoreCase(type)) {
                    c.setBackground(new Color(220, 255, 220)); // light green
                } else if ("Expense".equalsIgnoreCase(type)) {
                    c.setBackground(new Color(255, 230, 230)); // light red
                } else {
                    c.setBackground(Color.white); // default
                }

                if (isSelected) {
                    c.setBackground(new Color(180, 200, 255)); // highlight selection
                }

                return c;
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(2, 5));
        JTextField categoryField = new JTextField();
        JTextField amountField = new JTextField();
        JTextField dateField = new JTextField("YYYY-MM-DD");
        JCheckBox incomeCheck = new JCheckBox("Income");
        JButton addButton = new JButton("Add");

        inputPanel.add(new JLabel("Category"));
        inputPanel.add(new JLabel("Amount"));
        inputPanel.add(new JLabel("Date"));
        inputPanel.add(new JLabel("Type"));
        inputPanel.add(new JLabel(""));
        inputPanel.add(categoryField);
        inputPanel.add(amountField);
        inputPanel.add(dateField);
        inputPanel.add(incomeCheck);
        inputPanel.add(addButton);
        add(inputPanel, BorderLayout.NORTH);

        JButton chartButton = new JButton("Show Pie Chart");
        JButton exportButton = new JButton("Export CSV");
        JButton summaryButton = new JButton("Monthly Summary");
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(chartButton);
        bottomPanel.add(exportButton);
        bottomPanel.add(summaryButton);
        add(bottomPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> {
            try {
                String category = categoryField.getText();
                double amount = Double.parseDouble(amountField.getText());
                LocalDate date = LocalDate.parse(dateField.getText());
                boolean isIncome = incomeCheck.isSelected();
                Transaction t = new Transaction(category, amount, date, isIncome);
                transactions.add(t);
                model.addRow(new Object[]{date, category, amount, isIncome ? "Income" : "Expense"});
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input");
            }
        });

        chartButton.addActionListener(e -> showPieChart());
        exportButton.addActionListener(e -> CSVExporter.export(transactions));
        summaryButton.addActionListener(e -> showMonthlySummary());

        setVisible(true);
    }

    private void showPieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        Map<String, Double> categoryTotals = new HashMap<>();
        for (Transaction t : transactions) {
            if (!t.isIncome()) {
                categoryTotals.put(t.getCategory(),
                    categoryTotals.getOrDefault(t.getCategory(), 0.0) + t.getAmount());
            }
        }
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart chart = ChartFactory.createPieChart("Expense Distribution", dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
        ChartPanel panel = new ChartPanel(chart);
        JFrame chartFrame = new JFrame("Pie Chart");
        chartFrame.setSize(500, 400);
        chartFrame.add(panel);
        chartFrame.setVisible(true);
    }

    private void showMonthlySummary() {
        String monthStr = JOptionPane.showInputDialog(this, "Enter month (YYYY-MM):");
        if (monthStr == null || !monthStr.matches("\\d{4}-\\d{2}")) return;

        double income = 0, expense = 0;
        for (Transaction t : transactions) {
            String tMonth = t.getDate().toString().substring(0, 7);
            if (tMonth.equals(monthStr)) {
                if (t.isIncome()) income += t.getAmount();
                else expense += t.getAmount();
            }
        }
        JOptionPane.showMessageDialog(this,
            "Summary for " + monthStr + "\nIncome: ₹" + income + "\nExpense: ₹" + expense + "\nBalance: ₹" + (income - expense));
    }

    public static void main(String[] args) {
        new ExpenseTracker();
    }
}