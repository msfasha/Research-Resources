#ifndef PROCESSOR_H
#define PROCESSOR_H
#include <pthread.h>
#include <stdio.h>
#include <math.h>
#include <vector>
#include "quicksort.h"


class Processor
{
public:
    Processor();
    void SendBackSubArrays(int otis_group_id, int hhc_group_id, int hhc_node_id);
    void WaitForSubArrays(int wait_counter);
    void StartSorting();
    int ResolveProcessorUniqueID(int _OTISGroupId, int _hyperCubeNodeId, int _hhcNodeId);

    int UniqueId;
    int OTISGroupId; ///1..5/11/23/47 similar to HHC nodes in each OTISGroups
    int OTISNodeId; ///0..5/11/23/47 across all HHCs in an OTIS node
    int HyperCubeNodeId;///same as HHC group id
    int HHCNodeId; ///0..5 every node in one HHC
    std::vector<int> * Vector;
    pthread_mutex_t Mutex;
    pthread_cond_t cond;
    int ReceivedSubArrays;
    int OTISDimension;
    Processor * ProcessorsArray;
    QuickSort QuickSorter;

    virtual ~Processor();

    static int TotalOpticalMoves;
    static int TotalElectricalMoves;
protected:
private:



};

#endif // PROCESSOR_H
