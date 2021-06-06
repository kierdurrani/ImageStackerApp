package GUI.FileAndAlignmentSelection;

import GUI.StackerInterface;
import stacker.alignment.StackableImages;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StackSettingsPanel extends JPanel{

    // This field needs populating before continuing.
    StackableImages stackableImages;



    public StackSettingsPanel() {

        String dateString = (new  SimpleDateFormat("yyyy-MM-dd::HH:mm")).format(new Date());
        JTextField outFileField = new JTextField("MyStack:" + dateString + ".png" );

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setMaximumSize(new Dimension(400, 900));
        this.setPreferredSize(new Dimension(400, 900));

        // Visible list of files to use in the image
        FileSelectPanel p = new FileSelectPanel(StackerInterface.getStackableImages().getImagePaths());
        p.remove(p.buttonContainer);
        p.revalidate();
        p.repaint();
        p.setMaximumSize(new Dimension(700,300));
        p.setPreferredSize(new Dimension(500,300));
        this.add(p);

        // Add text fields
        JPanel ContainerPanel = new JPanel();
        ContainerPanel.setLayout(new BoxLayout(ContainerPanel, BoxLayout.Y_AXIS));
        this.add(ContainerPanel);

        String firstImagePath =  StackerInterface.getStackableImages().getImagePaths()[0];
        String folder = firstImagePath.replaceAll( "[^\\\\]*$" ,"").replaceAll("\\\\$","");

        JTextField outDirectoryField =  new JTextField(folder);
        ContainerPanel.add(new JLabel("Working Directory: "));
        ContainerPanel.add(outDirectoryField);

        ContainerPanel.add(new JLabel("Output FileName: "));
        ContainerPanel.add(outFileField);

        // Button to stack images
        JButton stackButton =  new JButton("Stack Images!");
        ContainerPanel.add(stackButton);

        stackButton.addActionListener(e -> {
            
            Thread stackerThread = new Thread( () -> {

                StackerInterface.stackImages1();


            });
            stackerThread.start();
        });



        this.setVisible(true);
        this.setEnabled(true);

        outDirectoryField.setMaximumSize(new Dimension(1000, 25 ) );


    }



}
