package GUI.FileAndAlignmentSelection;

import GUI.GeneralPanel;
import stacker.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AlignmentPanel extends JPanel {

    private FileAndAlignmentPanel parentFileAndAlignmentPanel;

    AlignmentPanel(FileAndAlignmentPanel parent) {

        parentFileAndAlignmentPanel = parent;
        setBorder(BorderFactory.createTitledBorder("Alignment Options"));
        setLayout(new BorderLayout());

        add(new AlignmentMethodRadioButton(), BorderLayout.CENTER);

        // TODO: Allow user to modify Start Detection params

        // Star Detection Test button
        JButton testStarDetection = new JButton("Test Star Detection");
        testStarDetection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: Only have button do anything if a pic is selected.
                System.out.println("Making array rep");
                RGBImage rgbImage = RGBImage.makeFromBufferedImage(GeneralPanel.getPreviewImage());

                System.out.println("Finding Stars");
                ArrayList<StarCoordinates> starCords = OffsetParameters.getStarCords(rgbImage);

                System.out.println("Marking Stars. There were: " + starCords.size());
                for (StarCoordinates cord : starCords) {
                    rgbImage.makeGreenCross(cord.getX(), cord.getY());
                }
                System.out.println("Converting back");
                GeneralPanel.setPreviewImage(rgbImage.makeBufferedImage());
            }
        });
        this.add(testStarDetection, BorderLayout.EAST);


        // Container Panel for additional options
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));

        JTextArea jTextArea = new JTextArea("we");
        jTextArea.setEditable(false);
        jTextArea.setText("Click To Align");
        containerPanel.add(jTextArea);

        JButton saveParamButton = new JButton("Save Parameters");
        this.add(containerPanel, BorderLayout.SOUTH);
        saveParamButton.setEnabled(false);

        // Button for Calculating Alignment Params
        JButton calcAlignParamsButton = new JButton("Calculate Alignment Parameters!");
        calcAlignParamsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO - multi thread to keep main panel responsive?
                saveParamButton.setEnabled(false);

                ListModel list = parentFileAndAlignmentPanel.fileSelectionPanel.fileJList.getModel();
                String[] imagePaths = new String[list.getSize()];

                for (int i = 0; i < list.getSize(); i++) {
                    System.out.println(list.getElementAt(i));
                    imagePaths[i] = (String) list.getElementAt(i);
                }
                System.out.println("Creating Alignment Params");
                StackableImages stackableImages = new StackableImages(imagePaths);

                System.out.println("Complete");
                System.out.println(stackableImages.toString());

                //TODO: interactions with other buttons e.g. save button.
                parentFileAndAlignmentPanel.stackableImages = stackableImages;
                saveParamButton.setVisible(true);
                saveParamButton.setEnabled(true);

            }
        });


        // Add the buttons to the container panel
        containerPanel.add(calcAlignParamsButton);
        containerPanel.add(saveParamButton);
        saveParamButton.setVisible(false);
        saveParamButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // TODO: clean up and save the actual real data
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.showSaveDialog(jFileChooser);

                File file = jFileChooser.getSelectedFile();
                System.out.println(file);
                if(file.exists()){
                    int response = JOptionPane.showConfirmDialog(jFileChooser,
                            "Do you want to replace the existing file?", //
                            "Confirm", JOptionPane.YES_NO_OPTION, //
                            JOptionPane.QUESTION_MESSAGE);
                    if (response != JOptionPane.YES_OPTION) {
                        return;
                    }
                }

                try {
                    System.out.println(file.getAbsolutePath());
                    ImageStackerMain.writeStringArrayToFile(file.getAbsolutePath(), parentFileAndAlignmentPanel.stackableImages.getStringRepresentation());

                } catch (IOException ioException) {
                    System.out.println("REEE");
                }
            }
        });

    }


}

