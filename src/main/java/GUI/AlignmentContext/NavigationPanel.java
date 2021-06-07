package GUI.AlignmentContext;

import GUI.GUILauncher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import GUI.GeneralPanel;
import GUI.StackerInterface;
import GUI.StackingContext.StackContextPanel;

public class NavigationPanel extends JPanel {

    public JButton backButton = new JButton("<< Back");
    public JButton nextButton = new JButton("Next >>");

    NavigationPanel(AlignmentContextPanel parentPanel){

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
                        JPanel panel = new StackContextPanel(false);
                        GeneralPanel.setOptionsPanel(panel);
                        //GeneralPanel.setOptionsPanel(panel);

                        System.out.println("got here");
                        System.out.println(GeneralPanel.getGeneralPanel().optionsPanel);


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
