package org.oikarinen.iwantdemo.wsdef;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;

import net.sf.iwant.api.javamodules.JavaSrcModule;
import net.sf.iwant.api.model.Path;
import net.sf.iwant.api.model.TargetEvaluationContext;
import net.sf.iwant.api.target.TargetBase;
import net.sf.iwant.core.javamodules.JavaModules;

public class CliDistro extends TargetBase {

	private static final String MAIN_CLASS_NAME = "org.oikarinen.iwantdemo.cli.IwantDemoCli";
	private final List<Path> runtimeJars;
	private final Path log4jProperties;

	public CliDistro(String name, JavaSrcModule mainModule,
			Path log4jProperties) {
		super(name);
		this.log4jProperties = log4jProperties;
		this.runtimeJars = JavaModules
				.mainArtifactJarsOf(JavaModules.runtimeDepsOf(mainModule));
	}

	@Override
	protected IngredientsAndParametersDefined ingredientsAndParameters(
			IngredientsAndParametersPlease iUse) {
		return iUse.parameter("MAIN_CLASS_NAME", MAIN_CLASS_NAME)
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
