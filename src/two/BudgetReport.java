package two;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class BudgetReport {

    private JPanel summary;
    private JPanel report;
    private JTable tableRpt;
    private JFrame frameRpt;
    private Connection con = null;
    private Statement st = null;
    private PreparedStatement pst = null;
    private ResultSet rs = null;
    private String monYr;
    private String[] columnNames = {"Month-Year", "Monthly Income", "Monthly Expense", "Net Gain/Loss"};

    public void Report() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {


                try {
                    Class.forName("org.sqlite.SQLiteJDBCLoader");
                    con = DriverManager.getConnection("jdbc:sqlite:budget_tracker.identifier.sqlite");
                    st = con.createStatement();
                    rs = st.executeQuery("SELECT * FROM totalNet");
                    Vector v = new Vector();
                    while (rs.next()) {
                        monYr = rs.getString(1);
                        v.add(monYr);
                    }
                    st.close();
                    rs.close();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }

                showTableData();
            }
        });
    }

    public void showTableData() {

        frameRpt = new JFrame("Monthly Report");
        frameRpt.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameRpt.setLayout(new BorderLayout());
        frameRpt.setLocationRelativeTo(null);

        //TableModel tm = new TableModel();
        //DefaultTableModel model = new DefaultTableModel(tm.getData1(), tm.getColumnNames());
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(columnNames);


        //table = new JTable(model);
        tableRpt = new JTable();
        tableRpt.setModel(model);
        tableRpt.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tableRpt.setFillsViewportHeight(true);
        JScrollPane scroll = new JScrollPane(tableRpt);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        //String textvalue = textbox.getText();
        String monYR = "";
        String monInc = "";
        String monExp = "";
        String net = "";

        try {
            pst = con.prepareStatement("SELECT * FROM totalNet WHERE monYr");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                monYR = rs.getString("monYr");
                monInc = rs.getString("salary + otherIncome");
                monExp = rs.getString("groceries + rent + utilities + misc");
                net = rs.getString("(salary + otherIncome) - (groceries + rent + utilities + misc)");
                model.addRow(new Object[]{monYR, monInc, monExp, net});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        frameRpt.add(scroll);
        frameRpt.setVisible(true);
        frameRpt.setSize(400, 200);
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
        report = new JPanel();
        report.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        summary = new JPanel();
        summary.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        report.add(summary, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return report;
    }
}


