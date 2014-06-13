/*
 * SparseNoisyPeriodicSignal.java
 *
 * Created on 13 April 2007, 14:55
 */

package snpe.generators;

import pubsim.distributions.NoiseGenerator;

/**
 * Generates a set of received times that are sparse and
 * noisy versions of the received signal.
 * <p>
 * The specific sparse pulses that will be transmitted can
 * be specified by @func setSparseSignal.
 * 
 * @author Robby McKilliam
 */
public class SparseNoisyPeriodicSignal {
    
    public final double T;
    public final int N;
    public final double phase;
    
    protected final double[] y;
    protected final NoiseGenerator<Double> noise;
    protected final SpareIntegerSequence sparsenoise;
    
    public SparseNoisyPeriodicSignal(int N, double T, double phase, SpareIntegerSequence sparsenoise, NoiseGenerator<Double> noise){
        if( N > sparsenoise.length() ) throw new ArrayIndexOutOfBoundsException("The sparse interger sequence is not long enough.");
        this.N = N;
        this.T = T;
        this.phase = phase;
        y = new double[N];
        this.noise = noise;
        this.sparsenoise = sparsenoise;
    }
    
    public double period(){ return T; }
    public double phase(){ return phase; }
    public int length() {return N; }

    /** Generate sparse noisy signal*/
    public double[] generate() {
          int[] s = sparsenoise.generate();
          for(int i = 0; i< N; i++)
              y[i] = T * s[i] + noise.noise() + phase;
          return y;
    }
    
}
