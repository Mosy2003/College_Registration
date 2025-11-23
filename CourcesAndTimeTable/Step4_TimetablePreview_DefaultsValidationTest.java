package CourcesAndTimeTable;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class Step4_TimetablePreview_DefaultsValidationTest {

    private static <T> T get(Object o, String name, Class<T> type) {
        try { Field f=o.getClass().getDeclaredField(name); f.setAccessible(true); return type.cast(f.get(o)); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    void defaults_and_clickPreview_showsValidationMessage() {
        Step4_TimetablePreview ui = new Step4_TimetablePreview();

        JComboBox<?> course = get(ui,"courseCombo",JComboBox.class);
        JComboBox<?> level  = get(ui,"levelCombo",JComboBox.class);
        JButton preview     = get(ui,"previewBtn",JButton.class);
        JLabel status       = get(ui,"status",JLabel.class);
        DefaultTableModel model = get(ui,"model",DefaultTableModel.class);

        // Default placeholders
        assertEquals(0, course.getSelectedIndex());
        assertEquals("-- Select a course --", course.getSelectedItem());
        assertEquals(0, level.getSelectedIndex());
        assertEquals("-- Select a level --",  level.getSelectedItem());

        // Click preview with placeholders → validation message
        preview.getActionListeners()[0].actionPerformed(null);
        assertEquals("Select both course and level.", status.getText());

        // Table should still be empty (no preview done)
        assertEquals(0, model.getRowCount());

        ui.dispose();
    }
}
