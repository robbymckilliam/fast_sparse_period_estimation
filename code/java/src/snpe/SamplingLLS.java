package snpe;

import pubsim.VectorFunctions;
import pubsim.lattices.Anstar.AnstarLinear;
import pubsim.lattices.Anstar.AnstarVaughan;
import pubsim.lattices.LatticeAndNearestPointAlgorithmInterface;

/**
 * A modification of the Bresenham estimator which samples the line segment
 * rather than tracing through all the Voronoi cell boundaries.
 *
 * @author Vaughan Clarkson, 16-Jun-05. Add calculateObjective method,
 * 13-Jan-07. Modified by Robby McKilliam 24-Feb-2014.
 */
public class SamplingLLS extends AbstractPRIEstimator.Sampled {

    final double[] zeta, fzeta;
    /**
     * Period and phase estimates
     */
    protected double That, phat;
    protected final LatticeAndNearestPointAlgorithmInterface lattice;
    
    public SamplingLLS(int N, double Tmin, double Tmax, int samples) {
        super(N,Tmin,Tmax,samples);
        lattice = new AnstarLinear(N-1);
        zeta = new double[N];
        fzeta = new double[N];
    }

    /** Default number of samples is 4N */
    public SamplingLLS(int N, double Tmin, double Tmax) {
        this(N,Tmin,Tmax,4*N);
    }
    
    ///Run estimator with given search grid width
    @Override
    public void estimate(Double[] y, double fstep) {
        if (N != y.length) 
            throw new RuntimeException("Data length " + y.length + " does not match estimator length " + N);
        
        //first compute the period estimate
        AnstarVaughan.project(y, zeta);
        double ztz = VectorFunctions.sum2(zeta);
        double bestL = Double.POSITIVE_INFINITY;
        double fhat = fmin;
        for (double f = fmin; f <= fmax; f += fstep) {
            for (int i = 0; i < N; i++) {
                fzeta[i] = f * zeta[i];
            }
            lattice.nearestPoint(fzeta);
            double[] v = lattice.getLatticePoint();
            double vtv = 0, vtz = 0;
            for (int i = 0; i < N; i++) {
                vtv += v[i] * v[i];
                vtz += v[i] * zeta[i];
            }
            double f0 = vtv / vtz;
            double L = ztz - 2 * vtz / f0 + vtv / (f0 * f0);
            if (L < bestL) {
                bestL = L;
                fhat = f0;
            }
        }
        That = 1 / fhat;

        //now compute the phase estimate
        phat = phasestor.getPhase(y, That);
    }

    @Override
    public double getPeriod() {
        return That;
    }

    @Override
    public double getPhase() {
        return phat;
    }
    
}
