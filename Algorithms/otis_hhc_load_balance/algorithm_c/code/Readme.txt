The program (HHC.exe) is a multi-threaded simulated implementation for a D-Dimension HHC internetwork load balancing algorithm.
It was built using GNU C language and CodeBlocks IDE on windows 7 64-bit environment.

To run the program, the library file pthreadGC2.dll has to exist in the system's path or in the same directory as the executable.

The program can be run in two modes:

1- The pre defined total network size mode. 
In this mode, a single node/processor/thread at a single HHC unit is established with a predefined 
value of workload (integer value). The algorithm shall distribute that load across all HHC nodes and dimensions.

To run this scenario, enter the following at the command line:
HHC [HHC Dimension] [Initial Data Size]
e.g.
HHC 5 100000

HHC Dimension is an integer value between 1 and 8
Initial Data Size is an integer value > 0

2- The second run mode is the random size initialization. 
In this node in the DDimension network will be initialized with a random integer value between [1..100].

To run the program in this mode, enter the following at the command line:

HHC [HHC Dimension Size] 
e.g. HHC 4
