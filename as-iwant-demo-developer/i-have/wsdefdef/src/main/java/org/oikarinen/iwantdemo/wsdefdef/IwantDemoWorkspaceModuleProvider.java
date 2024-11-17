package org.oikarinen.iwantdemo.wsdefdef;

import java.util.Set;

import org.fluentjava.iwant.api.javamodules.JavaCompliance;
import org.fluentjava.iwant.api.javamodules.JavaModule;
import org.fluentjava.iwant.api.javamodules.JavaSrcModule;
import org.fluentjava.iwant.api.javamodules.JavaSrcModule.IwantSrcModuleSpex;
import org.fluentjava.iwant.api.wsdef.WorkspaceModuleContext;
import org.fluentjava.iwant.api.wsdef.WorkspaceModuleProvider;

public class IwantDemoWorkspaceModuleProvider
		implements WorkspaceModuleProvider {

	public static final String MODULE_NAME_PREFIX = "iwant-demo-";

	public static final JavaSrcModule javabeanGenerator(
			Set<JavaModule> junit5runnerMods) {
		return srcModuleAvailableAtBuildTime("javabean-generator")
				.testJava("src/test/java").mainDeps().testDeps(junit5runnerMods)
				.testRuntimeDeps().end();
	}

	@Override
	public JavaSrcModule workspaceModule(WorkspaceModuleContext ctx) {
		Set<JavaModule> junit5runnerMods = ctx.iwantPlugin().junit5runner()
				.withDependencies();
		return JavaSrcModule.with().name("iwant-demo-wsdef")
				.locationUnderWsRoot("as-iwant-demo-developer/i-have/wsdef")
				.mainJava("src/main/java").mainDeps(ctx.iwantApiModules())
				.mainDeps(ctx.wsdefdefModule(),
						javabeanGenerator(junit5runnerMods))
				.mainDeps(ctx.iwantPlugin().findbugs().withDependencies())
				.mainDeps(ctx.iwantPlugin().github().withDependencies())
				.mainDeps(ctx.iwantPlugin().jacoco().withDependencies())
				.mainDeps(ctx.iwantPlugin().junit5runner().withDependencies())
				.end();
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

}
