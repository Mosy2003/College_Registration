package CourcesAndTimeTable;

import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;

class Step2_Validation_DefaultsTest {

    private static <T> T get(Object o, String name, Class<T> type) {
        try { Field f=o.getClass().getDeclaredField(name); f.setAccessible(true); return type.cast(f.get(o)); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    void title_placeholders_and_buttonPresent() {
        Step2_Validation ui = new Step2_Validation();

        assertEquals("Step 2 — Validation", ui.getTitle());

        JComboBox<?> course = get(ui,"courseCombo",JComboBox.class);
        JComboBox<?> level  = get(ui,"levelCombo",JComboBox.class);
        JButton assign      = get(ui,"assignBtn",JButton.class);

        assertEquals(0, course.getSelectedIndex());
        assertEquals("-- Select a course --", course.getSelectedItem());

        assertEquals(0, level.getSelectedIndex());
        assertEquals("-- Select a level --", level.getSelectedItem());

        assertNotNull(assign);
        assertEquals("Assign & Continue", assign.getText());

        ui.dispose();
    }
}
