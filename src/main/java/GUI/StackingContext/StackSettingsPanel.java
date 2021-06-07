package GUI.StackingContext;

import GUI.StackerInterface;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StackSettingsPanel extends JPanel {

    public JTextField workingDirectoryField;
    public JTextField outFileNameField;

    public StackSettingsPanel()
    {
        super();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // region Add Text Fields

        String firstImagePath =  StackerInterface.getStackableImages().getImagePaths()[0];

        this.add(new JLabel("Working Directory: "));
        String defaultWorkingDir = firstImagePath.replaceAll( "[^\\\\]*$" ,"").replaceAll("\\\\$","");
        workingDirectoryField = new JTextField(defaultWorkingDir);
        this.add(workingDirectoryField);

        this.add(new JLabel("Output FileName: "));
        String dateString = (new SimpleDateFormat("yyyyMMdd-HHmm")).format(new Date());
        outFileNameField = new JTextField("MyStack-" + dateString + ".png" );
        this.add(outFileNameField);

        // endregion

    }


}
