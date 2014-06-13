package snpe;

import snpe.generators.SparseNoisyPeriodicSignal;
import junit.framework.TestCase;
import pubsim.distributions.Gaussian;
import snpe.generators.FixedIntegerSequence;

/**
 *
 * @author Robby McKilliam
 */
public class SparseNoisyPeriodicSignalTest extends TestCase {
    
    public SparseNoisyPeriodicSignalTest(String testName) {
        super(testName);
    }
    
    /**
     * Test of generateSparseSignal method, of class simulator.SparseNoisyPeriodicSignal.
     */
    public void testGenerateRecievedSignal() {
        System.out.println("generateTransmittedSignal");
        
         int[] s = {1,2,3,6,8,10};
        int N = s.length;
        double T = 1.0;
        double phase = 0.1;
        FixedIntegerSequence fs = new FixedIntegerSequence(s);
        SparseNoisyPeriodicSignal sig = new SparseNoisyPeriodicSignal(N, T, phase, fs, new Gaussian(0, 0));
        double[] y = sig.generate();
        
        for (int i = 0; i < N; i++) assertTrue( Math.abs(y[i] - T*s[i] - phase) < 1e-9 );
        
    }
    
}
