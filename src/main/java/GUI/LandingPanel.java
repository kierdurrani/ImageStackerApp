package GUI;

import GUI.StackingContext.StackSettingsPanel;
import stacker.alignment.ImportException;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class LandingPanel extends JPanel {

    public LandingPanel()
    {

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel logoPanel = new LandingLogo();
        JPanel buttonContainer = new JPanel();


        // region button setup
        JButton scratchButton = new JButton("Select images and align");
        JButton importButton = new JButton("Import existing alignment settings");
        JButton editButton = new JButton("Edit saved photo");

        scratchButton.setMinimumSize(new Dimension(500,25));
        importButton.setMinimumSize(new Dimension(500,25));
        editButton.setMinimumSize(new Dimension(500,25));

        scratchButton.setPreferredSize(new Dimension(500,25));
        importButton.setPreferredSize(new Dimension(500,25));
        editButton.setPreferredSize(new Dimension(500,25));

        scratchButton.addActionListener(e -> {
            GUILauncher.mainFrame.setSize(new Dimension(900, 600));
            GUILauncher.landingPanel.setVisible(false);
            GUILauncher.generalPanel.setVisible(true);
        });
        importButton.addActionListener( e -> {

            JFileChooser chooser = new JFileChooser();
            chooser.showOpenDialog(null);
            File f = chooser.getSelectedFile();
            System.out.println(f.getAbsolutePath());

            try {
                StackerInterface.importStackableImage(f.getAbsolutePath());
            }catch ( ImportException exception ){
                System.out.println(exception.getMessage());

                JOptionPane optionPane = new JOptionPane(exception.getMessage(), JOptionPane.ERROR_MESSAGE);
                JDialog dialog = optionPane.createDialog("Failed to Import");
                dialog.setAlwaysOnTop(true);
                dialog.setVisible(true);
                return;

            }

            GeneralPanel.setOptionsPanel(new StackSettingsPanel(true));
            GUILauncher.mainFrame.setSize(new Dimension(900, 600));
            GUILauncher.landingPanel.setVisible(false);
            GUILauncher.generalPanel.setVisible(true);


        });
        // TODO: Implement functionality of other button(s).

        buttonContainer.setLayout(new GridLayout(3,1));
        buttonContainer.setPreferredSize(new Dimension(500,75));
        buttonContainer.setMaximumSize(new Dimension(10000,75));

        buttonContainer.add(scratchButton);
        buttonContainer.add(importButton);
        buttonContainer.add(editButton);
        // endregion

        this.add(logoPanel);
        this.add(buttonContainer);
    }

}
