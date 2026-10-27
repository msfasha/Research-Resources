#include "functions.h"
double t1,t2,t3,t4,t5,t6;

void RunAsMaster(int array_size, int number_of_nodes)
{
    int i,ii;
    int subarray_divider;
    vector<int> * vector_array;
    int * main_array;

    printf("*****************************************************************************\n");
    printf("**** Parallel program started... \n");
    printf("**** Generating random array with : %d elements \n", array_size);

    t1 = MPI_Wtime();
    main_array = GenerateRandomArray(array_size);
    t2 = MPI_Wtime();

    //get subarray_divider
    int min_value=0;
    int max_value=0;

    printf("**** Main array is being divided into : %d sub arrays \n", number_of_nodes);

    for (i=0; i< array_size; i++)
    {
        if (main_array[i] < min_value)
            min_value = main_array[i];
        if (main_array[i] > max_value)
            max_value = main_array[i];
    }

    subarray_divider = (max_value - min_value)/number_of_nodes;
    vector_array = new vector<int>[number_of_nodes];
    subarray_divider = min_value + subarray_divider;

    for (i=0; i<array_size; i++)
    {
        ii = main_array[i]/subarray_divider;
        if (ii > number_of_nodes - 1)
            ii = number_of_nodes-2;

        vector_array[ii].push_back(main_array[i]);
    }
    delete[] main_array;
    main_array = 0;

    t3= MPI_Wtime();

    printf("**** Master node is sending sub arrays to remote nodes \n");

    for (i=0; i<number_of_nodes; i++)
    {
        MPI_Send(&vector_array[i].front(),vector_array[i].size(), MPI_INT,i+1,0,MPI_COMM_WORLD);//i+1, send to all processors except root

        //Deallocate the vector
        vector_array[i].clear();
    }

    t4= MPI_Wtime();

    MPI_Status status;
    int amount;

    //First, receive the total execution time at the remote nodes
    double highest_remote_execution_time;
    double execution_time;

    for (i=1; i <= number_of_nodes; i++)
    {
     MPI_Recv(&execution_time,1, MPI_DOUBLE,i,MPI_ANY_TAG,MPI_COMM_WORLD,&status);

     if (execution_time > highest_remote_execution_time)
     highest_remote_execution_time = execution_time;
    }

    t5 = MPI_Wtime();

    //Second, receive the sorted sub arrays
    for (i=1; i <= number_of_nodes; i++)
    {
        MPI_Probe(i,0,MPI_COMM_WORLD,&status);
        MPI_Get_count(&status,MPI_INT,&amount);

        int * receive_buffer = new int[amount];

        MPI_Recv(receive_buffer,amount, MPI_INT,i,MPI_ANY_TAG,MPI_COMM_WORLD,&status);

        delete [] receive_buffer;
        receive_buffer=0;
    }

    t6 = MPI_Wtime();

    printf("*****************************************************************************\n");
    printf("** 1  ** Data Generation required..........................: %f seconds \n",(t2-t1));
    printf("** 2  ** Data Division required............................: %f seconds \n",(t3-t2));
    printf("** 3  ** Data Preperation (Generation + Division) required.: %f seconds \n",(t3-t1));
    printf("** 4  ** Data Sending required.............................: %f seconds \n",(t4-t3));
    printf("** 5  ** Computation Time at remote nodes..................: %f seconds \n",highest_remote_execution_time);
    printf("** 6  ** Data Receiveing required..........................: %f seconds \n",(t6-t5));
    printf("** 7  ** (Sending + Sorting + Receiveing Back) required....: %f seconds \n",(t6-t3));
    printf("** 8  ** Total execution time..............................: %f seconds \n",(t6-t1));
    printf("*****************************************************************************\n");
}

void RunAsSlave(int rank)
{
    int amount;
    MPI_Status status;
    double start_time, end_time, execution_time;

    MPI_Probe(0,0,MPI_COMM_WORLD,&status);
    MPI_Get_count(&status,MPI_INT,&amount);

    int * receive_buffer = new int[amount];//receive vector<int> as integer array
    MPI_Recv(receive_buffer,amount, MPI_INT,0,MPI_ANY_TAG,MPI_COMM_WORLD,&status);
//  PrintArray(receive_buffer, amount);

    start_time = MPI_Wtime();
    QuickSortArray(receive_buffer,0,amount-1);
    end_time = MPI_Wtime();

    execution_time =  end_time - start_time;

    //First, send total execution time at the remote node, then send the result
    MPI_Send(&execution_time,1, MPI_DOUBLE,0,0,MPI_COMM_WORLD); //to get cpmutation time
    MPI_Send(receive_buffer,amount, MPI_INT,0,0,MPI_COMM_WORLD);
}

void RunSequentialSort(int array_size)
{
    printf("** 1 ** Sequential sorting started \n");

    t1= MPI_Wtime();

    int * main_array = GenerateRandomArray(array_size);

    t2= MPI_Wtime();
    printf("** 2 ** Array generated in : %f seconds \n", (t2-t1));
;
    QuickSortArray(main_array,0,array_size-1);
    t3= MPI_Wtime();

    printf("** 3 ** Sorting time : %f seconds \n",t3-t2);
    printf("** 4 ** Total time : %f seconds \n",t3-t1);

}

int QuickSortArray(int * array ,int left, int right)
{
    int i = left;
    int j = right;
    int tmp;

    int pivot = (array[left] + array[right])/2;

    while (i<=j)
    {
        while (array[i]<pivot)
            i++;

        while (array[j]> pivot)
            j--;

        if (i<=j)
        {
            tmp = array[i];
            array[i]= array[j];
            array[j]= tmp;
            i++;
            j--;
        }
    }

    if (left < j)
        QuickSortArray(array,left,j);

    if (right> i)
        QuickSortArray(array,i,right);

    return 0;
}

void PrintArray (int * array,int size)
{
    if (size > 1000)
        cout << "To big to print... \n";
    else
    {
        for (int i=0; i < size; i++)
        {
            printf ("%d ",i);
        }
        printf("\n");
    }
}

int * GenerateRandomArray(int size)
{
    //initiate main array with random numbers
    int * new_array = new int[size];
    for (int i=0; i< size; i++)
        new_array[i] = (rand() / double(RAND_MAX)) *  size;

    return new_array;
}

