#ifndef QUICKSORTER_H
#define QUICKSORTER_H
#include "inttypes.h"
#include <stdio.h>      /* printf, scanf, puts, NULL */
#include <omp.h>

class QuickSorter
{
public:
    QuickSorter();
    void RunSortPivotMiddle(int* list, int start, int end);
    int RunMPISort();
    void ResetCounters();
    uint64_t GetNumberOfIterations();
    uint64_t GetNumberOfRecursions();
    uint64_t GetNumberOfSwaps();
    virtual ~QuickSorter();
protected:
private:
    uint64_t NumberOfIterations;
    uint64_t NumberOfRecursions;
    uint64_t NumberOfSwaps;
};

#endif // QUICKSORTER_H
