#ifndef DDimensionHHC_H
#define DDimensionHHC_H

#include "..\include\OneDimensionHHC.h"

class DDimensionHHC
{
public:
    DDimensionHHC(int dimention, int initialLoad);
    virtual ~DDimensionHHC();
    int GetHHCDimension();
    int GetNumberOfGroups();
    vector<OneDimensionHHC*> HCCGroups;
    int GetMaximumError();
    int GetTotalLoadBeforeLB();
    int GetTotalLoadAfterLB();
    void PrintLoadValues(char * title);
    void ResetLoad();
    void PrintNodesAndPeers();
    pthread_barrier_t GlobalBarrier;

protected:
private:
    void CreateExternalConnections();
    int HHCDimension;
    int NumberOfGroups;
    int ThreadsCounter;
};

#endif
