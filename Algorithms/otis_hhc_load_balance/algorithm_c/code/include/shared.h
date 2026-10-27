#ifndef Shared_H
#define Shared_H

#include <stdio.h>      /* printf, scanf, puts, NULL */
#include <stdlib.h>     /* srand, rand */
#include <time.h>       /* time */
#include <pthread.h>
#include <assert.h>
#include <math.h>
#include <vector>

using namespace std;

int GetHamiltonDistance (int value);
void* StartHHCLBOne(void * args);
void* StartHHCLBTwo(void * args);

#endif
