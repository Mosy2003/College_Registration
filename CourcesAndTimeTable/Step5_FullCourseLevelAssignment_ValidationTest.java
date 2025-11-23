package CourcesAndTimeTable;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class Step5_FullCourseLevelAssignment_ValidationTest {

    private static <T> T get(Object o, String name, Class<T> type) {
        try { Field f=o.getClass().getDeclaredField(name); f.setAccessible(true); return type.cast(f.get(o)); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    void defaults_and_clicks_show_validation_errors() {
        Step5_FullCourseLevelAssignment ui = new Step5_FullCourseLevelAssignment();

        JTextField sid          = get(ui,"studentIdField",JTextField.class);
        JComboBox<?> course     = get(ui,"courseCombo",JComboBox.class);
        JComboBox<?> level      = get(ui,"levelCombo",JComboBox.class);
        JButton assign          = get(ui,"assignBtn",JButton.class);
        JButton preview         = get(ui,"previewBtn",JButton.class);
        JLabel status           = get(ui,"statusLabel",JLabel.class);
        DefaultTableModel model = get(ui,"timetableModel",DefaultTableModel.class);

        // Defaults: placeholders selected
        assertEquals(0, course.getSelectedIndex());
        assertEquals(0, level.getSelectedIndex());

        // 1) Assign with everything empty -> "Student ID is required."
        sid.setText("");
        assign.getActionListeners()[0].actionPerformed(null);
        assertTrue(status.getText().toLowerCase().contains("student id is required"));

        // 2) Preview with placeholders -> "Select both..."
        preview.getActionListeners()[0].actionPerformed(null);
        assertTrue(status.getText().toLowerCase().contains("select both a course and a level"));

        // Table untouched
        assertEquals(0, model.getRowCount());

        ui.dispose();
    }
}
