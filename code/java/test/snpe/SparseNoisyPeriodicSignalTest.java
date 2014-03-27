package snpe;

import junit.framework.TestCase;
import pubsim.VectorFunctions;
import pubsim.distributions.GaussianNoise;
import pubsim.distributions.discrete.PoissonRandomVariable;

/**
 *
 * @author Robby McKilliam
 */
public class SparseNoisyPeriodicSignalTest extends TestCase {
    
    public SparseNoisyPeriodicSignalTest(String testName) {
        super(testName);
    }

    /**
     * Test of generateReceivedSignal method, of class simulator.SparseNoisyPeriodicSignal.
     */
    public void testGenerateTransmittedSignal() {
        System.out.println("generateReceivedSignal");
        
        SparseNoisyPeriodicSignal instance = new SparseNoisyPeriodicSignal(20,1,0.0,
                new PoissonRandomVariable(2),new GaussianNoise(0,1));
        
        Integer[] sig = instance.generateSparseSignal();
        boolean result = false;
        result = VectorFunctions.increasing(sig);
        for(int i = 0; i < sig.length; i++){
            result &= (((long)sig[i]) == Math.round(sig[i]));
        }
        assertEquals(true, result);
        
    }
    
    /**
     * Test of generateSparseSignal method, of class simulator.SparseNoisyPeriodicSignal.
     */
    public void testGenerateRecievedSignal() {
        System.out.println("generateTransmittedSignal");
        
        int length = 20;
        double T = 2.0;
        SparseNoisyPeriodicSignal instance = new SparseNoisyPeriodicSignal(length,T,0.0,
                new PoissonRandomVariable(2),new pubsim.distributions.UniformNoise(0.0, 1.0/3.0));

        Integer[] rec_sig = instance.generateSparseSignal();
        instance.setSparseSignal(rec_sig);
       
        Double[] sig = instance.generateReceivedSignal();
        boolean result;
        for (int i = 0; i < length; i++){
            result = (sig[i] <= (T*rec_sig[i] + 1.0)) && (sig[i] >= (T*rec_sig[i] - 1.0));
            assertEquals(true, result);
        }
        
    }
    
}
