package GUI.FileAndAlignmentSelection;

import GUI.GeneralPanel;
import stacker.StackableImages;

import javax.swing.*;
import java.awt.*;

public class FileAndAlignmentPanel extends JPanel {

    StackableImages stackableImages; // This field needs populating before continuing.

    FileSelectPanel fileSelectionPanel = new FileSelectPanel(this);
    AlignmentPanel alignmentPanel = new AlignmentPanel(this);
    NavigationPanel navigationPanel = new NavigationPanel(this);

    public FileAndAlignmentPanel(GeneralPanel parentGeneralPanel) {

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
}