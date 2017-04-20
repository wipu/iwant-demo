package org.oikarinen.iwantdemo.wsdefdef;

import net.sf.iwant.api.javamodules.JavaSrcModule;
import net.sf.iwant.api.wsdef.WorkspaceModuleContext;
import net.sf.iwant.api.wsdef.WorkspaceModuleProvider;

public class IwantDemoWorkspaceModuleProvider
		implements WorkspaceModuleProvider {

	@Override
	public JavaSrcModule workspaceModule(WorkspaceModuleContext ctx) {
		return JavaSrcModule.with().name("iwant-demo-wsdef")
				.locationUnderWsRoot("as-iwant-demo-developer/i-have/wsdef")
				.mainJava("src/main/java").mainDeps(ctx.iwantApiModules())
				.mainDeps(ctx.wsdefdefModule())
				.mainDeps(ctx.iwantPlugin().findbugs().withDependencies())
				.mainDeps(ctx.iwantPlugin().jacoco().withDependencies())
				.mainDeps(ctx.iwantPlugin().javamodules().withDependencies())
				.end();
	}

	@Override
	public String workspaceFactoryClassname() {
		return "org.oikarinen.iwantdemo.wsdef.IwantDemoWorkspaceFactory";
	}

}
