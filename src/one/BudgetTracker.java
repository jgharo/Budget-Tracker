package one;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import two.BudgetReport;
import three.BudgetEntries;

public class BudgetTracker {

    //Fields
    private JPanel panelMain;
    private JPanel income;
    private JLabel monInc;
    private JLabel otherInc;
    private JLabel salary;
    private JPanel expense;
    private JLabel monExp;
    private JLabel groceries;
    private JLabel rent;
    private JLabel utilities;
    private JLabel misc;
    private JPanel inputButton;
    private JButton saveBtn;
    private JButton reportBtn;
    private JFormattedTextField frmtxtSalary;
    private JFormattedTextField frmtxtOtherInc;
    private JFormattedTextField frmtxtFood;
    private JFormattedTextField frmtxtUtilities;
    private JFormattedTextField frmtxtMisc;
    private JFormattedTextField frmtxtRent;
    private JTextField txtMon;
    private JPanel monthly;
    private JPanel reportButton;
    private JButton monBtn;
    private JButton deleteBtn;
    private JButton updateBtn;


    public static void main(String[] args) {
        JFrame frame = new JFrame("Budget Tracker");
        frame.setContentPane(new BudgetTracker().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setVisible(true);

        BudgetTracker con = new BudgetTracker();
        con.connect();

        createNewTable();

        BudgetTracker nt = new BudgetTracker();
        nt.createTotalNet();

        BudgetTracker ins = new BudgetTracker();
        ins.insert();
    }

    public BudgetTracker() {
        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insert();

                // clears the field when data has been saved
                txtMon.setText("");
                frmtxtSalary.setText("");
                frmtxtOtherInc.setText("");
                frmtxtFood.setText("");
                frmtxtRent.setText("");
                frmtxtUtilities.setText("");
                frmtxtMisc.setText("");
            }
        });
        updateBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                update();

                // clears the field when data has been updated
                txtMon.setText("");
                frmtxtSalary.setText("");
                frmtxtOtherInc.setText("");
                frmtxtFood.setText("");
                frmtxtRent.setText("");
                frmtxtUtilities.setText("");
                frmtxtMisc.setText("");
            }
        });
        deleteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                delete();

                // clears the field when the data
                txtMon.setText("");
                frmtxtSalary.setText("");
                frmtxtOtherInc.setText("");
                frmtxtFood.setText("");
                frmtxtRent.setText("");
                frmtxtUtilities.setText("");
                frmtxtMisc.setText("");
            }
        });
        monBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                BudgetEntries mon = new BudgetEntries();
                mon.monReport();
            }
        });
        reportBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                BudgetReport rt = new BudgetReport();
                rt.Report();
            }
        });
    }

    // connecting to database
    private Connection connect() {
        // SQLite connection string
        try {
            Class.forName("org.sqlite.SQLiteJDBCLoader");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String url = "jdbc:sqlite:budget_tracker.identifier.sqlite";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void createNewTable() {
        // SQLite connection string
        String url = "jdbc:sqlite:budget_tracker.identifier.sqlite";

        // SQL statement for creating a new table
        String sql = "CREATE TABLE monthly_budget (\n" +
                "monYr UNKNOWN PRIMARY KEY," +
                "salary REAL NOT NULL," +
                "otherIncome REAL NOT NULL," +
                "groceries REAL NOT NULL," +
                "rent REAL NOT NULL, " +
                "utilities REAL NOT NULL, " +
                "misc REAL NOT NULL" +
                ");";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void createTotalNet() {
        String url = "jdbc:sqlite:budget_tracker.identifier.sqlite";

        String sql = "CREATE VIEW \"totalNet\" AS\n" +
                "    SELECT monYr, salary AS 'S', otherIncome AS 'OI', groceries AS 'G', rent AS 'R', utilities AS 'U', misc AS 'M',\n" +
                "    salary + otherIncome,\n" +
                "    groceries + rent + utilities + misc,\n" +
                "    (salary + otherIncome) - (groceries + rent + utilities + misc),\n" +
                "    CASE WHEN ((salary + otherIncome) - (groceries + rent + utilities + misc) > 0)\n" +
                "        THEN 'Gain' ELSE 'Loss' END AS 'Gain/Loss'\n" +
                "    FROM monthly_budget;";


        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insert() {

        String url = "jdbc:sqlite:budget_tracker.identifier.sqlite";
        String sql = "INSERT INTO monthly_budget (monYr, salary, otherIncome, groceries, rent, utilities, misc) VALUES (?,?,?,?,?,?,?)";
        String month = txtMon.getText();

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if ("".equals(month)) {  //User have not entered anything.
                JOptionPane.showMessageDialog(null, "Welcome to Budget Tracker! \n" +
                        "Please enter your information.");
                txtMon.requestFocusInWindow();
            } else {
                pstmt.setString(1, txtMon.getText());
                pstmt.setString(2, frmtxtSalary.getText());
                pstmt.setString(3, frmtxtOtherInc.getText());
                pstmt.setString(4, frmtxtFood.getText());
                pstmt.setString(5, frmtxtRent.getText());
                pstmt.setString(6, frmtxtUtilities.getText());
                pstmt.setString(7, frmtxtMisc.getText());
            }


            pstmt.execute();
            JOptionPane.showMessageDialog(null, "Saved");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void update() {

        String url = "jdbc:sqlite:budget_tracker.identifier.sqlite";
        String sql = "UPDATE monthly_budget SET salary = '" + frmtxtSalary.getText() + "'" +
                ",otherIncome ='" + frmtxtOtherInc.getText() + "'" +
                ",groceries ='" + frmtxtFood.getText() + "'" +
                ",rent = '" + frmtxtRent.getText() + "'" +
                ",utilities = '" + frmtxtUtilities.getText() + "'" +
                ",misc = '" + frmtxtUtilities.getText() + "'" +
                "WHERE monYr = '" + txtMon.getText() + "'";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement psmt = conn.prepareStatement(sql)) {

            psmt.execute();
            JOptionPane.showMessageDialog(null, "Updated");


        } catch (Exception err) {
            JOptionPane.showMessageDialog(null, err.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }


    }

    public void delete() {

        String url = "jdbc:sqlite:budget_tracker.identifier.sqlite";
        String sql = "DELETE FROM monthly_budget WHERE monYr =?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement psmt = conn.prepareStatement(sql)) {
            psmt.setString(1, txtMon.getText());

            psmt.execute();
            JOptionPane.showMessageDialog(null, "Deleted");


        } catch (Exception err) {
            JOptionPane.showMessageDialog(null, err.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panelMain = new JPanel();
        panelMain.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, -1));
        monthly = new JPanel();
        monthly.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panelMain.add(monthly, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Month/Year: (MM/YYYY)");
        monthly.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        txtMon = new JTextField();
        txtMon.setColumns(1);
        txtMon.setText("");
        monthly.add(txtMon, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 1, false));
        income = new JPanel();
        income.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        panelMain.add(income, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        monInc = new JLabel();
        monInc.setText("Monthly Income:");
        income.add(monInc, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        salary = new JLabel();
        salary.setText("Salary:");
        income.add(salary, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        otherInc = new JLabel();
        otherInc.setText("Other Income:");
        income.add(otherInc, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        frmtxtSalary = new JFormattedTextField();
        income.add(frmtxtSalary, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 1, false));
        frmtxtOtherInc = new JFormattedTextField();
        income.add(frmtxtOtherInc, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 1, false));
        expense = new JPanel();
        expense.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(5, 4, new Insets(0, 0, 0, 0), -1, -1));
        panelMain.add(expense, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        monExp = new JLabel();
        monExp.setText("Monthly Expense:");
        expense.add(monExp, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        groceries = new JLabel();
        groceries.setText("Groceries: ");
        expense.add(groceries, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        frmtxtFood = new JFormattedTextField();
        expense.add(frmtxtFood, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 1, false));
        frmtxtUtilities = new JFormattedTextField();
        expense.add(frmtxtUtilities, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 1, false));
        utilities = new JLabel();
        utilities.setText("Utilities: ");
        expense.add(utilities, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        frmtxtMisc = new JFormattedTextField();
        expense.add(frmtxtMisc, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 1, false));
        misc = new JLabel();
        misc.setText("Miscellaneous: ");
        expense.add(misc, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        rent = new JLabel();
        rent.setText("Rent/Mortgage:");
        expense.add(rent, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        frmtxtRent = new JFormattedTextField();
        expense.add(frmtxtRent, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 1, false));
        inputButton = new JPanel();
        inputButton.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panelMain.add(inputButton, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        deleteBtn = new JButton();
        deleteBtn.setText("Delete");
        inputButton.add(deleteBtn, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saveBtn = new JButton();
        saveBtn.setText("Save");
        inputButton.add(saveBtn, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        updateBtn = new JButton();
        updateBtn.setText("Update");
        inputButton.add(updateBtn, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        reportButton = new JPanel();
        reportButton.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panelMain.add(reportButton, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        reportBtn = new JButton();
        reportBtn.setText("Monthly Report");
        reportButton.add(reportBtn, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        monBtn = new JButton();
        monBtn.setText("Monthly Budget");
        reportButton.add(monBtn, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panelMain;
    }
}
