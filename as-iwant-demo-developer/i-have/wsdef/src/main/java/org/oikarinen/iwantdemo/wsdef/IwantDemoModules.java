package org.oikarinen.iwantdemo.wsdef;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.oikarinen.iwantdemo.wsdefdef.IwantDemoWorkspaceModuleProvider;

import net.sf.iwant.api.javamodules.JavaBinModule;
import net.sf.iwant.api.javamodules.JavaClasses;
import net.sf.iwant.api.javamodules.JavaModule;
import net.sf.iwant.api.javamodules.JavaSrcModule;
import net.sf.iwant.api.javamodules.JavaSrcModule.IwantSrcModuleSpex;
import net.sf.iwant.api.model.Path;
import net.sf.iwant.api.model.Source;
import net.sf.iwant.api.zip.Jar;
import net.sf.iwant.core.javafinder.WsdefJavaOf;
import net.sf.iwant.core.javamodules.JavaModules;

public class IwantDemoModules extends JavaModules {

	// 3rd party binaries

	final JavaBinModule asmAll = binModule("org.ow2.asm", "asm-all", "5.0.1");

	private final JavaModule commonsCli = binModule("commons-cli",
			"commons-cli", "1.4");

	private final JavaModule commonsIo = binModule("commons-io", "commons-io",
			"2.5");

	private final JavaModule commonsMath = binModule("org.apache.commons",
			"commons-math3", "3.6.1");

	private static final String HAMCREST_VER = "1.3";

	/**
	 * The full hamcrest for modules that use Matchers
	 */
	private final JavaModule hamcrestAll = binModule("org.hamcrest",
			"hamcrest-all", HAMCREST_VER);

	private final JavaModule junit = IwantDemoWorkspaceModuleProvider.junit;

	private final JavaModule log4j = binModule("log4j", "log4j", "1.2.17");

	private static final String SLF4J_VER = "1.7.25";

	private final JavaModule slf4jApi = binModule("org.slf4j", "slf4j-api",
			SLF4J_VER);

	private final JavaModule slf4jLog4j12 = binModule("org.slf4j",
			"slf4j-log4j12", SLF4J_VER, log4j, slf4jApi);

	// custom binaries

	private final JavaModule testLog4jProperties = JavaBinModule
			.providing(Source
					.underWsroot("common-resources/test-log4j-properties"))
			.end();

	// src modules

	private final JavaSrcModule javabeanGenerator;
	final JavaSrcModule cli;

	IwantDemoModules(WsdefJavaOf wsdefJavaOf) {
		Path generatedJavabeansJava = new GeneratedJavaBean(
				"generatedJavabeansJava.java",
				Source.underWsroot("generated-javabeans/beans.txt"),
				wsdefJavaOf);
		Path generatedJavabeansClasses = JavaClasses.with()
				.name("generatedJavabeansJava.classes")
				.srcDirs(generatedJavabeansJava).end();
		Path generatedJavabeansJar = Jar.with()
				.name("generatedJavabeansJava.jar")
				.classes(generatedJavabeansClasses).end();
		JavaModule generatedJavabeans = JavaBinModule
				.providing(generatedJavabeansJar, generatedJavabeansJava).end();
		JavaModule mathLib = iwantDemoModule("math-lib").noMainResources()
				.mainDeps(commonsMath, slf4jApi).testDeps(hamcrestAll, junit)
				.testRuntimeDeps(slf4jLog4j12).end();

		javabeanGenerator = buildTimeModule(
				IwantDemoWorkspaceModuleProvider.javabeanGenerator);

		cli = iwantDemoModule("cli")
				.mainDeps(commonsCli, generatedJavabeans, mathLib, slf4jApi)
				.mainRuntimeDeps(slf4jLog4j12).testDeps(commonsIo, junit).end();

	}

	// utils

	private IwantSrcModuleSpex iwantDemoModule(String name) {
		return srcModule(
				IwantDemoWorkspaceModuleProvider.MODULE_NAME_PREFIX + name);
	}

	/**
	 * Since buildTimeModules are defined by
	 * {@link IwantDemoWorkspaceModuleProvider}, they don't go through the
	 * srcModule method and thus don't get automatically added to allSrcModules.
	 * We handle that here.
	 */
	private JavaSrcModule buildTimeModule(JavaSrcModule module) {
		allSrcModules().add(module);
		return module;
	}

	/**
	 * The first test dep provides our own log4j properties so they will be used
	 * even if some other dependency contains them.
	 * 
	 * It's ok to define this even for modules that don't have tests.
	 */
	@Override
	protected IwantSrcModuleSpex commonSettings(IwantSrcModuleSpex m) {
		return super.commonSettings(m).testDeps(testLog4jProperties);
	}

	/**
	 * A side-job of this collection is to make sure all dead modules are
	 * detected: if a module is neither an entrypoint nor needed by another
	 * module, it will be shown as dead code by the compiler.
	 * 
	 * @return
	 */
	Set<JavaSrcModule> entrypointModules() {
		return new LinkedHashSet<>(Arrays.asList(cli, javabeanGenerator));
	}

}
