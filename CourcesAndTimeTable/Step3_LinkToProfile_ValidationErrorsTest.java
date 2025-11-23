package CourcesAndTimeTable;

import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.awt.event.ActionEvent;
import static org.junit.jupiter.api.Assertions.*;

class Step3_LinkToProfile_ValidationErrorsTest {
    private static <T> T get(Object o, String f, Class<T> t){ try{var x=o.getClass().getDeclaredField(f); x.setAccessible(true); return t.cast(x.get(o));}catch(Exception e){throw new RuntimeException(e);} }

    @Test
    void emptySid_noCourse_noLevel_messages() {
        Step3_LinkToProfile ui = new Step3_LinkToProfile();

        JTextField sid  = get(ui,"studentIdField",JTextField.class);
        JComboBox<?> course = get(ui,"courseCombo",JComboBox.class);
        JComboBox<?> level  = get(ui,"levelCombo",JComboBox.class);
        JButton assign = get(ui,"assignBtn",JButton.class);
        JLabel status  = get(ui,"status",JLabel.class);

        // 1) empty SID
        sid.setText("");
        course.setSelectedIndex(0);
        level.setSelectedIndex(0);
        assign.getActionListeners()[0].actionPerformed(new ActionEvent(assign,0,"click"));
        assertTrue(status.getText().toLowerCase().contains("student id is required"));

        // 2) SID set, no course
        sid.setText("S1");
        course.setSelectedIndex(0);
        level.setSelectedIndex(0);
        assign.getActionListeners()[0].actionPerformed(new ActionEvent(assign,0,"click"));
        assertTrue(status.getText().toLowerCase().contains("please select a course"));

        // 3) course set, no level
        course.setSelectedIndex(1); // Engineering
        level.setSelectedIndex(0);
        assign.getActionListeners()[0].actionPerformed(new ActionEvent(assign,0,"click"));
        assertTrue(status.getText().toLowerCase().contains("please select a level"));

        ui.dispose();
    }
}
