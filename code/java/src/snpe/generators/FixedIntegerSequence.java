package snpe.generators;

/**
 * A SparseIntegerSequence that does not change when generate() is called
 * @author Robby McKilliam
 */
public class FixedIntegerSequence implements SpareIntegerSequence {
    
    protected final int[] s;
    
    public FixedIntegerSequence(final int[] s){
        this.s = s.clone();
    }
    
    @Override
    public int length() {
        return s.length;
    }

    @Override
    public int[] generate() {
        return s;
    }
    
    
    
}
