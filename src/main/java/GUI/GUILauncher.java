package GUI;

import javax.swing.*;
import java.awt.*;
import stacker.StackableImages;

// Swing DOCS - https://docs.oracle.com/javase/tutorial/uiswing/components/list.html

public class GUILauncher {

    static LandingPanel landingPanel= new LandingPanel();
    static FileChooserPanel chooserPanel = new FileChooserPanel();
    static StackableImages stackableImages;

    public static void main(String args[]) throws InterruptedException {

        // Create window frame
        JFrame mainFrame = new JFrame("Image Stacker GUI");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add Panels to swap in and out using the card layout
        mainFrame.setLayout(new CardLayout());
        mainFrame.add( landingPanel);
        mainFrame.add( chooserPanel);
        chooserPanel.setVisible(false);

        mainFrame.setSize(new Dimension(500,600));
        mainFrame.setVisible(true);


    }
}
