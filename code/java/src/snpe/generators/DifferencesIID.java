package snpe.generators;

import pubsim.distributions.NoiseGenerator;

/**
 * A sequence of integers with differences between consecutive elements are given 
 * by discrete independent and identically distributed random variables
 * @author Robby McKilliam
 */
public class DifferencesIID implements SpareIntegerSequence {
    
    public final int N;
    protected final NoiseGenerator<Integer> discreterv;
    protected final int[] s;
    
    public DifferencesIID(int N, NoiseGenerator<Integer> discreterv){
        this.N = N;
        this.discreterv = discreterv;
        s = new int[N];
    }
    
    @Override
    public int length() {
        return N;
    }

    @Override
    public int[] generate() {
        s[0] = discreterv.getNoise();
        for(int i = 1; i < N; i++) s[i] = s[i-1] + discreterv.getNoise();
        return s;
    }
    
}
