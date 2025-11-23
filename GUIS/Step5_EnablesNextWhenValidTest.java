package GUIS;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class Step5_EnablesNextWhenValidTest {
    private static <T> T get(Object o, String f, Class<T> t) {
        try { Field x=o.getClass().getDeclaredField(f); x.setAccessible(true); return t.cast(x.get(o)); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    void validInputs_enableNext_andShowAllGood() {
        Step_5StudentRegistrationWithSummary ui = new Step_5StudentRegistrationWithSummary();

        JTextField name  = get(ui,"nameField",JTextField.class);
        JTextField email = get(ui,"emailField",JTextField.class);
        JTextField sid   = get(ui,"studentIdField",JTextField.class);
        JTextField phone = get(ui,"phoneField",JTextField.class);
        JTextArea  addr  = get(ui,"addressArea",JTextArea.class);
        JTextField town  = get(ui,"townField",JTextField.class);
        JRadioButton male = get(ui,"male",JRadioButton.class);
        JComboBox<String> day = get(ui,"day",JComboBox.class);
        JComboBox<String> month = get(ui,"month",JComboBox.class);
        JComboBox<String> year = get(ui,"year",JComboBox.class);
        JButton next = get(ui,"nextBtn",JButton.class);
        JLabel status = get(ui,"status",JLabel.class);

        name.setText("Alice");
        email.setText("alice@example.com");
        sid.setText("S12345");
        phone.setText("+353 87 123 4567");
        addr.setText("1 Main Street");
        town.setText("Dublin");
        male.setSelected(true);
        day.setSelectedIndex(1);   // 01
        month.setSelectedIndex(1); // 01
        year.setSelectedIndex(1);  // currentYear - 15

        // Trigger validation by simulating a tiny text edit
        name.postActionEvent();

        assertTrue(next.isEnabled(), "Next should be enabled when form is valid.");
        assertTrue(status.getText().toLowerCase().contains("all good"),
                "Status should indicate all good.");

        ui.dispose();
    }
}
