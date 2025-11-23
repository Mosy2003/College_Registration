package CodesCombines;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Step3_AddCourseLevel extends JFrame {
    // ===== Store (re-used idea) =====
    static final class StudentProfile {
        final String studentId;
        final UUID sessionId;
        String name, email, phone;
        String course, level;
        StudentProfile(String sid, UUID sess){ studentId=sid; sessionId=sess; }
    }
    static final class Store {
        private final Map<String, StudentProfile> byId = new ConcurrentHashMap<>();
        synchronized boolean exists(String sid){ return byId.containsKey(sid.trim()); }
        synchronized StudentProfile get(String sid){ return byId.get(sid.trim()); }
        synchronized void put(StudentProfile p){ byId.put(p.studentId.trim(), p); }
    }

    private final Store store = new Store();
    private final UUID sessionId = UUID.randomUUID();

    private final JTextField studentIdField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField phoneField = new JTextField();

    private final JComboBox<String> courseCombo = new JComboBox<>(new String[]{
            "-- Select a course --", "Engineering", "Law", "Computer Science", "Business"
    });
    private final JComboBox<String> levelCombo = new JComboBox<>(new String[]{
            "-- Select a level --", "Beginner (Level 5)", "Intermediate (Level 6)", "Advanced (Level 8)"
    });

    private final JButton savePersonalBtn = new JButton("Save Personal");
    private final JButton assignBtn = new JButton("Assign Course & Level");
    private final JLabel status = new JLabel(" ");

    public Step3_AddCourseLevel(){
        super("Step 3 — Link Course & Level to Student");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(720, 360);
        setLocationRelativeTo(null);

        JPanel left = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets=new Insets(6,6,6,6); c.fill=GridBagConstraints.HORIZONTAL; int row=0;

        addRow(left,c,row++,"Student ID *",studentIdField);
        addRow(left,c,row++,"Name *",nameField);
        addRow(left,c,row++,"Email *",emailField);
        addRow(left,c,row++,"Phone *",phoneField);

        JPanel right = new JPanel(new GridBagLayout());
        GridBagConstraints r = new GridBagConstraints();
        r.insets=new Insets(6,6,6,6); r.fill=GridBagConstraints.HORIZONTAL; int rr=0;

        addRow(right,r,rr++,"Course *",courseCombo);
        addRow(right,r,rr++,"Level *",levelCombo);
        r.gridx=0;r.gridy=rr;r.gridwidth=2; right.add(assignBtn, r);

        JPanel root = new JPanel(new GridLayout(1,2));
        root.add(left); root.add(right);

        setLayout(new BorderLayout());
        add(root, BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout());
        south.add(status, BorderLayout.CENTER);
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.add(savePersonalBtn);
        south.add(btns, BorderLayout.EAST);
        add(south, BorderLayout.SOUTH);

        savePersonalBtn.addActionListener(e -> onSavePersonal());
        assignBtn.addActionListener(e -> onAssign());
    }

    private void onSavePersonal(){
        String sid = t(studentIdField), n=t(nameField), e=t(emailField), p=t(phoneField);
        if(sid.isEmpty()||n.isEmpty()||e.isEmpty()||p.isEmpty()){ error("Fill Student ID, Name, Email, Phone"); return; }
        StudentProfile sp = new StudentProfile(sid, sessionId);
        sp.name=n; sp.email=e; sp.phone=p;
        store.put(sp);
        info("Personal saved for " + sid);
    }

    private void onAssign(){
        String sid=t(studentIdField);
        if(!store.exists(sid)){ error("Save personal info first."); return; }
        if(courseCombo.getSelectedIndex()<=0){ error("Select a course."); return; }
        if(levelCombo.getSelectedIndex()<=0){ error("Select a level."); return; }

        StudentProfile sp = store.get(sid);
        sp.course=(String)courseCombo.getSelectedItem();
        sp.level=(String)levelCombo.getSelectedItem();
        info("Assigned " + sp.course + " — " + sp.level + " to " + sid);
        JOptionPane.showMessageDialog(this,"Linked:\nSID="+sid+"\nCourse="+sp.course+"\nLevel="+sp.level);
    }

    private String t(JTextField tf){ return tf.getText()==null ? "" : tf.getText().trim(); }
    private void addRow(JPanel p, GridBagConstraints c, int row, String label, JComponent f){
        c.gridx=0;c.gridy=row;c.weightx=0; p.add(new JLabel(label),c);
        c.gridx=1;c.weightx=1; if(f instanceof JTextField tf) tf.setColumns(22); p.add(f,c);
    }
    private void info(String m){ status.setForeground(new Color(0x2e7d32)); status.setText(m); }
    private void error(String m){ status.setForeground(new Color(0xB00020)); status.setText(m); }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new Step3_AddCourseLevel().setVisible(true));
    }
}
