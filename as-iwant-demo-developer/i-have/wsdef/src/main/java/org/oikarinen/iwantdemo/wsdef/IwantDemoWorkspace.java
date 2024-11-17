package org.oikarinen.iwantdemo.wsdef;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

import org.fluentjava.iwant.api.javamodules.JavaSrcModule;
import org.fluentjava.iwant.api.model.Path;
import org.fluentjava.iwant.api.model.SideEffect;
import org.fluentjava.iwant.api.model.Source;
import org.fluentjava.iwant.api.model.Target;
import org.fluentjava.iwant.api.wsdef.SideEffectDefinitionContext;
import org.fluentjava.iwant.api.wsdef.TargetDefinitionContext;
import org.fluentjava.iwant.api.wsdef.Workspace;
import org.fluentjava.iwant.api.wsdef.WorkspaceContext;
import org.fluentjava.iwant.core.download.TestedIwantDependencies;
import org.fluentjava.iwant.core.javafinder.WsdefJavaOf;
import org.fluentjava.iwant.eclipsesettings.EclipseSettings;
import org.fluentjava.iwant.plugin.findbugs.FindbugsDistribution;
import org.fluentjava.iwant.plugin.findbugs.FindbugsOutputFormat;
import org.fluentjava.iwant.plugin.findbugs.FindbugsReport;
import org.fluentjava.iwant.plugin.jacoco.JacocoDistribution;
import org.fluentjava.iwant.plugin.jacoco.JacocoTargetsOfJavaModules;

public class IwantDemoWorkspace implements Workspace {

	private final WsdefJavaOf wsdefJavaOf;
	private final IwantDemoModules modules;
	private final Path mainLog4jProperties = Source.underWsroot(
			"common-resources/main-log4j-properties/log4j.properties");
	private final FindbugsDistribution findbugs = FindbugsDistribution._4_8_3;

	public IwantDemoWorkspace(WorkspaceContext ctx) {
		this.wsdefJavaOf = new WsdefJavaOf(ctx);
		this.modules = new IwantDemoModules(wsdefJavaOf, ctx);
	}

	@Override
	public List<? extends Target> targets(TargetDefinitionContext ctx) {
		List<Target> t = new ArrayList<>();
		t.add(new CliDistro("cli-distro", modules.cli, mainLog4jProperties,
				wsdefJavaOf));
		t.add(findbugsReport());
		t.add(jacocoReportAll());
		t.add(AnteruBuildSystemsExample.distro());
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

	private static Target jacocoReport(String name,
			SortedSet<JavaSrcModule> interestingModules) {
		return JacocoTargetsOfJavaModules.with().jacoco(jacoco())
				.antJars(TestedIwantDependencies.antJar(),
						TestedIwantDependencies.antLauncherJar())
				.modules(interestingModules).end().jacocoReport(name);

	}

	private static JacocoDistribution jacoco() {
		return JacocoDistribution.newestTestedVersion();
	}

	private Target findbugsReport() {
		return findbugsReport("findbugs-report", modules.allSrcModules(),
				FindbugsOutputFormat.HTML);

	}

	private FindbugsReport findbugsReport(String name,
			Collection<JavaSrcModule> modules,
			FindbugsOutputFormat outputFormat) {
		return FindbugsReport.with().name(name).outputFormat(outputFormat)
				.using(findbugs, TestedIwantDependencies.antJar(),
						TestedIwantDependencies.antLauncherJar())
				.modulesToAnalyze(modules).end();
	}

}
