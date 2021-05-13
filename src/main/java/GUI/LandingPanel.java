package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LandingPanel extends JPanel {

    public LandingPanel(){

        ImageIcon logoIcon = new ImageIcon("C:\\Users\\Kier\\Developing\\ImageStackerGUI\\src\\main\\java\\resources\\img.png");

        this.setLayout(new BorderLayout());
        this.add( new JLabel( logoIcon, JLabel.CENTER) );

        //region create buttonPanel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3,1));

        buttonPanel.setToolTipText("");

        JButton scratchButton = new JButton("Select images and align");
        JButton importButton = new JButton("Import existing alignment settings");
        JButton editButton = new JButton("Edit saved photo");

        scratchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GUILauncher.landingPanel.setVisible(false);
                GUILauncher.chooserPanel.setVisible(true);
            }
        });
        // TODO: Implement functionality of other buttons.

        buttonPanel.add(scratchButton);
        buttonPanel.add(importButton);
        buttonPanel.add(editButton);
        //endregion create buttonPanel

        // TODO: fix layout to make more aesthetic when expanded.
        this.add( buttonPanel, BorderLayout.SOUTH);

    }

}
