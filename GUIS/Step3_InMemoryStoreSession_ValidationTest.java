package GUIS;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class Step3_InMemoryStoreSession_ValidationTest {

    private static <T> T get(Object o, String name, Class<T> type) {
        try { Field f = o.getClass().getDeclaredField(name); f.setAccessible(true); return type.cast(f.get(o)); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    void invalid_email_and_blank_name_disables_save() {
        Step3_InMemoryStoreSession ui = new Step3_InMemoryStoreSession();

        JTextField name  = get(ui, "nameField", JTextField.class);
        JTextField email = get(ui, "emailField", JTextField.class);
        JButton next     = get(ui, "nextBtn", JButton.class);
        JLabel status    = get(ui, "status", JLabel.class);

        // leave name blank, set invalid email
        name.setText("");
        email.setText("bad-email");

        assertFalse(next.isEnabled(), "Save button should be disabled.");
        assertTrue(status.getText().toLowerCase().contains("name required")
                        || status.getText().toLowerCase().contains("valid email"),
                "Should show validation error message.");

        ui.dispose();
    }
}
