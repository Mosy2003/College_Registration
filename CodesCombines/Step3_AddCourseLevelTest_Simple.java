package CodesCombines;

import org.junit.jupiter.api.Test;
import javax.swing.*;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class Step3_AddCourseLevelTest_Simple {

    // tiny reflection helper
    @SuppressWarnings("unchecked")
    private static <T> T get(Object o, String name, Class<T> type) {
        try { Field f=o.getClass().getDeclaredField(name); f.setAccessible(true); return (T) f.get(o); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    void savePersonal_updatesStatus() {
        Step3_AddCourseLevel ui = new Step3_AddCourseLevel();

        JTextField sid   = get(ui, "studentIdField", JTextField.class);
        JTextField name  = get(ui, "nameField", JTextField.class);
        JTextField email = get(ui, "emailField", JTextField.class);
        JTextField phone = get(ui, "phoneField", JTextField.class);
        JButton saveBtn  = get(ui, "savePersonalBtn", JButton.class);
        JLabel status    = get(ui, "status", JLabel.class);

        sid.setText("S100");
        name.setText("Alice");
        email.setText("alice@example.com");
        phone.setText("0123456789");

        // click "Save Personal"
        saveBtn.getActionListeners()[0].actionPerformed(
                new ActionEvent(saveBtn, ActionEvent.ACTION_PERFORMED, "click"));

        assertTrue(status.getText().startsWith("Personal saved"), "Status should indicate personal info saved.");
        ui.dispose();
    }

    @Test
    void assign_requiresSavedFirst_thenAssigns() {
        // avoid real dialogs popping from JOptionPane in CI
        System.setProperty("java.awt.headless", "true");

        Step3_AddCourseLevel ui = new Step3_AddCourseLevel();

        JTextField sid   = get(ui, "studentIdField", JTextField.class);
        JTextField name  = get(ui, "nameField", JTextField.class);
        JTextField email = get(ui, "emailField", JTextField.class);
        JTextField phone = get(ui, "phoneField", JTextField.class);
        JComboBox<?> course = get(ui, "courseCombo", JComboBox.class);
        JComboBox<?> level  = get(ui, "levelCombo",  JComboBox.class);
        JButton assignBtn   = get(ui, "assignBtn", JButton.class);
        JButton saveBtn     = get(ui, "savePersonalBtn", JButton.class);
        JLabel status       = get(ui, "status", JLabel.class);

        // choose course/level
        course.setSelectedIndex(1); // "Engineering"
        level.setSelectedIndex(1);  // "Beginner (Level 5)"
        sid.setText("S200");

        // try assign BEFORE saving -> should show error in status
        assignBtn.getActionListeners()[0].actionPerformed(
                new ActionEvent(assignBtn, ActionEvent.ACTION_PERFORMED, "click"));
        assertTrue(status.getText().toLowerCase().contains("save personal info first"));

        // save personal
        name.setText("Bob");
        email.setText("bob@example.com");
        phone.setText("0712345678");
        saveBtn.getActionListeners()[0].actionPerformed(
                new ActionEvent(saveBtn, ActionEvent.ACTION_PERFORMED, "click"));

        // assign AFTER saving -> status shows "Assigned ..."
        try {
            assignBtn.getActionListeners()[0].actionPerformed(
                    new ActionEvent(assignBtn, ActionEvent.ACTION_PERFORMED, "click"));
        } catch (HeadlessException ignored) {
            // expected in headless mode when JOptionPane tries to show the dialog
        }
        assertTrue(status.getText().startsWith("Assigned "), "Status should indicate assignment happened.");
        ui.dispose();
    }
}
