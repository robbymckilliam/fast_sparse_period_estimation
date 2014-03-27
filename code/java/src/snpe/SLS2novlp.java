package snpe;

/**
 * Implementation of Sidiropoulos et al.'s SLS2-NOVLP algorithm for PRI
 * estimation.
 *
 * @author Vaughan Clarkson, 15-Jun-05. Added setSize method, 16-Jun-05.
 */
public class SLS2novlp extends AbstractPRIEstimator.Sampled {

    public final int m;
    final double[] d;
    final int[] u;
    /**
     * Period and phase estimates
     */
    protected double That, phat;

    public SLS2novlp(int N, double Tmin, double Tmax, int samples) {
        super(N, Tmin, Tmax, samples);
        m = N / 2;
        d = new double[m];
        u = new int[m];
    }

    /**
     * Default number of samples is 100
     */
    public SLS2novlp(int N, double Tmin, double Tmax) {
        this(N, Tmin, Tmax, 100);
    }

    @Override
    public void estimate(Double[] y, double fstep) {
        if (N != y.length) 
            throw new RuntimeException("Data length " + y.length + " does not match estimator length " + N);

        for (int i = 0; i < m; i++) {
            d[i] = y[(2 * i) + 1] - y[2 * i];
        }
        double bestL = Double.POSITIVE_INFINITY;
        double fhat = fmin;
        for (double f = fmin; f <= fmax; f += fstep) {
            double sumu2 = 0;
            double sumud = 0;
            for (int i = 0; i < m; i++) {
                u[i] = (int) Math.round(f * d[i]);
                sumu2 += u[i] * u[i];
                sumud += u[i] * d[i];
            }
            double f0 = sumu2 / sumud;
            double L = 0;
            for (int i = 0; i < m; i++) {
                double diff = d[i] - (u[i] / f0);
                L += diff * diff;
            }
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
