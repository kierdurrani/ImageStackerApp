package GUI;

import GUI.FileAndAlignmentSelection.FileAndAlignmentPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public final class GeneralPanel extends JPanel {

    // Singleton Class - Panel to hold any choice of optionsPanel + a preview of the image being worked on.
    private final static GeneralPanel generalPanel = new GeneralPanel();

    JPanel optionsPanel;
    PreviewPanel previewPanel;

    public static GeneralPanel getGeneralPanel(){
        return generalPanel;
    }

    public static BufferedImage getPreviewImage(){
        return getGeneralPanel().previewPanel.getImage();
    }

    public static void setPreviewImage(BufferedImage image){
        getGeneralPanel().previewPanel.setImage(image);
    }

    private GeneralPanel(){

        optionsPanel = new FileAndAlignmentPanel(this);
        previewPanel = new PreviewPanel();

        this.setLayout(new BorderLayout());
        this.add(optionsPanel, BorderLayout.WEST);
        this.add(previewPanel, BorderLayout.CENTER);

        optionsPanel.setPreferredSize(new Dimension(400,1080));


    }


}
