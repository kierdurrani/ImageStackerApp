package stacker.alignment;

import stacker.images.*;

import java.io.IOException;

public abstract class AbstractAlignmentMethod {

    // This interface can be implemented to take a set of filePaths and calculate their alignment parameters from this.
    // In C# this would probably be a delegate.

    public abstract OffsetParameters[][] calculateAllAlignments(String[] filePaths) throws IOException;


    public abstract OffsetParameters calculateOffsetParameters(GreyImage img1, GreyImage img2);

    // TODO - have the first function as a concrete function which calls the 2nd in an array

}
