package GUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PreviewPanel extends JPanel {

    private BufferedImage image;
    private static AffineTransform transform = AffineTransform.getScaleInstance(1, 1);

    public PreviewPanel() {
        try {
            image = ImageIO.read(new File(GUILauncher.resourcesRoot + "defaultImage.jpg" ));
            System.out.println("Loaded Image for default" );
        } catch (IOException ex) {
            System.out.println("Failed to load image:" + (GUILauncher.resourcesRoot + "defaultImage.jpg" ));
        }

    }

    public void setImage(BufferedImage img) {
        this.image = img;
        this.repaint();
    }

    public BufferedImage getImage() {
        return image;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        float xRatio = (float) image.getWidth() / this.getSize().width;
        float yRatio = (float) image.getHeight() / this.getSize().height;

        transform = AffineTransform.getScaleInstance(1 / xRatio, 1 / yRatio);

        ((Graphics2D) g).drawImage(image, transform, null);
        g.setColor(Color.BLUE);

        // Check documentation for Graphics2D
    }


}
