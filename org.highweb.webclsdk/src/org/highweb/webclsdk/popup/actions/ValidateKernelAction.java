package org.highweb.webclsdk.popup.actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.ui.console.FileLink;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;
import org.highweb.webclsdk.Activator;
import org.osgi.framework.Bundle;

public class ValidateKernelAction implements IObjectActionDelegate {

	private Shell shell;
	private ISelection selection;
	
	/**
	 * Constructor for Action1.
	 */
	public ValidateKernelAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
//		MessageDialog.openInformation(
//			shell,
//			"WebCL SDK",
//			"Validate Kernel Action was executed.");
		if(selection != null && selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			Object segmentObj = ssel.getFirstElement();
			IFile selFile = (IFile) Platform.getAdapterManager().getAdapter(segmentObj, IFile.class);
			if(selFile == null && segmentObj instanceof IAdaptable) {
				selFile = (IFile) ((IAdaptable) segmentObj).getAdapter(IFile.class);
			}
			IProject project = selFile.getProject();

			if(project == null && segmentObj instanceof IAdaptable) {
				project = (IProject) ((IAdaptable) segmentObj).getAdapter(IProject.class);
			}

			if(selFile != null && project != null) {
				IPath workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation();
				IFolder tempFolder = project.getFolder("temp");
				if(tempFolder != null) {
					File projectDir = new File(workspacePath.toOSString(), project.getName());
					File tempDir = new File(projectDir, "temp");
					if(!tempDir.exists()) {
						tempDir.mkdirs();
						tempFolder = project.getFolder("temp");
						try {
							tempFolder.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
						} catch (CoreException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} else {
					File projectDir = new File(workspacePath.toOSString(), project.getName());
					File tempDir = new File(projectDir, "temp");
					if(!tempDir.exists()) {
						tempDir.mkdirs();
						tempFolder = project.getFolder("temp");
						try {
							tempFolder.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
						} catch (CoreException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				String tempDirName = tempFolder.getLocation().toOSString();
				String rnd = "";
				Random random = new Random();
				for(int i = 0; i < 16; i++) {
					rnd = rnd + random.nextInt(9);
				}
				Date date = new Date();
				DateFormat format = new SimpleDateFormat("yyyyMMdd");
				String tempFileName = format.format(date) + rnd + ".ptx";

				String outputPath = tempDirName + File.separator + tempFileName;
				String inputPath = selFile.getLocation().toOSString();
				String argument = "-cl-nv-cstd=CL1.1";

				Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
				IPath bundleStatePath = Platform.getStateLocation(bundle);
				String clccPath = bundleStatePath.toOSString() + File.separator 
						+ "utils" + File.separator + "clcc" + File.separator + "nvidia" + File.separator
						+ "x86_64" + File.separator + "clcc.exe";

				final String editorId = "org.highweb.webclsdk.editors.CLEditor";
				try {
					MessageConsole console = new MessageConsole("Kernel Code Validation", null);

					final IFile targetFile = selFile;
					IPatternMatchListener pMatchListener = new IPatternMatchListener() {

						private TextConsole console;
						@Override
						public void connect(TextConsole console) {
							// TODO Auto-generated method stub
							this.console = console;
						}

						@Override
						public void disconnect() {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void matchFound(PatternMatchEvent event) {
							// TODO Auto-generated method stub
							IDocument document = console.getDocument();
							try {
								String matchedString = document.get(event.getOffset(), event.getLength());
								String[] v = matchedString.split(":");
								int lineNumber = Integer.parseInt(v[1]);
								IHyperlink link = new FileLink(targetFile, editorId, -1, -1, lineNumber);
								console.addHyperlink(link, event.getOffset(), event.getLength());
							} catch (BadLocationException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						@Override
						public int getCompilerFlags() {
							// TODO Auto-generated method stub
							return 0;
						}

						@Override
						public String getLineQualifier() {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public String getPattern() {
							// TODO Auto-generated method stub
							return "<kernel>:\\d+:\\d+";
						}
						
					};
					console.addPatternMatchListener(pMatchListener);

					ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] {console});
					ConsolePlugin.getDefault().getConsoleManager().showConsoleView(console);
					MessageConsoleStream stream = console.newMessageStream();

					ProcessBuilder pbuilder = new ProcessBuilder(new String[]{clccPath, argument, inputPath, outputPath});
					pbuilder.redirectErrorStream(true);
					Process proc = pbuilder.start();
					BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
					String line = null;
					stream.println("Validating " + inputPath + "\n\n");
					int logOffset = 0;
					int errCount = 0;
					while((line = in.readLine()) != null) {
						if(line.startsWith("<kernel>")) {
							errCount++;
						}
						stream.println(line);
					}
					proc.waitFor();

					stream.println("Validation result :");
					stream.print(inputPath + " is ");
					if(errCount > 0) {
						stream.println("Invalid, error:" + errCount);
					} else {
						stream.println("Valid.\nValidation complete.");
					}
					tempFolder.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

}
