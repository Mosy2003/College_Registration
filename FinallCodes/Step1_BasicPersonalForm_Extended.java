package FinallCodes;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class Step1_BasicPersonalForm_Extended extends JFrame {
    private final JTextField nameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField studentIdField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JTextArea addressArea = new JTextArea(3, 24);
    private final JRadioButton male = new JRadioButton("Male");
    private final JRadioButton female = new JRadioButton("Female");
    private final JRadioButton other = new JRadioButton("Other");
    private final ButtonGroup genderGroup = new ButtonGroup();
    private final JComboBox<String> day = new JComboBox<>();
    private final JComboBox<String> month = new JComboBox<>();
    private final JComboBox<String> year = new JComboBox<>();
    private final JTextField townField = new JTextField();
    private final JButton nextBtn = new JButton("Next");

    public Step1_BasicPersonalForm_Extended() {
        super("Step 1 — Personal Info (Basic, with DOB/Address/Gender/Town)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(720, 520);
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

        c.gridx = 0; c.gridy = row; form.add(new JLabel("Address"), c);
        c.gridx = 1; addressArea.setLineWrap(true); addressArea.setWrapStyleWord(true);
        form.add(new JScrollPane(addressArea), c); row++;

        JPanel gp = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        genderGroup.add(male); gp.add(male);
        genderGroup.add(female); gp.add(female);
        genderGroup.add(other); gp.add(other);
        addRow(form, c, row++, "Gender", gp);

        initDOB();
        JPanel dobp = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        dobp.add(day); dobp.add(month); dobp.add(year);
        addRow(form, c, row++, "DOB", dobp);

        addRow(form, c, row++, "Town", townField);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(nextBtn);

        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        nextBtn.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Captured (no validation):\n" +
                "Name=" + nameField.getText() + "\n" +
                "Email=" + emailField.getText() + "\n" +
                "Student ID=" + studentIdField.getText() + "\n" +
                "Phone=" + phoneField.getText() + "\n" +
                "Address=" + addressArea.getText() + "\n" +
                "Gender=" + selectedGender() + "\n" +
                "DOB=" + (getDOB() == null ? "" : getDOB()) + "\n" +
                "Town=" + townField.getText()));
    }

    private void initDOB() {
        day.addItem("Day"); for (int d = 1; d <= 31; d++) day.addItem(String.format("%02d", d));
        month.addItem("Month"); for (int m = 1; m <= 12; m++) month.addItem(String.format("%02d", m));
        int cy = LocalDate.now().getYear();
        year.addItem("Year"); for (int y = cy - 15; y >= cy - 100; y--) year.addItem(String.valueOf(y));
    }
    private LocalDate getDOB() {
        if (day.getSelectedIndex() <= 0 || month.getSelectedIndex() <= 0 || year.getSelectedIndex() <= 0) return null;
        try {
            int d = Integer.parseInt((String) day.getSelectedItem());
            int m = Integer.parseInt((String) month.getSelectedItem());
            int y = Integer.parseInt((String) year.getSelectedItem());
            return LocalDate.of(y, m, d);
        } catch (Exception ex) { return null; }
    }
    private String selectedGender() {
        if (male.isSelected()) return "Male";
        if (female.isSelected()) return "Female";
        if (other.isSelected()) return "Other";
        return "";
    }
    private void addRow(JPanel p, GridBagConstraints c, int row, String label, JComponent f) {
        c.gridx = 0; c.gridy = row; c.weightx = 0; p.add(new JLabel(label), c);
        c.gridx = 1; c.weightx = 1; if (f instanceof JTextField tf) tf.setColumns(24); p.add(f, c);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Step1_BasicPersonalForm_Extended().setVisible(true));
    }
}
