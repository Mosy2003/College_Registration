package CourcesAndTimeTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Step5_FullCourseLevelAssignment extends JFrame {
    // ===== Domain + Store =====
    static final class StudentProfile {
        final String studentId;
        String course, level;
        StudentProfile(String sid){ this.studentId = sid; }
    }
    static final class StudentStore {
        private final Map<String, StudentProfile> byId = new ConcurrentHashMap<>();
        synchronized StudentProfile getOrCreate(String sid){ return byId.computeIfAbsent(sid.trim(), StudentProfile::new); }
        synchronized StudentProfile find(String sid){ return byId.get(sid.trim()); }
    }

    // ===== Timetable =====
    static final class SessionInfo {
        final String day,time,module,lecturer,room;
        SessionInfo(String d,String t,String m,String l,String r){ day=d; time=t; module=m; lecturer=l; room=r; }
    }
    static final class TimetableService {
        private final Map<String, Map<String, List<SessionInfo>>> data = new HashMap<>();
        TimetableService(){
            String L5="Beginner (Level 5)", L6="Intermediate (Level 6)", L8="Advanced (Level 8)";
            put("Engineering", L5, List.of(
                    new SessionInfo("Mon","09:00 - 10:30","Statics","Dr. Patel","E201"),
                    new SessionInfo("Wed","11:00 - 12:30","Materials I","Prof. O'Neill","E105")
            ));
            put("Engineering", L6, List.of(
                    new SessionInfo("Tue","10:00 - 12:00","Dynamics","Dr. Brown","E203"),
                    new SessionInfo("Thu","14:00 - 16:00","Fluid Mechanics","Dr. Li","E210")
            ));
            put("Engineering", L8, List.of(
                    new SessionInfo("Tue","09:00 - 10:30","Thermodynamics","Dr. Li","E220"),
                    new SessionInfo("Fri","13:00 - 15:00","Capstone Studio","Panel","E401")
            ));
            put("Law", L5, List.of(
                    new SessionInfo("Mon","13:00 - 14:30","Intro to Law","Dr. Byrne","L101"),
                    new SessionInfo("Thu","10:00 - 11:30","Legal Writing","Dr. Nolan","L204")
            ));
            put("Law", L6, List.of(
                    new SessionInfo("Tue","12:00 - 13:30","Tort Law","Dr. Murphy","L210"),
                    new SessionInfo("Fri","09:00 - 10:30","Contract Law II","Dr. Kelly","L305")
            ));
            put("Law", L8, List.of(
                    new SessionInfo("Wed","15:00 - 16:30","EU Law","Dr. Kavanagh","L402"),
                    new SessionInfo("Fri","11:00 - 13:00","Moot Court","Panel","Courtroom A")
            ));
            put("Computer Science", L5, List.of(
                    new SessionInfo("Mon","09:00 - 10:30","Programming I","Ms. Daly","C101"),
                    new SessionInfo("Wed","14:00 - 16:00","Web Dev I","Mr. Shah","Lab C2")
            ));
            put("Computer Science", L6, List.of(
                    new SessionInfo("Tue","10:00 - 12:00","Data Structures","Dr. Grace","C220"),
                    new SessionInfo("Thu","13:00 - 14:30","Databases","Dr. Ahmed","C118")
            ));
            put("Computer Science", L8, List.of(
                    new SessionInfo("Wed","09:00 - 11:00","Distributed Systems","Dr. Grace","C305"),
                    new SessionInfo("Fri","11:15 - 12:45","AI & ML","Dr. Quinn","C410")
            ));
            put("Business", L5, List.of(
                    new SessionInfo("Mon","11:00 - 12:30","Intro to Business","Ms. Ryan","B101"),
                    new SessionInfo("Thu","09:00 - 10:30","Accounting I","Mr. Connolly","B204")
            ));
            put("Business", L6, List.of(
                    new SessionInfo("Tue","14:00 - 15:30","Marketing","Dr. Walsh","B210"),
                    new SessionInfo("Fri","10:00 - 12:00","Operations","Dr. Singh","B305")
            ));
            put("Business", L8, List.of(
                    new SessionInfo("Wed","13:00 - 14:30","Strategy","Dr. Walsh","B402"),
                    new SessionInfo("Thu","15:00 - 17:00","Entrepreneurship","Panel","Incubator 1")
            ));
        }
        private void put(String course, String level, List<SessionInfo> sessions){
            data.computeIfAbsent(course,k->new HashMap<>()).put(level, sessions);
        }
        List<SessionInfo> lookup(String course, String level){
            return data.getOrDefault(course, Collections.emptyMap())
                    .getOrDefault(level, Collections.emptyList());
        }
    }

    // ===== UI =====
    private final StudentStore store = new StudentStore();
    private final TimetableService timetable = new TimetableService();

    private final JTextField studentIdField = new JTextField();
    private final JComboBox<String> courseCombo = new JComboBox<>(new String[]{
            "-- Select a course --", "Engineering", "Law", "Computer Science", "Business"
    });
    private final JComboBox<String> levelCombo = new JComboBox<>(new String[]{
            "-- Select a level --", "Beginner (Level 5)", "Intermediate (Level 6)", "Advanced (Level 8)"
    });
    private final JButton assignBtn = new JButton("Assign & Save");
    private final JButton previewBtn = new JButton("Preview Timetable");
    private final JLabel statusLabel = new JLabel(" ");

    private final DefaultTableModel timetableModel = new DefaultTableModel(
            new Object[]{"Day","Time","Module","Lecturer","Room"}, 0){
        @Override public boolean isCellEditable(int r,int c){return false;}
    };
    private final JTable timetableTable = new JTable(timetableModel);

    public Step5_FullCourseLevelAssignment(){
        super("Step 5 — Full Course & Level Assignment");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 520);
        setLocationRelativeTo(null);

        add(buildForm(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildBottom(), BorderLayout.SOUTH);

        assignBtn.addActionListener(e -> onAssign());
        previewBtn.addActionListener(e -> onPreview());
    }

    private JPanel buildForm(){
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(12,12,6,12));
        GridBagConstraints c = new GridBagConstraints();
        c.insets=new Insets(6,6,6,6); c.fill=GridBagConstraints.HORIZONTAL; int row=0;

        addRow(p,c,row++,"Student ID *", studentIdField);
        addRow(p,c,row++,"Course *", courseCombo);
        addRow(p,c,row++,"Level *", levelCombo);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));
        btns.add(assignBtn); btns.add(previewBtn);
        c.gridx=0;c.gridy=row;c.gridwidth=2;c.weightx=1; p.add(btns,c);

        return p;
    }

    private JScrollPane buildCenter(){
        timetableTable.setFillsViewportHeight(true);
        JScrollPane sp = new JScrollPane(timetableTable);
        sp.setBorder(BorderFactory.createTitledBorder("Prepared Timetable"));
        return sp;
    }

    private JPanel buildBottom(){
        JPanel p = new JPanel(new BorderLayout());
        statusLabel.setForeground(new Color(0x444444));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0,12,12,12));
        p.add(statusLabel, BorderLayout.CENTER);
        return p;
    }

    private void onAssign(){
        String sid = text(studentIdField);
        if(sid.isEmpty()){ error("Student ID is required."); studentIdField.requestFocus(); return; }
        if(courseCombo.getSelectedIndex()<=0){ error("Please select a course."); courseCombo.requestFocus(); return; }
        if(levelCombo.getSelectedIndex()<=0){ error("Please select a level."); levelCombo.requestFocus(); return; }

        String course=(String)courseCombo.getSelectedItem();
        String level=(String)levelCombo.getSelectedItem();

        StudentProfile profile = store.getOrCreate(sid);
        profile.course = course; profile.level = level;

        List<SessionInfo> sessions = timetable.lookup(course, level);
        loadTable(sessions);

        info("Saved. " + sid + " → " + course + " — " + level + ". Timetable prepared.");
        JOptionPane.showMessageDialog(this,
                "Assignment Complete\n\nStudent ID: " + sid +
                "\nCourse: " + course + "\nLevel: " + level +
                "\nPrepared " + sessions.size() + " timetable item(s).",
                "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onPreview(){
        if(courseCombo.getSelectedIndex()<=0 || levelCombo.getSelectedIndex()<=0){
            error("Select both a course and a level to preview."); return;
        }
        String course=(String)courseCombo.getSelectedItem();
        String level=(String)levelCombo.getSelectedItem();
        loadTable(timetable.lookup(course, level));
        info("Previewing timetable for " + course + " — " + level + ".");
    }

    private void loadTable(List<SessionInfo> sessions){
        timetableModel.setRowCount(0);
        if(sessions==null || sessions.isEmpty()){
            timetableModel.addRow(new Object[]{"-","-","No sessions found","-","-"});
            return;
        }
        for(SessionInfo s : sessions){
            timetableModel.addRow(new Object[]{s.day,s.time,s.module,s.lecturer,s.room});
        }
    }

    private String text(JTextField t){ return t.getText()==null ? "" : t.getText().trim(); }
    private void info(String m){ statusLabel.setForeground(new Color(0x2e7d32)); statusLabel.setText(m); }
    private void error(String m){ statusLabel.setForeground(new Color(0xB00020)); statusLabel.setText(m); }

    private void addRow(JPanel p, GridBagConstraints c, int row, String label, JComponent field){
        c.gridx=0;c.gridy=row;c.weightx=0; p.add(new JLabel(label),c);
        c.gridx=1;c.weightx=1; if(field instanceof JTextField tf) tf.setColumns(18); p.add(field,c);
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new Step5_FullCourseLevelAssignment().setVisible(true));
    }
}
