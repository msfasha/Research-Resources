#include "..\include\Tester.h"
#include "..\include\QuickSorter.h"



Tester::Tester()
{
    NumberOfCPUs = omp_get_num_procs();
    GenerateSubArrayInfo();//Given CPU number and Array size, find the lower and upper bounds for each CPU
}

Tester::~Tester()
{
    //dtor
}

void Tester::StartTesting(void)
{
      clock_t StartTime, EndTime;
      QuickSorter quickSorter;

    printf("Number of CPUs on this machine is : %d \n\n",NumberOfCPUs);
    printf("Starting test with array size of : %d\n\n",ArraySize);
    printf("S I N G L E T O N   M O D E...\n\n");

    GenerateReversedArray();
    quickSorter.ResetCounters();
    StartTime = clock();
    quickSorter.RunSortPivotMiddle(MainArray,0,ArraySize-1);
    EndTime = clock();
    printf("Worst Scenario, Number of Iterations : %I64d, Swaps : %I64d CPU Time : %g Number of Recursions : %I64d \n",quickSorter.GetNumberOfIterations(),quickSorter.GetNumberOfSwaps(),(double)(EndTime - StartTime)/CLOCKS_PER_SEC,quickSorter.GetNumberOfRecursions());

    GenerateSortedArray();
    quickSorter.ResetCounters();
    StartTime = clock();
    quickSorter.RunSortPivotMiddle(MainArray,0,ArraySize-1);
    EndTime = clock();
    printf("Best Scenario, Number of Iterations : %I64d, Swaps : %I64d CPU Time : %g Number of Recursions : %I64d \n",quickSorter.GetNumberOfIterations(),quickSorter.GetNumberOfSwaps(),(double)(EndTime - StartTime)/CLOCKS_PER_SEC,quickSorter.GetNumberOfRecursions());

    GenerateRandomArray();
    quickSorter.ResetCounters();
    StartTime = clock();
    quickSorter.RunSortPivotMiddle(MainArray,0,ArraySize-1);
    EndTime = clock();
    printf("Average Scenario, Number of Iterations : %I64d, Swaps : %I64d CPU Time : %g Number of Recursions : %I64d \n",quickSorter.GetNumberOfIterations(),quickSorter.GetNumberOfSwaps(),(double)(EndTime - StartTime)/CLOCKS_PER_SEC,quickSorter.GetNumberOfRecursions());

    printf("___________________________________________________________\n\n");
    printf("M U L T I   T H R E A D I N G   M O D E...\n");

    GenerateSortedArray();
    StartTime = clock();
    RunMultiThreadSort();
    EndTime = clock();
    printf("MultiThread Scenario, Number of Iterations : %I64d, Swaps : %I64d CPU Time : %g Number of Recursions : %I64d \n",quickSorter.GetNumberOfIterations(),quickSorter.GetNumberOfSwaps(),(double)(EndTime - StartTime)/CLOCKS_PER_SEC,quickSorter.GetNumberOfRecursions());

    printf("___________________________________________________________\n\n");
    printf("M U L T I   T H R E A D I N G   O P E N M P   M O D E...\n");

    GenerateSortedArray();
    StartTime = clock();
    RunMultiThreadOpenMPSort();
    EndTime = clock();
    printf("MultiThread OPENMP Scenario, Number of Iterations : %I64d, Swaps : %I64d CPU Time : %g Number of Recursions : %I64d \n",quickSorter.GetNumberOfIterations(),quickSorter.GetNumberOfSwaps(),(double)(EndTime - StartTime)/CLOCKS_PER_SEC,quickSorter.GetNumberOfRecursions());
}

void* Tester::ThreadRoutine(void * args)
{
    QuickSorter innerQuickSorter;
    SubArrayInfoStructure *my = (SubArrayInfoStructure *)args;
    innerQuickSorter.ResetCounters();
    innerQuickSorter.RunSortPivotMiddle(MainArray, my->lower_bound,my->upper_bound);

    //printf("Finished sorting elements %d to %d, number of iterations %I64d Number of Recursions %I64d Number of Swaps %I64d ...\n",my->lower_bound,my->upper_bound,innerQuickSorter.GetNumberOfIterations(),innerQuickSorter.GetNumberOfRecursions(),innerQuickSorter.GetNumberOfSwaps());
    return NULL;
}

void Tester::RunMultiThreadSort()
{
    pthread_t * threadsArray = new pthread_t[NumberOfCPUs];

    for (int i =0 ; i<NumberOfCPUs ; i++)
    {
        int rc = pthread_create(&threadsArray[i], NULL, ThreadRoutine, (void *) (&SubArraysInfo[i]));
        assert(0 == rc);
        pthread_join(threadsArray[i],NULL);
    }
}

void Tester::RunMultiThreadOpenMPSort()
{
    int tid;

    /* Fork a team of threads giving them their own copies of variables */
    #pragma omp parallel private(tid)
    {
        QuickSorter innerQuickSorter;
        /* Obtain thread number */
        tid = omp_get_thread_num();
        innerQuickSorter.RunSortPivotMiddle(MainArray,SubArraysInfo[tid].lower_bound, SubArraysInfo[tid].upper_bound);
        //printf("Thread number %d finished sorting elements %d to %d\n", tid, SubArraysInfo[tid].lower_bound, SubArraysInfo[tid].upper_bound);
    }  /* All threads join master thread and disband */
}

void Tester::GenerateRandomArray()
{
    srand((unsigned)time(NULL));

    for(int i=0; i<=ArraySize-1; i++)
    {
        double farcPart = rand() / double(RAND_MAX);
        MainArray[i] = farcPart * ArraySize;
    }
}

void Tester::GenerateSortedArray()
{
    for(int i=0; i<=ArraySize-1; i++)
    {
        MainArray[i] = i;
    }
}

void Tester::GenerateReversedArray()
{
    int ii = ArraySize;

    for(int i=0; i<=ArraySize-1; i++)
    {
        MainArray[i] = ii--;
    }
}

void Tester::GenerateSubArrayInfo()
//Given CPU number and Array size, find the lower and upper bounds for each CPU
{
    int SubArraySize = ArraySize/NumberOfCPUs;
    SubArraysInfo = new SubArrayInfoStructure [NumberOfCPUs];

    for (int i = 0; i<NumberOfCPUs; i++)
    {
        SubArraysInfo[i].lower_bound  = i * SubArraySize;
        SubArraysInfo[i].upper_bound  = (SubArraySize * (i+1) -1);
    }
}

