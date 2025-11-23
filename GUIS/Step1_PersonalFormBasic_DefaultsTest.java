package GUIS;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class Step1_PersonalFormBasic_DefaultsTest {

    private static <T> T get(Object o, String name, Class<T> type) {
        try {
            Field f = o.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return type.cast(f.get(o));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void windowTitle_and_fieldDefaults_areCorrect() {
        Step1_PersonalFormBasic ui = new Step1_PersonalFormBasic();

        assertEquals("Step 1 — Basic Form (No Validation)", ui.getTitle());

        JTextField name = get(ui, "nameField", JTextField.class);
        JTextField email = get(ui, "emailField", JTextField.class);
        JTextField studentId = get(ui, "studentIdField", JTextField.class);
        JTextField phone = get(ui, "phoneField", JTextField.class);
        JButton next = get(ui, "nextBtn", JButton.class);

        // Verify all components exist and start empty
        assertNotNull(name);
        assertEquals("", name.getText());
        assertNotNull(email);
        assertEquals("", email.getText());
        assertNotNull(studentId);
        assertEquals("", studentId.getText());
        assertNotNull(phone);
        assertEquals("", phone.getText());

        assertNotNull(next);
        assertEquals("Next", next.getText());

        ui.dispose();
    }
}
