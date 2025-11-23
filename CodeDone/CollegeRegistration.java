package CodeDone;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class CollegeRegistration extends JFrame {

    // =========================
    // ===== Domain + Store ====
    // =========================

    static final class Student {
        final String studentId;
        final UUID sessionId;
        final LocalDateTime createdAt = LocalDateTime.now();

        String name;
        String email;
        String phone;
        String address;
        String gender;
        String town;
        LocalDate dob;
        String course;
        String level;

        Student(String studentId, UUID sessionId) {
            this.studentId = studentId;
            this.sessionId = sessionId;
        }
    }

    static final class Store {
        private final Map<String, Student> byId = new ConcurrentHashMap<>();

        synchronized boolean unique(String sid) {
            return sid != null && !byId.containsKey(sid.trim());
        }

        synchronized Student getOrCreate(String sid, UUID sess) {
            return byId.computeIfAbsent(sid.trim(), k -> new Student(k, sess));
        }

        synchronized Student find(String sid) {
            return byId.get(sid.trim());
        }
    }

    // =====================
    // ===== Timetable =====
    // =====================

    static final class SessionInfo {
        final String day;
        final String time;
        final String module;
        final String lecturer;
        final String room;

        SessionInfo(String day, String time, String module, String lecturer, String room) {
            this.day = day;
            this.time = time;
            this.module = module;
            this.lecturer = lecturer;
            this.room = room;
        }
    }

    static final class TimetableService {
        private final Map<String, Map<String, List<SessionInfo>>> data = new HashMap<>();

        TimetableService() {
            String L5 = "Beginner (Level 5)";
            String L6 = "Intermediate (Level 6)";
            String L8 = "Advanced (Level 8)";

            // Engineering
            put("Engineering", L5, Arrays.asList(
                    new SessionInfo("Monday", "09:00 - 10:30", "Statics", "Dr. Patel", "E201"),
                    new SessionInfo("Wednesday", "11:00 - 12:30", "Materials I", "Prof. O'Neill", "E105")
            ));
            put("Engineering", L6, Arrays.asList(
                    new SessionInfo("Tuesday", "10:00 - 12:00", "Dynamics", "Dr. Brown", "E203"),
                    new SessionInfo("Thursday", "14:00 - 16:00", "Fluid Mechanics", "Dr. Li", "E210")
            ));
            put("Engineering", L8, Arrays.asList(
                    new SessionInfo("Tuesday", "09:00 - 10:30", "Thermodynamics", "Dr. Li", "E220"),
                    new SessionInfo("Friday", "13:00 - 15:00", "Capstone Studio", "Panel", "E401")
            ));

            // Law
            put("Law", L5, Arrays.asList(
                    new SessionInfo("Monday", "13:00 - 14:30", "Intro to Law", "Dr. Byrne", "L101"),
                    new SessionInfo("Thursday", "10:00 - 11:30", "Legal Writing", "Dr. Nolan", "L204")
            ));
            put("Law", L6, Arrays.asList(
                    new SessionInfo("Tuesday", "12:00 - 13:30", "Tort Law", "Dr. Murphy", "L210"),
                    new SessionInfo("Friday", "09:00 - 10:30", "Contract Law II", "Dr. Kelly", "L305")
            ));
            put("Law", L8, Arrays.asList(
                    new SessionInfo("Wednesday", "15:00 - 16:30", "EU Law", "Dr. Kavanagh", "L402"),
                    new SessionInfo("Friday", "11:00 - 13:00", "Moot Court", "Panel", "Courtroom A")
            ));

            // Computer Science
            put("Computer Science", L5, Arrays.asList(
                    new SessionInfo("Monday", "09:00 - 10:30", "Programming I", "Ms. Daly", "C101"),
                    new SessionInfo("Wednesday", "14:00 - 16:00", "Web Dev I", "Mr. Shah", "Lab C2")
            ));
            put("Computer Science", L6, Arrays.asList(
                    new SessionInfo("Tuesday", "10:00 - 12:00", "Data Structures", "Dr. Grace", "C220"),
                    new SessionInfo("Thursday", "13:00 - 14:30", "Databases", "Dr. Ahmed", "C118")
            ));
            put("Computer Science", L8, Arrays.asList(
                    new SessionInfo("Wednesday", "09:00 - 11:00", "Distributed Systems", "Dr. Grace", "C305"),
                    new SessionInfo("Friday", "11:15 - 12:45", "AI & ML", "Dr. Quinn", "C410")
            ));

            // Business
            put("Business", L5, Arrays.asList(
                    new SessionInfo("Monday", "11:00 - 12:30", "Intro to Business", "Ms. Ryan", "B101"),
                    new SessionInfo("Thursday", "09:00 - 10:30", "Accounting I", "Mr. Connolly", "B204")
            ));
            put("Business", L6, Arrays.asList(
                    new SessionInfo("Tuesday", "14:00 - 15:30", "Marketing", "Dr. Walsh", "B210"),
                    new SessionInfo("Friday", "10:00 - 12:00", "Operations", "Dr. Singh", "B305")
            ));
            put("Business", L8, Arrays.asList(
                    new SessionInfo("Wednesday", "13:00 - 14:30", "Strategy", "Dr. Walsh", "B402"),
                    new SessionInfo("Thursday", "15:00 - 17:00", "Entrepreneurship", "Panel", "Incubator 1")
            ));
        }

        private void put(String course, String level, List<SessionInfo> sessions) {
            data.computeIfAbsent(course, k -> new HashMap<>()).put(level, sessions);
        }

        List<SessionInfo> lookup(String course, String level) {
            return data
                    .getOrDefault(course, Collections.emptyMap())
                    .getOrDefault(level, Collections.emptyList());
        }
    }

    // =====================
    // ===== App State =====
    // =====================

    private final Store store = new Store();
    private final TimetableService timetable = new TimetableService();
    private UUID sessionId = UUID.randomUUID();

    // ==============================
    // ===== Wizard Scaffolding =====
    // ==============================

    private static final String CARD_PERSONAL = "personal";
    private static final String CARD_COURSE   = "course";
    private static final String CARD_TABLE    = "table";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);
    private String currentCardKey = CARD_PERSONAL;

    private final JLabel sessionLabel = new JLabel();
    private final JLabel statusLabel = new JLabel(" ");
    private final JButton backButton = new JButton("Back");
    private final JButton nextButton = new JButton("Next");

    // =====================
    // ===== Personal ======
    // =====================

    private final JTextField nameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField studentIdField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JTextArea addressArea = new JTextArea(3, 24);
    private final JRadioButton maleRadio = new JRadioButton("Male");
    private final JRadioButton femaleRadio = new JRadioButton("Female");
    private final JRadioButton otherRadio = new JRadioButton("Other");
    private final ButtonGroup genderGroup = new ButtonGroup();
    private final JComboBox<String> dayCombo = new JComboBox<>();
    private final JComboBox<String> monthCombo = new JComboBox<>();
    private final JComboBox<String> yearCombo = new JComboBox<>();
    private final JTextField townField = new JTextField();

    private static final Pattern EMAIL_RX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PHONE_RX =
            Pattern.compile("^[+]?[(]?[0-9]{1,4}[)]?[-\\s./0-9]*$");

    // ===================
    // ===== Course ======
    // ===================

    private final JComboBox<String> courseCombo = new JComboBox<>(new String[]{
            "-- Select a course --", "Engineering", "Law", "Computer Science", "Business"
    });

    private final JComboBox<String> levelCombo = new JComboBox<>(new String[]{
            "-- Select a level --", "Beginner (Level 5)", "Intermediate (Level 6)", "Advanced (Level 8)"
    });

    private final JButton previewButton = new JButton("Preview Timetable");

    // =================
    // ===== Table =====
    // =================

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Day", "Time", "Module", "Lecturer", "Room"}, 0
    ) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };

    private final JTable table = new JTable(tableModel);

    // ==========================
    // ===== Constructor ========
    // ==========================

    public CollegeRegistration() {
        super("Full Combined Wizard (Fixed Timetable)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(920, 620);
        setLocationRelativeTo(null);

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildCards(), BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);

        backButton.addActionListener(e -> onBack());
        nextButton.addActionListener(e -> onNext());

        showCard(CARD_PERSONAL);
        validatePersonalForm(); // initial validation state
    }

    // ======================
    // ===== UI Builders ====
    // ======================

    private JPanel buildTopBar() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        top.add(new JLabel("Session:"));

        sessionLabel.setText(sessionId.toString());
        sessionLabel.setFont(sessionLabel.getFont().deriveFont(Font.BOLD));
        top.add(sessionLabel);

        return top;
    }

    private JPanel buildCards() {
        cardPanel.add(buildPersonalPanel(), CARD_PERSONAL);
        cardPanel.add(buildCoursePanel(), CARD_COURSE);
        cardPanel.add(buildTablePanel(), CARD_TABLE);
        return cardPanel;
    }

    private JPanel buildBottomBar() {
        JPanel south = new JPanel(new BorderLayout());

        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        south.add(statusLabel, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backButton.setEnabled(false);
        btns.add(backButton);
        btns.add(nextButton);
        south.add(btns, BorderLayout.EAST);

        return south;
    }

    private JPanel buildPersonalPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addRow(form, gbc, row++, "Name *", nameField);
        addRow(form, gbc, row++, "Email *", emailField);
        addRow(form, gbc, row++, "Student ID *", studentIdField);
        addRow(form, gbc, row++, "Phone *", phoneField);

        // Address
        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(new JLabel("Address *"), gbc);
        gbc.gridx = 1;
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        form.add(new JScrollPane(addressArea), gbc);
        row++;

        // Gender
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        genderGroup.add(otherRadio);
        genderPanel.add(maleRadio);
        genderPanel.add(femaleRadio);
        genderPanel.add(otherRadio);
        addRow(form, gbc, row++, "Gender *", genderPanel);

        // DOB
        initDOB();
        JPanel dobPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        dobPanel.add(dayCombo);
        dobPanel.add(monthCombo);
        dobPanel.add(yearCombo);
        addRow(form, gbc, row++, "DOB *", dobPanel);

        addRow(form, gbc, row, "Town *", townField);

        // Live validation hooks
        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { validatePersonalForm(); }
            public void removeUpdate(DocumentEvent e) { validatePersonalForm(); }
            public void changedUpdate(DocumentEvent e) { validatePersonalForm(); }
        };

        nameField.getDocument().addDocumentListener(dl);
        emailField.getDocument().addDocumentListener(dl);
        studentIdField.getDocument().addDocumentListener(dl);
        phoneField.getDocument().addDocumentListener(dl);
        addressArea.getDocument().addDocumentListener(dl);
        townField.getDocument().addDocumentListener(dl);

        maleRadio.addActionListener(e -> validatePersonalForm());
        femaleRadio.addActionListener(e -> validatePersonalForm());
        otherRadio.addActionListener(e -> validatePersonalForm());
        dayCombo.addActionListener(e -> validatePersonalForm());
        monthCombo.addActionListener(e -> validatePersonalForm());
        yearCombo.addActionListener(e -> validatePersonalForm());

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        wrap.add(form, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel buildCoursePanel() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addRow(form, gbc, row++, "Course *", courseCombo);
        addRow(form, gbc, row++, "Level *", levelCombo);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(previewButton);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        form.add(buttons, gbc);

        previewButton.addActionListener(e -> {
            if (courseCombo.getSelectedIndex() <= 0 || levelCombo.getSelectedIndex() <= 0) {
                setError("Select course and level first.");
                return;
            }
            loadTable((String) courseCombo.getSelectedItem(), (String) levelCombo.getSelectedItem());
            setOk("Previewing timetable.");
            showCard(CARD_TABLE);
            backButton.setEnabled(true);
            nextButton.setText("Finish");
        });

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        wrap.add(form, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Prepared Timetable", SwingConstants.LEFT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        panel.add(title, BorderLayout.NORTH);

        table.setFillsViewportHeight(true);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        if (field instanceof JTextField tf) {
            tf.setColumns(24);
        }
        panel.add(field, gbc);
    }

    // =======================
    // ===== Navigation ======
    // =======================

    private void onBack() {
        if (CARD_COURSE.equals(currentCardKey)) {
            showCard(CARD_PERSONAL);
            backButton.setEnabled(false);
            nextButton.setText("Next");
        } else if (CARD_TABLE.equals(currentCardKey)) {
            showCard(CARD_COURSE);
            backButton.setEnabled(true);
            nextButton.setText("Next");
        }
        statusLabel.setText(" ");
    }

    private void onNext() {
        if (CARD_PERSONAL.equals(currentCardKey)) {
            if (!validatePersonalForm()) return;

            String sid = studentIdField.getText().trim();
            if (!store.unique(sid)) {
                setError("Student ID already exists.");
                return;
            }

            Student s = store.getOrCreate(sid, sessionId);
            s.name = nameField.getText().trim();
            s.email = emailField.getText().trim();
            s.phone = phoneField.getText().trim();
            s.address = addressArea.getText().trim();
            s.gender = gender();
            s.dob = dob();
            s.town = townField.getText().trim();

            JOptionPane.showMessageDialog(this,
                    "Personal saved.\n\nSession: " + s.sessionId + "\nStudent ID: " + s.studentId +
                            "\nName: " + s.name + "\nEmail: " + s.email + "\nAddress: " + s.address +
                            "\nGender: " + s.gender + "\nDOB: " + s.dob + "\nTown: " + s.town,
                    "Saved", JOptionPane.INFORMATION_MESSAGE
            );

            showCard(CARD_COURSE);
            backButton.setEnabled(true);
            nextButton.setText("Next");
            statusLabel.setText("Select course and level.");
            return;
        }

        if (CARD_COURSE.equals(currentCardKey)) {
            if (courseCombo.getSelectedIndex() <= 0) {
                setError("Please select a course.");
                return;
            }
            if (levelCombo.getSelectedIndex() <= 0) {
                setError("Please select a level.");
                return;
            }

            String sid = studentIdField.getText().trim();
            Student s = store.find(sid);
            s.course = (String) courseCombo.getSelectedItem();
            s.level = (String) levelCombo.getSelectedItem();

            JOptionPane.showMessageDialog(this,
                    "Course/Level saved.\nCourse: " + s.course + "\nLevel: " + s.level,
                    "Saved", JOptionPane.INFORMATION_MESSAGE
            );

            loadTable(s.course, s.level);
            showCard(CARD_TABLE);
            nextButton.setText("Finish");
            statusLabel.setText("Review timetable. Click Finish to end.");
            return;
        }

        if (CARD_TABLE.equals(currentCardKey)) {
            dispose();
        }
    }

    private void showCard(String key) {
        cardLayout.show(cardPanel, key);
        currentCardKey = key;
    }

    // ==================================
    // ===== Validation & Helpers =======
    // ==================================

    private void initDOB() {
        dayCombo.addItem("Day");
        for (int d = 1; d <= 31; d++) {
            dayCombo.addItem(String.format("%02d", d));
        }

        monthCombo.addItem("Month");
        for (int m = 1; m <= 12; m++) {
            monthCombo.addItem(String.format("%02d", m));
        }

        int currentYear = LocalDate.now().getYear();
        yearCombo.addItem("Year");
        for (int y = currentYear - 15; y >= currentYear - 100; y--) {
            yearCombo.addItem(String.valueOf(y));
        }
    }

    private LocalDate dob() {
        if (dayCombo.getSelectedIndex() <= 0 ||
            monthCombo.getSelectedIndex() <= 0 ||
            yearCombo.getSelectedIndex() <= 0) {
            return null;
        }
        try {
            int d = Integer.parseInt((String) dayCombo.getSelectedItem());
            int m = Integer.parseInt((String) monthCombo.getSelectedItem());
            int y = Integer.parseInt((String) yearCombo.getSelectedItem());
            return LocalDate.of(y, m, d);
        } catch (Exception ex) {
            return null;
        }
    }

    private String gender() {
        if (maleRadio.isSelected()) return "Male";
        if (femaleRadio.isSelected()) return "Female";
        if (otherRadio.isSelected()) return "Other";
        return null;
    }

    private boolean required(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private boolean emailOk(String s) {
        return required(s) && EMAIL_RX.matcher(s.trim()).matches();
    }

    private boolean phoneOk(String s) {
        return required(s) && PHONE_RX.matcher(s.trim()).matches();
    }

    private boolean validatePersonalForm() {
        if (!required(nameField.getText())) {
            setError("Name is required.");
            return false;
        }
        if (!emailOk(emailField.getText())) {
            setError("Valid email required (e.g., user@domain.com).");
            return false;
        }
        if (!required(studentIdField.getText())) {
            setError("Student ID is required.");
            return false;
        }
        if (!phoneOk(phoneField.getText())) {
            setError("Phone looks invalid.");
            return false;
        }
        if (!required(addressArea.getText())) {
            setError("Address is required.");
            return false;
        }
        if (gender() == null) {
            setError("Please select a gender.");
            return false;
        }
        if (dob() == null) {
            setError("Please select a valid DOB.");
            return false;
        }
        if (!required(townField.getText())) {
            setError("Town is required.");
            return false;
        }

        setOk("All good. You can proceed.");
        return true;
    }

    private void loadTable(String course, String level) {
        List<SessionInfo> sessions = timetable.lookup(course, level);
        tableModel.setRowCount(0);

        if (sessions.isEmpty()) {
            tableModel.addRow(new Object[]{
                    "-", "-", "No sessions found for " + course + " — " + level, "-", "-"
            });
            return;
        }

        for (SessionInfo s : sessions) {
            tableModel.addRow(new Object[]{s.day, s.time, s.module, s.lecturer, s.room});
        }
    }

    private void setOk(String message) {
        statusLabel.setForeground(new Color(0x2E7D32));
        statusLabel.setText(message);
    }

    private void setError(String message) {
        statusLabel.setForeground(new Color(0xB00020));
        statusLabel.setText(message);
    }

    // =====================
    // ===== Entrypoint ====
    // =====================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CollegeRegistration().setVisible(true));
    }
}
