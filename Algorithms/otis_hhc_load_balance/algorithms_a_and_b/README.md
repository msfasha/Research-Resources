This folder contains two sub folders "Executable" and "Source Code".

"Executable" folder contains jar file which illustrates executable file of java.

to run, install Java runtime JRE 1.5 or later. After installing this Java, run the code using:

"java -jar hhc.jar". 

Include installed JRE "bin" folder path in "PATH" environment variable.

After running this file a dialog containing 4 options will be shown asking for the user's choice:

Choice 1) Experiment 1 (Execution Time of Algorithms A and B for constant dimension & variable load size).

In this scenario, a dialog asking for HHC dimension will be show, enter the dimension size. 5

Execution of Algorithm A and B will begin with max load of 10, 100, 1000, 10000, and 100000 on the dimension you have entered. 

The output will be a tabule showing three columns "Load Size", "Algorithm A", "Algorithm B".

Load Size: represents max load size of the execution.
Algorithm A: represents execution time in milliseconds of Algorithm A.
Algorithm B: represents execution time in milliseconds of Algorithm B.

Choice 2)Experiment 2 (Execution Time of Algorithms A and B for constant load size & variable dimensions).

In this scenario, a dialog asking for Max load size will be shown, enter maximum load size per node. 5

Executions of Algorithm A and B will be started with HHC dimension of 1, 2, 3, 4, and 5 on the load size you have entered. Note that output will be a table with three columns "Dimension", "Algorithm A", "Algorithm B".

Dimension: represents HHC dimension of the execution.
Algorithm A: represents execution time in milliseconds of Algorithm A.
Algorithm B: represents execution time in milliseconds of Algorithm B.

Choice 3)Experiment 3 (Speed of Algorithms A and B for variable dimensions).

In this scenario, a dialog asking for Max load size will arrise so you have to enter maximum load size per node. 8 executions of Algorithm A and B will be executed with HHC dimension of 1, 2, 3, 4, 5, 6, 7 and 8 on the load size you have entered. Note that output will be tabular containing three columns "Dimension", "Algorithm A", "Algorithm B".

Dimension: represents HHC dimension of the execution.
Algorithm A: Speed of Algorithm A bit/second.
Algorithm B: Speed of Algorithm B bit/second.

Otherwise) If none of the valid values 1,2, and 3 is entered the program will be terminated.

"Source Code" folder contains source files in a JDeveloper 11g workspace.

edu.ju.os.Main class is the main class to start from which contains static main method. Implementation of Algorithm A is done in class edu.ju.os.algorithm1.LBProcessor.This is thread class so that run method is overrided and all the implementation is done inside this method.

Implementation of Algorithm B is foudn in class edu.ju.os.algorithm2.LBProcessor.This is thread class so that run method is overided and all the implementation is performed inside this method.
