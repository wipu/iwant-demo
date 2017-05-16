#include "StaticLibraryHeader.h"

extern int LookupTable[];

namespace BuildTest {
int HelperFunction (int a, int b)
{
	if (a > 0 && a < 1024) {
		return LookupTable [a] + b;
	} else {
		return b;
	}
}
}