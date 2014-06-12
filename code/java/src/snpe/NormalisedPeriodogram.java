package snpe;

// Written by Vaughan Clarkson, 11-Jan-07 (fork from PeriodogramEstimator).
public class NormalisedPeriodogram extends PeriodogramEstimator  {

    public NormalisedPeriodogram(int N, double Tmin, double Tmax, int samples) {
        super(N, Tmin, Tmax, samples);
    }

    /**
     * Default number of samples is 4*N
     */
    public NormalisedPeriodogram(int N, double Tmin, double Tmax) {
        this(N, Tmin, Tmax, 4 * N);
    }

    private static double calculateObjective(double[] y, double f) {
        double sumur = 0, sumui = 0;
        int N = y.length;
        for (int i = 0; i < y.length; i++) {
            sumur += Math.cos(2 * Math.PI * f * y[i]);
            sumui += Math.sin(2 * Math.PI * f * y[i]);
        }
        return (N * N - sumur * sumur - sumui * sumui) / (f * f);
    }

    @Override
    public void estimate(double[] y, double fstep) {
        // Coarse search
        double minp = Double.POSITIVE_INFINITY;
        double fhat = fmin;
        for (double f = fmin; f <= fmax; f += fstep) {
            double p = calculateObjective(y, f);
            if (p < minp) {
                minp = p;
                fhat = f;
            }
        }
        //fhat = refine(fhat, y); //refine coarse frequencey estimate using Newton's method
        fhat = refine(fhat, fstep, y); //refine coarse frequencey estimate using Brent's method
        That = 1.0 / fhat;
        phat = phasestor.getPhase(y, That); //now compute the phase estimate
    }

    ///Modified Newton step minimses the normalised periodogram
    @Override
    protected double refine(double fhat, double[] y) {
        double minp = calculateObjective(y, fhat);
        int numIter = 0;
        double f = fhat;
        double lastf = f - 2 * EPSILON;
        double lastp = Double.POSITIVE_INFINITY;
        while (Math.abs(f - lastf) > EPSILON && numIter++ < MAX_ITER && f >= fmin && f <= fmax) {
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
            double p = (N * N - sumur * sumur - sumui * sumui) / (f * f);
            // System.err.println("f = " + f + " p = " + p);
            if (p > lastp) {
                f = (f + lastf) / 2;
            } else {
                lastf = f;
                lastp = p;
                if (p < minp) {
                    minp = p;
                    fhat = f;
                }
                double pd = 2 * (sumvr * sumur + sumvi * sumui) / (f * f);
                double pdd = 2 * (sumvr * sumvr + sumwr * sumur + sumvi * sumvi + sumwi * sumui) / (f * f);
                double pd2 = -pd - (2 * p / f);
                double pdd2 = -pdd + (4 * pd / f) + (6 * p / (f * f));
                f -= pd2 / Math.abs(pdd2);
            }
        }
        return fhat;
    }
}
