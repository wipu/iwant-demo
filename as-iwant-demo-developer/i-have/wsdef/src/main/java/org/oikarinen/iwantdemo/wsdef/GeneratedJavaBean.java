package org.oikarinen.iwantdemo.wsdef;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.oikarinen.iwantdemo.javabeangenerator.JavaBeanGenerator;

import org.fluentjava.iwant.api.model.Source;
import org.fluentjava.iwant.api.model.TargetEvaluationContext;
import org.fluentjava.iwant.api.target.TargetBase;
import org.fluentjava.iwant.core.javafinder.PathToClasspathLocationOf;
import org.fluentjava.iwant.core.javafinder.WsdefJavaOf;

public class GeneratedJavaBean extends TargetBase {

	private final Source source;
	private final Source me;

	public GeneratedJavaBean(String name, Source source,
			WsdefJavaOf wsdefJavaOf) {
		super(name);
		this.source = source;
		this.me = wsdefJavaOf.classUnderSrcMainJava(getClass());
	}

	@Override
	protected IngredientsAndParametersDefined ingredientsAndParameters(
			IngredientsAndParametersPlease iUse) {
		return iUse.ingredients("me", me)
				.ingredients("JavabeanGenerator.class",
						PathToClasspathLocationOf
								.class_(JavaBeanGenerator.class))
				.ingredients("source", source).nothingElse();
	}

	@Override
	public void path(TargetEvaluationContext ctx) throws Exception {
		File dest = ctx.cached(this);
		System.err.println("Generating " + dest);

		String sourceContent = FileUtils.readFileToString(ctx.cached(source));
		JavaBeanGenerator gen = JavaBeanGenerator.fromSource(sourceContent);

		File dir = new File(dest, gen.parentDirPath());
		File file = new File(dir, gen.basename());
		System.err.println("  " + file);
		FileUtils.forceMkdir(dir);
		FileUtils.writeStringToFile(file, gen.content());
	}

}
