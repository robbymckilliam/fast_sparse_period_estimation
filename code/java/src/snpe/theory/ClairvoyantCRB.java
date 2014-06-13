package snpe.theory;

import pubsim.lattices.Anstar.Anstar;
import pubsim.VectorFunctions;

/**
 * This is the clairvoyant Cramer-Rao bound.  It is
 * just the bound given by linear regression under the assumption
 * that the vector of indicies, s, is known.
 * @author Robby McKilliam
 */
public class ClairvoyantCRB {
    
    public final double bound;
    
    public ClairvoyantCRB(double[] s, double var){
        double[] x = new double[s.length];
        Anstar.project(s, x);
        double mag2x = VectorFunctions.sum2(x);
        bound = var/mag2x;
    }

    /** @return the clairvoyant CRB for the set parameters */
    public double bound(){
        return bound;
    }
    
    public static double bound(double[] s, double var){
        return new ClairvoyantCRB(s, var).bound;
    }
        
}
