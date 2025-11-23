package CodesCombines;

import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class Step1_BasicPersonalFormTest {

    @Test
    void testFormHasBasicComponents() {
        Step1_BasicPersonalForm form = new Step1_BasicPersonalForm();

        // Check window title
        assertEquals("Step 1 — Personal Info (Basic)", form.getTitle());

        // Check there's a Next button
        JButton nextButton = findButton(form.getContentPane(), "Next");
        assertNotNull(nextButton, "Next button should exist.");

        //  Check there are at least 4 JTextFields in the form
        int fieldCount = countComponentsOfType(form.getContentPane(), JTextField.class);
        assertTrue(fieldCount >= 4, "Form should contain at least 4 text fields.");

        form.dispose(); 
    }

    // Utility to find buttons by text
    private JButton findButton(Container container, String text) {
        for (Component c : container.getComponents()) {
            if (c instanceof JButton b && text.equals(b.getText())) return b;
            if (c instanceof Container child) {
                JButton found = findButton(child, text);
                if (found != null) return found;
            }
        }
        return null;
    }

    // 🔍 Utility to count components of a specific type (recursively)
    private <T extends Component> int countComponentsOfType(Container container, Class<T> type) {
        int count = 0;
        for (Component c : container.getComponents()) {
            if (type.isInstance(c)) count++;
            if (c instanceof Container child) count += countComponentsOfType(child, type);
        }
        return count;
    }
}



//Verifies that the frame title is correct.
//Ensures the Next button exists and has the right label.
//Confirms there are at least four text fields (Name, Email, Student ID, Phone)



