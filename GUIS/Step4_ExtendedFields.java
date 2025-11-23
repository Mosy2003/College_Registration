package GUIS;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class Step4_ExtendedFields extends JFrame {
    private final JTextField nameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField studentIdField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JTextArea addressArea = new JTextArea(3,24);
    private final JRadioButton male = new JRadioButton("Male");
    private final JRadioButton female = new JRadioButton("Female");
    private final JRadioButton other = new JRadioButton("Other");
    private final ButtonGroup genderGroup = new ButtonGroup();
    private final JComboBox<String> day = new JComboBox<>(), month = new JComboBox<>(), year = new JComboBox<>();
    private final JTextField townField = new JTextField();
    private final JButton nextBtn = new JButton("Next");
    private final JLabel status = new JLabel(" ");

    private static final Pattern EMAIL_RX = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);

    public Step4_ExtendedFields(){
        super("Step 4 — Extended Fields (Address/Gender/DOB/Town)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(760, 480);
        setLocationRelativeTo(null);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets=new Insets(6,6,6,6); c.fill=GridBagConstraints.HORIZONTAL; int row=0;

        addRow(form,c,row++,"Name *",nameField);
        addRow(form,c,row++,"Email *",emailField);
        addRow(form,c,row++,"Student ID *",studentIdField);
        addRow(form,c,row++,"Phone *",phoneField);

        c.gridx=0;c.gridy=row;form.add(new JLabel("Address *"),c);
        c.gridx=1; addressArea.setLineWrap(true); addressArea.setWrapStyleWord(true);
        form.add(new JScrollPane(addressArea), c); row++;

        JPanel gp = new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
        genderGroup.add(male); gp.add(male);
        genderGroup.add(female); gp.add(female);
        genderGroup.add(other); gp.add(other);
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
        btns.add(nextBtn); south.add(btns, BorderLayout.EAST);

        setLayout(new BorderLayout());
        add(form,BorderLayout.CENTER);
        add(south,BorderLayout.SOUTH);

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

        nextBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Validated extended fields.\nProceeding..."));
    }

    private void initDOB(){
        day.addItem("Day"); for(int d=1; d<=31; d++) day.addItem(String.format("%02d", d));
        month.addItem("Month"); for(int m=1; m<=12; m++) month.addItem(String.format("%02d", m));
        int cy = LocalDate.now().getYear();
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

    private void v(){
        if(blank(nameField)){set(false,"Name required");return;}
        if(!EMAIL_RX.matcher(emailField.getText().trim()).matches()){set(false,"Valid email required");return;}
        if(blank(studentIdField)){set(false,"Student ID required");return;}
        if(blank(phoneField)){set(false,"Phone required");return;}
        if(addressArea.getText().trim().isEmpty()){set(false,"Address required");return;}
        if(getGender()==null){set(false,"Select gender");return;}
        if(getDOB()==null){set(false,"Select a valid DOB");return;}
        if(blank(townField)){set(false,"Town required");return;}
        set(true,"All good.");
    }

    private String getGender(){ if(male.isSelected()) return "Male"; if(female.isSelected()) return "Female"; if(other.isSelected()) return "Other"; return null; }
    private boolean blank(JTextField tf){ return tf.getText()==null || tf.getText().trim().isEmpty(); }
    private void set(boolean ok, String msg){ nextBtn.setEnabled(ok); status.setText(msg); }
    private void addRow(JPanel p, GridBagConstraints c, int row, String label, JComponent f){
        c.gridx=0;c.gridy=row;c.weightx=0; p.add(new JLabel(label),c);
        c.gridx=1;c.weightx=1; if(f instanceof JTextField tf) tf.setColumns(22); p.add(f,c);
    }

    public static void main(String[] args){ SwingUtilities.invokeLater(() -> new Step4_ExtendedFields().setVisible(true)); }
}
