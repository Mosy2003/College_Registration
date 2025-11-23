package CodesCombines;

import org.junit.jupiter.api.Test;

import javax.swing.*;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class Step2_PersonalValidationAndStoreTest {

    /** Run code on the Swing Event Dispatch Thread and wait. */
    private static void onEDT(Runnable r) {
        try {
            if (SwingUtilities.isEventDispatchThread()) r.run();
            else SwingUtilities.invokeAndWait(r);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** Reflect a private field from the frame. */
    @SuppressWarnings("unchecked")
    private static <T> T getPrivate(Object target, String fieldName, Class<T> type) {
        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return (T) f.get(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void saveIsDisabledInitially_thenEnabledWithValidInputs() {
        final Step2_PersonalValidationAndStore[] ref = new Step2_PersonalValidationAndStore[1];

        onEDT(() -> {
            var frame = new Step2_PersonalValidationAndStore();
            ref[0] = frame;

            JTextField name   = getPrivate(frame, "nameField", JTextField.class);
            JTextField email  = getPrivate(frame, "emailField", JTextField.class);
            JTextField sid    = getPrivate(frame, "studentIdField", JTextField.class);
            JTextField phone  = getPrivate(frame, "phoneField", JTextField.class);
            JButton saveBtn   = getPrivate(frame, "saveBtn", JButton.class);

            // Initially disabled
            assertFalse(saveBtn.isEnabled(), "Save should be disabled initially.");

            // Enter valid data
            name.setText("Alice Example");
            email.setText("alice@example.com");
            sid.setText("S-1001");
            phone.setText("0123456789");

            // After DocumentListeners fire, button should be enabled
            assertTrue(saveBtn.isEnabled(), "Save should be enabled after valid input.");

            frame.dispose();
        });
    }

    @Test
    void duplicateStudentIdDisablesSaveAndShowsDuplicateMessage() {
        // Headless prevents real dialogs from popping; the action will still reach store.save(...)
        System.setProperty("java.awt.headless", "true");

        final Step2_PersonalValidationAndStore[] ref = new Step2_PersonalValidationAndStore[1];

        onEDT(() -> {
            var frame = new Step2_PersonalValidationAndStore();
            ref[0] = frame;

            JTextField name   = getPrivate(frame, "nameField", JTextField.class);
            JTextField email  = getPrivate(frame, "emailField", JTextField.class);
            JTextField sid    = getPrivate(frame, "studentIdField", JTextField.class);
            JTextField phone  = getPrivate(frame, "phoneField", JTextField.class);
            JButton saveBtn   = getPrivate(frame, "saveBtn", JButton.class);
            JLabel status     = getPrivate(frame, "status", JLabel.class);

            // First: enter valid data and "click" save (invoke the listener directly)
            name.setText("Bob One");
            email.setText("bob1@example.com");
            sid.setText("SID-200");
            phone.setText("0712345678");
            assertTrue(saveBtn.isEnabled(), "Save should be enabled for first-time valid input.");

            // Fire the first save action (triggers store.save + JOptionPane, but we're headless)
            // Call listener directly so we can catch exceptions, if any.
            assertTrue(saveBtn.getActionListeners().length > 0);
            try {
                saveBtn.getActionListeners()[0].actionPerformed(
                        new ActionEvent(saveBtn, ActionEvent.ACTION_PERFORMED, "click"));
            } catch (HeadlessException ignore) {
                // expected in headless mode when JOptionPane tries to show
            }

            // Now, keep the SAME Student ID, tweak name to trigger validation again
            name.setText("Bob Two");     // triggers DocumentListener, re-validates
            sid.setText("SID-200 ");     // add space so trim() still matches same ID; re-validates

            // After re-validation, Save should be disabled and status should mention duplicate
            assertFalse(saveBtn.isEnabled(), "Save should be disabled for duplicate Student ID.");
            assertTrue(status.getText().toLowerCase().contains("already exists")
                            || status.getText().toLowerCase().contains("student id already exists"),
                    "Status should indicate duplicate Student ID.");

            frame.dispose();
        });
    }
}
