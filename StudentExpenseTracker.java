import java.io.*;
import java.util.*;

/**
 * Student Expense Tracker
 * Simple Console Application using Java + File Handling
 */
public class StudentExpenseTracker {

    // File where data will be stored
    private static final String FILE_NAME = "expenses.txt";

    // In–memory list of expenses
    private static List<Expense> records = new ArrayList<>();

    // Expense model class
    static class Expense {
        String sName;
        String category;
        double amount;
        String date;   // simple string: e.g., 2025-12-04
        String note;

        Expense(String studentName, String category, double amount, String date, String note) {
            this.sName = studentName;
            this.category = category;
            this.amount = amount;
            this.date = date;
            this.note = note;
        }

        // Convert Expense to a line for file: CSV style
        String toFileString() {
            // Replace any commas in note to avoid breaking CSV
            String safeNote = note.replace(",", ";");
            return sName + "," + category + "," + amount + "," + date + "," + safeNote;
        }

        // Parse from file line
        static Expense fromFileString(String line) {
            String[] parts = line.split(",", 5); // allow note to have commas replaced
            if (parts.length < 5) return null;
            String studentName = parts[0];
            String category = parts[1];
            double amount;
            try {
                amount = Double.parseDouble(parts[2]);
            } catch (NumberFormatException e) {
                amount = 0.0;
            }
            String date = parts[3];
            String note = parts[4];
            return new Expense(studentName, category, amount, date, note);
        }

        @Override
        public String toString() {
            return "Student: " + sName +
                    " | Category: " + category +
                    " | Amount: " + amount +
                    " | Date: " + date +
                    " | Note: " + note;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Load existing data from file (if any)
        loadExpensesFromFile();

        int choice;
        do {
            System.out.println("\n===== STUDENT EXPENSE TRACKER =====");
            System.out.println("1. Add Expense");
            System.out.println("2. View All Expenses");
            System.out.println("3. View Expenses by Student");
            System.out.println("4. Show Total Expense per Student");
            System.out.println("5. Save & Exit");
            System.out.print("Enter your choice: ");

            while (!sc.hasNextInt()) {
                System.out.print("Please enter a valid number: ");
                sc.next();
            }
            choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    addExpense(sc);
                    break;
                case 2:
                    viewAllExpenses();
                    break;
                case 3:
                    viewExpensesByStudent(sc);
                    break;
                case 4:
                    showTotalExpensePerStudent();
                    break;
                case 5:
                    saveExpensesToFile();
                    System.out.println("Data saved to file. Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }

        } while (choice != 5);

        sc.close();
    }

    // 1. Add Expense
    private static void addExpense(Scanner sc) {
        System.out.print("Enter student name: ");
        String name = sc.nextLine().trim();

        System.out.print("Enter category (Food, Travel, Books, etc.): ");
        String category = sc.nextLine().trim();

        System.out.print("Enter amount: ");
        double amount;
        while (true) {
            try {
                amount = Double.parseDouble(sc.nextLine());
                break;
            } catch (NumberFormatException e) {
                System.out.print("Invalid amount. Enter again: ");
            }
        }

        System.out.print("Enter date (e.g., 2025-12-04): ");
        String date = sc.nextLine().trim();

        System.out.print("Enter note (optional): ");
        String note = sc.nextLine().trim();

        Expense exp = new Expense(name, category, amount, date, note);
        records.add(exp);
        System.out.println("Expense added successfully!");
    }

    // 2. View all expenses
    private static void viewAllExpenses() {
        if (records.isEmpty()) {
            System.out.println("No expenses found.");
            return;
        }
        System.out.println("\n--- All Expenses ---");
        for (Expense e : records) {
            System.out.println(e);
        }
    }

    // 3. View expenses by student
    private static void viewExpensesByStudent(Scanner sc) {
        System.out.print("Enter student name to search: ");
        String name = sc.nextLine().trim();

        boolean found = false;
        double total = 0.0;
        System.out.println("\n--- Expenses for " + name + " ---");
        for (Expense e : records) {
            if (e.sName.equalsIgnoreCase(name)) {
                System.out.println(e);
                found = true;
                total += e.amount;
            }
        }

        if (!found) {
            System.out.println("No expenses found for this student.");
        } else {
            System.out.println("Total Expense for " + name + " = " + total);
        }
    }

    // 4. Show total expense per student (summary)
    private static void showTotalExpensePerStudent() {
        if (records.isEmpty()) {
            System.out.println("No expenses found.");
            return;
        }

        // Map: studentName -> totalAmount
        Map<String, Double> totals = new HashMap<>();
        for (Expense e : records) {
            totals.put(e.sName,
                    totals.getOrDefault(e.sName, 0.0) + e.amount);
        }

        System.out.println("\n--- Total Expense per Student ---");
        for (Map.Entry<String, Double> entry : totals.entrySet()) {
            System.out.println("Student: " + entry.getKey() + " | Total: " + entry.getValue());
        }
    }

    // Load data from file into list
    private static void loadExpensesFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            // No data yet, that's fine
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                Expense e = Expense.fromFileString(line);
                if (e != null) {
                    records.add(e);
                    count++;
                }
            }
            System.out.println("Loaded " + count + " expense(s) from file.");
        } catch (IOException e) {
            System.out.println("Error reading from file: " + e.getMessage());
        }
    }

    // Save all expenses from list into file
    private static void saveExpensesToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Expense e : records) {
                pw.println(e.toFileString());
            }
            // pw.flush(); // not required, close() will flush
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }
}
