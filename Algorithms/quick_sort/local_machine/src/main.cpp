#include <stdio.h>      /* printf, scanf, puts, NULL */
#include "..\include\Tester.h"
using namespace std;

int Tester::ArraySize = 50000000;
int * Tester::MainArray = new int[Tester::ArraySize];

int main()
{
    Tester tester;
    tester.StartTesting();

    return 0;
}
