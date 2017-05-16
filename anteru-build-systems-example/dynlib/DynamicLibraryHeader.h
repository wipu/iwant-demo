#ifndef DYNAMIC_LIBRARY_HEADER_H
#define DYNAMIC_LIBRARY_HEADER_H

#if BUILD_DYNAMIC_LIBRARY
#ifdef  _MSC_VER
#define DYNAMIC_LIBRARY_EXPORT __declspec(dllexport)
#else
#define DYNAMIC_LIBRARY_EXPORT __attribute__ ((visibility ("default")))
#endif
#else
#define DYNAMIC_LIBRARY_EXPORT
#endif

namespace BuildTest {
DYNAMIC_LIBRARY_EXPORT int DynamicFunction (int a, int b, int c);
}

#endif
