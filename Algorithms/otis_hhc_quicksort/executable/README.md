To execute the program:
First we should make sure the execution file name is otis.exe rather than otis.ex_

Next

Please enter the program name "otis" followed by:

- Array Size, a number between 1 and 70
- OTIS Dimension, a number between 1 and 4, 0 for sequential sort
- Array Type, 1 for Random 2 for Sorted 3 for Reversed 4 forLocal Distribution

e.g. OTIS.exe 10 2 1
The above comman runs OTIS.exe with 10 MB array size, dimension two, array type random generated array.


p.s. The program execution depends on the available system resources at the moment
of execution, to get on higher values (dimension over 4 and array sizes over 100 MB) the 
program should be compiler using GNU C++ 64 bit version, the current execution is compiled
using GNU C++ 32 bit version.
Included in the same directory file named: otis.linux which should run directly under linux
./otis.linux

