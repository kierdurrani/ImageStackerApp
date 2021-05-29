package GUI.FileAndAlignmentSelection;

import GUI.GeneralPanel;
import GUI.StackerInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class AlignmentPanel extends JPanel {

    private FileAndAlignmentPanel parentFileAndAlignmentPanel;
    JButton saveParamButton = new JButton("Save Parameters");
    JTextArea jTextArea = new JTextArea("Text Display..");


    AlignmentPanel(FileAndAlignmentPanel parent) {

        this.parentFileAndAlignmentPanel = parent;
        setBorder(BorderFactory.createTitledBorder("Alignment Options"));

        // TODO: Allow user to modify star detection params & method

        // Panel: Radio buttons for alignment method.
        AlignmentMethodRadioPanel radioButtonPanel = new AlignmentMethodRadioPanel();

        // Panel: Config for this alignment method
        JPanel alignOptionsPanel = new JPanel();
        alignOptionsPanel.setLayout(new BoxLayout(alignOptionsPanel, BoxLayout.Y_AXIS));

        JButton testStarDetection = new JButton("Test Star Detection");
        testStarDetection.addActionListener(e -> {
            // TODO: Only enable button if a proper pic is selected.
            testStarDetection.setEnabled(false);
            System.out.println( parentFileAndAlignmentPanel.fileSelectionPanel.fileJList.getSelectedValue() );
            BufferedImage markedImage = StackerInterface.markStarsInImage(GeneralPanel.getPreviewImage());
            GeneralPanel.setPreviewImage( markedImage );
            testStarDetection.setEnabled(true);
        });
        alignOptionsPanel.add(testStarDetection);

        JButton calcAlignParamsButton = new JButton("Calculate Alignment Parameters!");

        calcAlignParamsButton.addActionListener(e -> {

            System.out.println("Clicked Calculate Alignment button..");
            parentFileAndAlignmentPanel.setNavigationButtonsEnabled(false);
            testStarDetection.setEnabled(false);
            calcAlignParamsButton.setEnabled(false);

            jTextArea.setText("Calculating Stacking Parameters..");

            // Obtain file paths from list and stack!
            ListModel list = parentFileAndAlignmentPanel.fileSelectionPanel.fileJList.getModel();
            String[] imagePaths = new String[list.getSize()];

            for (int i = 0; i < list.getSize(); i++) {
                System.out.println(list.getElementAt(i));
                imagePaths[i] = (String) list.getElementAt(i);
            }

            Thread t1 = new Thread( () -> {

                try {
                    StackerInterface.calculateStackableImages(imagePaths);
                }catch(IOException exception){
                    // TODO - implement error handling.
                }
                parentFileAndAlignmentPanel.setNavigationButtonsEnabled(true);
                calcAlignParamsButton.setEnabled(true);
                testStarDetection.setEnabled(true);


                if( StackerInterface.getStackableImages().isConsistent() ){
                    jTextArea.setText("Successfully Calculated alignment parms. \nReady to proceed. ");
                }else{
                    jTextArea.setText("Warning: stacking parameters not internally consistent.\nAlignment may fail");
                }

            });
            t1.start();
        });

        alignOptionsPanel.add(calcAlignParamsButton);

        // Panel: Results & Save options
        JPanel resultsPanel = new JPanel();


        jTextArea.setEditable(false);
        jTextArea.setText("");
        alignOptionsPanel.add(jTextArea);


        saveParamButton.setEnabled(false);
        alignOptionsPanel.add(saveParamButton);
        saveParamButton.setVisible(false);
        saveParamButton.addActionListener(e -> {

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
                // TODO - validation that this is not null - Ideally this button will always be disabled unless this is populated, but possibly extra validation for safety?
                StackerInterface.writeStringArrayToFile(file.getAbsolutePath(), StackerInterface.getStackableImages().getStringRepresentation());
            } catch (IOException ioException) {
                System.out.println("REEE");
                JOptionPane optionPane = new JOptionPane(ioException.getMessage(), JOptionPane.ERROR_MESSAGE);
                JDialog dialog = optionPane.createDialog("Failed to Save");
                dialog.setAlwaysOnTop(true);
                dialog.setVisible(true);
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

