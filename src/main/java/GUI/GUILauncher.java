package GUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

// Swing DOCS - https://docs.oracle.com/javase/tutorial/uiswing/components/list.html
// Buffered image has methods to edit image colour! https://docs.oracle.com/javase/7/docs/api/java/awt/image/BufferedImageOp.html

public class GUILauncher {

    public static JFrame mainFrame;
    public static LandingPanel landingPanel = new LandingPanel();
    public static GeneralPanel generalPanel = GeneralPanel.getGeneralPanel();

    public static final String resourcesRoot = "C:\\Users\\Kier\\Developing\\ImageStackerGUI\\src\\main\\resources\\"; // TODO - make dynamic

    public static void main(String args[]) throws InterruptedException {

        try {
            // TODO - fix
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        } catch (Exception e) { System.out.println("FAILURE: look and feel"); }

        // Create window frame
        mainFrame = new JFrame("Image Stacker GUI");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try{
            File f = new File(resourcesRoot + "icon.png");
            BufferedImage img = ImageIO.read(f);
            mainFrame.setIconImage(img);
        }catch(Exception e){}


        // Add Panels to swap in and out using the card layout
        mainFrame.setLayout(new CardLayout());
        mainFrame.add(landingPanel);
        landingPanel.setVisible(true);

        mainFrame.add(generalPanel);
        generalPanel.setVisible(false);

        // TODO
     //   PreviewPanel pPanel = new PreviewPanel();
     //   generalPanel.add(pPanel, BorderLayout.EAST);
     //   pPanel.setVisible(true);
     //   pPanel.repaint();


        mainFrame.validate();
        mainFrame.repaint();
        // TODO

        mainFrame.setSize(new Dimension(500,615));
        mainFrame.setVisible(true);

    }
}
