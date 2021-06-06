package stacker.stacking;

import stacker.ProgressBar;
import stacker.alignment.StackableImages;
import stacker.images.RGBImage;
import java.io.IOException;

// Any implementing class should take a set of stackable images and return the RGB image of the stack

public abstract class AbstractStackingMethod {

    public abstract RGBImage stackImages(StackableImages stackableImages, ProgressBar progressBar) throws IOException;

    public RGBImage stackImages(StackableImages stackableImages) throws IOException{
        return stackImages(stackableImages, new ProgressBar("Dummy Stacking Progressbar"));
    }

}
