package org.highweb.webclsdk.delegate;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.highweb.webclsdk.Activator;
import org.highweb.webclsdk.Utils;
import org.osgi.framework.Bundle;

public class CLSampleKernelInstallDelegate implements IDelegate {

	@Override
	public void execute(IProject pj, IProjectFacetVersion fv, Object config, IProgressMonitor monitor)
			throws CoreException {
		// TODO Auto-generated method stub
		monitor.beginTask("", 1);

		try {
			IFolder webInfAsset = Utils.getFolder(pj, "asset");
			if(!webInfAsset.exists()) {
				webInfAsset.create(IResource.NONE, true, monitor);
			}
			Utils.copyFromPlugin(new Path("asset/new_file.cl"),
					webInfAsset.getFile("new_file.cl"));

			IFolder webInfRoot = Utils.getFolder(pj, null);
			Utils.copyFromPlugin(new Path("asset/index.jsp"),
					webInfRoot.getFile("index.jsp"));
			monitor.worked(1);
		} finally {
			monitor.done();
		}
	}
}
