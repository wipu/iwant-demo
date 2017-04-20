package org.oikarinen.iwantdemo.wsdef;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;

import net.sf.iwant.api.javamodules.JavaSrcModule;
import net.sf.iwant.api.model.Path;
import net.sf.iwant.api.model.SideEffect;
import net.sf.iwant.api.model.Source;
import net.sf.iwant.api.model.Target;
import net.sf.iwant.api.wsdef.SideEffectDefinitionContext;
import net.sf.iwant.api.wsdef.TargetDefinitionContext;
import net.sf.iwant.api.wsdef.Workspace;
import net.sf.iwant.core.download.TestedIwantDependencies;
import net.sf.iwant.eclipsesettings.EclipseSettings;
import net.sf.iwant.plugin.jacoco.JacocoDistribution;
import net.sf.iwant.plugin.jacoco.JacocoTargetsOfJavaModules;

public class IwantDemoWorkspace implements Workspace {

	private final IwantDemoModules modules = new IwantDemoModules();

	private final Path mainLog4jProperties = Source.underWsroot(
			"common-resources/main-log4j-properties/log4j.properties");

	@Override
	public List<? extends Target> targets(TargetDefinitionContext ctx) {
		List<Target> t = new ArrayList<>();
		t.add(new CliDistro("cli-distro", modules.cli, mainLog4jProperties));
		t.add(jacocoReportAll());
		return t;
	}

	@Override
	public List<? extends SideEffect> sideEffects(
			SideEffectDefinitionContext ctx) {
		return Arrays.asList(EclipseSettings.with().name("eclipse-settings")
				.modules(ctx.wsdefdefJavaModule(), ctx.wsdefJavaModule())
				.modules(modules.allSrcModules()).end());
	}

	private Target jacocoReportAll() {
		return jacocoReport("jacoco-report-all", modules.allSrcModules());
	}

	private Target jacocoReport(String name,
			SortedSet<JavaSrcModule> interestingModules) {
		return JacocoTargetsOfJavaModules.with()
				.jacocoWithDeps(jacoco(), modules.asmAll.mainArtifact())
				.antJars(TestedIwantDependencies.antJar(),
						TestedIwantDependencies.antLauncherJar())
				.modules(interestingModules).end().jacocoReport(name);

	}

	private static JacocoDistribution jacoco() {
		return JacocoDistribution.newestTestedVersion();
	}

}
