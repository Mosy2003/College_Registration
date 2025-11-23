package SampleIdeas;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class GUIPersonalInformation {

    /* =========================
       Domain + In-Memory Store
       ========================= */
    static final class Student {
        final String studentId;               // required + unique
        final UUID sessionId;                 // session association
        final LocalDateTime createdAt = LocalDateTime.now();

        final String name;
        final String email;
        final String phone;
        final String address;
        final String gender;                  // Male/Female/Other
        final LocalDate dob;
        final String town;

        Student(String studentId, UUID sessionId, String name, String email, String phone,
                String address, String gender, LocalDate dob, String town) {
            this.studentId = studentId;
            this.sessionId = sessionId;
            this.name = name;
            this.email = email;
            this.phone = phone;
            this.address = address;
            this.gender = gender;
            this.dob = dob;
            this.town = town;
        }
    }

    static final class InMemoryStudentStore {
        private final Map<String, Student> byStudentId = new ConcurrentHashMap<>();
        private final Map<UUID, String> sessionToStudentId = new ConcurrentHashMap<>();

        synchronized boolean isStudentIdUnique(String id) {
            if (id == null) return false;
            return !byStudentId.containsKey(id.trim());
        }

        synchronized void save(Student s) {
            if (s.studentId == null || s.studentId.isBlank())
                throw new IllegalArgumentException("Student ID is required.");
            if (!isStudentIdUnique(s.studentId))
                throw new IllegalArgumentException("Student ID already exists.");
            byStudentId.put(s.studentId.trim(), s);
            sessionToStudentId.put(s.sessionId, s.studentId.trim());
        }

        Student findByStudentId(String id) { return byStudentId.get(id); }

        String findStudentIdBySession(UUID sessionId) { return sessionToStudentId.get(sessionId); }
    }

    static final class SessionManager {
        private UUID currentSessionId = UUID.randomUUID();
        UUID getCurrentSessionId() { return currentSessionId; }
        void newSession() { currentSessionId = UUID.randomUUID(); }
    }

    /* =========================
       Validators
       ========================= */
    static final class Validators {
        private static final Pattern EMAIL_RX = Pattern.compile(
                "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);
        private static final Pattern PHONE_RX = Pattern.compile("^[+]?[(]?[0-9]{1,4}[)]?[-\\s./0-9]*$");

        static boolean required(String s) { return s != null && !s.trim().isEmpty(); }

        static boolean email(String s) { return required(s) && EMAIL_RX.matcher(s.trim()).matches(); }

        static boolean phone(String s) { return required(s) && PHONE_RX.matcher(s.trim()).matches(); }
    }

    /* =========================
       Wizard Frame
       ========================= */
    static final class WizardFrame extends JFrame {
        // Cards
        private static final String CARD_FORM = "form";
        private static final String CARD_NEXT = "next";

        private final CardLayout cards = new CardLayout();
        private final JPanel cardPanel = new JPanel(cards);

        // Bottom bar
        private final JButton backBtn = new JButton("Back");
        private final JButton nextBtn = new JButton("Next");
        private final JButton newSessionBtn = new JButton("New Session");
        private final JLabel statusLabel = new JLabel(" ");

        // Session display
        private final JLabel sessionLabel = new JLabel();

        // Store + session
        private final InMemoryStudentStore store;
        private final SessionManager sessionManager;

        // Panels
        private final FormPanel formPanel;
        private final NextPanel nextPanel;

        WizardFrame(InMemoryStudentStore store, SessionManager sessionManager) {
            super("Student Intake — Personal Information");
            this.store = store;
            this.sessionManager = sessionManager;

            // Build panels
            formPanel = new FormPanel();
            nextPanel = new NextPanel();

            JPanel top = new JPanel(new BorderLayout());
            JPanel topLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
            topLeft.add(new JLabel("Current Session: "));
            sessionLabel.setFont(sessionLabel.getFont().deriveFont(Font.BOLD));
            topLeft.add(sessionLabel);
            top.add(topLeft, BorderLayout.WEST);

            JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
            topRight.add(newSessionBtn);
            top.add(topRight, BorderLayout.EAST);

            // Cards
            cardPanel.add(formPanel, CARD_FORM);
            cardPanel.add(nextPanel, CARD_NEXT);

            setLayout(new BorderLayout(8, 8));
            add(top, BorderLayout.NORTH);
            add(cardPanel, BorderLayout.CENTER);
            add(buildSouthBar(), BorderLayout.SOUTH);

            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setSize(820, 560);
            setLocationRelativeTo(null);

            updateSessionLabel();
            showCard(CARD_FORM);
            updateButtons();

            // Actions
            backBtn.addActionListener(e -> onBack());
            nextBtn.addActionListener(e -> onNext());
            newSessionBtn.addActionListener(e -> {
                sessionManager.newSession();
                updateSessionLabel();
                formPanel.reset();
                showCard(CARD_FORM);
                setStatus("Started a new session.");
                updateButtons();
            });
        }

        private JPanel buildSouthBar() {
            JPanel south = new JPanel(new BorderLayout());
            statusLabel.setForeground(new Color(0x444444));
            statusLabel.setText(" ");
            south.add(statusLabel, BorderLayout.CENTER);

            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttons.add(backBtn);
            buttons.add(nextBtn);
            south.add(buttons, BorderLayout.EAST);
            return south;
        }

        private void updateSessionLabel() {
            sessionLabel.setText(sessionManager.getCurrentSessionId().toString());
        }

        private void setStatus(String msg) {
            statusLabel.setForeground(msg != null && msg.toLowerCase().contains("error") ? new Color(0xB00020) : new Color(0x444444));
            statusLabel.setText(msg == null ? " " : msg);
        }

        private void showCard(String key) {
            cards.show(cardPanel, key);
            cardPanel.putClientProperty("card", key);
            cardPanel.revalidate();
            cardPanel.repaint();
        }

        private String currentCardKey() {
            Object v = cardPanel.getClientProperty("card");
            return v instanceof String s ? s : CARD_FORM;
        }

        private void updateButtons() {
            String current = currentCardKey();
            boolean onForm = CARD_FORM.equals(current);
            backBtn.setEnabled(!onForm);
            nextBtn.setText(onForm ? "Next" : "Close");
        }

        private void onBack() {
            if (CARD_NEXT.equals(currentCardKey())) {
                showCard(CARD_FORM);
                setStatus(" ");
                updateButtons();
            }
        }

        private void onNext() {
            if (CARD_FORM.equals(currentCardKey())) {
                // Validate + Save + go next
                try {
                    Student s = formPanel.validateBuildStudent(sessionManager.getCurrentSessionId(), store);
                    store.save(s); // definitive uniqueness enforcement

                    // Show confirmation popup
                    JOptionPane.showMessageDialog(this,
                            "Personal info saved successfully and linked to session.\n\n" +
                            "Session: " + s.sessionId + "\n" +
                            "Student ID: " + s.studentId + "\n" +
                            "Name: " + s.name + "\n" +
                            "Email: " + s.email + "\n" +
                            "Phone: " + s.phone + "\n" +
                            "Address: " + s.address + "\n" +
                            "Gender: " + s.gender + "\n" +
                            "DOB: " + s.dob + "\n" +
                            "Town: " + s.town,
                            "Success", JOptionPane.INFORMATION_MESSAGE);

                    nextPanel.showSummary(s);
                    showCard(CARD_NEXT);
                    setStatus("Record stored and associated with your session.");
                    updateButtons();
                } catch (IllegalArgumentException ex) {
                    setStatus("Error: " + ex.getMessage());
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    setStatus("Error: Unexpected error saving data.");
                    JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Close the wizard (or reset for another entry)
                dispose();
            }
        }

        /* =========================
           Form Panel (Stage 1)
           ========================= */
        final class FormPanel extends JPanel {
            private final JTextField nameField = new JTextField();
            private final JTextField emailField = new JTextField();
            private final JTextField studentIdField = new JTextField();
            private final JTextField phoneField = new JTextField();

            private final JTextArea addressArea = new JTextArea(3, 24);
            private final JRadioButton male = new JRadioButton("Male");
            private final JRadioButton female = new JRadioButton("Female");
            private final JRadioButton other = new JRadioButton("Other");
            private final ButtonGroup genderGroup = new ButtonGroup();

            // DOB via combos to avoid parsing issues
            private final JComboBox<String> dayCombo = new JComboBox<>();
            private final JComboBox<String> monthCombo = new JComboBox<>(new String[]{
                    "Month", "01","02","03","04","05","06","07","08","09","10","11","12"
            });
            private final JComboBox<String> yearCombo = new JComboBox<>();
            private final JTextField townField = new JTextField();

            private final JButton clearBtn = new JButton("Clear");

            FormPanel() {
                super(new BorderLayout(12, 12));
                setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

                // Build form grid
                JPanel form = new JPanel(new GridBagLayout());
                GridBagConstraints c = new GridBagConstraints();
                c.insets = new Insets(6, 6, 6, 6);
                c.fill = GridBagConstraints.HORIZONTAL;
                int row = 0;

                addRow(form, c, row++, "Name *", nameField);
                addRow(form, c, row++, "Email *", emailField);
                addRow(form, c, row++, "Student ID *", studentIdField);
                addRow(form, c, row++, "Phone Number *", phoneField);

                // Address
                c.gridx = 0; c.gridy = row; c.weightx = 0;
                form.add(new JLabel("Address *"), c);
                c.gridx = 1; c.weightx = 1.0;
                addressArea.setLineWrap(true);
                addressArea.setWrapStyleWord(true);
                form.add(new JScrollPane(addressArea), c);
                row++;

                // Gender
                JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
                genderGroup.add(male); genderPanel.add(male);
                genderGroup.add(female); genderPanel.add(female);
                genderGroup.add(other); genderPanel.add(other);
                addRow(form, c, row++, "Gender *", genderPanel);

                // DOB (combos)
                initDOBCombos();
                JPanel dobPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
                dobPanel.add(dayCombo); dobPanel.add(monthCombo); dobPanel.add(yearCombo);
                addRow(form, c, row++, "DOB *", dobPanel);

                addRow(form, c, row++, "Town *", townField);

                JLabel note = new JLabel("* Required");
                note.setForeground(Color.DARK_GRAY);
                c.gridx = 0; c.gridy = row; c.gridwidth = 2; c.weightx = 1;
                form.add(note, c);

                add(form, BorderLayout.CENTER);

                // Clear button
                JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT));
                south.add(clearBtn);
                add(south, BorderLayout.SOUTH);

                // Live validation: enable Next only when valid
                DocumentListener dl = new DocumentListener() {
                    public void insertUpdate(DocumentEvent e) { validateForm(); }
                    public void removeUpdate(DocumentEvent e) { validateForm(); }
                    public void changedUpdate(DocumentEvent e) { validateForm(); }
                };
                nameField.getDocument().addDocumentListener(dl);
                emailField.getDocument().addDocumentListener(dl);
                studentIdField.getDocument().addDocumentListener(dl);
                phoneField.getDocument().addDocumentListener(dl);
                addressArea.getDocument().addDocumentListener(dl);
                townField.getDocument().addDocumentListener(dl);

                // Combo/radio changes
                monthCombo.addActionListener(e -> validateForm());
                dayCombo.addActionListener(e -> validateForm());
                yearCombo.addActionListener(e -> validateForm());
                male.addActionListener(e -> validateForm());
                female.addActionListener(e -> validateForm());
                other.addActionListener(e -> validateForm());

                clearBtn.addActionListener(e -> reset());

                // Initial state
                validateForm();
            }

            private void initDOBCombos() {
                dayCombo.addItem("Day");
                for (int d = 1; d <= 31; d++) dayCombo.addItem(String.format("%02d", d));
                int currentYear = LocalDate.now().getYear();
                yearCombo.addItem("Year");
                // Allow ages ~15–100
                for (int y = currentYear - 15; y >= currentYear - 100; y--) {
                    yearCombo.addItem(String.valueOf(y));
                }
            }

            private void addRow(JPanel panel, GridBagConstraints c, int row, String label, JComponent field) {
                c.gridx = 0; c.gridy = row; c.gridwidth = 1; c.weightx = 0;
                panel.add(new JLabel(label), c);
                c.gridx = 1; c.weightx = 1.0;
                if (field instanceof JTextField tf) tf.setColumns(24);
                panel.add(field, c);
            }

            void reset() {
                nameField.setText("");
                emailField.setText("");
                studentIdField.setText("");
                phoneField.setText("");
                addressArea.setText("");
                townField.setText("");
                genderGroup.clearSelection();
                dayCombo.setSelectedIndex(0);
                monthCombo.setSelectedIndex(0);
                yearCombo.setSelectedIndex(0);
                validateForm();
            }

            private String selectedGender() {
                if (male.isSelected()) return "Male";
                if (female.isSelected()) return "Female";
                if (other.isSelected()) return "Other";
                return null;
            }

            private LocalDate selectedDOB() {
                int di = dayCombo.getSelectedIndex();
                int mi = monthCombo.getSelectedIndex();
                int yi = yearCombo.getSelectedIndex();
                if (di <= 0 || mi <= 0 || yi <= 0) return null;
                int day = Integer.parseInt((String) dayCombo.getSelectedItem());
                int month = Integer.parseInt((String) monthCombo.getSelectedItem());
                int year = Integer.parseInt((String) yearCombo.getSelectedItem());
                // Basic guard for invalid dates (e.g., 31/02)
                try {
                    return LocalDate.of(year, month, day);
                } catch (Exception ex) {
                    return null;
                }
            }

            private void markInvalid(JComponent comp, boolean ok) {
                if (ok) {
                    if (comp instanceof JTextField) {
                        comp.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
                    } else if (comp instanceof JTextArea) {
                        comp.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextArea.border"));
                    } else if (comp instanceof JComboBox) {
                        comp.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("ComboBox.border"));
                    } else {
                        comp.setBorder(null);
                    }
                } else {
                    comp.setBorder(BorderFactory.createLineBorder(Color.RED));
                }
            }

            private void setNextEnabled(boolean ok, String message) {
                nextBtn.setEnabled(ok);
                statusLabel.setForeground(ok ? new Color(0x2e7d32) : new Color(0xB00020));
                statusLabel.setText(message == null ? " " : message);
            }

            private boolean validateForm() {
                String name = nameField.getText();
                String email = emailField.getText();
                String sid = studentIdField.getText();
                String phone = phoneField.getText();
                String address = addressArea.getText();
                String town = townField.getText();
                String gender = selectedGender();
                LocalDate dob = selectedDOB();

                // Required checks
                if (!Validators.required(name)) {
                    setNextEnabled(false, "Name is required."); markInvalid(nameField, false); return false;
                } else markInvalid(nameField, true);

                if (!Validators.required(email)) {
                    setNextEnabled(false, "Email is required."); markInvalid(emailField, false); return false;
                } else markInvalid(emailField, true);

                if (!Validators.email(email)) {
                    setNextEnabled(false, "Email must be valid (e.g., user@domain.com)."); markInvalid(emailField, false); return false;
                } else markInvalid(emailField, true);

                if (!Validators.required(sid)) {
                    setNextEnabled(false, "Student ID is required."); markInvalid(studentIdField, false); return false;
                } else markInvalid(studentIdField, true);

                // Pre-check uniqueness (definitive check on save)
                if (!store.isStudentIdUnique(sid.trim())) {
                    setNextEnabled(false, "Student ID must be unique. The provided ID already exists.");
                    markInvalid(studentIdField, false); return false;
                } else markInvalid(studentIdField, true);

                if (!Validators.required(phone) || !Validators.phone(phone)) {
                    setNextEnabled(false, "Phone number looks invalid."); markInvalid(phoneField, false); return false;
                } else markInvalid(phoneField, true);

                if (!Validators.required(address)) {
                    setNextEnabled(false, "Address is required."); markInvalid(addressArea, false); return false;
                } else markInvalid(addressArea, true);

                if (gender == null) {
                    setNextEnabled(false, "Please select a gender."); return false;
                }

                if (dob == null) {
                    setNextEnabled(false, "Please select a valid date of birth."); markInvalid(dayCombo, false); markInvalid(monthCombo, false); markInvalid(yearCombo, false); return false;
                } else { markInvalid(dayCombo, true); markInvalid(monthCombo, true); markInvalid(yearCombo, true); }

                if (!Validators.required(town)) {
                    setNextEnabled(false, "Town is required."); markInvalid(townField, false); return false;
                } else markInvalid(townField, true);

                setNextEnabled(true, "All good. You can proceed.");
                return true;
            }

            Student validateBuildStudent(UUID sessionId, InMemoryStudentStore store) {
                if (!validateForm())
                    throw new IllegalArgumentException("Please correct the highlighted fields.");

                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String sid = studentIdField.getText().trim();
                String phone = phoneField.getText().trim();
                String address = addressArea.getText().trim();
                String town = townField.getText().trim();
                String gender = (male.isSelected() ? "Male" : female.isSelected() ? "Female" : "Other");
                LocalDate dob = selectedDOB();
                if (dob == null) throw new IllegalArgumentException("Invalid date of birth.");

                return new Student(sid, sessionId, name, email, phone, address, gender, dob, town);
            }
        }

        /* =========================
           Next Panel (Stage 2)
           ========================= */
        final class NextPanel extends JPanel {
            private final JTable summaryTable = new JTable(new DefaultTableModel(
                    new Object[][]{}, new String[]{"Field", "Value"}) {
                @Override public boolean isCellEditable(int row, int col) { return false; }
            });

            NextPanel() {
                super(new BorderLayout(10, 10));
                setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
                JLabel title = new JLabel("Next Stage — Record Stored", SwingConstants.LEFT);
                title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
                add(title, BorderLayout.NORTH);
                add(new JScrollPane(summaryTable), BorderLayout.CENTER);
                JLabel hint = new JLabel("Click Back to edit details, or Close to finish.");
                hint.setForeground(new Color(0x555555));
                add(hint, BorderLayout.SOUTH);
            }

            void showSummary(Student s) {
                DefaultTableModel model = (DefaultTableModel) summaryTable.getModel();
                model.setRowCount(0);
                addRow(model, "Session", s.sessionId.toString());
                addRow(model, "Student ID", s.studentId);
                addRow(model, "Name", s.name);
                addRow(model, "Email", s.email);
                addRow(model, "Phone", s.phone);
                addRow(model, "Address", s.address);
                addRow(model, "Gender", s.gender);
                addRow(model, "DOB", String.valueOf(s.dob));
                addRow(model, "Town", s.town);
                summaryTable.getColumnModel().getColumn(0).setPreferredWidth(140);
                summaryTable.getColumnModel().getColumn(1).setPreferredWidth(420);
            }

            private void addRow(DefaultTableModel m, String k, String v) { m.addRow(new Object[]{k, v}); }
        }
    }

    /* =========================
       Bootstrap
       ========================= */
    private static void initLookAndFeel() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            initLookAndFeel();
            InMemoryStudentStore store = new InMemoryStudentStore();
            SessionManager sessionManager = new SessionManager();
            WizardFrame frame = new WizardFrame(store, sessionManager);
            frame.setVisible(true);
        });
    }
}
