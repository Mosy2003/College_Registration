package CourcesAndTimeTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;

public class Step4_TimetablePreview extends JFrame {
    static final class SessionInfo {
        final String day,time,module,lecturer,room;
        SessionInfo(String d,String t,String m,String l,String r){ day=d; time=t; module=m; lecturer=l; room=r; }
    }
    static final class TimetableService {
        private final Map<String, Map<String, java.util.List<SessionInfo>>> data = new HashMap<>();
        TimetableService(){
            String L5="Beginner (Level 5)", L6="Intermediate (Level 6)", L8="Advanced (Level 8)";
            put("Engineering", L5, Arrays.asList(
                    new SessionInfo("Mon","09:00-10:30","Statics","Dr. Patel","E201"),
                    new SessionInfo("Wed","11:00-12:30","Materials I","Prof. O'Neill","E105")));
            put("Engineering", L6, Arrays.asList(
                    new SessionInfo("Tue","10:00-12:00","Dynamics","Dr. Brown","E203"),
                    new SessionInfo("Thu","14:00-16:00","Fluid Mechanics","Dr. Li","E210")));
            put("Law", L5, Arrays.asList(
                    new SessionInfo("Mon","13:00-14:30","Intro to Law","Dr. Byrne","L101")));
        }
        private void put(String course, String level, java.util.List<SessionInfo> sessions){
            data.computeIfAbsent(course,k->new HashMap<>()).put(level, sessions);
        }
        java.util.List<SessionInfo> lookup(String course, String level){
            return data.getOrDefault(course, Collections.emptyMap())
                    .getOrDefault(level, Collections.emptyList());
        }
    }

    private final TimetableService timetable = new TimetableService();

    private final JComboBox<String> courseCombo = new JComboBox<>(new String[]{
            "-- Select a course --", "Engineering", "Law", "Computer Science", "Business"
    });
    private final JComboBox<String> levelCombo = new JComboBox<>(new String[]{
            "-- Select a level --", "Beginner (Level 5)", "Intermediate (Level 6)", "Advanced (Level 8)"
    });
    private final JButton previewBtn = new JButton("Preview Timetable");
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Day","Time","Module","Lecturer","Room"}, 0){
        @Override public boolean isCellEditable(int r,int c){return false;}
    };
    private final JTable table = new JTable(model);
    private final JLabel status = new JLabel(" ");

    public Step4_TimetablePreview(){
        super("Step 4 — Timetable Preview");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(740, 420);
        setLocationRelativeTo(null);

        JPanel top = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets=new Insets(6,6,6,6); c.fill=GridBagConstraints.HORIZONTAL; int row=0;
        addRow(top,c,row++,"Course *",courseCombo);
        addRow(top,c,row++,"Level *",levelCombo);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btns.add(previewBtn);
        c.gridx=0;c.gridy=row;c.gridwidth=2; top.add(btns,c);

        table.setFillsViewportHeight(true);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createTitledBorder("Prepared Timetable"));

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(sp, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);

        previewBtn.addActionListener(e -> onPreview());
    }

    private void onPreview(){
        if(courseCombo.getSelectedIndex()<=0 || levelCombo.getSelectedIndex()<=0){
            status.setForeground(new Color(0xB00020)); status.setText("Select both course and level."); return;
        }
        String course=(String)courseCombo.getSelectedItem();
        String level=(String)levelCombo.getSelectedItem();
        java.util.List<SessionInfo> sessions = timetable.lookup(course, level);
        model.setRowCount(0);
        if(sessions.isEmpty()) model.addRow(new Object[]{"-","-","No sessions","-","-"});
        else for(SessionInfo s: sessions) model.addRow(new Object[]{s.day,s.time,s.module,s.lecturer,s.room});
        status.setForeground(new Color(0x2e7d32)); status.setText("Previewing " + course + " — " + level);
    }

    private void addRow(JPanel p, GridBagConstraints c, int row, String label, JComponent field){
        c.gridx=0;c.gridy=row;c.weightx=0;p.add(new JLabel(label),c);
        c.gridx=1;c.weightx=1;if(field instanceof JTextField tf) tf.setColumns(18);p.add(field,c);
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new Step4_TimetablePreview().setVisible(true));
    }
}
