package CodesCombines;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class Step4_CourseLevelTimetablePreview extends JFrame {
    // ===== Domain + Store =====
    static final class StudentProfile {
        final String studentId;
        final UUID sessionId;
        final LocalDateTime createdAt = LocalDateTime.now();

        String name, email, phone;
        String course, level;

        StudentProfile(String sid, UUID sess){ studentId=sid; sessionId=sess; }
    }
    static final class Store {
        private final Map<String, StudentProfile> byId = new ConcurrentHashMap<>();
        synchronized boolean unique(String sid){ return sid!=null && !byId.containsKey(sid.trim()); }
        synchronized StudentProfile getOrCreate(String sid, UUID sess){
            return byId.computeIfAbsent(sid.trim(), k -> new StudentProfile(k, sess));
        }
        synchronized StudentProfile find(String sid){ return byId.get(sid.trim()); }
    }

    // ===== Timetable =====
    static final class SessionInfo { final String day,time,module,lecturer,room;
        SessionInfo(String d,String t,String m,String l,String r){ day=d;time=t;module=m;lecturer=l;room=r; } }
    static final class TimetableService {
        private final Map<String, Map<String, java.util.List<SessionInfo>>> data = new HashMap<>();
        TimetableService(){
            String L5="Beginner (Level 5)", L6="Intermediate (Level 6)", L8="Advanced (Level 8)";
            put("Engineering", L5, Arrays.asList(
                    new SessionInfo("Mon","09:00 - 10:30","Statics","Dr. Patel","E201"),
                    new SessionInfo("Wed","11:00 - 12:30","Materials I","Prof. O'Neill","E105")));
            put("Engineering", L6, Arrays.asList(
                    new SessionInfo("Tue","10:00 - 12:00","Dynamics","Dr. Brown","E203"),
                    new SessionInfo("Thu","14:00 - 16:00","Fluid Mechanics","Dr. Li","E210")));
            put("Law", L5, Arrays.asList(
                    new SessionInfo("Mon","13:00 - 14:30","Intro to Law","Dr. Byrne","L101")));
            put("Computer Science", L6, Arrays.asList(
                    new SessionInfo("Tue","10:00 - 12:00","Data Structures","Dr. Grace","C220"),
                    new SessionInfo("Thu","13:00 - 14:30","Databases","Dr. Ahmed","C118")));
            put("Business", L8, Arrays.asList(
                    new SessionInfo("Wed","13:00 - 14:30","Strategy","Dr. Walsh","B402"),
                    new SessionInfo("Thu","15:00 - 17:00","Entrepreneurship","Panel","Incubator 1")));
        }
        private void put(String course, String level, java.util.List<SessionInfo> sessions){
            data.computeIfAbsent(course,k->new HashMap<>()).put(level, sessions);
        }
        java.util.List<SessionInfo> lookup(String course, String level){
            return data.getOrDefault(course, Collections.emptyMap())
                    .getOrDefault(level, Collections.emptyList());
        }
    }

    // ===== App State =====
    private final Store store = new Store();
    private final TimetableService timetable = new TimetableService();
    private UUID sessionId = UUID.randomUUID();

    // ===== Wizard scaffolding =====
    private static final String CARD_PERSONAL="personal", CARD_COURSE="course", CARD_TABLE="table";
    private final CardLayout cards = new CardLayout();
    private final JPanel cardPanel = new JPanel(cards);
    private final JLabel status = new JLabel(" ");
    private final JButton backBtn = new JButton("Back");
    private final JButton nextBtn = new JButton("Next");
    private final JLabel sessionLabel = new JLabel();

    // ===== Personal panel =====
    private final JTextField nameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField studentIdField = new JTextField();
    private final JTextField phoneField = new JTextField();

    private static final Pattern EMAIL_RX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
            Pattern.CASE_INSENSITIVE);

    // ===== Course panel =====
    private final JComboBox<String> courseCombo = new JComboBox<>(new String[]{
            "-- Select a course --", "Engineering", "Law", "Computer Science", "Business"
    });
    private final JComboBox<String> levelCombo = new JComboBox<>(new String[]{
            "-- Select a level --", "Beginner (Level 5)", "Intermediate (Level 6)", "Advanced (Level 8)"
    });

    // ===== Table panel =====
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Day","Time","Module","Lecturer","Room"}, 0){
        @Override public boolean isCellEditable(int r,int c){ return false; } };
    private final JTable table = new JTable(model);

    public Step4_CourseLevelTimetablePreview(){
        super("Step 5 — Full Combined Wizard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(860, 560);
        setLocationRelativeTo(null);

        // Top
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT,8,8));
        top.add(new JLabel("Session:"));
        sessionLabel.setText(sessionId.toString());
        sessionLabel.setFont(sessionLabel.getFont().deriveFont(Font.BOLD));
        top.add(sessionLabel);

        // Cards
        cardPanel.add(buildPersonalPanel(), CARD_PERSONAL);
        cardPanel.add(buildCoursePanel(), CARD_COURSE);
        cardPanel.add(buildTablePanel(), CARD_TABLE);

        // Bottom
        JPanel south = new JPanel(new BorderLayout());
        status.setBorder(BorderFactory.createEmptyBorder(0,8,0,0));
        south.add(status, BorderLayout.CENTER);
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backBtn.setEnabled(false);
        btns.add(backBtn); btns.add(nextBtn);
        south.add(btns, BorderLayout.EAST);

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        backBtn.addActionListener(e -> onBack());
        nextBtn.addActionListener(e -> onNext());

        cards.show(cardPanel, CARD_PERSONAL);
        validatePersonalForm(); // initial button state
    }

    // ---------- Panels ----------
    private JPanel buildPersonalPanel(){
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets=new Insets(6,6,6,6); c.fill=GridBagConstraints.HORIZONTAL; int row=0;

        addRow(form,c,row++,"Name *",nameField);
        addRow(form,c,row++,"Email *",emailField);
        addRow(form,c,row++,"Student ID *",studentIdField);
        addRow(form,c,row++,"Phone *",phoneField);

        DocumentListener dl = new DocumentListener(){ public void insertUpdate(DocumentEvent e){validatePersonalForm();}
            public void removeUpdate(DocumentEvent e){validatePersonalForm();} public void changedUpdate(DocumentEvent e){validatePersonalForm();}};
        nameField.getDocument().addDocumentListener(dl);
        emailField.getDocument().addDocumentListener(dl);
        studentIdField.getDocument().addDocumentListener(dl);
        phoneField.getDocument().addDocumentListener(dl);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        wrap.add(form, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel buildCoursePanel(){
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets=new Insets(6,6,6,6); c.fill=GridBagConstraints.HORIZONTAL; int row=0;

        addRow(form,c,row++,"Course *",courseCombo);
        addRow(form,c,row++,"Level *",levelCombo);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        wrap.add(form, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel buildTablePanel(){
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        JLabel title = new JLabel("Prepared Timetable", SwingConstants.LEFT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        p.add(title, BorderLayout.NORTH);

        table.setFillsViewportHeight(true);
        JScrollPane sp = new JScrollPane(table);
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private void addRow(JPanel p, GridBagConstraints c, int row, String label, JComponent f){
        c.gridx=0;c.gridy=row;c.weightx=0; p.add(new JLabel(label),c);
        c.gridx=1;c.weightx=1; if(f instanceof JTextField tf) tf.setColumns(24); p.add(f,c);
    }

    // ---------- Navigation ----------
    private void onBack(){
        String current = currentCard();
        if(CARD_COURSE.equals(current)){
            cards.show(cardPanel, CARD_PERSONAL);
            backBtn.setEnabled(false);
            nextBtn.setText("Next");
        } else if(CARD_TABLE.equals(current)){
            cards.show(cardPanel, CARD_COURSE);
            backBtn.setEnabled(true);
            nextBtn.setText("Next");
        }
        status.setText(" ");
    }

    private void onNext(){
        String current = currentCard();
        if(CARD_PERSONAL.equals(current)){
            if(!validatePersonalForm()) return;

            String sid = studentIdField.getText().trim();
            if(!store.unique(sid)){ setError("Student ID already exists."); return; }

            // Create/Update profile
            StudentProfile sp = store.getOrCreate(sid, sessionId);
            sp.name = nameField.getText().trim();
            sp.email = emailField.getText().trim();
            sp.phone = phoneField.getText().trim();

            JOptionPane.showMessageDialog(this,
                    "Personal saved & linked to session.\n\nSession: " + sp.sessionId +
                    "\nStudent ID: " + sp.studentId + "\nName: " + sp.name + "\nEmail: " + sp.email,
                    "Saved", JOptionPane.INFORMATION_MESSAGE);

            cards.show(cardPanel, CARD_COURSE);
            backBtn.setEnabled(true);
            nextBtn.setText("Next");
            status.setText("Select a course and level.");
        } else if(CARD_COURSE.equals(current)){
            if(courseCombo.getSelectedIndex()<=0){ setError("Please select a course."); return; }
            if(levelCombo.getSelectedIndex()<=0){ setError("Please select a level."); return; }

            String sid = studentIdField.getText().trim();
            StudentProfile sp = store.find(sid);
            sp.course = (String)courseCombo.getSelectedItem();
            sp.level  = (String)levelCombo.getSelectedItem();

            JOptionPane.showMessageDialog(this,
                    "Course/Level saved.\nCourse: " + sp.course + "\nLevel: " + sp.level,
                    "Saved", JOptionPane.INFORMATION_MESSAGE);

            // Load timetable
            loadTimetable(sp.course, sp.level);
            cards.show(cardPanel, CARD_TABLE);
            nextBtn.setText("Finish");
            status.setText("Review timetable. Click Finish to end.");
        } else if(CARD_TABLE.equals(current)){
            dispose(); // Finished
        }
    }

    private String currentCard(){
        for (Component comp : cardPanel.getComponents()) {
            if (comp.isVisible()) {
                if (comp == cardPanel.getComponent(0)) return CARD_PERSONAL;
                if (comp == cardPanel.getComponent(1)) return CARD_COURSE;
                if (comp == cardPanel.getComponent(2)) return CARD_TABLE;
            }
        }
        return CARD_PERSONAL;
    }

    // ---------- Validation & Helpers ----------
    private boolean validatePersonalForm(){
        String name = nameField.getText();
        String email = emailField.getText();
        String sid = studentIdField.getText();
        String phone = phoneField.getText();

        if(blank(name)){ setError("Name is required."); return false; }
        if(!EMAIL_RX.matcher(nz(email)).matches()){ setError("Valid email required (e.g., user@domain.com)."); return false; }
        if(blank(sid)){ setError("Student ID is required."); return false; }
        if(blank(phone)){ setError("Phone is required."); return false; }

        setOk("All good. You can proceed.");
        return true;
    }

    private void loadTimetable(String course, String level){
        java.util.List<SessionInfo> sessions = timetable.lookup(course, level);
        model.setRowCount(0);
        if(sessions.isEmpty()) model.addRow(new Object[]{"-","-","No sessions found","-","-"});
        else for(SessionInfo s : sessions) model.addRow(new Object[]{s.day,s.time,s.module,s.lecturer,s.room});
    }

    private boolean blank(String s){ return s==null || s.trim().isEmpty(); }
    private String nz(String s){ return s==null ? "" : s.trim(); }
    private void setOk(String m){ status.setForeground(new Color(0x2e7d32)); status.setText(m); }
    private void setError(String m){ status.setForeground(new Color(0xB00020)); status.setText(m); }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new Step4_CourseLevelTimetablePreview().setVisible(true));
    }
}
