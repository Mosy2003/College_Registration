package CourcesAndTimeTable;

import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;

class Step3_LinkToProfile_DefaultsTest {
    private static <T> T get(Object o, String f, Class<T> t){ try{var x=o.getClass().getDeclaredField(f); x.setAccessible(true); return t.cast(x.get(o));}catch(Exception e){throw new RuntimeException(e);} }

    @Test
    void title_placeholders_button() {
        Step3_LinkToProfile ui = new Step3_LinkToProfile();
        assertEquals("Step 3 — Link Course/Level to Student Profile", ui.getTitle());

        JComboBox<?> course = get(ui,"courseCombo",JComboBox.class);
        JComboBox<?> level  = get(ui,"levelCombo",JComboBox.class);
        JButton assign      = get(ui,"assignBtn",JButton.class);

        assertEquals(0, course.getSelectedIndex());
        assertEquals("-- Select a course --", course.getSelectedItem());
        assertEquals(0, level.getSelectedIndex());
        assertEquals("-- Select a level --", level.getSelectedItem());
        assertEquals("Assign & Save", assign.getText());

        ui.dispose();
    }
}
