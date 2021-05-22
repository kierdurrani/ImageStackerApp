package GUI;

import GUI.FileAndAlignmentSelection.FileAndAlignmentPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public final class GeneralPanel extends JPanel {

    // Singleton Class - Panel to hold any choice of optionsPanel + a preview of the image being worked on.
    private final static GeneralPanel generalPanel = new GeneralPanel();

    private JPanel optionsPanel;
    private PreviewPanel previewPanel;

    // constructor for singleton
    private GeneralPanel(){

        optionsPanel = new FileAndAlignmentPanel(this);
        previewPanel = new PreviewPanel();

        this.setLayout(new BorderLayout());
        this.add(optionsPanel, BorderLayout.WEST);
        this.add(previewPanel, BorderLayout.CENTER);

        optionsPanel.setPreferredSize(new Dimension(400,1080));

    }

    // Getters
    public static GeneralPanel getGeneralPanel(){
        return generalPanel;
    }

    public static BufferedImage getPreviewImage(){
        return getGeneralPanel().previewPanel.getImage();
    }

    public static void setPreviewImage(BufferedImage image){
        getGeneralPanel().previewPanel.setImage(image);
    }

    public static void setOptionsPanel(JPanel newOptionsPanel){
        try {
            getGeneralPanel().remove(getGeneralPanel().optionsPanel);
        }catch (NullPointerException e){
            System.out.println("The options panel was null. This is unexpected, but non fatal.");
        }
        getGeneralPanel().optionsPanel = newOptionsPanel;
        getGeneralPanel().add(newOptionsPanel, BorderLayout.WEST);
        getGeneralPanel().repaint();
    }




}
