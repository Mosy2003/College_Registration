package CourcesAndTimeTable;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class Step4_TimetablePreview_PopulatesTableTest {

    private static <T> T get(Object o, String name, Class<T> type) {
        try { Field f=o.getClass().getDeclaredField(name); f.setAccessible(true); return type.cast(f.get(o)); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    void engineeringLevel6_populatesWithDynamicsAndFluidMechanics() {
        Step4_TimetablePreview ui = new Step4_TimetablePreview();

        JComboBox<?> course = get(ui,"courseCombo",JComboBox.class);
        JComboBox<?> level  = get(ui,"levelCombo",JComboBox.class);
        JButton preview     = get(ui,"previewBtn",JButton.class);
        JLabel status       = get(ui,"status",JLabel.class);
        DefaultTableModel model = get(ui,"model",DefaultTableModel.class);

        // Choose a combo that exists in seed data
        course.setSelectedItem("Engineering");
        level.setSelectedItem("Intermediate (Level 6)");

        // Preview
        preview.getActionListeners()[0].actionPerformed(null);

        // Status text reflects the selection
        assertEquals("Previewing Engineering — Intermediate (Level 6)", status.getText());

        // Table should have the two seeded sessions
        assertEquals(2, model.getRowCount(), "Expected 2 sessions for Eng L6.");
        assertEquals("Dynamics",         model.getValueAt(0, 2));
        assertEquals("Fluid Mechanics",  model.getValueAt(1, 2));

        // Cells are non-editable
        assertFalse(model.isCellEditable(0, 0));

        ui.dispose();
    }
}
