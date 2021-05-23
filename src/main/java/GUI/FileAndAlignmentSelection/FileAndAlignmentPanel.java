package GUI.FileAndAlignmentSelection;

import GUI.GeneralPanel;
import stacker.StackableImages;

import javax.swing.*;
import java.awt.*;

public class FileAndAlignmentPanel extends JPanel {

    // StackableImages stackableImages; // This field needs populating before continuing.
    // TODO - if stackable images already exists in the parent, import this and set all enabled?

    FileSelectPanel fileSelectionPanel;
    AlignmentPanel alignmentPanel;
    NavigationPanel navigationPanel;

    public FileAndAlignmentPanel(GeneralPanel parentGeneralPanel) {

        fileSelectionPanel = new FileSelectPanel(this);
        alignmentPanel = new AlignmentPanel(this);
        navigationPanel = new NavigationPanel(this);


        // Create window frame, add scroll pain and button container.
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setMaximumSize(new Dimension(200, 900));

        // Add Components
        this.add(fileSelectionPanel);
        this.add(alignmentPanel);

        // Add glue to force navigation panel to bottom!
        Component glue = Box.createVerticalGlue();
        glue.setMaximumSize(new Dimension(450, 200));
        glue.setPreferredSize(new Dimension(450, 0));
        this.add(glue);
        this.add(navigationPanel);


        fileSelectionPanel.setMaximumSize(new Dimension(450, 700));
        fileSelectionPanel.setPreferredSize(new Dimension(450, 350));
        alignmentPanel.setMaximumSize(new Dimension(450,200));
        navigationPanel.setMaximumSize(new Dimension(450,30));
    }

    public void disableNavigationButtons(){

        navigationPanel.nextButton.setEnabled(false);
        navigationPanel.backButton.setEnabled(false);
        fileSelectionPanel.fileJList.setEnabled(false);
        fileSelectionPanel.setEnabled(false);
        fileSelectionPanel.ButtonAddImg.setEnabled(false);
        fileSelectionPanel.ButtonRemoveImg.setEnabled(false);
        alignmentPanel.saveParamButton.setEnabled(false);
        
        this.repaint();

    }

    public void setNavigationButtonsEnabled(boolean b){

        navigationPanel.nextButton.setEnabled(b);
        navigationPanel.backButton.setEnabled(b);
        fileSelectionPanel.fileJList.setEnabled(b);
        fileSelectionPanel.setEnabled(b);
        fileSelectionPanel.ButtonAddImg.setEnabled(b);
        fileSelectionPanel.ButtonRemoveImg.setEnabled(b);
        alignmentPanel.saveParamButton.setEnabled(b);

        if(b){
            alignmentPanel.saveParamButton.setVisible(true);
        }
        
        this.repaint();

    }


}
