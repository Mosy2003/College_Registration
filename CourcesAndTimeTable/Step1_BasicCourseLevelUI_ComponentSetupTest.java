package CourcesAndTimeTable;

import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class Step1_BasicCourseLevelUI_ComponentSetupTest {

    private static <T> T get(Object o, String name, Class<T> type) {
        try { Field f=o.getClass().getDeclaredField(name); f.setAccessible(true); return type.cast(f.get(o)); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    void studentId_has18Columns_and_courseItemsPresentInOrder() {
        Step1_BasicCourseLevelUI ui = new Step1_BasicCourseLevelUI();

        JTextField sid = get(ui,"studentIdField",JTextField.class);
        assertEquals(18, sid.getColumns(), "Student ID field should be 18 columns wide.");

        @SuppressWarnings("unchecked")
        JComboBox<String> course = get(ui,"courseCombo",JComboBox.class);
        List<String> expected = Arrays.asList(
                "-- Select a course --", "Engineering", "Law", "Computer Science", "Business"
        );
        assertEquals(expected.size(), course.getItemCount());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), course.getItemAt(i));
        }

        ui.dispose();
    }
}
