package snpe;

/**
 * Implementation of Sidiropoulos et al.'s SLS2-ALL algorithm for PRI
 * estimation.
 * @author Vaughan Clarkson, 16-Jun-05.
 * Stupid bug fix, 17-Jun-05.
 * Modifed Robby McKilliam 24-Feb-2014
 */
public class SLS2all extends AbstractPRIEstimator.Sampled {

    public final int m;
    
    final double[] d, kappa;
    final int[] u;

    /** Period and phase estimates */
    protected double That, phat;
    
    public SLS2all(int N, double Tmin, double Tmax, int samples){
        super(N,Tmin,Tmax,samples);
        m = N * (N-1) / 2;
        d = new double[m];
        u = new int[m];
        kappa = new double[N];
    }
    
    /** Default number of samples is 100 */
    public SLS2all(int N, double Tmin, double Tmax) {
        this(N,Tmin,Tmax,100);
    }

    @Override
    public void estimate(double[] y, double fstep) {
        if (N != y.length) 
            throw new RuntimeException("Data length " + y.length + " does not match estimator length " + N);

        //first compute the period estimate
	int k = 0;
	for (int i = 0; i < N-1; i++)
	    for (int j = i+1; j < N; j++)
		d[k++] = y[j] - y[i];
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
	That = 1/fhat;

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
