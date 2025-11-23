package GUIS;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class Step5_DefaultsTest {
    private static <T> T get(Object o, String f, Class<T> t) {
        try { Field x=o.getClass().getDeclaredField(f); x.setAccessible(true); return t.cast(x.get(o)); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    void nextStartsDisabled_andStatusShowsNameRequired_andDobPlaceholders() {
        Step_5StudentRegistrationWithSummary ui = new Step_5StudentRegistrationWithSummary();

        JButton next  = get(ui,"nextBtn",JButton.class);
        JLabel status = get(ui,"status",JLabel.class);
        JComboBox<?> day   = get(ui,"day",JComboBox.class);
        JComboBox<?> month = get(ui,"month",JComboBox.class);
        JComboBox<?> year  = get(ui,"year",JComboBox.class);

        assertFalse(next.isEnabled(), "Next should start disabled.");
        assertTrue(status.getText().toLowerCase().contains("name required"),
                "Initial status should mention name required.");
        assertEquals("Day",   day.getItemAt(0));
        assertEquals("Month", month.getItemAt(0));
        assertEquals("Year",  year.getItemAt(0));

        ui.dispose();
    }
}
