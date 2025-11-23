package CourcesAndTimeTable;

import javax.swing.*;
import java.awt.*;

public class Step1_BasicCourseLevelUI extends JFrame {
    private final JTextField studentIdField = new JTextField();
    private final JComboBox<String> courseCombo = new JComboBox<>(new String[]{
            "-- Select a course --", "Engineering", "Law", "Computer Science", "Business"
    });
    private final JComboBox<String> levelCombo = new JComboBox<>(new String[]{
            "-- Select a level --", "Beginner (Level 5)", "Intermediate (Level 6)", "Advanced (Level 8)"
    });
    private final JButton assignBtn = new JButton("Assign");

    public Step1_BasicCourseLevelUI() {
        super("Step 1 — Basic UI");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(520, 220);
        setLocationRelativeTo(null);

        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL;
        int row = 0;

        addRow(p, c, row++, "Student ID", studentIdField);
        addRow(p, c, row++, "Course", courseCombo);
        addRow(p, c, row++, "Level", levelCombo);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(assignBtn);

        setLayout(new BorderLayout());
        add(p, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        assignBtn.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "SID=" + studentIdField.getText() + "\n"
              + "Course=" + courseCombo.getSelectedItem() + "\n"
              + "Level=" + levelCombo.getSelectedItem()));
    }

    private void addRow(JPanel p, GridBagConstraints c, int row, String label, JComponent field){
        c.gridx=0; c.gridy=row; c.weightx=0; p.add(new JLabel(label), c);
        c.gridx=1; c.weightx=1; if(field instanceof JTextField tf) tf.setColumns(18); p.add(field, c);
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new Step1_BasicCourseLevelUI().setVisible(true));
    }
}
