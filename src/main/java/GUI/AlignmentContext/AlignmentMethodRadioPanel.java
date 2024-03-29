package GUI.AlignmentContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AlignmentMethodRadioPanel extends JPanel {

    public AlignmentMethodRadioPanel() {
        super();

        setLayout(new GridLayout());

        // Create and add Radio Fields
        final JRadioButton option1 = new JRadioButton("Hybrid: Star align & correlation", true);
        final JRadioButton option2 = new JRadioButton("Manual Parameter Input [WIP]", true);

        add(option1);
        add(option2);

        // create a ActionListeners for button groups
        // TODO - Implement different radio button options.
        option1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Option 1 selected");
            }
        });
        option2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Option 2 selected");
            }
        });


        // Only one Button in a ButtonGroup can be selected at once
        ButtonGroup group = new ButtonGroup();
        group.add(option1);
        group.add(option2);

        // current = option1;
    }




}
