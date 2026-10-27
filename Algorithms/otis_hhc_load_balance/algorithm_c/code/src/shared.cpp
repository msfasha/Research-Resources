#include "..\include\shared.h"
#include "..\include\HHCNode.h"
#include "..\include\Network.h"
#include "..\include\OneDimensionHHC.h"
#include "..\include\DDimensionHHC.h"

int GetTriangleNodesAverage(HHCNode * currentNode,Network * network);
void RunInTriangleLB(HHCNode * currentNode, Network * network, int averageLoad);
void RunCrossTriangleVerticalLB(HHCNode * currentNode,Network * network);


int GetHamiltonDistance (int value)
{
    int count = 0;
    while (value > 0)             // until all bits are zero
    {
        if ((value & 1) == 1)     // check lower bit
            count++;
        value >>= 1;              // shift bits, removing lower bit
    }
    return count;
}

/**________________________________________________________________________________________________________________________
                                    A L G O R I T H M     O N E
________________________________________________________________________________________________________________________**/
void* StartHHCLBOne(void * args)
{
    ///This code is run by every node-thread in the D-Dimension HHC
    HHCNode * currentNode = (HHCNode*)args;
    Network * network = new Network();

    int averageLoad = GetTriangleNodesAverage(currentNode,network);
    RunInTriangleLB(currentNode,network,averageLoad);
    RunCrossTriangleVerticalLB(currentNode,network);

    ///Run LB across D-Dimensions in a Hypercube pattern
    int excessLoad;

    /**Important, we put HHCDimension - 2 because HyperCubeD = HHCD -1 and we start from 0
    So for D hypercude iterations we count 0,1...D-1 where D = HHCD-1 **/
    for(int i = 0; i <=  currentNode->ParentOneDimensionHHC->ParentDDimensionHHC->GetHHCDimension()-2 ; i++)
    {
        for (unsigned int ii = 0; ii < currentNode->Peers.size(); ii++)
        {
            int mask =  pow(2, i);
            int xOred = currentNode->GetGroupId() ^ network->GetGroupId(currentNode->Peers[ii]);
            //printf("==> iteration %d mask %d xOred %d for nodes %d-%d peer is %d-%d\n",i, mask, xOred , currentNode->GetGroupId(), currentNode->GetNodeId(),currentNode->Peers[ii]->GetGroupId(),currentNode->Peers[ii]->GetNodeId() );
            if ( xOred == mask)
            {
                network->LockNode(currentNode->Peers[ii]);
                int peerLoad = network->GetLoadInfoFromNode(currentNode->Peers[ii]);
                if((currentNode->GetCurrentLoad() - peerLoad ) >= 2)
                {
                    averageLoad = floor((currentNode->GetCurrentLoad() +   peerLoad)/2);
                    excessLoad = currentNode->GetCurrentLoad() - averageLoad;
                    network->SendLoadtoNode(currentNode,currentNode->Peers[ii],excessLoad);
                }
                network->UnLockNode(currentNode->Peers[ii]);
            }
        }
        pthread_barrier_wait(&currentNode->ParentOneDimensionHHC->ParentDDimensionHHC->GlobalBarrier);
    }
    return NULL;
}

int GetTriangleNodesAverage(HHCNode * currentNode, Network * network)
{
    vector<int> weightsArray;

    weightsArray.push_back(currentNode->GetCurrentLoad());

    for (unsigned int i=0; i < currentNode->Peers.size(); i++)
    {
        if(currentNode->GetGroupId() == network->GetGroupId(currentNode->Peers[i]))
        {
            ///Nodes in each triangle differs by 2 in their ids at maximum with the other triangle members
            if (abs(currentNode->GetNodeId() - network->GetNodeId(currentNode->Peers[i])) <= 2) //valid triangle partner
                weightsArray.push_back(network->GetLoadInfoFromNode(currentNode->Peers[i]));
        }
    }

    ///Second step, each node in each triangle computes total load and average load
    int totalLoad = 0;

    for (unsigned int i=0; i < weightsArray.size(); i++)
        totalLoad+=weightsArray[i];

    return floor((totalLoad + 1) / weightsArray.size());//Add 1 to compensate for the missing one in division by 3

}

void RunInTriangleLB(HHCNode * currentNode, Network * network, int averageLoad)
{
    int excessLoad = currentNode->GetCurrentLoad() - averageLoad;

    for (unsigned int i=0; i < currentNode->Peers.size(); i++) ///Loop throught peers look for potential receivers
    {
        ///Does the node have excess load
        if(excessLoad > 0)
        {
            ///Check both nodes are in the same group
            if(currentNode->GetGroupId() == network->GetGroupId(currentNode->Peers[i]))
            {
                ///Look for Valid triangle partners only
                if (abs(currentNode->GetNodeId() - network->GetNodeId(currentNode->Peers[i])) <= 2)
                {
                    network->LockNode(currentNode->Peers[i]);

                    int neededWorkLoad = network->GetLoadInfoFromNode(currentNode->Peers[i]) - averageLoad;
                    if (neededWorkLoad < 0)
                    {
                        neededWorkLoad = abs(neededWorkLoad);

                        if (excessLoad >= neededWorkLoad )
                        {
                            network->SendLoadtoNode(currentNode, currentNode->Peers[i],neededWorkLoad);
                            excessLoad = excessLoad - neededWorkLoad;
                        }
                        else
                        {
                            network->SendLoadtoNode(currentNode,currentNode->Peers[i],excessLoad);
                            excessLoad=0;
                        }
                    }
                    network->UnLockNode(currentNode->Peers[i]);
                }
            }
        }
    }

    currentNode->RequestSynchronize();
    while (!currentNode->AllThreadsFinished())
        ;
}

void RunCrossTriangleVerticalLB(HHCNode * currentNode,Network * network)
{
    int excessLoad;
    int averageLoad;

    for (unsigned int i=0; i < currentNode->Peers.size(); i++)
    {
        if (currentNode->GetGroupId() == network->GetGroupId(currentNode->Peers[i]))//The same group
        {
            if (abs(currentNode->GetNodeId() - network->GetNodeId(currentNode->Peers[i])) == 4)//Vertically adjacent
            {
                network->LockNode(currentNode->Peers[i]);
                int peerLoad =  network->GetLoadInfoFromNode(currentNode->Peers[i]);
                if((currentNode->GetCurrentLoad() - peerLoad) >= 2)
                {
                    averageLoad = floor((currentNode->GetCurrentLoad() + peerLoad)/2);
                    excessLoad = currentNode->GetCurrentLoad() - averageLoad;
                    network->SendLoadtoNode(currentNode,currentNode->Peers[i],excessLoad);
                }
                network->UnLockNode(currentNode->Peers[i]);
            }
        }
    }

    pthread_barrier_wait(&currentNode->ParentOneDimensionHHC->ParentDDimensionHHC->GlobalBarrier);
}

