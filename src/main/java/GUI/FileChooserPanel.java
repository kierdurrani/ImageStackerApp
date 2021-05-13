package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class FileChooserPanel extends JPanel {

    // TODO: Implement singleton pattern?

    public FileChooserPanel(){

        // Initialise list model in the GUI with values. (A model is a list representation of the data)
        DefaultListModel listModel = new DefaultListModel();
        listModel.addElement("Jane Doe");
        listModel.addElement("John Smith");
        listModel.addElement("Kathy Green");

        JList list = new JList(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setVisibleRowCount(-1);

        // Add list to scroll pane
        JScrollPane scrollPane = new JScrollPane(list);
        // scrollPane.setPreferredSize(new Dimension(300,100));

        // Create Buttons
        JButton ButtonRemoveImg =new JButton("Remove Image");
        ButtonRemoveImg.setSize(new Dimension(250, 25));
        ButtonRemoveImg.setLayout(null);
        ButtonRemoveImg.setLocation(0,50);
        ButtonRemoveImg.addActionListener(	new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for(Object o : list.getSelectedValuesList() ){
                    System.out.println(o);
                    listModel.removeElement(o);
                }
            }
        });

        JButton ButtonAddImg =new JButton("Add Image");
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

        // Create Button Container
        JPanel ButtonContainer = new JPanel ();
        ButtonContainer.add(ButtonRemoveImg, BorderLayout.WEST);
        ButtonContainer.add(ButtonAddImg, BorderLayout.EAST);

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
