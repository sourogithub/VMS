#include "training.h"
int main()
{
 Training *t = new Training();
 t->generate_database();
 delete t;
 t = NULL;
 return 0;
}
