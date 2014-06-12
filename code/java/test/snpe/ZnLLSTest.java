/*
 * ZnLLSTest.java
 * JUnit based test
 *
 * Created on 9 July 2007, 12:24
 */

package snpe;

import snpe.generators.SparseNoisyPeriodicSignal;
import junit.framework.TestCase;
import pubsim.distributions.GaussianNoise;
import pubsim.distributions.discrete.PoissonRandomVariable;
import snpe.generators.DifferencesIID;

/**
 *
 * @author Robby McKilliam
 */
public class ZnLLSTest extends TestCase {
    
    public ZnLLSTest(String testName) {
        super(testName);
    }

    /**
     * Test of estimateFreq method, of class simulator.ZnLLS.
     */
    public void testEstimateFreq() {
        System.out.println("ZnLLS estimate frequency");
        
        double Tmin = 0.7;
        double Tmax = 1.3;
        int N = 30;
        double f = 0.9;
        double T = 1/f;
        double phase = 0.0;
        ZnLLS instance = new ZnLLS(N,Tmin,Tmax);
        
        double noisestd = 0.0001;
        GaussianNoise noise = new pubsim.distributions.GaussianNoise(0.0,noisestd*noisestd);
        PoissonRandomVariable drv = new PoissonRandomVariable(2);
        
        SparseNoisyPeriodicSignal sig = new SparseNoisyPeriodicSignal(N,T, phase, new DifferencesIID(N,drv),noise);
        double[] y = sig.generate();
        
        double expResult = T;
        instance.estimate(y);
        assertEquals(expResult, instance.getPeriod(), 0.001);

    }

   /**
     * Test of estimateFreq method, of class simulator.ZnLLS.
     */
    public void testEstimateWithPhase() {
        System.out.println("ZnLLS estimate with phase");

        double fmin = 0.7;
        double fmax = 1.3;
        int N = 30;
        double T = 1.0;
        double f = 1.0/T;
        double phase = 0.15;
        
        ZnLLS instance = new ZnLLS(N,1/fmax,1/fmin);

        double noisestd = 0.00001;
        GaussianNoise noise = new pubsim.distributions.GaussianNoise(0.0,noisestd*noisestd);

        PoissonRandomVariable drv = new PoissonRandomVariable(2);
        
        SparseNoisyPeriodicSignal sig = new SparseNoisyPeriodicSignal(N,T, phase, new DifferencesIID(N,drv),noise);
        double[] y = sig.generate();

        instance.estimate(y);
        double hatT = instance.getPeriod();
        double hatp = instance.getPhase();

        System.out.println(hatT + ", " + hatp);

        assertEquals(hatT, T, 0.001);
        assertEquals(hatp, phase, 0.001);

    }
    
}
