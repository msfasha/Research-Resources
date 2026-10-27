#include "..\include\DDimensionHHC.h"

DDimensionHHC::DDimensionHHC(int hhcDimension, int initialLoad)
{
    HHCDimension = hhcDimension;
    ThreadsCounter = 0;
    NumberOfGroups = pow(2,HHCDimension-1);

    pthread_barrier_init(&GlobalBarrier,NULL,NumberOfGroups * 6);


    srand(time(NULL)); //To be used in program to generate random load

    for(int i=0; i< NumberOfGroups; i++)
    {
        OneDimensionHHC * oneDimensionHHC = new OneDimensionHHC(i, initialLoad);

        oneDimensionHHC->ParentDDimensionHHC = this;

        HCCGroups.push_back(oneDimensionHHC);
    }

    CreateExternalConnections();
}

void DDimensionHHC::CreateExternalConnections()
{
    //Create Peer Connections
    for(int i=0; i< NumberOfGroups; i++)
    {
        for (int ii=i+1; ii<NumberOfGroups; ii++)
        {
            if (GetHamiltonDistance(HCCGroups[i]->GetGroupId() ^ HCCGroups[ii]->GetGroupId()) == 1)
            {
                for (int iii=0; iii<6; iii++)
                {
                    HCCGroups[i]->Nodes[iii].Peers.push_back(&HCCGroups[ii]->Nodes[iii]);
                    HCCGroups[ii]->Nodes[iii].Peers.push_back(&HCCGroups[i]->Nodes[iii]);
                }
            }
        }
    }
}

DDimensionHHC::~DDimensionHHC()
{
    pthread_barrier_destroy(&GlobalBarrier);
}

int DDimensionHHC::GetHHCDimension()
{
    return HHCDimension;
}

int DDimensionHHC::GetNumberOfGroups()
{
    return NumberOfGroups;
}

void DDimensionHHC::ResetLoad()
{
    for(int i=0; i< NumberOfGroups; i++)
    {
        HCCGroups[i]->ResetLoad();
    }
}

void DDimensionHHC::PrintLoadValues(char * title)
{
    printf("%s\n\n",title);
    for(int i=0; i< NumberOfGroups; i++)
    {
        for (int ii=0; ii < 6; ii++)
        printf("%d-%d Old : %-5d, New : %d\n", HCCGroups[i]->Nodes[ii].GetGroupId(), HCCGroups[i]->Nodes[ii].GetNodeId(), HCCGroups[i]->Nodes[ii].GetOldLoad(), HCCGroups[i]->Nodes[ii].GetCurrentLoad());
    }
}

int DDimensionHHC::GetTotalLoadBeforeLB()
{
    int TotalLoad = 0;

    for(int i=0; i< NumberOfGroups; i++)
    {
        for (int ii=0; ii < 6; ii++)
        TotalLoad += HCCGroups[i]->Nodes[ii].GetCurrentLoad();
    }
    return TotalLoad;
}

int DDimensionHHC::GetTotalLoadAfterLB()
{
    int TotalLoad = 0;

    for(int i=0; i< NumberOfGroups; i++)
    {
        for (int ii=0; ii < 6; ii++)
        TotalLoad += HCCGroups[i]->Nodes[ii].GetCurrentLoad();
    }
    return TotalLoad;
}

void DDimensionHHC::PrintNodesAndPeers()
{
    for(int i=0; i< NumberOfGroups; i++)
    {
        for (int ii=0; ii < 6; ii++)
        {
            printf("%d-%d, Peers Are : \n",HCCGroups[i]->Nodes[ii].GetGroupId(),HCCGroups[i]->Nodes[ii].GetNodeId());
            for (unsigned int iii=0; iii < HCCGroups[i]->Nodes[ii].Peers.size();iii++)
                printf("    %d-%d \n",HCCGroups[i]->Nodes[ii].Peers[iii]->GetGroupId(),HCCGroups[i]->Nodes[ii].Peers[iii]->GetNodeId());
        }
    }
}

int DDimensionHHC::GetMaximumError()
{
    int min = 999999999;
    int max = 0;

    for(int i=0; i< NumberOfGroups; i++)
    {
        for (int ii=0; ii < 6; ii++)
        {
            if (HCCGroups[i]->Nodes[ii].GetCurrentLoad() > max)
                max = HCCGroups[i]->Nodes[ii].GetCurrentLoad();

            if (HCCGroups[i]->Nodes[ii].GetCurrentLoad() < min)
                min = HCCGroups[i]->Nodes[ii].GetCurrentLoad();
        }
    }

    return (max-min);
}
