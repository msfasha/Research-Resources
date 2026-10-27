#ifndef HHCNode_H
#define HHCNode_H

#include <iostream>
#include <vector>
#include "shared.h"

using namespace std;

class OneDimensionHHC;//forward declarasion for recursive inclusion
class DDimensionHHC;//forward declarasion for recursive inclusion

class HHCNode
{
public:
    HHCNode();
    virtual ~HHCNode();
    vector<HHCNode*> Peers;
    void ReceiveNewLoad(int load);
    void DeductExcessLoad(int load);
    int GetCurrentLoad();
    int GetOldLoad();
    int GetNodeId();
    int GetGroupId();
    void SetNodeID(int nodeId);
    void SetGroupId(int groupIp);
    void SetCurrentLoad (int load);
    void SetOldLoad (int load);
    void LockNode();
    void UnLockNode();
    void ResetLoad();
    void RequestSynchronize();
    bool AllThreadsFinished();
    OneDimensionHHC * ParentOneDimensionHHC;

protected:

private:
    int CurrentLoad;
    int OldLoad;
    int NodeId;
    pthread_mutex_t thread_mutex;
    int GroupId;
};
#endif
