package GUI;

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

public class FileChooserPanel extends JPanel {

    GeneralPanel parentPanel;

    public FileChooserPanel( GeneralPanel parentGeneralPanel){

        parentPanel = parentGeneralPanel;

        // Initialise list model in the GUI with values. (A model is a list representation of the data)
        DefaultListModel listModel = new DefaultListModel();

        // Create Buttons
        JButton ButtonRemoveImg =new JButton("Remove Image(s)");
        JButton ButtonAddImg =new JButton("Add Image(s)");

        JList list = new JList(listModel);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setVisibleRowCount(-1);
        list.addListSelectionListener(new ListSelectionListener(){
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(list.getSelectedIndices().length > 0){
                    ButtonRemoveImg.setEnabled(true);
                    int x = e.getFirstIndex();
                    String selectedPath = (String) list.getSelectedValue();
                    try {
                        BufferedImage bufferedImagePreview = ImageIO.read(new File(selectedPath));
                        parentGeneralPanel.previewPanel.setImage(bufferedImagePreview);
                    } catch (IOException ioException) {
                        System.out.println("Failed buffered Import");

                    }

                }else{
                    ButtonRemoveImg.setEnabled(false);
                }
            }

        });

        // Add list to scroll pane
        JScrollPane scrollPane = new JScrollPane(list);

        // Button Settings
        ButtonAddImg.setEnabled(true);
        ButtonAddImg.setSize(new Dimension(250, 25));
        ButtonAddImg.addActionListener(	new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                JFileChooser chooser = new JFileChooser();
                chooser.setMultiSelectionEnabled(true);
                int returnVal = chooser.showOpenDialog(null);

                File f = chooser.getSelectedFile();
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File[] selectedFile = chooser.getSelectedFiles();
                    for( File fileN : selectedFile){
                        System.out.println( fileN.getAbsolutePath()) ;
                        // TODO: File validation & load thumbnails ?
                        if( ! listModel.contains(fileN.getAbsolutePath() )) {
                            listModel.addElement(fileN.getAbsolutePath());
                        }
                    }
                }
            }
        });

        ButtonRemoveImg.setSize(new Dimension(250, 25));
        ButtonRemoveImg.setLayout(null);
        ButtonRemoveImg.setLocation(0,50);
        ButtonRemoveImg.setEnabled(false);
        ButtonRemoveImg.addActionListener(	new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ButtonRemoveImg.setEnabled(false);
                for(Object o : list.getSelectedValuesList() ){
                    System.out.println(o);
                    listModel.removeElement(o);
                }
            }
        });

        // Create Button Container
        JPanel ButtonContainer = new JPanel ();
        ButtonContainer.setLayout(new BorderLayout());
        ButtonContainer.add(ButtonAddImg,  BorderLayout.NORTH);
        ButtonContainer.add(ButtonRemoveImg, BorderLayout.SOUTH);

        // Container Panel
        JPanel filePanel = new JPanel();
        filePanel.setLayout(new BorderLayout());
        filePanel.add(scrollPane, BorderLayout.CENTER);
        filePanel.add(ButtonContainer, BorderLayout.SOUTH);
        filePanel.setBorder(BorderFactory.createTitledBorder("Select Image Files"));

        // Create window frame, add scroll pain and button container.
        this.setLayout(new BorderLayout());
        this.add( filePanel, BorderLayout.CENTER );

       this.add( new RadioButtonPanel(), BorderLayout.SOUTH);



    }
}
