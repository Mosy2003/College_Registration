package FinallCodes;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class Step3_AssignCourseLevel_Preview_DefaultsValidationTest {

    private static <T> T get(Object o, String f, Class<T> t) {
        try { Field x=o.getClass().getDeclaredField(f); x.setAccessible(true); return t.cast(x.get(o)); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    void placeholders_and_click_shows_validation_messages() {
        Step3_AssignCourseLevel_Preview ui = new Step3_AssignCourseLevel_Preview();

        JTextField sid  = get(ui,"studentIdField",JTextField.class);
        JComboBox<?> course = get(ui,"courseCombo",JComboBox.class);
        JComboBox<?> level  = get(ui,"levelCombo",JComboBox.class);
        JButton link       = get(ui,"linkBtn",JButton.class);
        JLabel status      = get(ui,"status",JLabel.class);
        DefaultTableModel model = get(ui,"model",DefaultTableModel.class);

        // Defaults
        assertEquals(0, course.getSelectedIndex());
        assertEquals("-- Select a course --", course.getSelectedItem());
        assertEquals(0, level.getSelectedIndex());
        assertEquals("-- Select a level --", level.getSelectedItem());

        // 1) Empty SID
        sid.setText("");
        link.getActionListeners()[0].actionPerformed(null);
        assertTrue(status.getText().toLowerCase().contains("enter student id"));

        // 2) SID set but no course
        sid.setText("S1");
        course.setSelectedIndex(0);
        level.setSelectedIndex(0);
        link.getActionListeners()[0].actionPerformed(null);
        assertTrue(status.getText().toLowerCase().contains("select a course"));

        // 3) Course set but no level
        course.setSelectedItem("Engineering");
        level.setSelectedIndex(0);
        link.getActionListeners()[0].actionPerformed(null);
        assertTrue(status.getText().toLowerCase().contains("select a level"));

        // Table should still be empty
        assertEquals(0, model.getRowCount());

        ui.dispose();
    }
}



