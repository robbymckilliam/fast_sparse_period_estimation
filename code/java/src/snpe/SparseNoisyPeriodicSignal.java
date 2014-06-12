/*
 * SparseNoisyPeriodicSignal.java
 *
 * Created on 13 April 2007, 14:55
 */

package snpe;

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
    
    protected final Integer[] sparseSignal;
    protected final Double[] recievedSignal;
    protected final NoiseGenerator<Double> noise;
    protected final NoiseGenerator<Integer> sparsenoise;
    
    public SparseNoisyPeriodicSignal(int N, double T, double phase, NoiseGenerator<Integer> sparsenoise, NoiseGenerator<Double> noise){
        this.N = N;
        this.T = T;
        this.phase = phase;
        sparseSignal = new Integer[N];
        recievedSignal = new Double[N];
        this.noise = noise;
        this.sparsenoise = sparsenoise;
        generateSparseSignal();
    }
    
    public void setSparseSignal(Integer[] S){
        if(S.length != N) throw new ArrayIndexOutOfBoundsException("Tried to set sparse signal to the wrong length");
        System.arraycopy(S, 0, sparseSignal, 0, N);
    }
    
    public double getPeriod(){ return T; }
    public double getPhase(){ return phase; }
    
    public int getLength() {return N; }
    
    public Integer[] generateSparseSignal(){
        int sum = 0;
        for(int i = 0; i < N; i++){
            sum += sparsenoise.getNoise();
            sparseSignal[i] = sum;
        }
        //arguably this should be sorted here?
        return sparseSignal;
    }

    /**
     * Generate sparse noisy signal
     */
    public Double[] generateReceivedSignal() {
          
          for(int i = 0; i< sparseSignal.length; i++){
              recievedSignal[i] = T * sparseSignal[i]
                                    + noise.getNoise() + phase;
          }
          return recievedSignal;
    }

    public NoiseGenerator<Integer> getSparseGenerator(){ return sparsenoise; }

    public NoiseGenerator<Double> getNoiseGenerator(){ return noise; }
    
    /**
     * Set the seed for the random generator used
     * to create the sparse signal
     */
    public void setSeed(long seed){
        sparsenoise.setSeed(seed);
    }
    
    /** Randomise the seed for the sparse signal */ 
    public void randomSeed(){
        sparsenoise.randomSeed();
        noise.randomSeed();
    }
    
}
