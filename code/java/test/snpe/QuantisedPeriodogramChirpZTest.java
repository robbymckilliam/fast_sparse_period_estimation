package snpe;

import snpe.generators.SparseNoisyPeriodicSignal;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import pubsim.distributions.GaussianNoise;
import pubsim.distributions.discrete.PoissonRandomVariable;
import snpe.generators.DifferencesIID;

/**
 *
 * @author Robby McKilliam
 */
public class QuantisedPeriodogramChirpZTest {
    
    public QuantisedPeriodogramChirpZTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

        /**
     * Test of estimate method, of class Quantised Periodogram FFT.
     */
    @Test
    public void testEstimate() {
        System.out.println("Quantised Periodogram ChirpZ estimator");

        double Tmin = 0.7;
        double Tmax = 1.3;
        int n = 100;
        double T = 1.0;
        double phase = 0.4;
        QuantisedPeriodogramChirpZ instance = new QuantisedPeriodogramChirpZ(n, Tmin, Tmax, 4/Tmin);

        double noisestd = 0.0001;
        GaussianNoise noise = new pubsim.distributions.GaussianNoise(0.0,noisestd*noisestd);
        PoissonRandomVariable drv = new PoissonRandomVariable(2);
        
        SparseNoisyPeriodicSignal sig = new SparseNoisyPeriodicSignal(n,T, phase, new DifferencesIID(n,drv),noise);
        double[] y = sig.generate();

        instance.estimate(y);
        System.out.println(instance.getPeriod() + "\t" + instance.getPhase());
        assertEquals(T, instance.getPeriod(), 0.001);
        assertEquals(phase, instance.getPhase(), 0.001);
    }
    
}
    
