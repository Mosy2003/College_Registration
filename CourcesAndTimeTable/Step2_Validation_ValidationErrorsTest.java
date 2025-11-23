package CourcesAndTimeTable;

import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;

class Step2_Validation_ValidationErrorsTest {

    private static <T> T get(Object o, String name, Class<T> type) {
        try { Field f=o.getClass().getDeclaredField(name); f.setAccessible(true); return type.cast(f.get(o)); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    void emptySid_thenNoCourse_thenNoLevel_setsExpectedMessages() {
        Step2_Validation ui = new Step2_Validation();

        JTextField sid  = get(ui,"studentIdField",JTextField.class);
        JComboBox<?> course = get(ui,"courseCombo",JComboBox.class);
        JComboBox<?> level  = get(ui,"levelCombo",JComboBox.class);
        JButton assign = get(ui,"assignBtn",JButton.class);
        JLabel status  = get(ui,"status",JLabel.class);

        // 1) Empty SID
        sid.setText("");
        course.setSelectedIndex(0);
        level.setSelectedIndex(0);
        assign.getActionListeners()[0].actionPerformed(new ActionEvent(assign, 0, "click"));
        assertTrue(status.getText().toLowerCase().contains("student id is required"));

        // 2) SID set but no course
        sid.setText("S1");
        course.setSelectedIndex(0);
        level.setSelectedIndex(0);
        assign.getActionListeners()[0].actionPerformed(new ActionEvent(assign, 0, "click"));
        assertTrue(status.getText().toLowerCase().contains("please select a course"));

        // 3) Course set but no level
        course.setSelectedIndex(1); // "Engineering"
        level.setSelectedIndex(0);
        assign.getActionListeners()[0].actionPerformed(new ActionEvent(assign, 0, "click"));
        assertTrue(status.getText().toLowerCase().contains("please select a level"));

        ui.dispose();
    }
}
