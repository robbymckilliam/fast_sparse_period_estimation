package snpe;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import pubsim.distributions.GaussianNoise;
import pubsim.distributions.discrete.PoissonRandomVariable;

/**
 *
 * @author Robby McKilliam
 */
public class PeriodogramEstimatorTest {
    
    public PeriodogramEstimatorTest() {
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
     * Test of estimate method, of class NormalisedSamplingLLS.
     */
    @Test
    public void testEstimate() {
        System.out.println("Periodogram estimator");

        double Tmin = 0.7;
        double Tmax = 1.3;
        int n = 30;
        double T = 1.1;
        double phase = 0.4;
        PeriodogramEstimator instance = new PeriodogramEstimator(n, Tmin, Tmax, 2*n);

        double noisestd = 0.001;
        GaussianNoise noise = new pubsim.distributions.GaussianNoise(0.0,noisestd*noisestd);

        SparseNoisyPeriodicSignal sig = new SparseNoisyPeriodicSignal(n, T, phase, new PoissonRandomVariable(2),noise);
        sig.generateSparseSignal();
        Double[] y = sig.generateReceivedSignal();

        instance.estimate(y);
        System.out.println(instance.getPeriod() + "\t" + instance.getPhase());
        assertEquals(T, instance.getPeriod(), 0.001);
        assertEquals(phase, instance.getPhase(), 0.001);
    }
    
}