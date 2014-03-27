package snpe;

import java.util.Arrays;
import pubsim.ChirpZ;
import pubsim.Complex;

/**
 * A version of the quantised periodogram estimator that uses the Chirp z-transform
 * rather than the FFT.  I don't expect it to be faster.
 * @author Robby McKilliam
 */
public class QuantisedPeriodogramChirpZ extends PeriodogramEstimator {
    
    ///the quantisation level. Large is fine quantisation and small is coarse quantisation
    public final double q;
    
    /** Stores sorted copy of received data. 
     * Makes internal method signatures mimic the paper better. Mutable and not particularly safe.
     */
    protected final Double[] y; 
    
    public QuantisedPeriodogramChirpZ(int N, double Tmin, double Tmax, double q) {
        super(N,Tmin,Tmax);
        this.q = q;
        if( q <= fmax - fmin ) throw new RuntimeException("q must be larger than fmax - fmin");
        y = new Double[N];
    }
    
    @Override
    public void estimate(Double[] y) {
        if(y.length != N) throw new ArrayIndexOutOfBoundsException("input vector y must have length " + N);
        System.arraycopy(y, 0, this.y, 0, N); //copy to internal memory
        Arrays.sort(this.y); //sort the recieved signal (following code assumes accending order).  This sort is not strictly neccessary.  You could instead just take the min and max.
        
        final double D = 1.0/2.0/(y[N-1]-y[0]); //grid width is that suggested by Ye
        final int K = (int) Math.floor( (fmax - fmin) / D ); //number of elements in the grid
        Complex[] V = computeV(D, K);
        
        //find the index that maximises D
        int khat = 0;
        double Vmax = V[0].abs2();
        for(int k = 1; k < K; k++){
            if( V[k].abs2() > Vmax ) {
                khat = k;
                Vmax = V[k].abs2();
            }
        }        
        double ftilde = fmin + D*khat;
        double fhat = refine(ftilde, y); //refine coarse frequency estimate
        That = 1.0 / fhat;
        phat = phasestor.getPhase(y, That); //now compute the phase estimate        
    }

    private Complex[] computeV(final double D, final int K) {
        Complex A = Complex.one;
        Complex B = new Complex.UnitCircle(-2*Math.PI*D/q); //ensure stuff stays on the unit circle during chirp z transform
        int L = ell(N) - ell(1) + 1;
        Complex[] x = new Complex[L];
        for(int m = 0; m < L; m++) x[m] = Complex.zero;
        for(int n=1; n <= N; n++) { //fast way to fill d
            int m = ell(n) - ell(1);
            x[m] = x[m].add(Complex.polar(1.0, -2 * Math.PI * fmin * m / q));
        }
        return ChirpZ.compute(A,B,K,x);
    }
    
    /// The sequence ell n from the paper 
    protected int ell(int n) {
        return (int) Math.round(y[n-1]*q);
    }
    
}
