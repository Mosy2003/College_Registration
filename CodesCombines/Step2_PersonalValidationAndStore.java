package CodesCombines;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class Step2_PersonalValidationAndStore extends JFrame {
    // ===== Domain + Store + Session =====
    static final class Student {
        final String studentId, name, email, phone;
        final UUID sessionId;
        final LocalDateTime createdAt = LocalDateTime.now();
        Student(String sid, String n, String e, String p, UUID sess) {
            studentId = sid; name = n; email = e; phone = p; sessionId = sess;
        }
    }
    static final class Store {
        private final Map<String, Student> byId = new ConcurrentHashMap<>();
        synchronized boolean isUnique(String sid) { return sid != null && !byId.containsKey(sid.trim()); }
        synchronized void save(Student s) {
            if (s.studentId == null || s.studentId.isBlank()) throw new IllegalArgumentException("Student ID required.");
            if (!isUnique(s.studentId)) throw new IllegalArgumentException("Student ID already exists.");
            byId.put(s.studentId.trim(), s);
        }
    }
    private final Store store = new Store();
    private UUID sessionId = UUID.randomUUID();

    // ===== UI + Validation =====
    private final JTextField nameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField studentIdField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JLabel sessionLabel = new JLabel();
    private final JLabel status = new JLabel(" ");
    private final JButton saveBtn = new JButton("Save");

    private static final Pattern EMAIL_RX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
            Pattern.CASE_INSENSITIVE);

    public Step2_PersonalValidationAndStore() {
        super("Step 2 — Personal Validation + Session + Unique ID");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 320);
        setLocationRelativeTo(null);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        top.add(new JLabel("Session:"));
        sessionLabel.setText(sessionId.toString());
        sessionLabel.setFont(sessionLabel.getFont().deriveFont(Font.BOLD));
        top.add(sessionLabel);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL; int row = 0;

        addRow(form, c, row++, "Name *", nameField);
        addRow(form, c, row++, "Email *", emailField);
        addRow(form, c, row++, "Student ID *", studentIdField);
        addRow(form, c, row++, "Phone *", phoneField);

        JPanel south = new JPanel(new BorderLayout());
        status.setBorder(BorderFactory.createEmptyBorder(0,8,0,0));
        south.add(status, BorderLayout.CENTER);
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.add(saveBtn); south.add(btns, BorderLayout.EAST);

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { v(); }
            public void removeUpdate(DocumentEvent e) { v(); }
            public void changedUpdate(DocumentEvent e) { v(); }
        };
        nameField.getDocument().addDocumentListener(dl);
        emailField.getDocument().addDocumentListener(dl);
        studentIdField.getDocument().addDocumentListener(dl);
        phoneField.getDocument().addDocumentListener(dl);
        v();

        saveBtn.addActionListener(e -> {
            try {
                Student s = new Student(studentIdField.getText().trim(),
                        nameField.getText().trim(), emailField.getText().trim(),
                        phoneField.getText().trim(), sessionId);
                store.save(s);
                JOptionPane.showMessageDialog(this, "Saved:\nSession=" + s.sessionId +
                        "\nStudent ID=" + s.studentId + "\nName=" + s.name + "\nEmail=" + s.email);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private boolean req(String s){ return s!=null && !s.trim().isEmpty(); }
    private boolean emailOk(String s){ return req(s) && EMAIL_RX.matcher(s.trim()).matches(); }

    private void v(){
        if(!req(nameField.getText())) { set(false,"Name required"); return; }
        if(!emailOk(emailField.getText())) { set(false,"Valid email required"); return; }
        if(!req(studentIdField.getText())) { set(false,"Student ID required"); return; }
        if(!store.isUnique(studentIdField.getText().trim())) { set(false,"Student ID already exists"); return; }
        if(!req(phoneField.getText())) { set(false,"Phone required"); return; }
        set(true,"All good.");
    }

    private void set(boolean ok, String msg){ saveBtn.setEnabled(ok); status.setText(msg); }
    private void addRow(JPanel p, GridBagConstraints c, int row, String label, JComponent f){
        c.gridx=0;c.gridy=row;c.weightx=0;p.add(new JLabel(label),c);
        c.gridx=1;c.weightx=1;if(f instanceof JTextField tf) tf.setColumns(24);p.add(f,c);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Step2_PersonalValidationAndStore().setVisible(true));
    }
}
