package GUI.StackSettingsSelection;

import GUI.GeneralPanel;
import stacker.StackableImages;

import javax.swing.*;
import java.awt.*;

public class StackSettingsPanel extends JPanel{

    // This field needs populating before continuing.
    StackableImages stackableImages;

    public StackSettingsPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setMaximumSize(new Dimension(200, 900));
    }



}
