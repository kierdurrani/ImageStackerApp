package GUI.FileAndAlignmentSelection;

import javax.swing.*;
import javax.swing.plaf.BorderUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NavigationPanel extends JPanel {

    FileAndAlignmentPanel parent;

    NavigationPanel(FileAndAlignmentPanel parentPanel){

        this.parent = parentPanel;

        JButton backButton = new JButton("<< Back");
        JButton nextButton = new JButton("Next >>");

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        this.setLayout(new BorderLayout());
        this.add(backButton, BorderLayout.WEST);
        this.add(nextButton, BorderLayout.EAST);

        backButton.setPreferredSize(new Dimension(200,25));
        nextButton.setPreferredSize(new Dimension(200,25));

    }

}
