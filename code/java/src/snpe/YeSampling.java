package snpe;

/**
 * Wraps a PRIEstimator.Sampled and choose the sampling interval according to
 * the given data. Specifically the width of the search grid is 1/(scale*(y_n-y_1)) where scale
 * is by default 2.0, but can be set at the constructor.
 * 
 * @author Robby McKilliam
 */
public class YeSampling implements PRIEstimator {
    
    public final double scale;
    
    protected final PRIEstimator.Sampled periodest;
    
    public YeSampling(PRIEstimator.Sampled est){
        periodest = est;
        scale = 2.0;
    }
    
    public YeSampling(PRIEstimator.Sampled est, double scale){
        periodest = est;
        this.scale = scale;
    }

    @Override
    public void estimate(double[] y) {
        double Ymax = y[y.length - 1]; //supposes data is in accending order
        double Ymin = y[0];
        for(double v : y){ //get guaranteed minimum and maximum y
            if(Ymax < v) Ymax = v;
            if(Ymin > v) Ymin = v;
        }
        double W = 1.0 / scale / (Ymax - Ymin); //Haohuan et. al. suggested grid width
        periodest.estimate(y, W);
    }

    @Override
    public double getPeriod() {
        return periodest.getPeriod();
    }

    @Override
    public double getPhase() {
        return periodest.getPhase();
    }

    @Override
    public int getLength() {
        return periodest.getLength();
    }
    
}
