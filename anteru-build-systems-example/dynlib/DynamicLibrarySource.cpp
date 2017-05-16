#include "DynamicLibraryHeader.h"

#include "StaticLibraryHeader.h"

namespace BuildTest {
int DynamicFunction (int a, int b, int c)
{
	return a * HelperFunction (b, c);
}
}