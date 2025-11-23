package GUIS;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.regex.Pattern;

public class Step2_PersonalFormValidation extends JFrame {
    private final JTextField nameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField studentIdField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JButton nextBtn = new JButton("Next");
    private final JLabel status = new JLabel(" ");

    private static final Pattern EMAIL_RX = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);

    public Step2_PersonalFormValidation() {
        super("Step 2 — Validation (Required + Email)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(560, 320);
        setLocationRelativeTo(null);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL;
        int row = 0;

        addRow(form, c, row++, "Name *", nameField);
        addRow(form, c, row++, "Email *", emailField);
        addRow(form, c, row++, "Student ID *", studentIdField);
        addRow(form, c, row++, "Phone *", phoneField);

        JPanel south = new JPanel(new BorderLayout());
        status.setBorder(BorderFactory.createEmptyBorder(0,8,0,0));
        south.add(status, BorderLayout.CENTER);
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.add(nextBtn);
        south.add(btns, BorderLayout.EAST);

        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e){validateForm();}
            public void removeUpdate(DocumentEvent e){validateForm();}
            public void changedUpdate(DocumentEvent e){validateForm();}
        };
        nameField.getDocument().addDocumentListener(dl);
        emailField.getDocument().addDocumentListener(dl);
        studentIdField.getDocument().addDocumentListener(dl);
        phoneField.getDocument().addDocumentListener(dl);
        validateForm();

        nextBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "All good — moving to next step."));
    }

    private boolean required(String s){ return s!=null && !s.trim().isEmpty(); }
    private boolean email(String s){ return required(s) && EMAIL_RX.matcher(s.trim()).matches(); }

    private void validateForm() {
        if(!required(nameField.getText())) { set(false,"Name is required."); return;}
        if(!email(emailField.getText()))   { set(false,"Email must be valid (e.g., user@domain.com)."); return;}
        if(!required(studentIdField.getText())) { set(false,"Student ID is required."); return;}
        if(!required(phoneField.getText())) { set(false,"Phone is required."); return;}
        set(true, "All good. You can proceed.");
    }

    private void set(boolean ok, String msg){ nextBtn.setEnabled(ok); status.setText(msg); }

    private void addRow(JPanel p, GridBagConstraints c, int row, String label, JComponent field) {
        c.gridx=0; c.gridy=row; c.weightx=0; p.add(new JLabel(label), c);
        c.gridx=1; c.weightx=1; if (field instanceof JTextField tf) tf.setColumns(22); p.add(field, c);
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new Step2_PersonalFormValidation().setVisible(true)); }
}
