#ifndef NETWORK_H
#define NETWORK_H
#include "HHCNode.h"

#define NETWORK_SPEED 256000000 ///250 Mbps
#define DATA_UNIT_SIZE 1024  /// 1 KB Per Unit
#define NETWORK_SLAG 5 /// milliseconds

class Network
{
    public:
        Network();
        virtual ~Network();
        int GetLoadInfoFromNode(HHCNode *targetNode);
        void SendLoadtoNode(HHCNode * sender,HHCNode * receiver, int load);
        int GetNodeId(HHCNode * node);
        int GetGroupId(HHCNode * node);
        void LockNode(HHCNode * node);
        void UnLockNode(HHCNode * node);
        int GetNumberOfLoadInfoExchangeSteps();
        int GetNumberOfDataMoveSteps();
        int GetNumberOfActualUnitsMoved();
        static int TotalNumberOfLoadInfoExchangeSteps;
        static int TotalNumberOfDataMoveSteps;
        static int TotalNumberOfActualUnitsMoved;
        static double TotalExecutionTime;

    protected:
    private:
        int NumberOfLoadInfoExchangeSteps;
        int NumberOfDataMoveSteps;
        int NumberOfActualUnitsMoved;
        double ExecutionTime;
        void GenerateLoadInfoExchangeOverHead();
        void GenerateDataMoveOverHead(int load);
};

#endif // NETWORK_H
