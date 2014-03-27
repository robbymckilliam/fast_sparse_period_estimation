fast_spare_period_estimation
============================

Software and latex for the paper

R. G. McKilliam, I. V. L. Clarkson and B. G. Quinn "Fast sparse period estimation", 2014

The command

bash build.sh

will compile the latex file to paper.pdf using the data existing in the repository.  Software for the simulations is contained in the code/ directory.  Montecarlo simulations are run with

bash runsim.sh

and the benchmarks are run with

bash runbenchmark.sh

You need a working java virtual machine and Scala 2.10.x installed to run the simulations. Source code and tests are contained in the code/java/ directory. The code makes use of the pubsim library available at https://github.com/robbymckilliam/pubsim
