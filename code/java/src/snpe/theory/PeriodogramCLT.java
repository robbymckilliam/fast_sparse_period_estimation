package snpe.theory;

import pubsim.distributions.RealRandomVariable;

/**
 * Computes the asymptotic variance of the periodogram estimator of a sparse
 * noisy periodic signal.
 * @author Robby McKilliam
 */
public class PeriodogramCLT implements CLT {

    protected final double scalefac;

    /* 
     * @param noise: A version of the noise distribution that is scaled by 1/T0. This is U_n in Barry's writeup.
     */
    public PeriodogramCLT(RealRandomVariable noise, double discretemean, double T0) {

        double numer = 3 * T0 * T0 * (1 - noise.characteristicFunction(4 * Math.PI).re());
        double chrf2pi = noise.characteristicFunction(2*Math.PI).re();
        double denom = 2 * Math.PI * Math.PI * discretemean * discretemean * chrf2pi * chrf2pi;
        scalefac = numer / denom;
    }

    /** 
     * This is wrong, but is set this way for convenience.  Should really add this later
     */
    @Override
    public double phaseVar(int N) {
        return 0.0;
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double periodVar(int N) {
        return scalefac/N/N/N;
    }

    @Override
    public double periodPhaseCoVar(int N) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
