package org.oikarinen.iwantdemo.wsdefdef;

import org.fluentjava.iwant.api.javamodules.JavaBinModule;
import org.fluentjava.iwant.api.javamodules.JavaCompliance;
import org.fluentjava.iwant.api.javamodules.JavaModule;
import org.fluentjava.iwant.api.javamodules.JavaSrcModule;
import org.fluentjava.iwant.api.javamodules.JavaSrcModule.IwantSrcModuleSpex;
import org.fluentjava.iwant.api.wsdef.WorkspaceModuleContext;
import org.fluentjava.iwant.api.wsdef.WorkspaceModuleProvider;
import org.fluentjava.iwant.core.javamodules.JavaModules;

public class IwantDemoWorkspaceModuleProvider
		implements WorkspaceModuleProvider {

	public static final String MODULE_NAME_PREFIX = "iwant-demo-";

	private static final String HAMCREST_VER = "1.3";

	/**
	 * The minimum of hamcrest to make junit work (a runtime dependency of
	 * junit)
	 */
	public static final JavaModule hamcrestCore = binModule("org.hamcrest",
			"hamcrest-core", HAMCREST_VER);

	public static final JavaModule junit = binModule("junit", "junit", "4.12",
			hamcrestCore);

	public static final JavaSrcModule javabeanGenerator = srcModuleAvailableAtBuildTime(
			"javabean-generator").testJava("src/test/java").mainDeps()
					.testDeps(junit).testRuntimeDeps().end();

	@Override
	public JavaSrcModule workspaceModule(WorkspaceModuleContext ctx) {
		return JavaSrcModule.with().name("iwant-demo-wsdef")
				.locationUnderWsRoot("as-iwant-demo-developer/i-have/wsdef")
				.mainJava("src/main/java").mainDeps(ctx.iwantApiModules())
				.mainDeps(ctx.wsdefdefModule(), javabeanGenerator)
				.mainDeps(ctx.iwantPlugin().findbugs().withDependencies())
				.mainDeps(ctx.iwantPlugin().github().withDependencies())
				.mainDeps(ctx.iwantPlugin().jacoco().withDependencies()).end();
	}

	@Override
	public String workspaceFactoryClassname() {
		return "org.oikarinen.iwantdemo.wsdef.IwantDemoWorkspaceFactory";
	}

	private static IwantSrcModuleSpex srcModuleAvailableAtBuildTime(
			String name) {
		return JavaSrcModule.with().name(MODULE_NAME_PREFIX + name)
				.mavenLayout().noMainResources().noTestJava().noTestResources()
				.javaCompliance(JavaCompliance.JAVA_1_8);
	}

	private static JavaBinModule binModule(String group, String name,
			String version, JavaModule... runtimeDeps) {
		return JavaModules.binModule(group, name, version, runtimeDeps);
	}

}
