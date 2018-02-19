package org.highweb.webclsdk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.highweb.webclsdk.views.commons.EventEmitter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.highweb.webclsdk"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	public static Process nodejsProcess;
	public static Process androidLogcatProcess;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		Bundle bundle = context.getBundle();
		IPath bundleStatePath = Platform.getStateLocation(bundle);

		String clccPathx86 = "utils" + File.separator + "clcc" + File.separator + "nvidia" + File.separator
				+ "x86" + File.separator + "clcc.exe";
		File clccFilex86 = new File(bundleStatePath.toOSString(), clccPathx86);
		if(!clccFilex86.exists()) {
			new File(bundleStatePath.toOSString(),
					clccPathx86.substring(0, clccPathx86.indexOf(File.separator + "clcc.exe"))).mkdirs();
			Utils.copyFromPlugin(new Path("utils/clcc/nvidia/x86/clcc.exe"), clccFilex86);
		}

		String clccPathx86_64 = "utils" + File.separator + "clcc" + File.separator + "nvidia" + File.separator
				+ "x86_64" + File.separator + "clcc.exe";
		File clccFilex86_64 = new File(bundleStatePath.toOSString(), clccPathx86_64);
		if(!clccFilex86_64.exists()) {
			new File(bundleStatePath.toOSString(),
					clccPathx86_64.substring(0, clccPathx86_64.indexOf(File.separator + "clcc.exe"))).mkdirs();
			Utils.copyFromPlugin(new Path("utils/clcc/nvidia/x86_64/clcc.exe"), clccFilex86_64);
		}

		String cparserPath = "utils" + File.separator + "cparser" + File.separator + "cparser.exe";
		File cparserFile = new File(bundleStatePath.toOSString(), cparserPath);
		if(!cparserFile.exists()) {
			new File(bundleStatePath.toOSString(),
					cparserPath.substring(0, cparserPath.indexOf(File.separator + "cparser.exe"))).mkdirs();
			Utils.copyFromPlugin(new Path("utils/cparser/cparser.exe"), cparserFile);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		if(nodejsProcess != null && nodejsProcess.isAlive()) {
			nodejsProcess.destroy();
		}
		if(androidLogcatProcess != null && androidLogcatProcess.isAlive()) {
			androidLogcatProcess.destroy();
		}
		plugin = null;
		
		super.stop(context);
	}

	
	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

}
