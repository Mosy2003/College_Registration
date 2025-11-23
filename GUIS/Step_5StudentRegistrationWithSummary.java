package GUIS;

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

public class Step_5StudentRegistrationWithSummary extends JFrame {
    // ===== Domain + Store + Session =====
    static final class Student {
        final String studentId, name, email, phone, address, gender, town;
        final LocalDate dob;
        final UUID sessionId; final LocalDateTime createdAt = LocalDateTime.now();
        Student(String sid, UUID sess, String n, String e, String p, String addr, String g, LocalDate d, String t){
            studentId=sid; sessionId=sess; name=n; email=e; phone=p; address=addr; gender=g; dob=d; town=t;
        }
    }
    static final class Store {
        private final Map<String, Student> byId = new ConcurrentHashMap<>();
        synchronized boolean unique(String sid){ return sid!=null && !byId.containsKey(sid.trim()); }
        synchronized void save(Student s){
            if(s.studentId==null || s.studentId.isBlank()) throw new IllegalArgumentException("Student ID required.");
            if(!unique(s.studentId)) throw new IllegalArgumentException("Student ID already exists.");
            byId.put(s.studentId.trim(), s);
        }
    }
    private final Store store = new Store();
    private UUID sessionId = UUID.randomUUID();

    // ===== UI scaffolding =====
    private static final String CARD_FORM="form", CARD_SUMMARY="summary";
    private final CardLayout cards = new CardLayout();
    private final JPanel cardPanel = new JPanel(cards);
    private final JLabel sessionLabel = new JLabel();
    private final JLabel status = new JLabel(" ");
    private final JButton backBtn = new JButton("Back");
    private final JButton nextBtn = new JButton("Next");

    // Form
    private final JTextField nameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField studentIdField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JTextArea addressArea = new JTextArea(3,24);
    private final JRadioButton male=new JRadioButton("Male"), female=new JRadioButton("Female"), other=new JRadioButton("Other");
    private final ButtonGroup genderGroup = new ButtonGroup();
    private final JComboBox<String> day=new JComboBox<>(), month=new JComboBox<>(), year=new JComboBox<>();
    private final JTextField townField = new JTextField();

    // Summary
    private final DefaultTableModel model = new DefaultTableModel(new Object[][]{}, new String[]{"Field","Value"}){
        @Override public boolean isCellEditable(int r,int c){return false;}
    };
    private final JTable summaryTable = new JTable(model);

    private static final Pattern EMAIL_RX = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);

    public Step_5StudentRegistrationWithSummary(){
        super("Student Registration — Summary & Save");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(860, 560);
        setLocationRelativeTo(null);

        // Top bar
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT,8,8));
        top.add(new JLabel("Session:"));
        sessionLabel.setText(sessionId.toString());
        sessionLabel.setFont(sessionLabel.getFont().deriveFont(Font.BOLD));
        top.add(sessionLabel);

        // Cards
        cardPanel.add(buildFormPanel(), CARD_FORM);
        cardPanel.add(buildSummaryPanel(), CARD_SUMMARY);

        // Bottom bar
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

        backBtn.addActionListener(e -> { cards.show(cardPanel, CARD_FORM); backBtn.setEnabled(false); nextBtn.setText("Next"); status.setText(" "); });
        nextBtn.addActionListener(e -> onNext());

        cards.show(cardPanel, CARD_FORM);
    }

    private JPanel buildFormPanel(){
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets=new Insets(6,6,6,6); c.fill=GridBagConstraints.HORIZONTAL; int row=0;

        addRow(form,c,row++,"Name *",nameField);
        addRow(form,c,row++,"Email *",emailField);
        addRow(form,c,row++,"Student ID *",studentIdField);
        addRow(form,c,row++,"Phone *",phoneField);

        c.gridx=0;c.gridy=row; form.add(new JLabel("Address *"),c);
        c.gridx=1; addressArea.setLineWrap(true); addressArea.setWrapStyleWord(true);
        form.add(new JScrollPane(addressArea), c); row++;

        JPanel gp=new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
        genderGroup.add(male); gp.add(male); genderGroup.add(female); gp.add(female); genderGroup.add(other); gp.add(other);
        addRow(form,c,row++,"Gender *",gp);

        initDOB();
        JPanel dobp=new JPanel(new FlowLayout(FlowLayout.LEFT,6,0)); dobp.add(day);dobp.add(month);dobp.add(year);
        addRow(form,c,row++,"DOB *",dobp);

        addRow(form,c,row++,"Town *",townField);

        // Live validation
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

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        wrap.add(form, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel buildSummaryPanel(){
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        JLabel title = new JLabel("Summary — Saved to In-Memory Store", SwingConstants.LEFT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        p.add(title, BorderLayout.NORTH);
        p.add(new JScrollPane(summaryTable), BorderLayout.CENTER);
        return p;
    }

    private void addRow(JPanel p, GridBagConstraints c, int row, String label, JComponent f){
        c.gridx=0;c.gridy=row;c.weightx=0; p.add(new JLabel(label),c);
        c.gridx=1;c.weightx=1; if(f instanceof JTextField tf) tf.setColumns(22); p.add(f,c);
    }

    private void initDOB(){
        day.addItem("Day"); for(int d=1; d<=31; d++) day.addItem(String.format("%02d", d));
        month.addItem("Month"); for(int m=1; m<=12; m++) month.addItem(String.format("%02d", m));
        int cy=LocalDate.now().getYear();
        year.addItem("Year"); for(int y=cy-15; y>=cy-100; y--) year.addItem(String.valueOf(y));
    }

    private LocalDate getDOB(){
        int di=day.getSelectedIndex(), mi=month.getSelectedIndex(), yi=year.getSelectedIndex();
        if(di<=0 || mi<=0 || yi<=0) return null;
        int d=Integer.parseInt((String)day.getSelectedItem());
        int m=Integer.parseInt((String)month.getSelectedItem());
        int y=Integer.parseInt((String)year.getSelectedItem());
        try { return LocalDate.of(y,m,d); } catch(Exception e){ return null; }
    }

    private boolean req(String s){ return s!=null && !s.trim().isEmpty(); }
    private boolean emailOk(String s){ return req(s) && EMAIL_RX.matcher(s.trim()).matches(); }

    private void v(){
        if(!req(nameField.getText())) { set(false,"Name required"); return; }
        if(!emailOk(emailField.getText())) { set(false,"Valid email required"); return; }
        if(!req(studentIdField.getText())) { set(false,"Student ID required"); return; }
        if(!req(phoneField.getText())) { set(false,"Phone required"); return; }
        if(addressArea.getText().trim().isEmpty()) { set(false,"Address required"); return; }
        if(getGender()==null) { set(false,"Select gender"); return; }
        if(getDOB()==null) { set(false,"Select a valid DOB"); return; }
        if(!req(townField.getText())) { set(false,"Town required"); return; }
        set(true,"All good. You can proceed.");
    }

    private String getGender(){ if(male.isSelected()) return "Male"; if(female.isSelected()) return "Female"; if(other.isSelected()) return "Other"; return null; }
    private void set(boolean ok, String msg){ nextBtn.setEnabled(ok); status.setText(msg); }

    private void onNext(){
        if(nextBtn.getText().equals("Next")){
            // unique check + save
            if(!store.unique(studentIdField.getText().trim())) { JOptionPane.showMessageDialog(this,"Student ID already exists.","Error",JOptionPane.ERROR_MESSAGE); return; }
            Student s = new Student(
                    studentIdField.getText().trim(), sessionId,
                    nameField.getText().trim(), emailField.getText().trim(), phoneField.getText().trim(),
                    addressArea.getText().trim(), getGender(), getDOB(), townField.getText().trim()
            );
            try{
                store.save(s);
                // build summary
                model.setRowCount(0);
                add("Session", s.sessionId.toString());
                add("Student ID", s.studentId);
                add("Name", s.name); add("Email", s.email); add("Phone", s.phone);
                add("Address", s.address); add("Gender", s.gender); add("DOB", String.valueOf(s.dob)); add("Town", s.town);
                JOptionPane.showMessageDialog(this, "Saved & linked to session.\nProceed to summary.");
                cards.show(cardPanel, CARD_SUMMARY);
                backBtn.setEnabled(true);
                nextBtn.setText("Close");
                status.setText("Record stored in memory.");
            }catch(Exception ex){
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            dispose();
        }
    }

    private void add(String k, String v){ model.addRow(new Object[]{k,v}); }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new Step_5StudentRegistrationWithSummary().setVisible(true));
    }
}
