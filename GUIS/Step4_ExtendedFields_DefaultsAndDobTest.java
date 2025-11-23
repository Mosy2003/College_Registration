package GUIS;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class Step4_ExtendedFields_DefaultsAndDobTest {

    private static <T> T get(Object o, String f, Class<T> t) {
        try { Field x=o.getClass().getDeclaredField(f); x.setAccessible(true); return t.cast(x.get(o)); }
        catch(Exception e){ throw new RuntimeException(e); }
    }

    @Test
    void nextStartsDisabled_statusShowsNameRequired_andDobCombosLoaded() {
        Step4_ExtendedFields ui = new Step4_ExtendedFields();

        JButton next  = get(ui,"nextBtn",JButton.class);
        JLabel status = get(ui,"status", JLabel.class);
        JComboBox<?> day   = get(ui,"day",   JComboBox.class);
        JComboBox<?> month = get(ui,"month", JComboBox.class);
        JComboBox<?> year  = get(ui,"year",  JComboBox.class);

        assertFalse(next.isEnabled(), "Next should start disabled.");
        assertTrue(status.getText().toLowerCase().contains("name required"));

        // First item placeholders
        assertEquals("Day",   day.getItemAt(0));
        assertEquals("Month", month.getItemAt(0));
        assertEquals("Year",  year.getItemAt(0));

        // Has at least 31 days + placeholder
        assertTrue(day.getItemCount() >= 32);
        // Has 12 months + placeholder
        assertEquals(13, month.getItemCount());
        // Year list has a placeholder + at least ~85 years (range 15..100)
        assertTrue(year.getItemCount() >= 1 + 85);

        ui.dispose();
    }
}
