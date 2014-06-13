package snpe.theory;

import pubsim.lattices.Anstar.Anstar;
import static pubsim.VectorFunctions.intArrayToDoubleArray;
import static pubsim.VectorFunctions.sum2;

/**
 * This is the clairvoyant Cramer-Rao bound.  It is
 * just the bound given by linear regression under the assumption
 * that the vector of indicies, s, is known.
 * @author Robby McKilliam
 */
public class ClairvoyantCRB {
    
    public final double bound;
    
    public ClairvoyantCRB(int[] s, double var){
        this(intArrayToDoubleArray(s), var);
    }
    
    public ClairvoyantCRB(double[] s, double var){
        double[] x = new double[s.length];
        Anstar.project(s, x); //projects into the zero mean plane (i.e. the plane orthogonal to (1,...1)
        double mag2x = sum2(x);
        bound = var/mag2x;
    }

    /** @return the clairvoyant CRB for the set parameters */
    public double bound(){
        return bound;
    }
    
    public static double bound(double[] s, double var){
        return new ClairvoyantCRB(s, var).bound;
    }
    
    public static double bound(int[] s, double var){
        return new ClairvoyantCRB(s, var).bound;
    }
        
}
