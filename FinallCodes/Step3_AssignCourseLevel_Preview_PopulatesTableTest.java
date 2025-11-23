package FinallCodes;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class Step3_AssignCourseLevel_Preview_PopulatesTableTest {

    private static <T> T get(Object o, String f, Class<T> t) {
        try { Field x=o.getClass().getDeclaredField(f); x.setAccessible(true); return t.cast(x.get(o)); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    void engineeringLevel6_addsDynamics_and_FluidMechanics_rows() {
        Step3_AssignCourseLevel_Preview ui = new Step3_AssignCourseLevel_Preview();

        JTextField sid  = get(ui,"studentIdField",JTextField.class);
        JComboBox<?> course = get(ui,"courseCombo",JComboBox.class);
        JComboBox<?> level  = get(ui,"levelCombo",JComboBox.class);
        JButton link       = get(ui,"linkBtn",JButton.class);
        JLabel status      = get(ui,"status",JLabel.class);
        DefaultTableModel model = get(ui,"model",DefaultTableModel.class);

        sid.setText("SID-200");
        course.setSelectedItem("Engineering");
        level.setSelectedItem("Intermediate (Level 6)");

        link.getActionListeners()[0].actionPerformed(null);

        assertTrue(status.getText().startsWith("Linked SID-200 → Engineering — Intermediate (Level 6)"));
        assertEquals(2, model.getRowCount(), "Expected two sessions for Eng L6.");
        assertEquals("Dynamics",        model.getValueAt(0, 2));
        assertEquals("Fluid Mechanics", model.getValueAt(1, 2));

        // cells non-editable
        assertFalse(model.isCellEditable(0,0));

        ui.dispose();
    }
}
