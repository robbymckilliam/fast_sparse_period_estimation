package snpe;

import java.util.Arrays;
import pubsim.Complex;
import static pubsim.FFT.fft;

/**
 * Class computes the periodogram estimator after quantising the observed time of arrivals.
 * This enables use of the FFT.  Function names are notation are described in the paper
 * 
 * R. G. McKilliam, I. V. L. Clarkson and B. G. Quinn "Fast sparse period estimation", Feb. 2014.
 * 
 * @author Robby McKilliam
 */
public class QuantisedPeriodogramFFT extends PeriodogramEstimator {

    ///the quantisation level. Large is fine quantisation and small is coarse quantisation
    public final double q;
    
    /** Stores sorted copy of received data. 
     * Makes internal method signatures mimic the paper better. Mutable and not particularly safe.
     */
    protected final double[] y; 

    public QuantisedPeriodogramFFT(int N, double Tmin, double Tmax, double q) {
        super(N, Tmin, Tmax);
        this.q = q;
        if( q <= fmax - fmin ) throw new RuntimeException("q must be larger than fmax - fmin");
        y = new double[N];
    }
    
    @Override
    public void estimate(double[] y) {
        if(y.length != N) throw new ArrayIndexOutOfBoundsException("input vector y must have length " + N);
        System.arraycopy(y, 0, this.y, 0, N); //copy to internal memory
        Arrays.sort(this.y); //sort the recieved signal (following code assumes accending order).  This sort is not strictly neccessary.  You could instead just take the min and max.
        double D = q/M(); //width of the search grid
        Complex[] V = computeV(); //compute the vector V by FFT
        
        //find the index that maximises V
        int K = (int) Math.floor( (fmax - fmin) / D );
        int khat = 0;
        double Vmax = V[0].abs2();
        for(int k = 1; k < K; k++){
            if( V[k].abs2() > Vmax ) {
                khat = k;
                Vmax = V[k].abs2();
            }
        }        
        double ftilde = fmin + D*khat;
        double fhat = refine(ftilde, y); //refine coarse frequencey estimate using Newton's method
        //double fhat = refine(ftilde, D, y); //refine coarse frequencey estimate using Brent's method
        That = 1.0 / fhat;
        phat = phasestor.phase(y, That); //now compute the phase estimate        
    }
    
    /// Calculates the vector v and returns its Fourier transform computed by FFT
    protected Complex[] computeV() {
        int M = M();
        Complex[] v = new Complex[M];
        //for (int m = 0; m < M; m++)  //this is the slow direct way to fill v
        //    v[m] = Complex.polar(b(ell(N) - m), 2 * Math.PI * fmin * (ell(N) - m) / q); //works because b(i) is real
        for(int m = 0; m < M; m++) v[m] = Complex.zero;
        for(int n=1; n <= N; n++) { //fast way to fill v
            int m = ell(n) - ell(1);
            v[m] = v[m].add(Complex.polar(1.0, -2 * Math.PI * fmin * m / q));
        }
        return fft(v);
    }
    
    /// The sequence ell n from the paper 
    protected int ell(int n) {
        return (int) Math.round(y[n-1]*q);
    }
    
    /// This is the recommended value of M.  Could ovveride this class to change this
    protected int M() {
        return 2*(ell(N) - ell(1) + 1); //set M to minimum possible
    }
    
    
    //////// Functions below here are only used for tests /////////
    
    /// Quantised observations 
    protected double z(int n) {
        return ell(n)/q;
    }
    
    /// The periodogram of z 
    protected double Iz(double f) {
        Complex sum = Complex.zero;
        for(int n = 1; n <= N; n++)  
            sum = sum.add(Complex.polar(1.0, 2*Math.PI*f*z(n)));
        return sum.abs2();
    }
        
    @Override
    public void estimate(double[] y, double fstep) {
        throw new UnsupportedOperationException("You cannot set the grid with. Set quantisation level q instead.");
    }

        
}
