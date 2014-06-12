package snpe;

import snpe.generators.SparseNoisyPeriodicSignal;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import pubsim.Complex;
import pubsim.distributions.GaussianNoise;
import pubsim.distributions.discrete.PoissonRandomVariable;
import snpe.generators.DifferencesIID;

/**
 *
 * @author Robby McKilliam
 */
public class QuantisedPeriodogramFFTTest {
    
    public QuantisedPeriodogramFFTTest() {
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
     * Test of the Iz periodgram.
     */
    @Test
    public void testPeriodgramIzWithZeroData() {
        System.out.println("test Iz periodogram with data all zeros");
        final int N = 10;
        final double Tmin = 0.7;
        final double Tmax = 1.2;
        final double q = 10;
        final double[] y = new double[N];
        for(int i = 0; i < y.length; i++) y[i] = 0.0;
        QuantisedPeriodogramFFT instance = new QuantisedPeriodogramFFT(N,Tmin,Tmax,q);
        instance.estimate(y);
        for(double f = 0; f <= q; f+=0.01) assertEquals(instance.Iz(f),  N*N, 0.000001);
    }
    
    /**
     * Test of the D against B
     */
    @Test
    public void testPeriodgramIzAgainstD() {
        System.out.println("test slow Iz versus fast FFT computed D");
        final int N = 10;
        final double Tmin = 0.7;
        final double Tmax = 1.2;
        final double q = 10;
        final double[] y = new double[N];
        for(int i = 0; i < y.length; i++) y[i] = 1.0*i;
        QuantisedPeriodogramFFT instance = new QuantisedPeriodogramFFT(N,Tmin,Tmax,q);
        instance.estimate(y);
        int M = instance.M(); //set M to minimum possible
        double W = q/M; //width of the search grid
        Complex[] D = instance.computeV(); //compute the vector D by FFT
        int K = (int) Math.floor( (instance.fmax - instance.fmin) / W );
        for(int k = 0; k < K; k++) {
            double f = instance.fmin + W*k;
            assertEquals(instance.Iz(f),  D[k].abs2(), 0.000001);
        }
    }
    
    /**
     * Test of estimate method, of class Quantised Periodogram FFT.
     */
    @Test
    public void testEstimate() {
        System.out.println("Quantised Periodogram FFT estimator");

        double Tmin = 0.7;
        double Tmax = 1.3;
        int n = 50;
        double T = 1.0;
        double phase = 0.4;
        QuantisedPeriodogramFFT instance = new QuantisedPeriodogramFFT(n, Tmin, Tmax, 10);

        double noisestd = 0.001;
        GaussianNoise noise = new pubsim.distributions.GaussianNoise(0.0,noisestd*noisestd);
        PoissonRandomVariable drv = new PoissonRandomVariable(2);
        
        SparseNoisyPeriodicSignal sig = new SparseNoisyPeriodicSignal(n,T, phase, new DifferencesIID(n,drv),noise);
        double[] y = sig.generate();

        instance.estimate(y);
        System.out.println(instance.period() + "\t" + instance.phase());
        assertEquals(T, instance.period(), 0.001);
        assertEquals(phase, instance.phase(), 0.001);
    }
    
}