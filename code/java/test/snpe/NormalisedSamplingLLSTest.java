package snpe;

import snpe.generators.SparseNoisyPeriodicSignal;
import static org.junit.Assert.assertEquals;
import org.junit.*;
import pubsim.distributions.Gaussian;
import pubsim.distributions.discrete.Poisson;
import snpe.generators.DifferencesIID;

/**
 *
 * @author Robby McKilliam
 */
public class NormalisedSamplingLLSTest {

    public NormalisedSamplingLLSTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
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
        System.out.println("NormalisedSamplingLLS estimate");

        double Tmin = 0.7;
        double Tmax = 1.3;
        int n = 30;
        double T = 1.1;
        double phase = 0.4;
        NormalisedSamplingLLS instance = new NormalisedSamplingLLS(n, Tmin, Tmax, 2*n);

        double noisestd = 0.001;
        Gaussian noise = new Gaussian(0.0,noisestd*noisestd);
        Poisson drv = new Poisson(2);
        
        SparseNoisyPeriodicSignal sig = new SparseNoisyPeriodicSignal(n,T, phase, new DifferencesIID(n,drv),noise);
        double[] y = sig.generate();

        instance.estimate(y);
        System.out.println(instance.period() + "\t" + instance.phase());
        assertEquals(T, instance.period(), 0.001);
        assertEquals(phase, instance.phase(), 0.001);
    }

}