package org.oikarinen.iwantdemo.wsdef;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;

import org.fluentjava.iwant.api.javamodules.JavaSrcModule;
import org.fluentjava.iwant.api.model.Path;
import org.fluentjava.iwant.api.model.Source;
import org.fluentjava.iwant.api.model.TargetEvaluationContext;
import org.fluentjava.iwant.api.target.TargetBase;
import org.fluentjava.iwant.core.javafinder.WsdefJavaOf;
import org.fluentjava.iwant.core.javamodules.JavaModules;

public class CliDistro extends TargetBase {

	private static final String MAIN_CLASS_NAME = "org.oikarinen.iwantdemo.cli.IwantDemoCli";
	private final List<Path> runtimeJars;
	private final Path log4jProperties;
	private final Source me;

	public CliDistro(String name, JavaSrcModule mainModule,
			Path log4jProperties, WsdefJavaOf wsdefJavaOf) {
		super(name);
		this.log4jProperties = log4jProperties;
		this.runtimeJars = JavaModules
				.mainArtifactJarsOf(JavaModules.runtimeDepsOf(mainModule));
		this.me = wsdefJavaOf.classUnderSrcMainJava(getClass());
	}

	@Override
	protected IngredientsAndParametersDefined ingredientsAndParameters(
			IngredientsAndParametersPlease iUse) {
		return iUse.parameter("MAIN_CLASS_NAME", MAIN_CLASS_NAME)
				.ingredients("me", me)
				.ingredients("log4jProperties", log4jProperties)
				.ingredients("runtimeJars", runtimeJars).nothingElse();
	}

	@Override
	public void path(TargetEvaluationContext ctx) throws Exception {
		File dest = ctx.cached(this);

		File classes = new File(dest, "classes");
		classes.mkdirs();
		FileUtils.copyFileToDirectory(ctx.cached(log4jProperties), classes);

		File lib = new File(dest, "lib");
		lib.mkdirs();
		for (Path jar : runtimeJars) {
			FileUtils.copyFileToDirectory(ctx.cached(jar), lib);
		}

		StringBuilder sh = new StringBuilder();
		sh.append("#!/bin/bash\n");
		sh.append("set -eu\n");
		sh.append("HERE=$(dirname \"$0\")\n");
		sh.append("java -cp \"$HERE/classes:$HERE/lib/*\"");
		sh.append(" ").append(MAIN_CLASS_NAME).append(" \"$@\"\n");

		File shFile = new File(dest, "run.sh");
		FileUtils.writeStringToFile(shFile, sh.toString());
		shFile.setExecutable(true);
	}

}
