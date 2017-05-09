package org.oikarinen.iwantdemo.wsdefdef;

import net.sf.iwant.api.javamodules.JavaBinModule;
import net.sf.iwant.api.javamodules.JavaCompliance;
import net.sf.iwant.api.javamodules.JavaModule;
import net.sf.iwant.api.javamodules.JavaSrcModule;
import net.sf.iwant.api.javamodules.JavaSrcModule.IwantSrcModuleSpex;
import net.sf.iwant.api.model.Path;
import net.sf.iwant.api.wsdef.WorkspaceModuleContext;
import net.sf.iwant.api.wsdef.WorkspaceModuleProvider;
import net.sf.iwant.core.download.FromRepository;

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
		Path jar = FromRepository.repo1MavenOrg().group(group).name(name)
				.version(version);
		return binModule(jar, runtimeDeps);
	}

	private static JavaBinModule binModule(Path mainArtifact,
			JavaModule... runtimeDeps) {
		return JavaBinModule.providing(mainArtifact).runtimeDeps(runtimeDeps)
				.end();
	}

}
