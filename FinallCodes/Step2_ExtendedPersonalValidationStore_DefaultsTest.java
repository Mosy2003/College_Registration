package FinallCodes;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class Step2_ExtendedPersonalValidationStore_DefaultsTest {

    private static <T> T get(Object o, String name, Class<T> type) {
        try { Field f=o.getClass().getDeclaredField(name); f.setAccessible(true); return type.cast(f.get(o)); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    void starts_disabled_with_name_required_and_dob_placeholders() {
        Step2_ExtendedPersonalValidationStore ui = new Step2_ExtendedPersonalValidationStore();

        JButton save  = get(ui,"saveBtn",JButton.class);
        JLabel status = get(ui,"status", JLabel.class);
        JComboBox<?> day   = get(ui,"day",   JComboBox.class);
        JComboBox<?> month = get(ui,"month", JComboBox.class);
        JComboBox<?> year  = get(ui,"year",  JComboBox.class);

        assertFalse(save.isEnabled(), "Save should start disabled");
        assertTrue(status.getText().toLowerCase().contains("name required"));

        assertEquals("Day",   day.getItemAt(0));
        assertEquals("Month", month.getItemAt(0));
        assertEquals("Year",  year.getItemAt(0));

        ui.dispose();
    }
}
