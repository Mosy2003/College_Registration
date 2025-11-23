package FinallCodes;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class Step2_ExtendedPersonalValidationStore_ValidationErrorsTest {

    private static <T> T get(Object o, String name, Class<T> type) {
        try { Field f=o.getClass().getDeclaredField(name); f.setAccessible(true); return type.cast(f.get(o)); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    void shows_specific_errors_for_invalid_email_phone_gender_dob_town() {
        Step2_ExtendedPersonalValidationStore ui = new Step2_ExtendedPersonalValidationStore();

        JTextField name   = get(ui,"nameField",JTextField.class);
        JTextField email  = get(ui,"emailField",JTextField.class);
        JTextField sid    = get(ui,"studentIdField",JTextField.class);
        JTextField phone  = get(ui,"phoneField",JTextField.class);
        JTextArea  addr   = get(ui,"addressArea",JTextArea.class);
        JTextField town   = get(ui,"townField",JTextField.class);
        JComboBox<?> day  = get(ui,"day",JComboBox.class);
        JComboBox<?> month= get(ui,"month",JComboBox.class);
        JComboBox<?> year = get(ui,"year",JComboBox.class);
        JLabel status     = get(ui,"status",JLabel.class);

        // Fill baseline (but keep some invalid to trigger each message)
        name.setText("Ann");
        email.setText("bad-email"); // invalid
        sid.setText("S100");
        phone.setText("abc");       // invalid phone
        addr.setText("1 Main St");
        town.setText("");           // missing town
        // gender not selected
        day.setSelectedItem("31"); month.setSelectedItem("02"); // invalid date (Feb 31)
        if (year.getItemCount()>1) year.setSelectedIndex(1);

        assertTrue(status.getText().toLowerCase().contains("valid email"),
                "Should complain about email first");

        // fix email, now expect phone error
        email.setText("ann@example.com");
        assertTrue(status.getText().toLowerCase().contains("phone looks invalid"));

        // fix phone, now expect gender error
        phone.setText("0123456789");
        assertTrue(status.getText().toLowerCase().contains("select gender"));

        // select gender, expect DOB error
        JRadioButton female = get(ui,"female",JRadioButton.class);
        female.setSelected(true);
        for (var l : female.getActionListeners()) l.actionPerformed(null);
        assertTrue(status.getText().toLowerCase().contains("valid dob"));

        // fix DOB but leave town empty → town error
        day.setSelectedItem("15"); month.setSelectedItem("05");
        if (year.getItemCount()>1) year.setSelectedIndex(1);
        assertTrue(status.getText().toLowerCase().contains("town required"));

        ui.dispose();
    }
}
