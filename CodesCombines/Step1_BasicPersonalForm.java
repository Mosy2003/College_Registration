package CodesCombines;

import javax.swing.*;
import java.awt.*;

public class Step1_BasicPersonalForm extends JFrame {
    private final JTextField nameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField studentIdField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JButton nextBtn = new JButton("Next");

    public Step1_BasicPersonalForm() {
        super("Step 1 — Personal Info (Basic)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(540, 260);
        setLocationRelativeTo(null);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        int row = 0;

        addRow(form, c, row++, "Name", nameField);
        addRow(form, c, row++, "Email", emailField);
        addRow(form, c, row++, "Student ID", studentIdField);
        addRow(form, c, row++, "Phone", phoneField);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(nextBtn);

        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        nextBtn.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Captured (no validation yet):\n" +
                "Name=" + nameField.getText() + "\n" +
                "Email=" + emailField.getText() + "\n" +
                "Student ID=" + studentIdField.getText() + "\n" +
                "Phone=" + phoneField.getText()));
    }

    private void addRow(JPanel p, GridBagConstraints c, int row, String label, JComponent field) {
        c.gridx = 0; c.gridy = row; c.weightx = 0; p.add(new JLabel(label), c);
        c.gridx = 1; c.weightx = 1; if (field instanceof JTextField tf) tf.setColumns(24); p.add(field, c);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Step1_BasicPersonalForm().setVisible(true));
    }
}
