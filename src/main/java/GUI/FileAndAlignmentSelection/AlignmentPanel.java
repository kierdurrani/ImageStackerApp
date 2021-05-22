package GUI.FileAndAlignmentSelection;

import GUI.GeneralPanel;
import GUI.StackerInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class AlignmentPanel extends JPanel {

    private FileAndAlignmentPanel parentFileAndAlignmentPanel;
    JButton saveParamButton;

    AlignmentPanel(FileAndAlignmentPanel parent) {

        this.parentFileAndAlignmentPanel = parent;
        setBorder(BorderFactory.createTitledBorder("Alignment Options"));

        // TODO: Allow user to modify star detection params

        // Panel: Radio buttons for alignment method.
        AlignmentMethodRadioPanel radioButtonPanel = new AlignmentMethodRadioPanel();

        // Panel: Config for this alignment method
        JPanel alignOptionsPanel = new JPanel();
        alignOptionsPanel.setLayout(new BoxLayout(alignOptionsPanel, BoxLayout.Y_AXIS));

        JButton testStarDetection = new JButton("Test Star Detection");
        testStarDetection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: Only enable button if a proper pic is selected.

                BufferedImage markedImage = StackerInterface.markStarsInImage(GeneralPanel.getPreviewImage());
                GeneralPanel.setPreviewImage( markedImage );
            }
        });
        alignOptionsPanel.add(testStarDetection);

        JButton calcAlignParamsButton = new JButton("Calculate Alignment Parameters!");

        calcAlignParamsButton.addActionListener(e -> {

            System.out.println("Clicked Calculate Alignment button..");
            parentFileAndAlignmentPanel.disableNavigationButtons();
            calcAlignParamsButton.setEnabled(false);

            // Obtain file paths from list and stack!
            ListModel list = parentFileAndAlignmentPanel.fileSelectionPanel.fileJList.getModel();
            String[] imagePaths = new String[list.getSize()];

            for (int i = 0; i < list.getSize(); i++) {
                System.out.println(list.getElementAt(i));
                imagePaths[i] = (String) list.getElementAt(i);
            }

            Thread t1 = new Thread(new Runnable() {
                public void run()
                {
                    StackerInterface.calculateStackableImages(imagePaths);
                    parentFileAndAlignmentPanel.enableNavigationButtons();
                    calcAlignParamsButton.setEnabled(true);
                }
            });
            t1.start();
        });

        alignOptionsPanel.add(calcAlignParamsButton);

        // Panel: Results & Save options
        JPanel resultsPanel = new JPanel();

        JTextArea jTextArea = new JTextArea("Text Display..");
        jTextArea.setEditable(false);
        jTextArea.setText("");
        alignOptionsPanel.add(jTextArea);

        saveParamButton = new JButton("Save Parameters");
        saveParamButton.setEnabled(false);
        alignOptionsPanel.add(saveParamButton);
        saveParamButton.setVisible(false);
        saveParamButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // TODO: clean up and save the actual real data
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.showSaveDialog(jFileChooser);

                if(jFileChooser.getSelectedFile() == null){
                    System.out.println("No File Selected");
                    return;
                }

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
                    // TODO - validation that this is not null
                    // Ideally this button will always be disabled unless this is populated, but possibly extra valiation for safety?
                    StackerInterface.writeStringArrayToFile(file.getAbsolutePath(), StackerInterface.getStackableImages().getStringRepresentation());
                            //stackableImages.getStringRepresentation());

                } catch (IOException ioException) {
                    System.out.println("REEE");
                }
            }
        });


        // Add the button and containers to this panel.

        // this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setLayout(new BorderLayout());
        this.add(radioButtonPanel, BorderLayout.NORTH); //, BorderLayout.NORTH);
        this.add(alignOptionsPanel, BorderLayout.CENTER); //, BorderLayout.EAST);
        this.add(resultsPanel, BorderLayout.SOUTH); //, BorderLayout.SOUTH);

    }

}

