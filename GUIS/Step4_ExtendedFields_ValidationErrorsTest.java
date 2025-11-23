package GUIS;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class Step4_ExtendedFields_ValidationErrorsTest {

    private static <T> T get(Object o, String f, Class<T> t) {
        try { Field x=o.getClass().getDeclaredField(f); x.setAccessible(true); return t.cast(x.get(o)); }
        catch(Exception e){ throw new RuntimeException(e); }
    }

    @Test
    void missingGender_and_invalidDob_triggerMessages() {
        Step4_ExtendedFields ui = new Step4_ExtendedFields();

        JTextField name  = get(ui,"nameField",JTextField.class);
        JTextField email = get(ui,"emailField",JTextField.class);
        JTextField sid   = get(ui,"studentIdField",JTextField.class);
        JTextField phone = get(ui,"phoneField",JTextField.class);
        JTextArea  addr  = get(ui,"addressArea",JTextArea.class);
        JTextField town  = get(ui,"townField",JTextField.class);
        JComboBox<?> day   = get(ui,"day",   JComboBox.class);
        JComboBox<?> month = get(ui,"month", JComboBox.class);
        JComboBox<?> year  = get(ui,"year",  JComboBox.class);
        JLabel status      = get(ui,"status",JLabel.class);

        // Fill all required (except gender/DOB)
        name.setText("Ann");
        email.setText("ann@example.com");
        sid.setText("S100");
        phone.setText("0123456789");
        addr.setText("1 Main St");
        town.setText("Dublin");

        // Choose an invalid date: 31 Feb (day=31, month=02, any valid year)
        day.setSelectedItem("31");
        month.setSelectedItem("02");
        // Pick a year (e.g., first non-placeholder if exists)
        if (year.getItemCount() > 1) year.setSelectedIndex(1);

        // Since gender is not selected yet → expect "Select gender"
        assertTrue(status.getText().toLowerCase().contains("select gender"),
                "Should prompt to select gender first.");

        // Select gender, keep invalid date → expect "Select a valid DOB"
        JRadioButton female = get(ui,"female",JRadioButton.class);
        female.setSelected(true);
        // Fire action to re-validate
        for (var al : female.getActionListeners()) al.actionPerformed(null);

        assertTrue(status.getText().toLowerCase().contains("valid dob"),
                "Should prompt for a valid DOB with 31/02.");

        ui.dispose();
    }
}
