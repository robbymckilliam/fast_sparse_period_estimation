/**
* Run benchmarks.
*/

import pubsim.distributions.discrete.GeometricRandomVariable
import pubsim.distributions.GaussianNoise
import pubsim.distributions.RealRandomVariable
import snpe.SparseNoisyPeriodicSignal
import snpe.PRIEstimator
import snpe.YeSampling
import snpe.PeriodogramEstimator
import snpe.NormalisedSamplingLLS
import snpe.QuantisedPeriodogramFFT
import snpe.QuantisedPeriodogramChirpZ
import snpe.SLS2novlp
import snpe.SLS2new

val T_0 = scala.math.Pi/3 //true period
val T_min = 0.8 //minimum period
val T_max = 1.5 //maximum period
val f_min = 1.0/T_max //minimum frequency
val f_max = 1.0/T_min //maximum frequency
val theta_0 = 0.2 //true phase
val p = 1.0/3.0 //parameter of geometric random variable for generating sparse noise
def discretedist = new GeometricRandomVariable(p) //the discrete distribution for generating different sN - sN-1
def noisedist = new GaussianNoise(0,0.01) //the noise distribution we use with variance v

//time used for benchmark warmup in nanoseconds.  Benchmark runtime is approximately twice this.
val MIN_BENCH_DURATION : Long = 5000000000L; // (10 secs)  

val Ns = List(10,14,20,30,40,50,60,80,100,120,150,200,250,300,400,600,900,1200,1500,2000,2600,3500,4700,6000,7800,10000,13000,18000,25000)
//val Ns = List(10,14,20,30,40,50,60,80,100,120,150,200,250,300,400,600,900,1200,1500,2000,2600,3500,4700,6000,7800,10000,13000,18000,25000,35000,50000,70000)
//val Ls = List(1000)
//val Ns = (1 to 40).map(i => (5*scala.math.pow(1.1,i)).toInt).distinct //number of symbols

//runbench(Ns, (N : Int) => new YeSampling(new PeriodogramEstimator(N,T_min,T_max)), "Periodogram")
runbench(Ns, (N : Int) => new YeSampling(new NormalisedSamplingLLS(N,T_min,T_max),6.0), "LeastSquares")
//runbench(Ns, (N : Int) => new YeSampling(new SLS2novlp(N,T_min,T_max)), "SLS2novlp")
//runbench(Ns, (N : Int) => new YeSampling(new SLS2new(N,T_min,T_max)), "SLS2new")
//for( q <- List(5.0) ) runbench(Ns, (N : Int) => new QuantisedPeriodogramFFT(N,T_min,T_max,q*f_max), "QuantisedPeriodogramq" + q)
//for( q <- List(5.0) ) runbench(Ns, (N : Int) => new QuantisedPeriodogramChirpZ(N,T_min,T_max,q*f_max), "QuantisedPeriodogramChirpZq" + q)

//benchmarks for Barry's chirpz tests.
//for( q <- List(4.0) ) runbench(Ns, (N : Int) => new QuantisedPeriodogramFFT(N,T_min,T_max,q*f_max), "QuantisedPeriodogramFFTq" + q)
//for( q <- List(4.0) ) runbench(Ns, (N : Int) => new QuantisedPeriodogramChirpZ(N,T_min,T_max,q*T_max), "QuantisedPeriodogramChirpZq" + q)

/** Runs a simulation with given parameters and stores output in a file */
def runbench(Ns: Seq[Int], estf : (Int) => PRIEstimator, name : String) {

  println("Running " + name)

  val runtimeslist = Ns.map{ N =>
    print(" N = " + N)
			    
    val gen = new SparseNoisyPeriodicSignal(N, T_0, theta_0, discretedist, noisedist)
    val est = estf(N)

    print(" ... Warming up ... ")
    var numiters = 0
    val warmupstarttime = System.nanoTime
    while(System.nanoTime - warmupstarttime < MIN_BENCH_DURATION){
      gen.generateSparseSignal
      est.estimate(gen.generateReceivedSignal)
      numiters = numiters+1
    }
    
   print("Benchmarking ... ")
   val benchstarttime = System.nanoTime
   for( i <- 1 to numiters) {
      gen.generateSparseSignal
      est.estimate(gen.generateReceivedSignal)
   }
   val tNano = (System.nanoTime - benchstarttime +(numiters)/2) / numiters //copied from Alan Eliasen java BigInteger benchmarks
   val tMilli = tNano/1000000.0 //time in milliseconds per iteration
   println(tMilli + " ms per iteration, " + tMilli/N + " ms per observation")
   tMilli //return time into list
  
  }.toList

  println
      
  //write output to files
  val filet = new java.io.FileWriter("data/" + name + "Benchmark")
  (runtimeslist, Ns).zipped.foreach
  { (time, N) =>
    filet.write(N.toString.replace('E', 'e') + "\t" + time.toString.replace('E', 'e')  + "\n") 
 }
  filet.close
 
}
