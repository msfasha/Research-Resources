#include "..\include\OneDimensionHHC.h"
#include "..\include\HHCNode.h"
#include "..\include\DDimensionHHC.h"

OneDimensionHHC::OneDimensionHHC(int groupId, int initialLoad)
{
    GroupID = groupId;
    AllThreadsFinished = false;
    OneDimensionHHC_mutex = PTHREAD_MUTEX_INITIALIZER;
    ThreadsCounter = 0;

    for (int i=0; i < 6; i++)
    {
        Nodes[i].SetNodeID(i >=3?i+1:i);///to skip id 3
        Nodes[i].SetGroupId(GroupID);
        Nodes[i].ParentOneDimensionHHC = this;

        if (initialLoad == 0)
        {
            Nodes[i].SetCurrentLoad(rand() % 100);
            Nodes[i].SetOldLoad(Nodes[i].GetCurrentLoad());
        }
        else
        {
            if((Nodes[i].GetGroupId()== 0) and (Nodes[i].GetNodeId()== 0))
            {
                Nodes[i].SetCurrentLoad (initialLoad);
                Nodes[i].SetOldLoad(Nodes[i].GetCurrentLoad());
            }
        }
    }

    CreateInternalConnections();
}

void OneDimensionHHC::CreateInternalConnections()
{
    ///Create Peer Connections
    for (int i= 0; i< 6; i++)
    {
        for (int ii=i+1; ii<6; ii++)
        {
            if (GetHamiltonDistance(Nodes[i].GetNodeId() ^ Nodes[ii].GetNodeId()) == 1)
            {
                Nodes[i].Peers.push_back(&Nodes[ii]);
                Nodes[ii].Peers.push_back(&Nodes[i]);
            }
        }
    }

    //These connections break the Hamilton distance rule
    Nodes[1].Peers.push_back(&Nodes[2]);
    Nodes[2].Peers.push_back(&Nodes[1]);
    Nodes[4].Peers.push_back(&Nodes[5]);
    Nodes[5].Peers.push_back(&Nodes[4]);

}

OneDimensionHHC::~OneDimensionHHC()
{
    //dtor
}

void OneDimensionHHC::NewSynchronizeRequest()
{
    pthread_mutex_lock(&OneDimensionHHC_mutex);

    if (AllThreadsFinished)
    {
        ThreadsCounter=0;
        AllThreadsFinished=false;
    }

    ThreadsCounter = ThreadsCounter + 1;

    if (ThreadsCounter >= 6)
        AllThreadsFinished = true;

    pthread_mutex_unlock(&OneDimensionHHC_mutex);
}

int OneDimensionHHC::GetGroupId()
{
    return GroupID;
}

void OneDimensionHHC::ResetLoad()
{
    for (int i=0; i < 6; i++)
    {
        Nodes[i].ResetLoad();
    }
}
