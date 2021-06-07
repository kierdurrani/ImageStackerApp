package GUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class LandingLogo extends JPanel {


    private BufferedImage logoBufferedImage;

    public BufferedImage getLogo(String path) throws IOException
    {
        if(logoBufferedImage != null){
            return logoBufferedImage;
        }

        File file = new File(path);
        BufferedImage img = null;
        try{
            img = ImageIO.read(file);
            logoBufferedImage = img;
            return img;
        } catch (IOException e) {
            System.out.println("IO ERROR while trying to load logo image");
        }
        return img;
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        try
        {

            BufferedImage img = getLogo("C:\\Users\\Kier\\Developing\\ImageStackerGUI\\src\\main\\resources\\img.png");
            Graphics2D g2d = (Graphics2D) g.create();

            g2d.setTransform(new AffineTransform(this.getWidth()/400.0,0,0,this.getHeight()/400.0,0,0));

            g2d.drawImage(img,  null ,0,0);

            g2d.setColor(new Color(255,200,0));

            g2d.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, 28));
            g2d.drawString("Image Stacker", 165,485);

            g2d.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, 12));
            g2d.drawString("Version: " + GUILauncher.version, 350,460);


            g2d.dispose();


        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

}


