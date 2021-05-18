package GUI;

import javax.swing.*;
import java.awt.*;

public class FileAndAlignmentPanel extends JPanel {

    GeneralPanel parentGeneralPanel;
    FileSelectionPanel fileSelectionPanel;

    public FileAndAlignmentPanel(GeneralPanel parentGeneralPanel) {

        this.parentGeneralPanel = parentGeneralPanel;
        fileSelectionPanel = new FileSelectionPanel(this);


        // Create window frame, add scroll pain and button container.

        this.setLayout(new BorderLayout());
        this.setMaximumSize(new Dimension(200, 900));

        this.add(fileSelectionPanel, BorderLayout.CENTER);
        this.add(new AlignmentPanel(this), BorderLayout.SOUTH);

    }
}
