#include "QuickSort.h"


QuickSort::QuickSort()
{
NumberOfIterations = 0;
NumberOfRecursions = 0;
NumberOfSwaps = 0;
}


void QuickSort::RunQuickSort(int * _arr, int _left, int _right)
{
    int i = _left, j = _right;
    int tmp;
    int pivot = _arr[(_left + _right) / 2];

    ++NumberOfRecursions;

    while (i <= j)
    {
        while (_arr[i] < pivot)
        {
            ++NumberOfIterations;
            i++;
        }

        while (_arr[j] > pivot)
        {
            ++NumberOfIterations;
            j--;
        }
        if (i <= j)
        {
            ++NumberOfSwaps;
            tmp = _arr[i];
            _arr[i] = _arr[j];
            _arr[j] = tmp;
            i++;
            j--;
        }
    }

    if (_left < j)
        RunQuickSort(_arr, _left, j);
    if (i < _right)
        RunQuickSort(_arr, i, _right);
}

void QuickSort::RunQuickSortVector(std::vector<int> * _vector, int _left, int _right)
{
    try
    {
    int i = _left, j = _right;
    int tmp;
    int pivot = _vector->at((_left + _right) / 2);

    ++NumberOfRecursions;

    while (i <= j)
    {
        while (_vector->at(i) < pivot)
        {
            ++NumberOfIterations;
            i++;
        }

        while (_vector->at(j) > pivot)
        {
            ++NumberOfIterations;
            j--;
        }
        if (i <= j)
        {
            ++NumberOfSwaps;
            tmp = _vector->at(i);
            _vector->at(i) = _vector->at(j);
            _vector->at(j) = tmp;
            i++;
            j--;
        }
    }

    if (_left < j)
        RunQuickSortVector(_vector, _left, j);
    if (i < _right)
        RunQuickSortVector(_vector, i, _right);
    }

    catch (const std::out_of_range& oor)
    {
        printf("Error Occured  \n");
    }

    catch (std::runtime_error& oor)
    {
        printf("Error Occured  \n");
    }

    catch (...)
    {
        printf("Error Occured  \n");
    }
}

