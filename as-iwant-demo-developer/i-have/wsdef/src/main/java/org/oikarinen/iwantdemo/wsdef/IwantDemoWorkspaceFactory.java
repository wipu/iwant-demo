package org.oikarinen.iwantdemo.wsdef;

import net.sf.iwant.api.wsdef.Workspace;
import net.sf.iwant.api.wsdef.WorkspaceContext;
import net.sf.iwant.api.wsdef.WorkspaceFactory;

public class IwantDemoWorkspaceFactory implements WorkspaceFactory {

	@Override
	public Workspace workspace(WorkspaceContext ctx) {
		return new IwantDemoWorkspace();
	}

}
