#include "..\include\shared.h"
#include "..\include\DDimensionHHC.h"
#include "..\include\Network.h"


using namespace std;
DDimensionHHC * dDimensionHHC;
pthread_t * threadsArray;

clock_t StartTime;
clock_t EndTime;

void PrintInputErrorMessage();
void PrintFinalResults(char * title);
void RunInputValidation(int argc, char **argv);

int main(int argc, char **argv)
{
    RunInputValidation(argc, argv);

    dDimensionHHC = new DDimensionHHC(atoi(argv[1]), atoi(argv[2]));
    threadsArray = new pthread_t[dDimensionHHC->HCCGroups.size()*6];

    ///dDimensionHHC->PrintNodesAndPeers(); ///For testing the validity of the DDimension HHC construct

    StartTime = clock();
    for (unsigned int i = 0; i < dDimensionHHC->HCCGroups.size(); i++)
    {
        for (int ii = 0; ii < 6; ii++)
        {
            int rc = pthread_create(&threadsArray[6 * i + ii], NULL, StartHHCLBOne, (void *) (&dDimensionHHC->HCCGroups[i]->Nodes[ii]));
            assert(0 == rc);
        }
    }
    for (unsigned int i = 0; i < dDimensionHHC->HCCGroups.size(); i++)
    {
        for (int ii = 0; ii < 6; ii++)
            pthread_join(threadsArray[6 * i + ii],NULL);
    }
    EndTime = clock();

    dDimensionHHC->PrintLoadValues(const_cast<char*>("First Algorithm Result"));
    PrintFinalResults(const_cast<char*>("Second Algorithm Results :"));

    return 0;
}

void PrintInputErrorMessage()
{
    printf("____________________________________________________\n\n");
    printf("Please enter the program name followed by : \n\n");
    printf ("1-HHC Dimension number between 1 and 8 e.g. \n\n");
    printf("HHC.exe 5 \n\n");
    printf ("OR \n\n");
    printf("2-HHC Dimension number and initial data size e.g. \n\n");
    printf("HHC.exe 5 100000 \n\n");
    printf("If the initial load size is not entered, random load size\n");
    printf("will be generated at each node.\n");
    printf("If initial load size is provided, only one node will be\n");
    printf("set with this load value and the load balancing algorithm\n");
    printf("will distribute the load across all members\n\n");
    exit(1);
}

void PrintFinalResults(char * title)
{
    printf("\n%s\n",title);
    printf("____________________________________________________________\n");
    printf("Number of HHC Units............................: %d \n",dDimensionHHC->HCCGroups.size());
    printf("Number of HHC Nodes/Threads....................: %d \n",dDimensionHHC->HCCGroups.size() * 6);
    printf("Number of Communications Steps (Info Exchange).: %d \n", Network::TotalNumberOfLoadInfoExchangeSteps);
    printf("Number of Communications Steps (Data Moves)....: %d \n", Network::TotalNumberOfDataMoveSteps);
    printf("Total Load Before Load Balancing...............: %d \n", dDimensionHHC->GetTotalLoadBeforeLB());
    printf("Total Load Before Load Balancing...............: %d \n", dDimensionHHC->GetTotalLoadAfterLB());
    printf("Total Number of Communications Steps...........: %d \n", Network::TotalNumberOfLoadInfoExchangeSteps + Network::TotalNumberOfDataMoveSteps);
    //printf("Total Number of Load Units Moved...............: %d \n", Network::TotalNumberOfActualUnitsMoved);
    printf("Estimated Execution Time.......................: %g \n", Network::TotalExecutionTime);
    printf("Maximum Error..................................: %d \n", dDimensionHHC->GetMaximumError());
    printf("Actual Execution Time..........................: %g \n",(double)(EndTime - StartTime)/CLOCKS_PER_SEC);
    printf("____________________________________________________________\n");
}
void RunInputValidation(int argc, char **argv)
{
    if(argc < 2)
    {
        PrintInputErrorMessage();
    }
    else if ((atoi(argv[1]) < 1) ^ (atoi(argv[1]) > 8))
    {
        PrintInputErrorMessage();
    }
    if(argc > 3)
        PrintInputErrorMessage();

    if (argc == 3)
    {
        if (atoi(argv[2]) <= 0)
            PrintInputErrorMessage();
    }
}
