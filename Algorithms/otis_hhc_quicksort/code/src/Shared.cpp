#include "Shared.h"

int AggregateHHCLevel(Processor * _processor)
{
    if (_processor->OTISGroupId == 0)
        AggregateHHCLevelMasterHHCNode(_processor);
    else
        AggregateHHCLevelNormalHHCNode(_processor);

    return 0;
}

int AggregateHHCLevelMasterHHCNode(Processor * _processor)
{
    int normalHHCNodeWaitFor = (GetHyperCubeNodesNumber(_processor->OTISDimension) * 6) + 1;
    int aggregateHHCNodeWaitFor = normalHHCNodeWaitFor * 2;
    int normalHHCHeadNodeWaitFor = normalHHCNodeWaitFor * 6;
    int masterHHCHeadNodeWaitFor = (normalHHCNodeWaitFor * 5) + 1;

    if ((_processor->HHCNodeId == 0) and (_processor->HyperCubeNodeId == 0))
        _processor->WaitForSubArrays(masterHHCHeadNodeWaitFor);
    else if (_processor->HHCNodeId == 0)
        _processor->WaitForSubArrays(normalHHCHeadNodeWaitFor);

    if ((_processor->HHCNodeId == 1) or (_processor->HHCNodeId == 2))
    {
        _processor->WaitForSubArrays(aggregateHHCNodeWaitFor);
        _processor->SendBackSubArrays(_processor->OTISGroupId,_processor->HyperCubeNodeId, 0);
    }

    if (_processor->HHCNodeId == 3)
    {
        _processor->WaitForSubArrays(normalHHCNodeWaitFor);
        _processor->SendBackSubArrays(_processor->OTISGroupId,_processor->HyperCubeNodeId, 1);
    }

    if (_processor->HHCNodeId == 4)
    {
        _processor->WaitForSubArrays(normalHHCNodeWaitFor);
        _processor->SendBackSubArrays(_processor->OTISGroupId,_processor->HyperCubeNodeId, 2);
    }

    if (_processor->HHCNodeId == 5)
    {
        _processor->WaitForSubArrays(normalHHCNodeWaitFor);
        _processor->SendBackSubArrays(_processor->OTISGroupId,_processor->HyperCubeNodeId, 0);
    }

    return 0;
}

int AggregateHHCLevelNormalHHCNode(Processor * _processor)
{
    if (_processor->HHCNodeId == 0)
    {
        _processor->WaitForSubArrays(6);
    }

    if ((_processor->HHCNodeId == 1) or (_processor->HHCNodeId == 2))
    {
        _processor->WaitForSubArrays(2);
        _processor->SendBackSubArrays(_processor->OTISGroupId,_processor->HyperCubeNodeId, 0);
    }

    if (_processor->HHCNodeId == 3)
    {
        _processor->SendBackSubArrays(_processor->OTISGroupId,_processor->HyperCubeNodeId, 1);
    }

    if (_processor->HHCNodeId == 4)
    {
        _processor->SendBackSubArrays(_processor->OTISGroupId,_processor->HyperCubeNodeId, 2);
    }

    if (_processor->HHCNodeId == 5)
    {
        _processor->SendBackSubArrays(_processor->OTISGroupId,_processor->HyperCubeNodeId, 0);
    }

    return 0;
}

int AggregateHyperCubeLevel(Processor * _processor)
{
    ///No Need to accumulate, already aggregated in HHCNode 0
    if (_processor->OTISDimension == 1)
        return 0;

    ///HyperCube number zero receive only, also HHC != zero don't participate they already
    ///accumulated their data, only HHCNodeId == 0 shall participate in HyperCube phase
    if ((_processor->HyperCubeNodeId == 0) or (_processor->HHCNodeId > 0))
        return 0;

    int mySetBit = GetHyperCubeFirstSetBit(_processor);

///Two types of waiting at the HyperCube level, HHC head nodes == zero other than OTIS group has a simple
///wait approach, each node waits for an amount of sub arrays depending on its first set bit number * 6
///while HHC head nodes at OTIS group zero remains active until the last step of the data accumulation where
///they wait for the load from all nodes
///therefore, for OTIS group zero, HyperCube accumulation shall occur after:
///1- HHC accumulation for all HHC node other than OTIS group zero
///2- HyperCube accumulation in all groups other than OTIS group zero
///3- OTIS level data accumulation from all group heads
///4- HHC accumulation at OTIS group zero
///5- then finally HyperCube accumulation for OTIS group zero

    int waitForSubArrays;
    if(_processor->OTISGroupId != 0)
    {
        waitForSubArrays = 6 * pow(2, mySetBit - 1);
    }
    else
    {
        int normalHHCNodeWaitFor = (GetHyperCubeNodesNumber(_processor->OTISDimension) * 6) + 1;
        int normalHHCHeadNodeWaitFor = normalHHCNodeWaitFor * 6;
        waitForSubArrays = normalHHCHeadNodeWaitFor * pow(2, mySetBit - 1);
    }

    int sendTo = _processor->HyperCubeNodeId - pow(2,mySetBit-1);

    _processor->WaitForSubArrays(waitForSubArrays);
    _processor->SendBackSubArrays(_processor->OTISGroupId,sendTo,0);
    return 0;
}

int AggregateOTISLevel(Processor * _processor)
{
    if ((_processor->OTISNodeId == 0) and  (_processor->OTISGroupId != 0))
    {
        ///make sure that you have received the correct load, otherwise, wait
        _processor->WaitForSubArrays(GetOTISNodesNumber(_processor->OTISDimension));

        int targetHyperCubeId = ((_processor->OTISGroupId / 6) + 1) - 1;
        int targetHHCNodeId =  _processor->OTISGroupId % 6;

        _processor->SendBackSubArrays(0,targetHyperCubeId, targetHHCNodeId);
    }

    return 0;
}

int CreateThreads(int _OTISDimension, int _numberOfThreads, std::vector<int> * _vectorsArray)
{
    int unique_id = 0;
    pthread_t * threadsArray;
    Processor * processorsArray;

    threadsArray = new pthread_t[_numberOfThreads];
    processorsArray = new Processor[_numberOfThreads];

    for (int i = 0 ; i < GetOTISNodesNumber(_OTISDimension); i++) ///OTIS Nodes
    {
        for (int ii = 0 ; ii < GetHyperCubeNodesNumber(_OTISDimension); ii++) ///HHCs HyperCube
        {
            for (int iii = 0 ; iii < 6; iii++) ///Node Id
            {
                processorsArray[unique_id].ProcessorsArray = processorsArray;
                processorsArray[unique_id].OTISDimension = _OTISDimension;
                processorsArray[unique_id].UniqueId = unique_id;
                processorsArray[unique_id].HHCNodeId = iii;
                processorsArray[unique_id].HyperCubeNodeId = ii;
                processorsArray[unique_id].OTISGroupId = i;
                processorsArray[unique_id].OTISNodeId = (6 * ii) + iii;
                processorsArray[unique_id].ReceivedSubArrays = 1; ///since we are starting with combine process
                processorsArray[unique_id].Vector = &_vectorsArray[unique_id];

                int rc = pthread_create(&threadsArray[unique_id], NULL, ThreadRun, (void *) (&processorsArray[unique_id]));
                if (rc != 0)
                    printf ("Error %s\n",strerror(rc));
                assert(0 == rc);

                unique_id++;
            }
        }
    }

    for (int i = 0; i < _numberOfThreads; i++)
        pthread_join(threadsArray[i],NULL);


    uint64_t totalRecurions = 0;
    uint64_t totalIterations = 0;
    uint64_t totalSwaps = 0;

    for (int i = 0; i < _numberOfThreads; i++)
    {
        totalRecurions += processorsArray[i].QuickSorter.NumberOfRecursions;
        totalIterations += processorsArray[i].QuickSorter.NumberOfIterations;
        totalSwaps += processorsArray[i].QuickSorter.NumberOfSwaps;
    }

    for (int i=0; i< (6 * pow(2,_OTISDimension-1)) * (6 * pow(2,_OTISDimension-1)); i++)
    {
        if ((processorsArray[i].OTISGroupId == 0))
            printf("Unique ID % d OTIS Group %d HyperCube %d OTIS Node %d Total Load %d Sub Arrays\n",processorsArray[i].UniqueId, processorsArray[i].OTISGroupId,processorsArray[i].HyperCubeNodeId,processorsArray[i].OTISNodeId, processorsArray[i].ReceivedSubArrays);
    }
    printf("Recursion Calls %I64d, Iterations %I64d, Swaps %I64d \n\n",totalRecurions,totalIterations, totalSwaps);



    return 0;
}

void * ThreadRun (void * args)
{
    Processor * processor = (Processor*) args;
/*
This is the main entry code for threads execution
First, data accumulation at the HHC level shall occur
All nodes except nodes that belong to OTIS group zero shall participate
Next, data accumulation at the HyperCube level shall occur, this happens
internally within each OTIS node depending on OTIS dimension e.g. OTIS 3 == HyperCube 2
therefore two HHC groups 00 and 01
Next, data accumulation shall occur at the OTIS level where all group heads send there data
to their designated node at OTIS group zero where OTIS Group i Node 0 sends to OTIS Group 0 Node i
After that, the data accumulation inside OTIS group 0 shall be triggered once received the correct
sub arrays numbers from other OTIS groups. Now HHC internal level can occur at OTIS group 0 and after which
HyperCube level accumulation can proceed at OTIS group 0, eventually all data shall be accumulated at the
master node ==> OTIS Group 0, HyperCube (HHC Group) 0, HHC Node 0
*/

    processor->StartSorting();
    AggregateHHCLevel(processor);
    AggregateHyperCubeLevel(processor);
    AggregateOTISLevel(processor);

    return 0;
}

int GetOTISNodesNumber(int _OTISDimension)
{
    return (6 * pow(2,_OTISDimension-1));
}

int GetHyperCubeNodesNumber(int _OTISDimension)
{
    return pow(2,_OTISDimension-1);
}

int GetArraySubDivider(int * _intArray,int _size, int _threadsNumber)
{
    int i =0;

    int min_value=0;
    int max_value=0;

    for (i=0; i< _size; i++)
    {
        if (_intArray[i] < min_value)
            min_value = _intArray[i];
        if (_intArray[i] > max_value)
            max_value = _intArray[i];
    }

    return (max_value - min_value) / (_threadsNumber);
}

void SplitArray(int * _intArray, int _size, int _threadsNumber, std::vector<int> * _vectors_array, int _subarray_divider)
{
    int targetArray;
    for (int i=0; i < _size; i++)
    {
        targetArray = (_intArray[i] / _subarray_divider) - 1;
        if (targetArray < 0)
            targetArray = 0;
        else if (targetArray > _threadsNumber - 1)
            targetArray = _threadsNumber -1;

        _vectors_array[targetArray].push_back(_intArray[i]);
    }
}

void GenerateRandomArray(int * _intArray, int _size)
{
    srand((unsigned)time(NULL));

    for(int i=0; i < _size - 1; i++)
    {
        double farcPart = rand() / double(RAND_MAX);
        _intArray[i] = farcPart * _size;
    }
}

void GenerateSortedArray(int * _intArray, int _size)
{
    for(int i=0; i < _size - 1; i++)
    {
        _intArray[i] = i;
    }
}

void GenerateReversedArray(int * _intArray, int _size)
{
    int ii = _size;

    for(int i=0; i < _size - 1; i++)
    {
        _intArray[i] = ii--;
    }
}

void GenerateLocalDistributionArray(int * _intArray, int _size)
{
    int middleElement = _size / 2;
    for(int i=0; i <= middleElement; i++)
    {
        _intArray[i] = i;
    }

    srand((unsigned)time(NULL));

    for(int i= middleElement + 1; i < _size - 1; i++)
    {
        double farcPart = rand() / double(RAND_MAX);
        _intArray[i] = farcPart * _size;
    }
}

int GetHyperCubeFirstSetBit(Processor * _processor)
{
    int hyperCubeDimension = _processor->OTISDimension - 1;

    int mySetBit = 0;

    for (int i = 1; i <= hyperCubeDimension; i++)///loop until first least significant set bit is found
    {
        int maskValue = pow(2,i-1);
        if ((_processor->HyperCubeNodeId & maskValue) == maskValue)
        {
            mySetBit = i;
            break;
        }
    }

    return mySetBit;
}
