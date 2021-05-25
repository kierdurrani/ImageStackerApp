package GUI.FileAndAlignmentSelection;

import GUI.GUILauncher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import GUI.GeneralPanel;
import GUI.StackerInterface;

public class NavigationPanel extends JPanel {

    FileAndAlignmentPanel parent;

    JButton backButton = new JButton("<< Back");
    JButton nextButton = new JButton("Next >>");

    NavigationPanel(FileAndAlignmentPanel parentPanel){

        this.parent = parentPanel;

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GUILauncher.generalPanel.setVisible(false);
                GUILauncher.landingPanel.setVisible(true);
                GUILauncher.mainFrame.setSize(new Dimension(500,615));
            }
        });
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // GeneralPanel.getGeneralPanel().option

                if( StackerInterface.getStackableImages() != null ){


                        System.out.println("Next button clicked. Going to the stacking panel");
                        JPanel panel = new StackSettingsPanel();
                        GeneralPanel.setOptionsPanel(panel);
                        //GeneralPanel.setOptionsPanel(panel);

                        System.out.println("got here");
                        System.out.println(GeneralPanel.getGeneralPanel().optionsPanel);

            // todo- see


                    // FIXME - doesnt render properly

                }
            }
        });
        nextButton.setEnabled(false);

        this.setLayout(new BorderLayout());
        this.add(backButton, BorderLayout.WEST);
        this.add(nextButton, BorderLayout.EAST);

        backButton.setPreferredSize(new Dimension(200,30));
        nextButton.setPreferredSize(new Dimension(200,30));

    }

}
