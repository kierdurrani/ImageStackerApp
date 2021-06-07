package GUI.AlignmentContext;

import GUI.GeneralPanel;

import javax.swing.*;
import java.awt.*;

public class AlignmentContextPanel extends JPanel {

    // StackableImages stackableImages; // This field needs populating before continuing.
    // TODO - if stackable images already exists in the parent, import this and set all enabled?

    FileSelectPanel fileSelectionPanel;
    AlignmentOptionsPanel alignmentPanel;
    NavigationPanel navigationPanel;

    public AlignmentContextPanel() {

        String[] defaultValues = {
                "C:\\Users\\Kier\\Developing\\Space Image Stack Project\\PICTURE LIBRARY\\282CANON\\IMG_1311.JPG",
                "C:\\Users\\Kier\\Developing\\Space Image Stack Project\\PICTURE LIBRARY\\282CANON\\IMG_1320.JPG"};
        fileSelectionPanel = new FileSelectPanel(defaultValues);
        alignmentPanel = new AlignmentOptionsPanel(this);
        navigationPanel = new NavigationPanel(this);

        // Create window frame, add scroll pain and button container.
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setPreferredSize(new Dimension(400, 900));
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

    public void setNavigationButtonsEnabled(boolean isEnabled){

        navigationPanel.nextButton.setEnabled(isEnabled);
        navigationPanel.backButton.setEnabled(isEnabled);
        fileSelectionPanel.fileJList.setEnabled(isEnabled);
        fileSelectionPanel.setEnabled(isEnabled);
        fileSelectionPanel.ButtonAddImg.setEnabled(isEnabled);
        fileSelectionPanel.ButtonRemoveImg.setEnabled(isEnabled);
        alignmentPanel.saveParamButton.setEnabled(isEnabled);

        if(isEnabled){
            alignmentPanel.saveParamButton.setVisible(true);
        }
        
        this.repaint();

    }


}
