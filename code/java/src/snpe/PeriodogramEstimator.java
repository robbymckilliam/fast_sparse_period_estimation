package snpe;

import pubsim.optimisation.Brent;
import pubsim.optimisation.SingleVariateFunction;

/**
 * Written by Vaughan Clarkson, 05-Jan-07. New method calculatePeriodogram and
 * fixed the Newton iteration steps, 08-Jan-07.
 *
 */
public class PeriodogramEstimator extends AbstractPRIEstimator.Sampled {

    public static final int MAX_ITER = 10;
    public static final double EPSILON = 1e-10;
    /**
     * Period and phase estimates
     */
    protected double That, phat;
    
    protected final double[] kappa;

    public PeriodogramEstimator(int N, double Tmin, double Tmax, int samples) {
        super(N, Tmin, Tmax,samples);
        kappa = new double[N];
    }

    /**
     * Default number of samples is 4N
     */
    public PeriodogramEstimator(int N, double Tmin, double Tmax) {
        this(N, Tmin, Tmax, 4 * N);
    }

    public static double calculatePeriodogram(Double[] y, double f) {
        double sumur = 0, sumui = 0;
        for (int i = 0; i < y.length; i++) {
            sumur += Math.cos(2 * Math.PI * f * y[i]);
            sumui += Math.sin(2 * Math.PI * f * y[i]);
        }
        return sumur * sumur + sumui * sumui;
    }

    @Override
    public void estimate(Double[] y, double fstep) {
        double maxp = 0;
        double fhat = fmin;
        for (double f = fmin; f <= fmax; f += fstep) {
            double p = calculatePeriodogram(y, f);
            if (p > maxp) {
                maxp = p;
                fhat = f;
            }
        }
        //fhat = refine(fhat, y); //refine coarse frequencey estimate using Newton's method
        fhat = refine(fhat, fstep, y); //refine coarse frequencey estimate using Brent's method
        That = 1.0 / fhat;
        phat = phasestor.getPhase(y, That); //now compute the phase estimate
    }

    /// Modified Newton step
    protected double refine(double fhat, Double[] y) {
        double maxp = calculatePeriodogram(y, fhat);
        int numIter = 0;
        double f = fhat;
        double lastf = f - 2 * EPSILON;
        double lastp = 0;
        while (Math.abs(f - lastf) > EPSILON && numIter <= MAX_ITER && f >= fmin && f <= fmax) {
            double sumur = 0, sumui = 0, sumvr = 0, sumvi = 0, sumwr = 0, sumwi = 0;
            for (int i = 0; i < N; i++) {
                double ur = Math.cos(2 * Math.PI * f * y[i]);
                double ui = Math.sin(2 * Math.PI * f * y[i]);
                double vr = -2 * Math.PI * y[i] * ui;
                double vi = 2 * Math.PI * y[i] * ur;
                double wr = -2 * Math.PI * y[i] * vi;
                double wi = 2 * Math.PI * y[i] * vr;
                sumur += ur;
                sumui += ui;
                sumvr += vr;
                sumvi += vi;
                sumwr += wr;
                sumwi += wi;
            }
            double p = sumur * sumur + sumui * sumui;
            // System.err.println("f = " + f + " p = " + p);
            if (p < lastp) {
                f = (f + lastf) / 2;
            } else {
                lastf = f;
                lastp = p;
                if (p > maxp) {
                    maxp = p;
                    fhat = f;
                }
                double pd = 2 * (sumvr * sumur + sumvi * sumui);
                double pdd = 2 * (sumvr * sumvr + sumwr * sumur + sumvi * sumvi + sumwi * sumui);
                f += pd / Math.abs(pdd);
            }
            numIter++;
        }
        return fhat;
    }
    
    ///Brent's method
    protected double refine(double fhat, double fstep, final Double[] y) {
        SingleVariateFunction periodogram = new SingleVariateFunction() {
            @Override
            public double value(double x) {
                return -calculatePeriodogram(y,x);
            }
        };
        return new Brent(periodogram,fhat - fstep/2, fhat, fhat + fstep/2).xmin();
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
