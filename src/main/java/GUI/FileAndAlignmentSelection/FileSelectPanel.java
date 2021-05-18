package GUI.FileAndAlignmentSelection;

import GUI.GeneralPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FileSelectPanel extends JPanel {

    JList fileJList;
    FileAndAlignmentPanel parentFileAndAlignmentPanel;

    FileSelectPanel(FileAndAlignmentPanel parentPanel){
        this.parentFileAndAlignmentPanel = parentPanel;

        // Initialise listmodel = list representation of the data + dynamic methods
        DefaultListModel listModel = new DefaultListModel();
        fileJList = new JList(listModel);
        fileJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        fileJList.setLayoutOrientation(JList.VERTICAL);
        fileJList.setVisibleRowCount(-1);

        listModel.addElement("C:\\Users\\Kier\\Developing\\Space Image Stack Project\\PICTURE LIBRARY\\282CANON\\IMG_1311.JPG");
        listModel.addElement("C:\\Users\\Kier\\Developing\\Space Image Stack Project\\PICTURE LIBRARY\\282CANON\\IMG_1320.JPG");

        // Add list to scroll pane
        JScrollPane scrollPane = new JScrollPane(fileJList);

        // Create Add/Remove image Buttons
        JButton ButtonRemoveImg = new JButton("Remove Image(s)");
        JButton ButtonAddImg = new JButton("Add Image(s)");

        ButtonAddImg.setEnabled(true);
        ButtonAddImg.setSize(new Dimension(200, 25));
        ButtonAddImg.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                JFileChooser chooser = new JFileChooser();
                chooser.setMultiSelectionEnabled(true);
                int returnVal = chooser.showOpenDialog(null);

                File f = chooser.getSelectedFile();
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File[] selectedFile = chooser.getSelectedFiles();
                    for (File fileN : selectedFile) {
                        System.out.println(fileN.getAbsolutePath());
                        // TODO: File validation & load thumbnails ?
                        if (!listModel.contains(fileN.getAbsolutePath())) {
                            listModel.addElement(fileN.getAbsolutePath());
                        }
                    }
                }
            }
        });

        ButtonRemoveImg.setSize(new Dimension(200, 25));
        ButtonRemoveImg.setLayout(null);
        ButtonRemoveImg.setLocation(0, 50);
        ButtonRemoveImg.setEnabled(false);
        ButtonRemoveImg.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ButtonRemoveImg.setEnabled(false);
                for (Object o : fileJList.getSelectedValuesList()) {
                    System.out.println(o);
                    listModel.removeElement(o);
                }
            }
        });

        // Create Button Container - to keep layout tidy.
        JPanel ButtonContainer = new JPanel();
        ButtonContainer.setLayout(new BorderLayout());
        ButtonContainer.add(ButtonAddImg, BorderLayout.NORTH);
        ButtonContainer.add(ButtonRemoveImg, BorderLayout.SOUTH);

        // JList functionality
        fileJList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (fileJList.getSelectedIndices().length > 0) {
                    ButtonRemoveImg.setEnabled(true);
                    int x = e.getFirstIndex();
                    String selectedPath = (String) fileJList.getSelectedValue();
                    try {
                        BufferedImage bufferedImagePreview = ImageIO.read(new File(selectedPath));
                        GeneralPanel.setPreviewImage(bufferedImagePreview);
                    } catch (IOException ioException) {
                        System.out.println("Failed buffered Import");
                    }

                } else {
                    ButtonRemoveImg.setEnabled(false);
                }
            }
        });

        // Button Container Panel - for layout
        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(ButtonContainer, BorderLayout.SOUTH);
        this.setBorder(BorderFactory.createTitledBorder("Select Image Files"));

    }

}
