#include "DynamicLibraryHeader.h"

#include <iostream>

int main ()
{
	std::cout << BuildTest::DynamicFunction (2, 5, 23) << std::endl;
}