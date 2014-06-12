package snpe;

/**
 * Abstract class for period estimators.  Includes a least squares phase estimator 
 * and various recommended final definitions
 * @author Robby McKilliam
 */
public abstract class AbstractPRIEstimator implements PRIEstimator {
    
    public final int N;
    public final double Tmin, Tmax;
    public final double fmin, fmax;
    public final PhaseEstimator phasestor;
    
    public AbstractPRIEstimator(int N, double Tmin, double Tmax){
        this.N = N;
        this.Tmin = Tmin;
        this.Tmax = Tmax;
        fmin = 1/Tmax;
        fmax = 1/Tmin;
        phasestor = new PhaseEstimator(N);
    }
    
    @Override
    public int getLength() {
        return N;
    }
    
    /** Abstract class for period estimators that sample their objective function */
    public static abstract class Sampled extends AbstractPRIEstimator implements PRIEstimator.Sampled {
        
        public final int NUM_SAMPLES; //number of samples used by the periodogram estimator
        
        public Sampled(int N, double Tmin, double Tmax, int samples){
            super(N,Tmin,Tmax);
            NUM_SAMPLES = samples;
        }
        
        /// Default estimate uses NUM_SAMPLES set during construction
        @Override
        public void estimate(double[] y) {
            double fmin = 1 / Tmax;
            double fmax = 1 / Tmin;
            double W = (fmax - fmin) / NUM_SAMPLES; //search grid width
            estimate(y, W);
        }

    }
    
}
