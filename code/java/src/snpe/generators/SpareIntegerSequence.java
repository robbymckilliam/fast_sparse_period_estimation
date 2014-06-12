package snpe.generators;

/**
 * Interface for genearting sparse integer sequences
 * @author Robby McKilliam
 */
public interface SpareIntegerSequence {
    
    /** The length of thus sparse sequence */
    int length();
    
    /** Returns an array of integers of length length(); */
    int[] generate();
    
}
