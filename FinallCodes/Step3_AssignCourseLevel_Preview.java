package FinallCodes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Step3_AssignCourseLevel_Preview extends JFrame {
    // ===== Domain + Store (re-usable from Step 2, simplified here) =====
    static final class Student {
        final String studentId; final UUID sessionId; final LocalDateTime createdAt = LocalDateTime.now();
        String name, email, phone, address, gender, town; LocalDate dob;
        String course, level;
        Student(String sid, UUID sess){ studentId=sid; sessionId=sess; }
    }
    static final class Store {
        private final Map<String, Student> byId = new ConcurrentHashMap<>();
        synchronized boolean exists(String sid){ return sid!=null && byId.containsKey(sid.trim()); }
        synchronized Student get(String sid){ return byId.get(sid.trim()); }
        synchronized Student getOrCreate(String sid, UUID sess){ return byId.computeIfAbsent(sid.trim(), k -> new Student(k, sess)); }
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
        }
        private void put(String c, String l, java.util.List<SessionInfo> s){ data.computeIfAbsent(c,k->new HashMap<>()).put(l,s); }
        java.util.List<SessionInfo> lookup(String c, String l){ return data.getOrDefault(c,Collections.emptyMap()).getOrDefault(l,Collections.emptyList()); }
    }

    private final Store store = new Store();
    private final TimetableService timetable = new TimetableService();
    private final UUID sessionId = UUID.randomUUID();

    private final JTextField studentIdField = new JTextField();
    private final JComboBox<String> courseCombo = new JComboBox<>(new String[]{
            "-- Select a course --", "Engineering", "Law", "Computer Science", "Business"
    });
    private final JComboBox<String> levelCombo = new JComboBox<>(new String[]{
            "-- Select a level --", "Beginner (Level 5)", "Intermediate (Level 6)", "Advanced (Level 8)"
    });
    private final JButton linkBtn = new JButton("Link & Preview");
    private final JLabel status = new JLabel(" ");

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Day","Time","Module","Lecturer","Room"}, 0){
        @Override public boolean isCellEditable(int r,int c){return false;}
    };
    private final JTable table = new JTable(model);

    public Step3_AssignCourseLevel_Preview(){
        super("Step 3 — Assign Course & Level + Timetable Preview");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(780, 480);
        setLocationRelativeTo(null);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets=new Insets(6,6,6,6); c.fill=GridBagConstraints.HORIZONTAL; int row=0;
        addRow(form,c,row++,"Student ID *",studentIdField);
        addRow(form,c,row++,"Course *",courseCombo);
        addRow(form,c,row++,"Level *",levelCombo);
        c.gridx=0;c.gridy=row;c.gridwidth=2; form.add(linkBtn, c);

        table.setFillsViewportHeight(true);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createTitledBorder("Prepared Timetable"));

        setLayout(new BorderLayout());
        add(form, BorderLayout.NORTH);
        add(sp, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);

        linkBtn.addActionListener(e -> onLink());
    }

    private void onLink(){
        String sid = t(studentIdField);
        if(sid.isEmpty()){ set(false,"Enter Student ID."); return; }
        if(courseCombo.getSelectedIndex()<=0){ set(false,"Select a course."); return; }
        if(levelCombo.getSelectedIndex()<=0){ set(false,"Select a level."); return; }

        Student s = store.getOrCreate(sid, sessionId); // demo: create if not present
        s.course = (String) courseCombo.getSelectedItem();
        s.level  = (String) levelCombo.getSelectedItem();

        java.util.List<SessionInfo> sessions = timetable.lookup(s.course, s.level);
        model.setRowCount(0);
        if(sessions.isEmpty()) model.addRow(new Object[]{"-","-","No sessions found","-","-"});
        else for(SessionInfo si : sessions) model.addRow(new Object[]{si.day,si.time,si.module,si.lecturer,si.room});

        set(true,"Linked " + sid + " → " + s.course + " — " + s.level);
    }

    private String t(JTextField tf){ return tf.getText()==null ? "" : tf.getText().trim(); }
    private void addRow(JPanel p, GridBagConstraints c, int row, String label, JComponent f){
        c.gridx=0;c.gridy=row;c.weightx=0;p.add(new JLabel(label),c);
        c.gridx=1;c.weightx=1;if(f instanceof JTextField tf) tf.setColumns(20);p.add(f,c);
    }
    private void set(boolean ok, String m){ status.setForeground(ok?new Color(0x2e7d32):new Color(0xB00020)); status.setText(m); }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new Step3_AssignCourseLevel_Preview().setVisible(true));
    }
}
