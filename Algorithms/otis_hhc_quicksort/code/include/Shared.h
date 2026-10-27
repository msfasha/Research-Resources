#ifndef SHARED_H_INCLUDED
#define SHARED_H_INCLUDED
#include <assert.h>
#include <errno.h>
#include "Processor.h"
#include "quicksort.h"

int AggregateHHCLevel(Processor * _processor);
int AggregateHHCLevelNormalHHCNode(Processor * _processor);
int AggregateHHCLevelMasterHHCNode(Processor * _processor);
int AggregateHyperCubeLevel(Processor * _processor);
int AggregateOTISLevel(Processor * _processor);

int CreateThreads(int _OTISDimension, int _numberOfThreads, std::vector<int> * _vectorsArray);
void * ThreadRun (void * args);
int GetOTISNodesNumber(int _OTISDimension);
int GetHyperCubeNodesNumber(int _OTISDimension);
int GetArraySubDivider(int * _intArray,int _size, int _threadsNumber);
void SplitArray(int * _intArray, int _size, int _threadsNumber, std::vector<int> * _vectors_array, int _subarray_divider);
void GenerateRandomArray(int * _intArray, int _size);
void GenerateReversedArray(int * _intArray, int _size);
void GenerateSortedArray(int * _intArray, int _size);
void GenerateLocalDistributionArray(int * _intArray, int _size);
int GetHyperCubeFirstSetBit(Processor * _processor);

#endif // SHARED_H_INCLUDED
