package FinallCodes;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class Step2_ExtendedPersonalValidationStore extends JFrame {
    // ===== Domain + Store =====
    static final class Student {
        final String studentId; final UUID sessionId; final LocalDateTime createdAt = LocalDateTime.now();
        final String name, email, phone, address, gender, town;
        final LocalDate dob;
        Student(String sid, UUID sess, String n, String e, String p, String addr, String g, LocalDate d, String t){
            studentId=sid; sessionId=sess; name=n; email=e; phone=p; address=addr; gender=g; dob=d; town=t;
        }
    }
    static final class Store {
        private final Map<String, Student> byId = new ConcurrentHashMap<>();
        synchronized boolean isUnique(String sid){ return sid!=null && !byId.containsKey(sid.trim()); }
        synchronized void save(Student s){
            if(s.studentId==null || s.studentId.isBlank()) throw new IllegalArgumentException("Student ID required.");
            if(!isUnique(s.studentId)) throw new IllegalArgumentException("Student ID already exists.");
            byId.put(s.studentId.trim(), s);
        }
    }

    private final Store store = new Store();
    private UUID sessionId = UUID.randomUUID();

    // ===== UI =====
    private final JTextField nameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField studentIdField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JTextArea addressArea = new JTextArea(3, 24);
    private final JRadioButton male = new JRadioButton("Male");
    private final JRadioButton female = new JRadioButton("Female");
    private final JRadioButton other = new JRadioButton("Other");
    private final ButtonGroup genderGroup = new ButtonGroup();
    private final JComboBox<String> day = new JComboBox<>();
    private final JComboBox<String> month = new JComboBox<>();
    private final JComboBox<String> year = new JComboBox<>();
    private final JTextField townField = new JTextField();
    private final JLabel sessionLabel = new JLabel();
    private final JLabel status = new JLabel(" ");
    private final JButton saveBtn = new JButton("Save");

    private static final Pattern EMAIL_RX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern PHONE_RX = Pattern.compile("^[+]?[(]?[0-9]{1,4}[)]?[-\\s./0-9]*$");

    public Step2_ExtendedPersonalValidationStore(){
        super("Step 2 — Validation + Session + Unique ID (Extended Fields)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(860, 560);
        setLocationRelativeTo(null);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT,8,8));
        top.add(new JLabel("Session: "));
        sessionLabel.setText(sessionId.toString());
        sessionLabel.setFont(sessionLabel.getFont().deriveFont(Font.BOLD));
        top.add(sessionLabel);

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

        JPanel gp = new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
        new ButtonGroup(){{
            add(male); add(female); add(other);
        }};
        gp.add(male); gp.add(female); gp.add(other);
        addRow(form,c,row++,"Gender *",gp);

        initDOB();
        JPanel dobp = new JPanel(new FlowLayout(FlowLayout.LEFT,6,0));
        dobp.add(day); dobp.add(month); dobp.add(year);
        addRow(form,c,row++,"DOB *",dobp);

        addRow(form,c,row++,"Town *",townField);

        JPanel south = new JPanel(new BorderLayout());
        status.setBorder(BorderFactory.createEmptyBorder(0,8,0,0));
        south.add(status, BorderLayout.CENTER);
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.add(saveBtn); south.add(btns, BorderLayout.EAST);

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        DocumentListener dl = new DocumentListener(){ public void insertUpdate(DocumentEvent e){v();}
            public void removeUpdate(DocumentEvent e){v();} public void changedUpdate(DocumentEvent e){v();}};
        nameField.getDocument().addDocumentListener(dl);
        emailField.getDocument().addDocumentListener(dl);
        studentIdField.getDocument().addDocumentListener(dl);
        phoneField.getDocument().addDocumentListener(dl);
        addressArea.getDocument().addDocumentListener(dl);
        townField.getDocument().addDocumentListener(dl);
        male.addActionListener(e->v()); female.addActionListener(e->v()); other.addActionListener(e->v());
        day.addActionListener(e->v()); month.addActionListener(e->v()); year.addActionListener(e->v());
        v();

        saveBtn.addActionListener(e -> {
            try {
                Student s = new Student(
                        studentIdField.getText().trim(), sessionId,
                        nameField.getText().trim(), emailField.getText().trim(), phoneField.getText().trim(),
                        addressArea.getText().trim(), gender(), dob(), townField.getText().trim()
                );
                store.save(s);
                JOptionPane.showMessageDialog(this,
                        "Saved & linked to session:\nSession=" + s.sessionId +
                                "\nStudent ID=" + s.studentId + "\nName=" + s.name + "\nEmail=" + s.email +
                                "\nAddress=" + s.address + "\nGender=" + s.gender + "\nDOB=" + s.dob + "\nTown=" + s.town);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void initDOB(){
        day.addItem("Day"); for(int d=1; d<=31; d++) day.addItem(String.format("%02d", d));
        month.addItem("Month"); for(int m=1; m<=12; m++) month.addItem(String.format("%02d", m));
        int cy = LocalDate.now().getYear();
        year.addItem("Year"); for(int y=cy-15; y>=cy-100; y--) year.addItem(String.valueOf(y));
    }
    private LocalDate dob(){
        if(day.getSelectedIndex()<=0 || month.getSelectedIndex()<=0 || year.getSelectedIndex()<=0) return null;
        try {
            int d=Integer.parseInt((String)day.getSelectedItem());
            int m=Integer.parseInt((String)month.getSelectedItem());
            int y=Integer.parseInt((String)year.getSelectedItem());
            return LocalDate.of(y,m,d);
        } catch(Exception ex){ return null; }
    }
    private String gender(){
        if(male.isSelected()) return "Male";
        if(female.isSelected()) return "Female";
        if(other.isSelected()) return "Other";
        return null;
    }

    private boolean req(String s){ return s!=null && !s.trim().isEmpty(); }
    private boolean emailOk(String s){ return req(s) && EMAIL_RX.matcher(s.trim()).matches(); }
    private boolean phoneOk(String s){ return req(s) && PHONE_RX.matcher(s.trim()).matches(); }

    private void v(){
        if(!req(nameField.getText())) { set(false,"Name required"); return; }
        if(!emailOk(emailField.getText())) { set(false,"Valid email required (user@domain.com)"); return; }
        if(!req(studentIdField.getText())) { set(false,"Student ID required"); return; }
        if(!store.isUnique(studentIdField.getText().trim())) { set(false,"Student ID already exists"); return; }
        if(!phoneOk(phoneField.getText())) { set(false,"Phone looks invalid"); return; }
        if(!req(addressArea.getText())) { set(false,"Address required"); return; }
        if(gender()==null) { set(false,"Select gender"); return; }
        if(dob()==null) { set(false,"Select a valid DOB"); return; }
        if(!req(townField.getText())) { set(false,"Town required"); return; }
        set(true,"All good. You can save.");
    }

    private void set(boolean ok, String msg){ saveBtn.setEnabled(ok); status.setForeground(ok?new Color(0x2e7d32):new Color(0xB00020)); status.setText(msg); }
    private void addRow(JPanel p, GridBagConstraints c, int row, String label, JComponent f){
        c.gridx=0;c.gridy=row;c.weightx=0; p.add(new JLabel(label),c);
        c.gridx=1;c.weightx=1; if(f instanceof JTextField tf) tf.setColumns(24); p.add(f,c);
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new Step2_ExtendedPersonalValidationStore().setVisible(true));
    }
}
