import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

public class ImageStackerApp {

    public static Logger MainLogger = Logger.getLogger(ImageStackerApp.class);
    public static int write = 0;

    public static void main(String[] args) throws IOException {
        MainLogger.info("Program has started.");

        // IMPORTS IMAGES AND PUTS THEM INTO THE ARRAY: allImages
        int numOfImages = 2;
        RGBImage[] allImages = new RGBImage[numOfImages];
        for (int i = 1; i <= numOfImages; i++) {
            String ImagePath = "C:\\Users\\Fairooz\\Desktop\\Stack Testing\\INPUT2\\" + i + ".jpg";
            allImages[i - 1] = new RGBImage(ImagePath);
            MainLogger.info("Image #" + i + " Imported Successfully");
        }


        ConsistentSetOfStackableImages CSSI = new ConsistentSetOfStackableImages(allImages);

        CSSI.isConsistent();
        CSSI.print();


        ObjectMapper objMapper = new ObjectMapper();
        try {

            objMapper.writeValue(new File("C:\\Users\\Fairooz\\Desktop\\Stack Testing\\JSON\\CSoSIout2.json"), CSSI);
            String thisAsString2 = objMapper.writeValueAsString(CSSI);
            System.out.println(thisAsString2);

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }




//        ConsistentSetOfStackableImages inportedStack = new ConsistentSetOfStackableImages(allImages, "C:\\Users\\Fairooz\\Desktop\\Stack Testing\\output\\testout2.txt");
//       RGBImage finalImage = inportedStack.stackAll();
//        finalImage.writeToDisk("C:\\Users\\Fairooz\\Desktop\\Stack Testing\\xmlFINAL.png");

    }
}
