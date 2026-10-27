#include <iostream>
#include <stdlib.h>
#include <mpi.h>
#include <vector>

using namespace std;

void RunAsMaster(int array_size,int number_of_nodes);
void RunAsSlave(int rank);
int QuickSortVector(vector<int> *vecPointer,int left, int right);
int QuickSortArray(int * array,int left, int right);
void PrintArray (int * array, int size);
void RunSequentialSort(int array_size);
int * GenerateRandomArray(int size);
