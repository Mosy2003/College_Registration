package CodesCombines;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class Step4_CourseLevelTimetablePreviewTest_Small {

    // tiny reflection helper
    @SuppressWarnings("unchecked")
    private static <T> T get(Object o, String name, Class<T> type) {
        try { Field f=o.getClass().getDeclaredField(name); f.setAccessible(true); return (T) f.get(o); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    void endToEnd_flow_populatesTimetable() {
        // prevent JOptionPane dialogs during test
        System.setProperty("java.awt.headless", "true");

        Step4_CourseLevelTimetablePreview ui = new Step4_CourseLevelTimetablePreview();

        // fill personal (valid)
        get(ui,"nameField",JTextField.class).setText("Test User");
        get(ui,"emailField",JTextField.class).setText("test@demo.com");
        get(ui,"studentIdField",JTextField.class).setText("SID-999");
        get(ui,"phoneField",JTextField.class).setText("0123456789");

        JButton next = get(ui,"nextBtn",JButton.class);

        // go to Course card
        try { next.getActionListeners()[0].actionPerformed(new ActionEvent(next, ActionEvent.ACTION_PERFORMED, "next")); }
        catch (HeadlessException ignored) { /* JOptionPane in headless */ }

        // pick a combo that exists in data (Computer Science + Intermediate L6)
        JComboBox<?> course = get(ui,"courseCombo",JComboBox.class);
        JComboBox<?> level  = get(ui,"levelCombo",JComboBox.class);
        course.setSelectedItem("Computer Science");
        level.setSelectedItem("Intermediate (Level 6)");

        // go to Timetable card (loads table)
        try { next.getActionListeners()[0].actionPerformed(new ActionEvent(next, ActionEvent.ACTION_PERFORMED, "next")); }
        catch (HeadlessException ignored) { }

        // assert rows loaded
        DefaultTableModel model = get(ui,"model",DefaultTableModel.class);
        assertTrue(model.getRowCount() >= 1, "Timetable should have at least one row.");

        // sanity: one of the modules should be "Data Structures" (per seed data)
        boolean hasDataStructures = false;
        for (int r = 0; r < model.getRowCount(); r++) {
            if ("Data Structures".equals(model.getValueAt(r, 2))) { hasDataStructures = true; break; }
        }
        assertTrue(hasDataStructures, "Expected 'Data Structures' in the timetable for CS Level 6.");

        ui.dispose();
    }
}
