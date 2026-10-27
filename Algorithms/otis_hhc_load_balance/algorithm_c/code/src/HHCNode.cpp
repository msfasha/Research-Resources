#include "..\include\HHCNode.h"
#include "..\include\OneDimensionHHC.h"
#include "..\include\DDimensionHHC.h"

HHCNode::HHCNode()
{
    thread_mutex = PTHREAD_MUTEX_INITIALIZER;
    CurrentLoad = 0;
    OldLoad = 0;
}

HHCNode::~HHCNode()
{
    //dtor
}

void HHCNode::DeductExcessLoad(int load)
{
    CurrentLoad = CurrentLoad - load;
}

void HHCNode::ReceiveNewLoad(int load)
{
    CurrentLoad = CurrentLoad + load;
}

int HHCNode::GetCurrentLoad()
{
    return CurrentLoad;
}

int HHCNode::GetOldLoad()
{
    return OldLoad;
}

void HHCNode::SetCurrentLoad (int load)
{
    CurrentLoad = load;
}

void HHCNode::SetOldLoad (int load)
{
    OldLoad = load;
}

int HHCNode::GetNodeId()
{
    return NodeId;
}

void HHCNode::ResetLoad()
{
    CurrentLoad = OldLoad;
}

int HHCNode::GetGroupId()
{
    return GroupId;
}
void HHCNode::SetNodeID(int nodeId)
{
    NodeId = nodeId;
}
void HHCNode::SetGroupId(int groupIp)
{
    GroupId = groupIp;
}
void HHCNode::LockNode()
{
    pthread_mutex_lock(&thread_mutex);
}
void HHCNode::UnLockNode()
{
    pthread_mutex_unlock(&thread_mutex);
}
void HHCNode::RequestSynchronize()
{
    ParentOneDimensionHHC->NewSynchronizeRequest();
}

bool HHCNode::AllThreadsFinished()
{
    return ParentOneDimensionHHC->AllThreadsFinished;;
}

