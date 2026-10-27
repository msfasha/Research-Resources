#include "..\include\QuickSort.h"

QuickSort::QuickSort()
{
    //ctor
}

QuickSort::~QuickSort()
{
    //dtor inttypes.h
}

void QuickSort::ResetCounters()
{
    NumberOfIterations = 0;
    NumberOfRecursions = 0;
    NumberOfSwaps = 0;
}

uint64_t  QuickSort::GetNumberOfIterations()
{
    return NumberOfIterations;
}

uint64_t  QuickSort::GetNumberOfRecursions()
{
    return NumberOfRecursions;
}

uint64_t  QuickSort::GetNumberOfSwaps()
{
    return NumberOfSwaps;
}

void RunMPISort()
{
    #pragma omp parallel
    printf("Hello from thread %d, nthreads %d\n", omp_get_thread_num(), omp_get_num_threads());
}

void QuickSort::RunSortPivotMiddle(int* arr, int left, int right)
{
    int i = left, j = right;
    int tmp;
    int pivot = arr[(left + right) / 2];
    //++NumberOfRecursions;
    /* partition */
    while (i <= j)
    {
        while (arr[i] < pivot)
        {
            //++NumberOfIterations;
            i++;
        }

        while (arr[j] > pivot)
        {
           // ++NumberOfIterations;
            j--;

        }
        if (i <= j)
        {
            tmp = arr[i];
            arr[i] = arr[j];
            arr[j] = tmp;
            i++;
            j--;
            //++NumberOfSwaps;
        }
    }
    /* recursion */
    if (left < j)
        RunSortPivotMiddle(arr, left, j);
    if (i < right)
        RunSortPivotMiddle(arr, i, right);
}
Tester.h
#include <stdio.h>      /* printf, scanf, puts, NULL */
#include <stdlib.h>     /* srand, rand */
#include <pthread.h>
#include <assert.h>
#include <time.h>       /* time */


class Tester
{
public:
    Tester();
    virtual ~Tester();
    void StartTesting(void);

    static int ArraySize ;
    static int * MainArray;
protected:

private:
    void  GenerateSortedArray();
    void  GenerateReversedArray();
    void  GenerateRandomArray();

    void  RunMultiThreadSort();
    void  RunMultiThreadOpenMPSort();
    void  GenerateSubArrayInfo();
    static  void* ThreadRoutine(void * args);

    int NumberOfCPUs;

    typedef struct SubArrayInfoStructure
    {
        int lower_bound;
        int upper_bound;
    } SubArrayInfoStructure;

    SubArrayInfoStructure * SubArraysInfo;

};
