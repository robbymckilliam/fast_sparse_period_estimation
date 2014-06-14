/**
 * Run simulations of various sparse noise period estimators.
 */
import pubsim.distributions.discrete.DiscreteRandomVariable
import pubsim.distributions.discrete.Geometric
import pubsim.distributions.discrete.Poisson
import pubsim.distributions.Gaussian
import pubsim.distributions.Uniform
import pubsim.distributions.RealRandomVariable
import snpe.generators.SparseNoisyPeriodicSignal
import snpe.generators.FixedIntegerSequence
import snpe.generators.DifferencesIID
import snpe.PRIEstimator
import snpe.YeSampling
import snpe.PeriodogramEstimator
import snpe.NormalisedSamplingLLS
import snpe.QuantisedPeriodogramFFT
import snpe.SLS2novlp
import snpe.SLS2new
import snpe.theory.PeriodogramCLT
import snpe.theory.NormalisedLLSCLT
import snpe.theory.ClairvoyantCRB

val iters = 500
val T_0 = scala.math.Pi/3 //true period
val T_min = 0.8 //minimum period
val T_max = 1.5 //maximum period
val f_min = 1.0/T_max //minimum frequency
val f_max = 1.0/T_min //maximum frequency
val theta_0 = 0.2 //true phase

val starttime = (new java.util.Date).getTime

/////// Simulations with Poisson discrete random variables and Gaussian noise
{
 val N = 200
  //for discrete distributions with mean 1 and 10
  for( m <- List( 1, 10 ) ) {
    def discretedist() = new Poisson(m);
    def noisedist(v : Double) = new Gaussian(0,v) //the noise distribution we use with variance v
    def sparsenoisygenerator(v : Double) = new SparseNoisyPeriodicSignal(N,T_0,theta_0,new DifferencesIID(N,discretedist),noisedist(v))
    val varbs = -38.0 to -5.0 by 1.5 //variances we use in dB

    runsim(varbs, sparsenoisygenerator, () => new YeSampling(new PeriodogramEstimator(N,T_min,T_max)), "PeriodogramN" + N + "poisson" + m)
    runsim(varbs, sparsenoisygenerator, () => new YeSampling(new NormalisedSamplingLLS(N,T_min,T_max),6.0), "NormalisedSamplingLLSN" + N + "poisson" + m)
    //runsim(varbs, sparsenoisygenerator, () => new YeSampling(new SLS2novlp(N,T_min,T_max)), "SLS2novlpN" + N + "poisson" + m)
    runsim(varbs, sparsenoisygenerator, () => new YeSampling(new SLS2new(N,T_min,T_max)), "SLS2newN" + N + "poisson" + m)
    for( q <- List(1.5,5.0) ) runsim(varbs, sparsenoisygenerator, () => new QuantisedPeriodogramFFT(N,T_min,T_max,q*f_max), "QuantisedPeriodogramFFTN" + N + "q" + q + "poisson" + m)

    //now compute CLTs
    val vardbsfiner = -38.0 to -5.0 by 0.2
    runCLT(N, vardbsfiner, () => discretedist, (v : Double, dm : Double) => new PeriodogramCLT(noisedist(v/T_0/T_0), dm, T_0), "PeriodogramCLTN" + N + "poisson" + m)
    runCLT(N, vardbsfiner, () => discretedist, (v : Double, dm : Double) => new NormalisedLLSCLT(noisedist(v/T_0/T_0), dm, T_0), "NormalisedLLSCLTN" + N + "poisson" + m)
  }
}

/////// Simulations with Geometric discrete random variables and uniform noise
{
val N = 200
  //for discrete distributions with mean 1 and 10
  for( m <- List( 1, 10 ) ) {
    def discretedist() = new Geometric.StartingAtZero(1.0/(m+1.0))
    def noisedist(v : Double) = new Uniform(0,v) //the noise distribution we use with variance v
    def sparsenoisygenerator(v : Double) = new SparseNoisyPeriodicSignal(N,T_0,theta_0,new DifferencesIID(N,discretedist),noisedist(v))
    val twelthdB = scala.math.log10(1.0/12)*10
    val varbs = -25.0 until twelthdB by 0.75 //variances we use in dB

    runsim(varbs, sparsenoisygenerator, () => new YeSampling(new PeriodogramEstimator(N,T_min,T_max)), "PeriodogramN" + N + "uniformgeom" + m)
    runsim(varbs, sparsenoisygenerator, () => new YeSampling(new NormalisedSamplingLLS(N,T_min,T_max),6.0), "NormalisedSamplingLLSN" + N + "uniformgeom" + m)
    //runsim(varbs, sparsenoisygenerator, () => new YeSampling(new SLS2novlp(N,T_min,T_max)), "SLS2novlpN" + N + "uniformgeom" + m)
    runsim(varbs, sparsenoisygenerator, () => new YeSampling(new SLS2new(N,T_min,T_max)), "SLS2newN" + N + "uniformgeom" + m)
    for( q <- List(1.5,5.0) ) runsim(varbs, sparsenoisygenerator, () => new QuantisedPeriodogramFFT(N,T_min,T_max,q*f_max), "QuantisedPeriodogramFFTN" + N + "q" + q + "uniformgeom" + m)

    //now compute CLTs
    val vardbsfiner = -25.0 until twelthdB by 0.05
    runCLT(N, vardbsfiner, () => discretedist, (v : Double, dm : Double) => new PeriodogramCLT(noisedist(v/T_0/T_0), dm, T_0), "PeriodogramCLTN" + N + "uniformgeom" + m)
    runCLT(N, vardbsfiner, () => discretedist, (v : Double, dm : Double) => new NormalisedLLSCLT(noisedist(v/T_0/T_0), dm, T_0), "NormalisedLLSCLTN" + N + "uniformgeom" + m)
  }
}

/////// Simulations with fixed Geometric discrete random variables and Gaussian noise
{
  val N = 500
  //for discrete distributions with mean 1 and 10
  for( m <- List( 1, 10 ) ) {
    val discretedist = new Geometric.StartingAtZero(1.0/(m+1.0))
    val fixedseq = new FixedIntegerSequence(new DifferencesIID(N,discretedist).generate) //a single fixed sequence.
    def noisedist(v : Double) = new Gaussian(0,v) //the noise distribution we use with variance v
    def sparsenoisygenerator(v : Double) = new SparseNoisyPeriodicSignal(N,T_0,theta_0,fixedseq,noisedist(v))
    val twelthdB = scala.math.log10(1.0/12)*10
    val varbs = -34.0 to -5.0 by 1.5 //variances we use in dB

    runsim(varbs, sparsenoisygenerator, () => new YeSampling(new PeriodogramEstimator(N,T_min,T_max)), "PeriodogramN" + N + "fixed" + m)
    runsim(varbs, sparsenoisygenerator, () => new YeSampling(new NormalisedSamplingLLS(N,T_min,T_max),6.0), "NormalisedSamplingLLSN" + N + "fixed" + m)
    //runsim(varbs, sparsenoisygenerator, () => new YeSampling(new SLS2novlp(N,T_min,T_max)), "SLS2novlpN" + N + "fixed" + m)
    runsim(varbs, sparsenoisygenerator, () => new YeSampling(new SLS2new(N,T_min,T_max)), "SLS2newN" + N + "fixed" + m)
    for( q <- List(1.5,5.0) ) runsim(varbs, sparsenoisygenerator, () => new QuantisedPeriodogramFFT(N,T_min,T_max,q*f_max), "QuantisedPeriodogramFFTN" + N + "q" + q + "fixed" + m)

    //now compute CLTs
    val vardbsfiner = -34.0 to -5.0 by 0.2 //variances we use in dB
    runCLT(N, vardbsfiner, () => discretedist, (v : Double, dm : Double) => new PeriodogramCLT(noisedist(v/T_0/T_0), dm, T_0), "PeriodogramCLTN" + N + "fixed" + m)
    runCLT(N, vardbsfiner, () => discretedist, (v : Double, dm : Double) => new NormalisedLLSCLT(noisedist(v/T_0/T_0), dm, T_0), "NormalisedLLSCLTN" + N + "fixed" + m)

    //now compute CRB and write to file
    val name = "CRBN" + N + "fixed" + m
    print("Computed " + name + " ")
    val eststarttime = (new java.util.Date).getTime
    val vars = vardbsfiner.map(db => scala.math.pow(10.0, db/10.0)) //list of variances
    //now write all the data to a file
    val file = new java.io.FileWriter("data/" + name) //list of files to write to
    vars.foreach{ v =>
      val mse = ClairvoyantCRB.bound(fixedseq.generate,v)
      file.write(v.toString.replace('E', 'e') + "\t" + mse.toString.replace('E', 'e')  + "\n")
    }
    file.close //close all the files we wrote to
    val estruntime = (new java.util.Date).getTime - eststarttime
    println("in " + (estruntime/1000.0) + " seconds.")
  }
}
 

val runtime = (new java.util.Date).getTime - starttime
println("Simulation finshed in " + (runtime/1000.0) + " seconds.\n")


/** Runs a simulation with given parameters and stores output in a file */
def runsim(varbs : Seq[Double], spngen : (Double) => SparseNoisyPeriodicSignal, estf : () => PRIEstimator, name : String) {

  val vars = varbs.map(db => scala.math.pow(10.0, db/10.0)) //list of variances
  val signalgenerators = vars.map{ v => spngen(v) }    

  print("Running " + name + " ")
  val eststarttime = (new java.util.Date).getTime
  
  //for all the noise distributions (in parallel threads)
  val mselist = signalgenerators.par.map { signalgenerator => 		      
    val mse = runMonteCarlo(estf(), signalgenerator, iters)
    print(".")
    mse
  }.toList

  //now write all the data to a file
  val file = new java.io.FileWriter("data/" + name) //list of files to write to
  (mselist, vars).zipped.foreach{ (mse, v) =>
      file.write(v.toString.replace('E', 'e') + "\t" + mse.toString.replace('E', 'e')  + "\n")
  }
  file.close //close all the files we wrote to 
  
  val estruntime = (new java.util.Date).getTime - eststarttime
  println(" finished in " + (estruntime/1000.0) + " seconds.")

}

/** Run iterations Monte-Carlo simulations for est and gen and return mean square error in period */
def runMonteCarlo(est : PRIEstimator, gen : SparseNoisyPeriodicSignal, iterations : Int) : Double = {
  val T_0 = gen.period //get the true period
  if( est.length != gen.length ) throw new RuntimeException("Lengths of estimator and generator don't match")
  
  //run the iterations and accumulate square error
  val sse = (1 to iterations).foldLeft(0.0) { (sum, i) => 
    est.estimate(gen.generate)
    val That = est.period 
    val error = That - T_0
    sum + error*error
  }
  return sse/iterations //return mean square error
}


/** Compute CLT and write to file */
def runCLT(N : Int, varbs : Seq[Double], discretedist : () => DiscreteRandomVariable, cltf : (Double, Double) => snpe.theory.CLT, name : String) {

  print("Computed " + name + " ")
  val eststarttime = (new java.util.Date).getTime

  val vars = varbs.map(db => scala.math.pow(10.0, db/10.0)) //list of variances

  //now write all the data to a file
  val file = new java.io.FileWriter("data/" + name) //list of files to write to
  vars.foreach{ v =>
    val mse = cltf(v, discretedist().mean).periodVar(N)
    file.write(v.toString.replace('E', 'e') + "\t" + mse.toString.replace('E', 'e')  + "\n")
  }
  file.close //close all the files we wrote to 

  val estruntime = (new java.util.Date).getTime - eststarttime
  println("in " + (estruntime/1000.0) + " seconds.")

} 





