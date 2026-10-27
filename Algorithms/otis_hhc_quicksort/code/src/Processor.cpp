#include "Processor.h"

int Processor::TotalOpticalMoves = 0;
int Processor::TotalElectricalMoves = 0;

Processor::Processor()
{
    UniqueId = -1;
    OTISGroupId = -1;
    OTISNodeId = -1;
    HyperCubeNodeId = -1;
    HHCNodeId = -1;
    ReceivedSubArrays = 0;
    Mutex = PTHREAD_MUTEX_INITIALIZER;
    cond = PTHREAD_COND_INITIALIZER;
}

Processor::~Processor()
{
    //dtor
}


void Processor::SendBackSubArrays(int _OTISGroupId, int _hyperCubeNodeId, int _hhcNodeId)
{
    int ProcessorId = ResolveProcessorUniqueID(_OTISGroupId, _hyperCubeNodeId, _hhcNodeId);

    Processor * TargetProcessor = &ProcessorsArray[ProcessorId];

    pthread_mutex_lock(&TargetProcessor->Mutex);
    TargetProcessor->ReceivedSubArrays = ReceivedSubArrays + TargetProcessor->ReceivedSubArrays;
    pthread_mutex_unlock(&TargetProcessor->Mutex);

    ///My own buffer will be empty since its all moved
    ReceivedSubArrays = 0;

    pthread_cond_signal(&TargetProcessor->cond);

    if (TargetProcessor->OTISGroupId == OTISGroupId)
        ++TotalElectricalMoves;
    else
        ++TotalOpticalMoves;
}

void Processor::WaitForSubArrays(int _waitCouonter)
{
    pthread_mutex_lock(&Mutex);
    while (ReceivedSubArrays < _waitCouonter)
    {
        pthread_cond_wait(&cond, &Mutex);

    }
    pthread_mutex_unlock(&Mutex);
}

int Processor::ResolveProcessorUniqueID(int _OTISGroupId, int _hyperCubeNodeId, int _hhcNodeId)
{
    return _OTISGroupId * (6 * pow(2,OTISDimension -1)) + 6 * _hyperCubeNodeId + _hhcNodeId;
}

void Processor::StartSorting()
{
    QuickSorter.RunQuickSortVector(Vector,0, Vector->size()-1);
}
