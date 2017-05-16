package org.oikarinen.iwantdemo.wsdef;

import net.sf.iwant.api.core.Concatenated;
import net.sf.iwant.api.core.Directory;
import net.sf.iwant.api.core.Directory.DirectoryContentPlease;
import net.sf.iwant.api.core.ScriptGenerated;
import net.sf.iwant.api.model.Path;
import net.sf.iwant.api.model.Source;
import net.sf.iwant.api.model.Target;

/**
 * This example is copied from https://bitbucket.org/Anteru/build-systems that
 * hosts a simple C++ project with python-generated sources with several build
 * solutions. Here we are demonstrating how to build it with iwant.
 */
class AnteruBuildSystemsExample {

	private static final String CXX = "g++";
	private static final String NAME = "anteru-build-systems-example";
	private static final Path STATLIB = Source.underWsroot(NAME + "/statlib");
	private static final String STATLIB_NAME = "StaticLibrary";
	private static final Path DYNLIB = Source.underWsroot(NAME + "/dynlib");
	private static final String DYNLIB_NAME = "DynamicLibrary";

	static Target distro() {
		Path statlibO = statlibO();
		Path tableO = tableO();
		Path statlibA = statlibA(statlibO, tableO);
		Path dynlibO = dynlibO();
		Path statlibDir = libDir(STATLIB_NAME, "a", statlibA);
		Path dynlibSo = dynlibSo(dynlibO, statlibDir);
		Path executableO = executableO();
		Path dynlibDir = libDir(DYNLIB_NAME, "so", dynlibSo);
		Path executableExe = executableExe(executableO, dynlibDir);

		Path runSh = Concatenated.named(NAME + "-run.sh").string("#/bin/bash\n")
				.string("HERE=$(dirname \"$0\")\n")
				.string("export LD_LIBRARY_PATH=$(cd \"$HERE\" && pwd)\n")
				.string("$HERE/executable").end();

		DirectoryContentPlease<Directory> distro = Directory.named(NAME);
		distro.copyOf(runSh).named("run.sh").executable(true);
		distro.copyOf(executableExe).named("executable");
		distro.copyOf(dynlibSo).named("libDynamicLibrary.so");
		return distro.end();
	}

	private static Path statlibO() {
		Path cpp = Source.underWsroot(STATLIB + "/StaticLibrarySource.cpp");
		return o(NAME + "-statlib", cpp);
	}

	private static Path tableO() {
		Path tablegenPy = Source.underWsroot(STATLIB + "/tablegen.py");
		Path cppSh = Concatenated.named(NAME + "-table.cpp.sh")
				.string("#!/bin/bash -x\n").string("python3 '")
				.unixPathTo(tablegenPy).string("' > \"$1\"\n").end();
		Path cpp = ScriptGenerated.named(NAME + "-table.cpp").byScript(cppSh);
		return o(NAME + "-table", cpp);
	}

	private static Path statlibA(Path statlibO, Path tableO) {
		Path sh = Concatenated.named(NAME + "-statlib.a.sh")
				.string("#!/bin/bash -x\n").string("ar rcs \"$1\" '")
				.unixPathTo(statlibO).string("' '").unixPathTo(tableO)
				.string("'\n").end();
		return ScriptGenerated.named(NAME + "-statlib.a").byScript(sh);
	}

	private static Path dynlibO() {
		Path cpp = Source.underWsroot(DYNLIB + "/DynamicLibrarySource.cpp");
		Path sh = Concatenated.named(NAME + "-dynlib.o.sh")
				.string("#!/bin/bash -x\n").string(CXX + " -I'")
				.unixPathTo(STATLIB)
				.string("' -DDBUILD_DYNAMIC_LIBRARY=1 -fPIC  -c -o \"$1\" '")
				.unixPathTo(cpp).string("'\n").end();
		return ScriptGenerated.named(NAME + "-dynlib.o").byScript(sh);
	}

	private static Path dynlibSo(Path dynlibO, Path statlibDir) {
		Path sh = Concatenated.named(NAME + "-dynlib.so.sh")
				.string("#!/bin/bash -x\n").string(CXX + " '")
				.unixPathTo(dynlibO).string("' -shared -L'")
				.unixPathTo(statlibDir)
				.string("' -l" + STATLIB_NAME + " -o \"$1\"\n").end();
		return ScriptGenerated.named(NAME + "-dynlib.so").byScript(sh);
	}

	/**
	 * gcc assumes certain conventions when finding libraries so it is most
	 * convenient to distribute each library as a separate directory that
	 * contains the library named by convention so the directory can be passed
	 * as a value to the "-L" option.
	 */
	private static Path libDir(String name, String extension, Path lib) {
		return Directory.named(NAME + "-libdir-" + name).copyOf(lib)
				.named("lib" + name + "." + extension).end().end();
	}

	private static Path executableO() {
		Path cpp = Source
				.underWsroot(NAME + "/executable/ExecutableSource.cpp");
		Path sh = Concatenated.named(NAME + "-executable.o.sh")
				.string("#!/bin/bash -x\n").string(CXX + " -I'")
				.unixPathTo(DYNLIB).string("' -c -o \"$1\" '").unixPathTo(cpp)
				.string("'\n").end();
		return ScriptGenerated.named(NAME + "-executable.o").byScript(sh);
	}

	private static Path executableExe(Path executableO, Path dynlibDir) {
		Path sh = Concatenated.named(NAME + "-executable.sh")
				.string("#!/bin/bash -x\n").string(CXX + " '")
				.unixPathTo(executableO).string("' -L'").unixPathTo(dynlibDir)
				.string("' -l" + DYNLIB_NAME + " -o \"$1\"\n").end();
		return ScriptGenerated.named(NAME + "-executable").byScript(sh);
	}

	private static Path o(String name, Path cpp) {
		Path sh = Concatenated.named(name + ".o.sh").string("#!/bin/bash -x\n")
				.string(CXX + " -fPIC -c -o \"$1\" '").unixPathTo(cpp)
				.string("'\n").end();
		return ScriptGenerated.named(name + ".o").byScript(sh);
	}

}
