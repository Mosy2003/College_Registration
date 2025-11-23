package FinallCodes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class CollegeRegistration extends JFrame {
    // ===== Domain + Store =====
    static final class Student {
        final String studentId; final UUID sessionId; final LocalDateTime createdAt = LocalDateTime.now();
        String name, email, phone, address, gender, town; LocalDate dob;
        String course, level;
        Student(String sid, UUID sess){ studentId=sid; sessionId=sess; }
    }
    static final class Store {
        private final Map<String, Student> byId = new ConcurrentHashMap<>();
        synchronized boolean unique(String sid){ return sid!=null && !byId.containsKey(sid.trim()); }
        synchronized Student getOrCreate(String sid, UUID sess){ return byId.computeIfAbsent(sid.trim(), k -> new Student(k, sess)); }
        synchronized Student find(String sid){ return byId.get(sid.trim()); }
    }

    // ===== Timetable =====
    static final class SessionInfo { final String day,time,module,lecturer,room;
        SessionInfo(String d,String t,String m,String l,String r){ day=d;time=t;module=m;lecturer=l;room=r; } }

    static final class TimetableService {
        private final Map<String, Map<String, java.util.List<SessionInfo>>> data = new HashMap<>();
        TimetableService(){
            String L5="Beginner (Level 5)", L6="Intermediate (Level 6)", L8="Advanced (Level 8)";

            // Engineering
            put("Engineering", L5, Arrays.asList(
                    new SessionInfo("Monday","09:00 - 10:30","Statics","Dr. Patel","E201"),
                    new SessionInfo("Wednesday","11:00 - 12:30","Materials I","Prof. O'Neill","E105")));
            put("Engineering", L6, Arrays.asList(
                    new SessionInfo("Tuesday","10:00 - 12:00","Dynamics","Dr. Brown","E203"),
                    new SessionInfo("Thursday","14:00 - 16:00","Fluid Mechanics","Dr. Li","E210")));
            put("Engineering", L8, Arrays.asList(
                    new SessionInfo("Tuesday","09:00 - 10:30","Thermodynamics","Dr. Li","E220"),
                    new SessionInfo("Friday","13:00 - 15:00","Capstone Studio","Panel","E401")));

            // Law
            put("Law", L5, Arrays.asList(
                    new SessionInfo("Monday","13:00 - 14:30","Intro to Law","Dr. Byrne","L101"),
                    new SessionInfo("Thursday","10:00 - 11:30","Legal Writing","Dr. Nolan","L204")));
            put("Law", L6, Arrays.asList(
                    new SessionInfo("Tuesday","12:00 - 13:30","Tort Law","Dr. Murphy","L210"),
                    new SessionInfo("Friday","09:00 - 10:30","Contract Law II","Dr. Kelly","L305")));
            put("Law", L8, Arrays.asList(
                    new SessionInfo("Wednesday","15:00 - 16:30","EU Law","Dr. Kavanagh","L402"),
                    new SessionInfo("Friday","11:00 - 13:00","Moot Court","Panel","Courtroom A")));

            // Computer Science
            put("Computer Science", L5, Arrays.asList(
                    new SessionInfo("Monday","09:00 - 10:30","Programming I","Ms. Daly","C101"),
                    new SessionInfo("Wednesday","14:00 - 16:00","Web Dev I","Mr. Shah","Lab C2")));
            put("Computer Science", L6, Arrays.asList(
                    new SessionInfo("Tuesday","10:00 - 12:00","Data Structures","Dr. Grace","C220"),
                    new SessionInfo("Thursday","13:00 - 14:30","Databases","Dr. Ahmed","C118")));
            put("Computer Science", L8, Arrays.asList(
                    new SessionInfo("Wednesday","09:00 - 11:00","Distributed Systems","Dr. Grace","C305"),
                    new SessionInfo("Friday","11:15 - 12:45","AI & ML","Dr. Quinn","C410")));

            // Business
            put("Business", L5, Arrays.asList(
                    new SessionInfo("Monday","11:00 - 12:30","Intro to Business","Ms. Ryan","B101"),
                    new SessionInfo("Thursday","09:00 - 10:30","Accounting I","Mr. Connolly","B204")));
            put("Business", L6, Arrays.asList(
                    new SessionInfo("Tuesday","14:00 - 15:30","Marketing","Dr. Walsh","B210"),
                    new SessionInfo("Friday","10:00 - 12:00","Operations","Dr. Singh","B305")));
            put("Business", L8, Arrays.asList(
                    new SessionInfo("Wednesday","13:00 - 14:30","Strategy","Dr. Walsh","B402"),
                    new SessionInfo("Thursday","15:00 - 17:00","Entrepreneurship","Panel","Incubator 1")));
        }
        private void put(String c,String l,java.util.List<SessionInfo> s){ data.computeIfAbsent(c,k->new HashMap<>()).put(l,s); }
        java.util.List<SessionInfo> lookup(String c,String l){ return data.getOrDefault(c,Collections.emptyMap()).getOrDefault(l,Collections.emptyList()); }
    }

    // ===== App State =====
    private final Store store = new Store();
    private final TimetableService timetable = new TimetableService();
    private UUID sessionId = UUID.randomUUID();

    // ===== Wizard scaffolding =====
    private static final String CARD_PERSONAL="personal", CARD_COURSE="course", CARD_TABLE="table";
    private final CardLayout cards = new CardLayout();
    private final JPanel cardPanel = new JPanel(cards);
    private String currentCardKey = CARD_PERSONAL;

    private final JLabel sessionLabel = new JLabel();
    private final JLabel status = new JLabel(" ");
    private final JButton backBtn = new JButton("Back");
    private final JButton nextBtn = new JButton("Next");

    // ===== Personal =====
    private final JTextField nameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField studentIdField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JTextArea addressArea = new JTextArea(3,24);
    private final JRadioButton male=new JRadioButton("Male"), female=new JRadioButton("Female"), other=new JRadioButton("Other");
    private final ButtonGroup genderGroup = new ButtonGroup();
    private final JComboBox<String> day=new JComboBox<>(), month=new JComboBox<>(), year=new JComboBox<>();
    private final JTextField townField = new JTextField();

    private static final Pattern EMAIL_RX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PHONE_RX = Pattern.compile("^[+]?[(]?[0-9]{1,4}[)]?[-\\s./0-9]*$");

    // ===== Course =====
    private final JComboBox<String> courseCombo = new JComboBox<>(new String[]{
            "-- Select a course --", "Engineering", "Law", "Computer Science", "Business"
    });
    private final JComboBox<String> levelCombo = new JComboBox<>(new String[]{
            "-- Select a level --", "Beginner (Level 5)", "Intermediate (Level 6)", "Advanced (Level 8)"
    });
    private final JButton previewBtn = new JButton("Preview Timetable");

    // ===== Table =====
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Day","Time","Module","Lecturer","Room"}, 0){ @Override public boolean isCellEditable(int r,int c){ return false; } };
    private final JTable table = new JTable(model);

    public CollegeRegistration(){
        super("Full Combined Wizard (Fixed Timetable)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(920, 620);
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

        showCard(CARD_PERSONAL);
        validatePersonalForm(); // initial
    }

    private void showCard(String key){
        cards.show(cardPanel, key);
        currentCardKey = key;
    }

    private JPanel buildPersonalPanel(){
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets=new Insets(6,6,6,6); c.fill=GridBagConstraints.HORIZONTAL; int row=0;

        addRow(form,c,row++,"Name *",nameField);
        addRow(form,c,row++,"Email *",emailField);
        addRow(form,c,row++,"Student ID *",studentIdField);
        addRow(form,c,row++,"Phone *",phoneField);

        c.gridx=0;c.gridy=row; form.add(new JLabel("Address *"), c);
        c.gridx=1; addressArea.setLineWrap(true); addressArea.setWrapStyleWord(true);
        form.add(new JScrollPane(addressArea), c); row++;

        JPanel gp=new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
        genderGroup.add(male); genderGroup.add(female); genderGroup.add(other);
        gp.add(male); gp.add(female); gp.add(other);
        addRow(form,c,row++,"Gender *",gp);

        initDOB();
        JPanel dobp=new JPanel(new FlowLayout(FlowLayout.LEFT,6,0)); dobp.add(day);dobp.add(month);dobp.add(year);
        addRow(form,c,row++,"DOB *",dobp);

        addRow(form,c,row++,"Town *",townField);

        DocumentListener dl = new DocumentListener(){ public void insertUpdate(DocumentEvent e){validatePersonalForm();}
            public void removeUpdate(DocumentEvent e){validatePersonalForm();} public void changedUpdate(DocumentEvent e){validatePersonalForm();}};
        nameField.getDocument().addDocumentListener(dl);
        emailField.getDocument().addDocumentListener(dl);
        studentIdField.getDocument().addDocumentListener(dl);
        phoneField.getDocument().addDocumentListener(dl);
        addressArea.getDocument().addDocumentListener(dl);
        townField.getDocument().addDocumentListener(dl);
        male.addActionListener(e->validatePersonalForm());
        female.addActionListener(e->validatePersonalForm());
        other.addActionListener(e->validatePersonalForm());
        day.addActionListener(e->validatePersonalForm());
        month.addActionListener(e->validatePersonalForm());
        year.addActionListener(e->validatePersonalForm());

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

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(previewBtn);
        c.gridx=0; c.gridy=row; c.gridwidth=2;
        form.add(buttons, c);

        previewBtn.addActionListener(e -> {
            if(courseCombo.getSelectedIndex()<=0 || levelCombo.getSelectedIndex()<=0){
                setError("Select course and level first.");
                return;
            }
            loadTable((String)courseCombo.getSelectedItem(), (String)levelCombo.getSelectedItem());
            setOk("Previewing timetable.");
            showCard(CARD_TABLE);
            backBtn.setEnabled(true);
            nextBtn.setText("Finish");
        });

        JPanel wrap=new JPanel(new BorderLayout());
        wrap.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        wrap.add(form,BorderLayout.CENTER);
        return wrap;
    }

    private JPanel buildTablePanel(){
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        JLabel title = new JLabel("Prepared Timetable", SwingConstants.LEFT);
        title.setFont(title.getFont().deriveFont(Font.BOLD,16f));
        p.add(title, BorderLayout.NORTH);
        table.setFillsViewportHeight(true);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private void addRow(JPanel p, GridBagConstraints c, int row, String label, JComponent f){
        c.gridx=0;c.gridy=row;c.weightx=0; p.add(new JLabel(label),c);
        c.gridx=1;c.weightx=1; if(f instanceof JTextField tf) tf.setColumns(24); p.add(f,c);
    }

    // ---------- Navigation ----------
    private void onBack(){
        if (CARD_COURSE.equals(currentCardKey)) {
            showCard(CARD_PERSONAL);
            backBtn.setEnabled(false);
            nextBtn.setText("Next");
        } else if (CARD_TABLE.equals(currentCardKey)) {
            showCard(CARD_COURSE);
            backBtn.setEnabled(true);
            nextBtn.setText("Next");
        }
        status.setText(" ");
    }

    private void onNext(){
        if (CARD_PERSONAL.equals(currentCardKey)) {
            if(!validatePersonalForm()) return;
            String sid = studentIdField.getText().trim();
            if(!store.unique(sid)){ setError("Student ID already exists."); return; }

            Student s = store.getOrCreate(sid, sessionId);
            s.name = nameField.getText().trim();
            s.email = emailField.getText().trim();
            s.phone = phoneField.getText().trim();
            s.address = addressArea.getText().trim();
            s.gender = gender();
            s.dob = dob();
            s.town = townField.getText().trim();

            JOptionPane.showMessageDialog(this,
                    "Personal saved.\n\nSession: "+s.sessionId+"\nStudent ID: "+s.studentId+
                            "\nName: "+s.name+"\nEmail: "+s.email+"\nAddress: "+s.address+
                            "\nGender: "+s.gender+"\nDOB: "+s.dob+"\nTown: "+s.town,
                    "Saved", JOptionPane.INFORMATION_MESSAGE);

            showCard(CARD_COURSE);
            backBtn.setEnabled(true);
            nextBtn.setText("Next");
            status.setText("Select course and level.");
        } else if (CARD_COURSE.equals(currentCardKey)) {
            if(courseCombo.getSelectedIndex()<=0){ setError("Please select a course."); return; }
            if(levelCombo.getSelectedIndex()<=0){ setError("Please select a level."); return; }

            String sid = studentIdField.getText().trim();
            Student s = store.find(sid);
            s.course = (String)courseCombo.getSelectedItem();
            s.level  = (String)levelCombo.getSelectedItem();

            JOptionPane.showMessageDialog(this,
                    "Course/Level saved.\nCourse: "+s.course+"\nLevel: "+s.level,
                    "Saved", JOptionPane.INFORMATION_MESSAGE);

            loadTable(s.course, s.level);
            showCard(CARD_TABLE);
            nextBtn.setText("Finish");
            status.setText("Review timetable. Click Finish to end.");
        } else if (CARD_TABLE.equals(currentCardKey)) {
            dispose();
        }
    }

    // ---------- Validation & helpers ----------
    private void initDOB(){
        day.addItem("Day"); for(int d=1; d<=31; d++) day.addItem(String.format("%02d", d));
        month.addItem("Month"); for(int m=1; m<=12; m++) month.addItem(String.format("%02d", m));
        int cy=LocalDate.now().getYear();
        year.addItem("Year"); for(int y=cy-15; y>=cy-100; y--) year.addItem(String.valueOf(y));
    }
    private LocalDate dob(){
        if(day.getSelectedIndex()<=0 || month.getSelectedIndex()<=0 || year.getSelectedIndex()<=0) return null;
        try{
            int d=Integer.parseInt((String)day.getSelectedItem());
            int m=Integer.parseInt((String)month.getSelectedItem());
            int y=Integer.parseInt((String)year.getSelectedItem());
            return LocalDate.of(y,m,d);
        }catch(Exception ex){ return null; }
    }
    private String gender(){ if(male.isSelected()) return "Male"; if(female.isSelected()) return "Female"; if(other.isSelected()) return "Other"; return null; }

    private boolean req(String s){ return s!=null && !s.trim().isEmpty(); }
    private boolean emailOk(String s){ return req(s) && EMAIL_RX.matcher(s.trim()).matches(); }
    private boolean phoneOk(String s){ return req(s) && PHONE_RX.matcher(s.trim()).matches(); }

    private boolean validatePersonalForm(){
        if(!req(nameField.getText())){ setError("Name is required."); return false; }
        if(!emailOk(emailField.getText())){ setError("Valid email required (e.g., user@domain.com)."); return false; }
        if(!req(studentIdField.getText())){ setError("Student ID is required."); return false; }
        if(!phoneOk(phoneField.getText())){ setError("Phone looks invalid."); return false; }
        if(!req(addressArea.getText())){ setError("Address is required."); return false; }
        if(gender()==null){ setError("Please select a gender."); return false; }
        if(dob()==null){ setError("Please select a valid DOB."); return false; }
        if(!req(townField.getText())){ setError("Town is required."); return false; }
        setOk("All good. You can proceed.");
        return true;
    }

    private void loadTable(String course, String level){
        java.util.List<SessionInfo> sessions = timetable.lookup(course, level);
        model.setRowCount(0);
        if(sessions.isEmpty()) {
            model.addRow(new Object[]{"-","-","No sessions found for "+course+" — "+level,"-","-"});
        } else {
            for(SessionInfo s : sessions) {
                model.addRow(new Object[]{s.day,s.time,s.module,s.lecturer,s.room});
            }
        }
    }

    private void setOk(String m){ status.setForeground(new Color(0x2e7d32)); status.setText(m); }
    private void setError(String m){ status.setForeground(new Color(0xB00020)); status.setText(m); }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new CollegeRegistration().setVisible(true));
    }
}
