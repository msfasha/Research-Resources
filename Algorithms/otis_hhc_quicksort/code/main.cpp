#include <iostream>
#include "Shared.h"

#define MEGA 1000000

using namespace std;

///Functions Declaration
void * ThreadRun (void *);
int CreateThreads(int OTISDimension, int numberOfThreads, vector<int> * vectorsArray);
int StartScenario(int _arraySize, int _arrayType, int _OTISDimension);
void ValidateInput(int argc, char **argv);
void PrintInputErrorMessage();
void RunSequential(int * _intArray, int _arraySize);
void RunParallel(int * _intArray, int _arraySize, int _OTISDimension);

int main(int argc, char **argv)
{
    ValidateInput(argc, argv);

    StartScenario(MEGA * atoi(argv[1]), atoi(argv[2]), atoi(argv[3]));

    return 0;
}

int StartScenario(int _arraySize, int _OTISDimension , int _arrayType)
{
    int * intArray = new int [_arraySize];

    switch (_arrayType)
    {
    case 1:
        printf("Generating Random Array of size %d MB \n\n", _arraySize / MEGA);
        GenerateRandomArray(intArray, _arraySize);
        break;
    case 2:
        printf("Generating Sorted Array of size %d MB \n\n", _arraySize / MEGA);
        GenerateSortedArray(intArray, _arraySize);
        break;
    case 3:
        printf("Generating Reversed Array of size %d MB  \n\n", _arraySize / MEGA);
        GenerateReversedArray(intArray, _arraySize);
        break;
    case 4:
        printf("Generating Local Distribution Array of size %d MB  \n\n", _arraySize / MEGA);
        GenerateLocalDistributionArray(intArray, _arraySize);
        break;
    default:
        printf("Generating Random Array of size %d MB  \n\n", _arraySize / MEGA);
        GenerateRandomArray(intArray, _arraySize);
        break;
    }

    if (_OTISDimension == 0)///then sequential
    {
        RunSequential(intArray, _arraySize);
    }
    else ///else its parallel with an OTIS dimension
    {
        RunParallel(intArray, _arraySize, _OTISDimension);
    }

    return 0;
}

void PrintInputErrorMessage()
{
    printf("____________________________________________________\n\n");
    printf("Please enter the program name followed by : \n\n");
    printf ("1-Array Size between 1 and 70 \n\n");
    printf ("2-OTIS Dimension number between 1 and 4, 0 for sequential sort \n\n");
    printf ("3-Array Type 1-Random 2-Sorted 3-Reversed 4-Local Distribution \n\n");
    printf("e.g. OTIS.exe 10 2 1 \n\n");

    exit(1);
}

void ValidateInput(int argc, char **argv)
{
    if(argc < 4)
    {
        PrintInputErrorMessage();
    }

    if (atoi(argv[1]) < 1)///Array Size
    {
        PrintInputErrorMessage();
    }

    if ((atoi(argv[2]) < 0) | (atoi(argv[2]) > 5))///OTIS Dimension
    {
        PrintInputErrorMessage();
    }

    if (atoi(argv[3]) < 0)///Array Type
    {
        PrintInputErrorMessage();
    }
}

void RunSequential(int * _intArray, int _arraySize)
{
    clock_t startTime, endTime;
    printf("Running Sequential Sort \n");

    std::vector<int>  intVector;
    for (int i=0; i < _arraySize; i++)
        intVector.push_back(_intArray[i]);

    QuickSort qs;

    startTime = clock();
    qs.RunQuickSortVector(&intVector,0, intVector.size() - 1);
    endTime = clock();
    printf("Sequential, total CPU Time : %g \n",(double)(endTime - startTime)/CLOCKS_PER_SEC);
    printf("Recursion Calls %I64d, Iterations %I64d, Swaps %I64d \n\n",qs.NumberOfRecursions,qs.NumberOfIterations,qs.NumberOfSwaps);

}

void RunParallel(int * _intArray, int _arraySize, int _OTISDimension)
{
    clock_t startTime, endTime;
    int numberOfThreads = pow(6 * pow(2,_OTISDimension - 1),2);

    printf("OTIS Dimension %d \n\n",_OTISDimension );

    std::vector<int> * vectorsArray = new std::vector<int>[numberOfThreads];
    int subarrayDivider = GetArraySubDivider(_intArray,_arraySize,numberOfThreads);
    SplitArray(_intArray, _arraySize, numberOfThreads, vectorsArray, subarrayDivider);

    printf("Running Parallel Sort on %d threads \n", numberOfThreads);

    startTime = clock();
    CreateThreads(_OTISDimension, numberOfThreads,vectorsArray);
    endTime = clock();
    printf("Parallel, total CPU Time : %g \n",(double)(endTime - startTime)/CLOCKS_PER_SEC);

    printf("Electrical moves %d, Optical moves %d, Total : %d \n\n",
           Processor::TotalElectricalMoves, Processor::TotalOpticalMoves, Processor::TotalElectricalMoves + Processor::TotalOpticalMoves);
}
