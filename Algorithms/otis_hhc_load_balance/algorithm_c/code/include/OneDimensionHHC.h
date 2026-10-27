#ifndef OneDimensionHHC_H
#define OneDimensionHHC_H
#include "..\include\shared.h"
#include "..\include\HHCNode.h"

class DDimensionHHC;

class OneDimensionHHC
{
public:
    OneDimensionHHC(int groupId, int initialLoad);
    virtual ~OneDimensionHHC();
    int GetGroupId();
    HHCNode  Nodes[6];
    void NewSynchronizeRequest();
    bool AllThreadsFinished;
    DDimensionHHC * ParentDDimensionHHC;
    void ResetLoad();
protected:
private:
    pthread_mutex_t OneDimensionHHC_mutex;
    void CreateInternalConnections();
    int GroupID;
    int ThreadsCounter;
};

#endif
