package org.highweb.webclsdk.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.handlers.HandlerUtil;
import org.highweb.webclsdk.preferences.WebCLSDKPreferencePage;
import org.json.JSONException;
import org.json.JSONObject;

public class HighWebAppBuildHandler extends AbstractHandler {

	public HighWebAppBuildHandler() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		IWorkbenchPage activePage = window.getActivePage();
		ISelection selection = activePage.getSelection();
		if(selection != null && selection instanceof ITreeSelection) {
			TreeSelection treeSelection = (TreeSelection) selection;
			TreePath[] treePaths = treeSelection.getPaths();
			TreePath treePath = treePaths[0];

			Object firstSegmentObj = treePath.getFirstSegment();

			IProject project = (IProject) ((IAdaptable) firstSegmentObj).getAdapter(IProject.class);
			String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
			//File projectDir = new File(workspacePath + File.separator + project.getName());
			File projectDir = new File(project.getLocation().toString());
			
			String pythonPath = WebCLSDKPreferencePage.getPythonPath();
			if(pythonPath == null || pythonPath.isEmpty()) {
				MessageDialog.openWarning(window.getShell(), "HighWeb Warning - Python Path",
                        "Python 寃쎈줈媛� �꽭�똿�릺�뼱 �엳吏� �븡�뒿�땲�떎.\n"
                        + "Window - Preferences - HighWeb Tool �뿉�꽌 Python 寃쎈줈瑜� �꽭�똿�빐 二쇱꽭�슂");
				return null;
			}
			
			String antDirectory = WebCLSDKPreferencePage.getAntDirectory();
			if(antDirectory == null || antDirectory.isEmpty()) {
				MessageDialog.openWarning(window.getShell(), "HighWeb Warning - Ant Directory",
                        "Ant �뵒�젆�넗由ш� �꽭�똿�릺�뼱 �엳吏� �븡�뒿�땲�떎.\n"
                        + "Window - Preferences - HighWeb Tool �뿉�꽌 Ant �뵒�젆�넗由щ�� �꽭�똿�빐 二쇱꽭�슂");
				return null;
			}

			String androidSDKDirectory = WebCLSDKPreferencePage.getAndroidSDKDirectory();
			if(androidSDKDirectory == null || androidSDKDirectory.isEmpty()) {
				MessageDialog.openWarning(window.getShell(), "HighWeb Warning - Android SDK Directory",
                        "Android SDK �뵒�젆�넗由ш� �꽭�똿�릺�뼱 �엳吏� �븡�뒿�땲�떎.\n"
                        + "Window - Preferences - HighWeb Tool �뿉�꽌 Android SDK �뵒�젆�넗由щ�� �꽭�똿�빐 二쇱꽭�슂");
				return null;
			}

			String crosswalkDirectory = WebCLSDKPreferencePage.getCrosswalkDirectory();
			if(crosswalkDirectory == null || crosswalkDirectory.isEmpty()) {
				MessageDialog.openWarning(window.getShell(), "HighWeb Warning - Android SDK Directory",
                        "Android SDK �뵒�젆�넗由ш� �꽭�똿�릺�뼱 �엳吏� �븡�뒿�땲�떎.\n"
                        + "Window - Preferences - HighWeb Tool �뿉�꽌 Android SDK �뵒�젆�넗由щ�� �꽭�똿�빐 二쇱꽭�슂");
				return null;
			}

			final Job job = new Job("Build HighWeb Application") {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					// TODO Auto-generated method stub
					monitor.beginTask("Build HighWeb Application", 4);
					String path = System.getenv("path");
					String tempPath = new String(path);
					StringBuffer pathbuf = new StringBuffer(path);
					if(!path.endsWith(";")) {
						pathbuf.append(';');
					}
					pathbuf.append(antDirectory + File.separator + "bin")
						.append(';').append(androidSDKDirectory + File.separator + "tools")
						.append(';').append(androidSDKDirectory + File.separator + "platform-tools");
					path = pathbuf.toString();
					monitor.worked(1);

					String manifestPath = projectDir.getAbsolutePath() + File.separator + "WebContent" + File.separator + "manifest.json";
					FileReader manifestReader = null;
					try {
						manifestReader = new FileReader(new File(manifestPath));
						char[] cbuf = new char[32];
						StringBuffer sb = new StringBuffer();
						int i;
						while((i = manifestReader.read(cbuf)) != -1) {
							sb.append(cbuf, 0, i);
						}

						JSONObject manifestObj = new JSONObject(sb.toString());
						String packagename = (String) manifestObj.get("package");

						String[] args = new String[] {
								"cmd.exe",
								"/C",
								pythonPath,
								crosswalkDirectory + File.separator + "make_apk.py",
								"--package=" + packagename,
								"--manifest=WebContent/manifest.json"
						};
						MessageConsole console = new MessageConsole("HighWeb Application Build", null);
						ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] {console});
						ConsolePlugin.getDefault().getConsoleManager().showConsoleView(console);
						MessageConsoleStream stream = console.newMessageStream();
						ProcessBuilder pBuilder = new ProcessBuilder(args);
						Map<String,String> env = pBuilder.environment();
						env.put("PATH", path);
						while(true) {
							Thread.sleep(100);
							if(pBuilder.environment().get("PATH").contains(antDirectory + File.separator + "bin")
									&& pBuilder.environment().get("PATH").contains(androidSDKDirectory + File.separator + "tools")
									&& pBuilder.environment().get("PATH").contains(androidSDKDirectory + File.separator + "platform-tools")) {
								break;
							}
						}
						monitor.worked(1);

						monitor.subTask("Building Web app to Mobile Web app");
						pBuilder.redirectErrorStream(true);
						pBuilder.directory(projectDir);
						Process proc = pBuilder.start();
						BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
						String line = null;
						stream.println("Building " + packagename + "\n\n");
						while((line = in.readLine()) != null) {
							stream.println(line);
							if(monitor.isCanceled()) {
								stream.println("Building is cancelled.");
								return Status.CANCEL_STATUS;
							}
						}
						stream.println("\n\nBuild finished.");
						monitor.worked(1);

						project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
						monitor.worked(1);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						if(manifestReader != null) {
							try {
								manifestReader.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						monitor.done();
					}

					return Status.OK_STATUS;
				}
				
			};
			job.setUser(true);
			job.schedule();

		}
		return null;
	}

}
