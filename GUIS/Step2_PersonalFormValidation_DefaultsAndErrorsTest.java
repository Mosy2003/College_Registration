package GUIS;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class Step2_PersonalFormValidation_DefaultsAndErrorsTest {

    private static <T> T get(Object o, String name, Class<T> type) {
        try { Field f=o.getClass().getDeclaredField(name); f.setAccessible(true); return type.cast(f.get(o)); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    void initially_disabled_and_shows_first_required_message() {
        Step2_PersonalFormValidation ui = new Step2_PersonalFormValidation();

        JButton next  = get(ui, "nextBtn", JButton.class);
        JLabel status = get(ui, "status",  JLabel.class);

        // At startup validateForm() runs, so Next is disabled and first required message shows
        assertFalse(next.isEnabled(), "Next should start disabled.");
        assertTrue(status.getText().toLowerCase().contains("name is required"),
                "Should show 'Name is required.' initially.");

        // Now type a name but keep email invalid → should show email error and stay disabled
        JTextField name  = get(ui, "nameField", JTextField.class);
        JTextField email = get(ui, "emailField", JTextField.class);
        name.setText("Alice");
        email.setText("not-an-email");

        assertFalse(next.isEnabled(), "Next stays disabled for invalid email.");
        assertTrue(status.getText().toLowerCase().contains("email must be valid"),
                "Should show email validity error.");

        ui.dispose();
    }
}
