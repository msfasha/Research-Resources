Usage:

This folder has three sub-folders, source code, results and executable folder.
The source code has different folders, mainly the source code files in the src folder and the header files in the include folder.
The source files are arrange into a open source CodeBlocks IDE files and directories, with HHC.cbp as a project file that is opened by CodeBlocks.

The executable folder contains the execution file HHC.exe
This file was change from .exe to .ex_ for security and data transfer purposes, rename it back to HHC.exe for usage.
The program (HHC.exe) is a multi-threaded simulated implementation for a D-Dimension HHC internetwork load balancing algorithm.
It was built using (MinGW) GNU C version 4.7.2 and CodeBlocks version 12.11 IDE on windows 7 Professional SP1 64-bit environment.

To run the program, the library file pthreadGC2.dll has to exist in the system's/user path or in the same directory as the executable.

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

