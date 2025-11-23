package CourcesAndTimeTable;

import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;

class Step2_Validation_SuccessFlowTest {

    private static <T> T get(Object o, String name, Class<T> type) {
        try { Field f=o.getClass().getDeclaredField(name); f.setAccessible(true); return type.cast(f.get(o)); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    void validInputs_setOkStatus_andTryShowDialog() {
        // Avoid real JOptionPane in CI/Headless
        System.setProperty("java.awt.headless", "true");

        Step2_Validation ui = new Step2_Validation();

        JTextField sid  = get(ui,"studentIdField",JTextField.class);
        JComboBox<?> course = get(ui,"courseCombo",JComboBox.class);
        JComboBox<?> level  = get(ui,"levelCombo",JComboBox.class);
        JButton assign = get(ui,"assignBtn",JButton.class);
        JLabel status  = get(ui,"status",JLabel.class);

        sid.setText("SID-123");
        course.setSelectedItem("Law");
        level.setSelectedItem("Intermediate (Level 6)");

        try {
            assign.getActionListeners()[0].actionPerformed(new ActionEvent(assign, 0, "click"));
        } catch (HeadlessException ignored) {
            // expected because JOptionPane.showMessageDialog runs in headless mode
        }

        assertEquals("OK. Inputs are valid.", status.getText());
        ui.dispose();
    }
}
