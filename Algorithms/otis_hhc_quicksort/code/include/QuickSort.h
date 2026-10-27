#ifndef QUICKSORT_H
#define QUICKSORT_H
#include <stdio.h>      /* printf, scanf, puts, NULL */
#include <stdlib.h>     /* srand, rand */
#include <time.h>       /* time */
#include <vector>
#include <stdexcept>


using namespace std;

class QuickSort
{
public:
QuickSort();
void RunQuickSort(int* _arr, int _left, int _right);
void RunQuickSortVector(std::vector<int> * _vector, int _left, int _right);

uint64_t NumberOfIterations;
uint64_t NumberOfRecursions;
uint64_t NumberOfSwaps;

};
#endif // QUICKSORT_H
