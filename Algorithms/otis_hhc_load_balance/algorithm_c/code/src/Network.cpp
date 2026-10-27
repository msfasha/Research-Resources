#include "..\include\Network.h"

int Network::TotalNumberOfLoadInfoExchangeSteps = 0;
int Network::TotalNumberOfDataMoveSteps = 0;
int Network::TotalNumberOfActualUnitsMoved = 0;
double Network::TotalExecutionTime = 0;

Network::Network()
{
    NumberOfLoadInfoExchangeSteps = 0;
    NumberOfDataMoveSteps = 0;
    NumberOfActualUnitsMoved = 0;
    ExecutionTime = 0;
}

Network::~Network()
{
    //dtor
}

int Network::GetLoadInfoFromNode(HHCNode *targetNode)
{
    GenerateLoadInfoExchangeOverHead();
    return targetNode->GetCurrentLoad();
}

void Network::SendLoadtoNode(HHCNode * sender,HHCNode * receiver, int load)
{
    sender->DeductExcessLoad(load);
    GenerateDataMoveOverHead(load);
    receiver->ReceiveNewLoad(load);
}

int Network::GetNodeId(HHCNode * node)
{
    //ReadDataOverHead();
    return node->GetNodeId();
}

int Network::GetGroupId(HHCNode * node)
{
    //ReadDataOverHead();
    return node->GetGroupId();
}

void Network::LockNode(HHCNode * node)
{
    node->LockNode();
    //ReadDataOverHead();
}

void Network::UnLockNode(HHCNode * node)
{
    node->UnLockNode();
    //ReadDataOverHead();
}

void Network::GenerateLoadInfoExchangeOverHead()
{
    NumberOfLoadInfoExchangeSteps = NumberOfLoadInfoExchangeSteps + 1;
    TotalNumberOfLoadInfoExchangeSteps = TotalNumberOfLoadInfoExchangeSteps + 1;
   // Sleep(NETWORK_SLAG);
}

void Network::GenerateDataMoveOverHead(int load)
{
    NumberOfDataMoveSteps = NumberOfDataMoveSteps + 1;
    TotalNumberOfDataMoveSteps = TotalNumberOfDataMoveSteps + 1;

    NumberOfActualUnitsMoved = NumberOfActualUnitsMoved + 1;
    TotalNumberOfActualUnitsMoved = TotalNumberOfActualUnitsMoved + 1;

    double tmp = double(load * DATA_UNIT_SIZE * 8) / NETWORK_SPEED;
    ExecutionTime = ExecutionTime + tmp;
    TotalExecutionTime = TotalExecutionTime + tmp;
}

int Network::GetNumberOfLoadInfoExchangeSteps()
{
    return NumberOfLoadInfoExchangeSteps;
}

int Network::GetNumberOfDataMoveSteps()
{
    return NumberOfDataMoveSteps;
}

int Network::GetNumberOfActualUnitsMoved()
{
    return NumberOfActualUnitsMoved;
}

