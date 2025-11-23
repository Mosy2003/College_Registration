package CourcesAndTimeTable;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Step3_LinkToProfile extends JFrame {
    static final class StudentProfile {
        final String studentId;
        String course, level;
        StudentProfile(String id){ this.studentId=id; }
    }
    static final class StudentStore {
        private final Map<String, StudentProfile> byId = new ConcurrentHashMap<>();
        synchronized StudentProfile getOrCreate(String sid){
            return byId.computeIfAbsent(sid.trim(), StudentProfile::new);
        }
        synchronized StudentProfile find(String sid){ return byId.get(sid.trim()); }
    }

    private final StudentStore store = new StudentStore();

    private final JTextField studentIdField = new JTextField();
    private final JComboBox<String> courseCombo = new JComboBox<>(new String[]{
            "-- Select a course --", "Engineering", "Law", "Computer Science", "Business"
    });
    private final JComboBox<String> levelCombo = new JComboBox<>(new String[]{
            "-- Select a level --", "Beginner (Level 5)", "Intermediate (Level 6)", "Advanced (Level 8)"
    });
    private final JButton assignBtn = new JButton("Assign & Save");
    private final JLabel status = new JLabel(" ");

    public Step3_LinkToProfile(){
        super("Step 3 — Link Course/Level to Student Profile");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(560, 240);
        setLocationRelativeTo(null);

        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets=new Insets(6,6,6,6); c.fill=GridBagConstraints.HORIZONTAL; int row=0;
        addRow(p,c,row++,"Student ID *",studentIdField);
        addRow(p,c,row++,"Course *",courseCombo);
        addRow(p,c,row++,"Level *",levelCombo);

        JPanel south = new JPanel(new BorderLayout());
        status.setBorder(BorderFactory.createEmptyBorder(0,8,0,0));
        south.add(status, BorderLayout.CENTER);
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.add(assignBtn); south.add(btns, BorderLayout.EAST);

        setLayout(new BorderLayout());
        add(p,BorderLayout.CENTER);
        add(south,BorderLayout.SOUTH);

        assignBtn.addActionListener(e -> onAssign());
    }

    private void onAssign(){
        String sid = text(studentIdField);
        if(sid.isEmpty()){ error("Student ID is required."); return; }
        if(courseCombo.getSelectedIndex() <= 0){ error("Please select a course."); return; }
        if(levelCombo.getSelectedIndex() <= 0){ error("Please select a level."); return; }

        StudentProfile profile = store.getOrCreate(sid);
        profile.course = (String) courseCombo.getSelectedItem();
        profile.level  = (String) levelCombo.getSelectedItem();

        info("Saved: " + sid + " → " + profile.course + " / " + profile.level);
        JOptionPane.showMessageDialog(this,
                "Linked profile:\nSID=" + sid + "\nCourse=" + profile.course + "\nLevel=" + profile.level);
    }

    private String text(JTextField t){ return t.getText()==null ? "" : t.getText().trim(); }
    private void info(String m){ status.setForeground(new Color(0x2e7d32)); status.setText(m); }
    private void error(String m){ status.setForeground(new Color(0xB00020)); status.setText(m); }

    private void addRow(JPanel p, GridBagConstraints c, int row, String label, JComponent field){
        c.gridx=0;c.gridy=row;c.weightx=0;p.add(new JLabel(label),c);
        c.gridx=1;c.weightx=1;if(field instanceof JTextField tf) tf.setColumns(18);p.add(field,c);
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new Step3_LinkToProfile().setVisible(true));
    }
}
