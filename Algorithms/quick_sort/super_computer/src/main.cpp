#include "functions.h"

using namespace std;

int rank,number_of_nodes;

int main(int argc, char **argv )
{
    MPI_Init(&argc, &argv);
    MPI_Comm_rank (MPI_COMM_WORLD, &rank);
    MPI_Comm_size (MPI_COMM_WORLD, &number_of_nodes);


    if(argc < 2)
    {
        if (rank==0)
        {
            cout << "Please enter number of nodes and array size..." << endl;
            cout << "To run execution sequentially on master node, enter processor number = 1" << endl;
        }
        MPI_Finalize();
        return 0;
    }


if (rank==0)
{
    int array_size = atoi(argv[1]);

    if(number_of_nodes < 3) //Number of entered processors < 3, then run sequentially
        RunSequentialSort(array_size);
    else
        RunAsMaster(array_size,number_of_nodes-1); //We want to skip node 0, keep it as a master controller
}
else
    RunAsSlave(rank);


MPI_Finalize();
return 0;
}
