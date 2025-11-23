package CourcesAndTimeTable;

import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;

class Step1_BasicCourseLevelUI_DefaultsTest {

    private static <T> T get(Object o, String name, Class<T> type) {
        try { Field f=o.getClass().getDeclaredField(name); f.setAccessible(true); return type.cast(f.get(o)); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    void title_placeholders_and_button() {
        Step1_BasicCourseLevelUI ui = new Step1_BasicCourseLevelUI();

        assertEquals("Step 1 — Basic UI", ui.getTitle());

        JComboBox<String> course = get(ui,"courseCombo",JComboBox.class);
        JComboBox<String> level  = get(ui,"levelCombo",JComboBox.class);
        JButton assign          = get(ui,"assignBtn",JButton.class);

        assertEquals(0, course.getSelectedIndex());
        assertEquals("-- Select a course --", course.getSelectedItem());

        assertEquals(0, level.getSelectedIndex());
        assertEquals("-- Select a level --", level.getSelectedItem());

        assertNotNull(assign);
        assertEquals("Assign", assign.getText());

        ui.dispose();
    }
}
