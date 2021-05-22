package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LandingPanel extends JPanel {

    public LandingPanel(){

        ImageIcon logoIcon = new ImageIcon("C:\\Users\\Kier\\Developing\\ImageStackerGUI\\src\\main\\java\\resources\\img.png");

        // this.setLayout(new BorderLayout());

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));




        //region create buttonPanel
        //JPanel buttonPanel = new JPanel();
        //buttonPanel.setLayout(new GridLayout(3,1));

        //buttonPanel.setToolTipText("");
        JLabel menuImage = new JLabel( logoIcon, JLabel.CENTER);
        JButton scratchButton = new JButton("Select images and align");
        JButton importButton = new JButton("Import existing alignment settings");
        JButton editButton = new JButton("Edit saved photo");

        menuImage.setPreferredSize(new Dimension(500, 500));
        scratchButton.setPreferredSize(new Dimension(500,25));
        importButton.setPreferredSize(new Dimension(500,25));
        editButton.setPreferredSize(new Dimension(500,25));

        menuImage.setMaximumSize(new Dimension(1920, 1080));
        scratchButton.setMaximumSize(new Dimension(1920,25));
        importButton.setMaximumSize(new Dimension(1920,25));
        editButton.setMaximumSize(new Dimension(1920,25));


        scratchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GUILauncher.mainFrame.setSize(new Dimension(900, 600));
                GUILauncher.landingPanel.setVisible(false);
                GUILauncher.generalPanel.setVisible(true);
            }
        });
        // TODO: Implement functionality of other buttons.

        this.add( menuImage );
        this.add(scratchButton);
        this.add(importButton);
        this.add(editButton);
        //endregion create buttonPanel

        // TODO: fix layout to make more aesthetic when expanded.
       // this.add( buttonPanel, BorderLayout.SOUTH);

    }

}
