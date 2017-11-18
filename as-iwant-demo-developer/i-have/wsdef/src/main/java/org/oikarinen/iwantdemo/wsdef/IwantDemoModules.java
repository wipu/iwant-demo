package org.oikarinen.iwantdemo.wsdef;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.oikarinen.iwantdemo.wsdefdef.IwantDemoWorkspaceModuleProvider;

import org.fluentjava.iwant.api.core.SubPath;
import org.fluentjava.iwant.api.javamodules.JavaBinModule;
import org.fluentjava.iwant.api.javamodules.JavaClasses;
import org.fluentjava.iwant.api.javamodules.JavaModule;
import org.fluentjava.iwant.api.javamodules.JavaSrcModule;
import org.fluentjava.iwant.api.javamodules.JavaSrcModule.IwantSrcModuleSpex;
import org.fluentjava.iwant.api.model.Path;
import org.fluentjava.iwant.api.model.Source;
import org.fluentjava.iwant.api.zip.Jar;
import org.fluentjava.iwant.core.javafinder.WsdefJavaOf;
import org.fluentjava.iwant.core.javamodules.JavaModules;
import org.fluentjava.iwant.plugin.github.FromGithub;

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

	private final Path joulu = FromGithub.user("wipu").project("joulu")
			.commit("1e913b69b31b145cebf89f5e7821060ddecc8f38");

	private JavaBinModule jouluUnsignedByte() {
		Path java = new SubPath("joulu-unsigned-byte-java", joulu,
				"unsigned-byte/src/main/java");
		Path classes = JavaClasses.with().name("joulu-unsigned-byte-classes")
				.srcDirs(java).end();
		Path jar = Jar.with().name("joulu-unsigned-byte.jar").classes(classes)
				.end();

		return JavaBinModule.providing(jar, java).end();
	}

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
		Path generatedJavaBeansJava = new GeneratedJavaBean(
				"generatedJavaBeansJava.java",
				Source.underWsroot("generated-javabeans/beans.txt"),
				wsdefJavaOf);
		Path generatedJavaBeansClasses = JavaClasses.with()
				.name("generatedJavaBeansJava.classes")
				.srcDirs(generatedJavaBeansJava).end();
		Path generatedJavaBeansJar = Jar.with()
				.name("generatedJavaBeansJava.jar")
				.classes(generatedJavaBeansClasses).end();
		JavaModule generatedJavaBeans = JavaBinModule
				.providing(generatedJavaBeansJar, generatedJavaBeansJava).end();
		JavaModule mathLib = iwantDemoModule("math-lib").noMainResources()
				.mainDeps(commonsMath, slf4jApi).testDeps(hamcrestAll, junit)
				.testRuntimeDeps(slf4jLog4j12).end();
		JavaModule sloppyLegacy = iwantDemoModule("sloppy-legacy")
				.codeStyle(IwantDemoCodeStyles.LEGACY)
				.codeFormatter(IwantDemoCodeStyles.legacyEclipseFormatting())
				.noMainResources().noTestJava().noTestResources().mainDeps()
				.end();

		javabeanGenerator = buildTimeModule(
				IwantDemoWorkspaceModuleProvider.javabeanGenerator);

		cli = iwantDemoModule("cli")
				.mainDeps(commonsCli, generatedJavaBeans, jouluUnsignedByte(),
						mathLib, slf4jApi, sloppyLegacy)
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

	@Override
	protected IwantSrcModuleSpex commonSettings(IwantSrcModuleSpex m) {
		IwantSrcModuleSpex cs = super.commonSettings(m);

		/*
		 * The first test dep provides our own log4j properties so they will be
		 * used even if some other dependency contains them.
		 * 
		 * It's ok to define this even for modules that don't have tests.
		 */
		cs.testDeps(testLog4jProperties);

		/*
		 * This is optional: we have a slightly modified code style in this
		 * project.
		 */
		cs.codeStyle(IwantDemoCodeStyles.COMMON);

		return cs;
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
