package GUI.StackingContext;

import GUI.AlignmentContext.AlignmentContextPanel;
import GUI.AlignmentContext.FileSelectPanel;
import GUI.AlignmentContext.NavigationPanel;
import GUI.GUILauncher;
import GUI.GeneralPanel;
import GUI.StackerInterface;
import stacker.ProgressBar;
import stacker.images.RGBImage;
import stacker.stacking.StackingMethodPreTransform;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class StackContextPanel extends JPanel{

    public JTextArea statusTextArea = new JTextArea("Click 'Stack Images' to start");

    public StackContextPanel(AlignmentContextPanel previousNavigationPanel)
    {

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setMaximumSize(new Dimension(400, 900));
        this.setPreferredSize(new Dimension(400, 900));

        // region Add file list panel
        FileSelectPanel fileSelectPanel = new FileSelectPanel(StackerInterface.getStackableImages().getImagePaths());
        fileSelectPanel.remove(fileSelectPanel.buttonContainer);
        fileSelectPanel.revalidate();
        fileSelectPanel.repaint();

        fileSelectPanel.setMinimumSize  (new Dimension(500,200));
        fileSelectPanel.setPreferredSize(new Dimension(500,300));
        fileSelectPanel.setMaximumSize  (new Dimension(700,300));

        this.add(fileSelectPanel);
        // endregion

        StackSettingsPanel stackSettingsPanel = new StackSettingsPanel();

        // Button to stack images
        JButton stackButton =  new JButton("            Stack Images!            ");
        stackButton.setPreferredSize(new Dimension(500, 25 ));
        stackButton.setMinimumSize(new Dimension(500, 25 ));
        stackSettingsPanel.add(stackButton);

        stackButton.addActionListener(e -> {
            
            Thread stackerThread = new Thread( () -> {

                ProgressBar progressBar = new ProgressBar("Stacking Images");
                Thread progressPollerThread = new Thread( () -> {
                    while(true)
                    {
                        try
                        {
                            Thread.sleep(500);
                            statusTextArea.setForeground(new Color(70, 70, 70));
                            statusTextArea.setText(progressBar.printPercent());

                        } catch (InterruptedException exception) {}
                    }
                });
                progressPollerThread.start();

                try
                {
                    String workingDir = stackSettingsPanel.workingDirectoryField.getText();
                    String stackName  = stackSettingsPanel.outFileNameField.getText();

                    StackingMethodPreTransform preTransform = new StackingMethodPreTransform(workingDir, stackName);
                    RGBImage stack =  StackerInterface.stackImages(preTransform, progressBar);
                    stack.writeToDisk(workingDir + "\\" + stackName);
                    progressPollerThread.interrupt();
                    progressPollerThread.stop();

                    statusTextArea.setForeground(new Color(0, 150, 0));
                    statusTextArea.setText("Stacking Complete \nImage saved: " + stackName);

                    GeneralPanel.setPreviewImage(stack.makeBufferedImage());


                } catch (IOException exception) {
                    // Files missing during stacking
                    exception.printStackTrace();
                }


            });
            stackerThread.start();
        });
        this.add(stackSettingsPanel);

        statusTextArea.setPreferredSize(new Dimension(500,25 ));
        statusTextArea.setEditable(false);
        statusTextArea.setBackground(new Color(255,255,255));
        this.add(statusTextArea);

        // Back Button
        JButton backButton =  new JButton("<< Back");
        stackButton.setPreferredSize(new Dimension(500, 25 ));
        this.add(backButton);
        backButton.addActionListener( e -> {
            if(previousNavigationPanel == null){

                GUILauncher.generalPanel.setVisible(false);
                GUILauncher.landingPanel.setVisible(true);
                GUILauncher.mainFrame.setSize(new Dimension(500,615));

            }else{

                GeneralPanel.setOptionsPanel(previousNavigationPanel);
            }
        });


        this.setVisible(true);
        this.setEnabled(true);
    }

}
