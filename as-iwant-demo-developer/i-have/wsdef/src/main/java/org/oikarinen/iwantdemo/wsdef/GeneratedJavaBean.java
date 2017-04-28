package org.oikarinen.iwantdemo.wsdef;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.oikarinen.iwantdemo.javabeangenerator.JavabeanGenerator;

import net.sf.iwant.api.core.TargetBase;
import net.sf.iwant.api.model.Source;
import net.sf.iwant.api.model.TargetEvaluationContext;

public class GeneratedJavaBean extends TargetBase {

	private Source source;

	public GeneratedJavaBean(String name, Source source) {
		super(name);
		this.source = source;
	}

	@Override
	protected IngredientsAndParametersDefined ingredientsAndParameters(
			IngredientsAndParametersPlease iUse) {
		return iUse.ingredients("source", source).nothingElse();
	}

	@Override
	public void path(TargetEvaluationContext ctx) throws Exception {
		File dest = ctx.cached(this);
		System.err.println("Generating " + dest);

		String sourceContent = FileUtils.readFileToString(ctx.cached(source));
		JavabeanGenerator gen = JavabeanGenerator.fromSource(sourceContent);

		File dir = new File(dest, gen.parentDirPath());
		File file = new File(dir, gen.basename());
		System.err.println("  " + file);
		FileUtils.forceMkdir(dir);
		FileUtils.writeStringToFile(file, gen.content());
	}

}
