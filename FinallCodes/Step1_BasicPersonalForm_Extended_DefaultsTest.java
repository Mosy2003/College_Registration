package FinallCodes;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class Step1_BasicPersonalForm_Extended_DefaultsTest {

    private static <T> T get(Object o, String name, Class<T> type) {
        try { Field f=o.getClass().getDeclaredField(name); f.setAccessible(true); return type.cast(f.get(o)); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    void title_placeholders_and_button_present() {
        Step1_BasicPersonalForm_Extended ui = new Step1_BasicPersonalForm_Extended();

        assertEquals("Step 1 — Personal Info (Basic, with DOB/Address/Gender/Town)", ui.getTitle());

        JComboBox<?> day   = get(ui, "day",   JComboBox.class);
        JComboBox<?> month = get(ui, "month", JComboBox.class);
        JComboBox<?> year  = get(ui, "year",  JComboBox.class);
        JButton next       = get(ui, "nextBtn", JButton.class);

        // DOB placeholders
        assertEquals("Day",   day.getItemAt(0));
        assertEquals("Month", month.getItemAt(0));
        assertEquals("Year",  year.getItemAt(0));

        // Sanity: counts look right (31 + placeholder, 12 + placeholder, years range + placeholder)
        assertTrue(day.getItemCount() >= 32);
        assertEquals(13, month.getItemCount());
        assertTrue(year.getItemCount() >= 2);

        assertNotNull(next);
        assertEquals("Next", next.getText());

        ui.dispose();
    }
}
