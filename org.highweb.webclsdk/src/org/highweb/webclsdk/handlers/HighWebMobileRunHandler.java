package org.highweb.webclsdk.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.handlers.HandlerUtil;
import org.highweb.webclsdk.preferences.WebCLSDKPreferencePage;
import org.highweb.webclsdk.views.dialog.DeviceSelectDialog;
import org.json.JSONException;
import org.json.JSONObject;

public class HighWebMobileRunHandler extends AbstractHandler {
	
	private String id;
	
	public HighWebMobileRunHandler() {
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


			String androidSDKDirectory = WebCLSDKPreferencePage.getAndroidSDKDirectory();
			if(androidSDKDirectory == null || androidSDKDirectory.isEmpty()) {
				MessageDialog.openWarning(window.getShell(), "HighWeb Warning - Android SDK Directory",
                        "Android SDK �뵒�젆�넗由ш� �꽭�똿�릺�뼱 �엳吏� �븡�뒿�땲�떎.\n"
                        + "Window - Preferences - HighWeb Tool �뿉�꽌 Android SDK �뵒�젆�넗由щ�� �꽭�똿�빐 二쇱꽭�슂");
				return null;
			}
	        
	        String[] argsFroUSBMode = new String[] {
	        		androidSDKDirectory + File.separator + "platform-tools" + File.separator + "adb.exe", "usb"
		        };
		        
	        String[] argsForDeviceId = new String[] {
	        		androidSDKDirectory + File.separator + "platform-tools" + File.separator + "adb.exe", "devices"
	        };
		        
	        BufferedReader in = null;
	        ProcessBuilder pBuilder = null;
	        Process process = null;
	        try {
	        	//USB MODE
	        	pBuilder = new ProcessBuilder(argsFroUSBMode);
	            pBuilder.redirectErrorStream(true);
	            process = pBuilder.start();
	            process.destroy();
	            
	            //Connection
	            pBuilder = new ProcessBuilder(argsForDeviceId);
	            pBuilder.redirectErrorStream(true);
	            process = pBuilder.start();
	            in = new BufferedReader(new InputStreamReader(process.getInputStream()));
	            String str = in.readLine();
	            List<String> ids = new ArrayList<>();
	            while((str = in.readLine()) != null){
	            	ids.add(str.split("\t")[0].trim());
	            }
	            process.destroy();
	            in.close();
	            
	            DeviceSelectDialog dialog = new DeviceSelectDialog(Display.getDefault().getActiveShell(), ids);
	            if(dialog.open() == 0){
	            	id =  dialog.getSelected_ID();
	            }  
	            
	        } catch (Exception err) {
	            err.printStackTrace();
	        } finally {
	            try {
	                if (in != null) {
	                    in.close();
	                }
	                if (process != null) {
	                    process.destroy();
	                }
	            } catch (Exception err) {
	                err.printStackTrace();
	            }
	        }
			

			final Job job = new Job("Running app on Mobile") {
				
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					
					if(id == null) return Status.CANCEL_STATUS;
					
					// TODO Auto-generated method stub
					monitor.beginTask("Running app on Mobile", 3);
					String manifestPath = projectDir.getAbsolutePath() + File.separator + "WebContent" + File.separator + "manifest.json";
					FileReader manifestReader = null;
					try {
						MessageConsole console = new MessageConsole("HighWeb Application run on Mobile", null);
						ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] {console});
						ConsolePlugin.getDefault().getConsoleManager().showConsoleView(console);
						MessageConsoleStream stream = console.newMessageStream();
						manifestReader = new FileReader(new File(manifestPath));
						char[] cbuf = new char[32];
						StringBuffer sb = new StringBuffer();
						int i;
						while((i = manifestReader.read(cbuf)) != -1) {
							sb.append(cbuf, 0, i);
						}
						JSONObject manifestObj = new JSONObject(sb.toString());
						String packagename = (String) manifestObj.get("package");
						String version = (String) manifestObj.get("xwalk_version");

						String projectName = project.getName().toLowerCase();
						String modProjectName = makeFirstCharUpperCase(projectName);
						String apkName = modProjectName + "_" + version + "_arm.apk";
						monitor.worked(1);

						monitor.subTask("Installing...");
						String[] args = new String[] {
								"cmd.exe",
								"/C",
								androidSDKDirectory + File.separator + "platform-tools" + File.separator + "adb.exe",
								"-s",
								id,
								"install",
								"-r",
								projectDir.getAbsolutePath() + File.separator + apkName
						};

						ProcessBuilder pBuilder = new ProcessBuilder(args);
						pBuilder.redirectErrorStream(true);
						Process proc = pBuilder.start();
						BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
						String line = null;
						stream.println("Installing " + apkName + "\n");
						while((line = in.readLine()) != null) {
							stream.println(line);
							if(monitor.isCanceled()) {
								stream.println("Installing is cancelled.");
								return Status.CANCEL_STATUS;
							}
						}
						in.close();
						stream.println("\nInstallation done\n\n");
						monitor.worked(1);

						monitor.subTask("Running...");
						args = new String[] {
								"cmd.exe",
								"/C",
								androidSDKDirectory + File.separator + "platform-tools" + File.separator + "adb.exe",
								"-s",
								id,
								"shell",
								"am",
								"start",
								"-n",
								packagename + "/." + modProjectName + "Activity"
						};

						pBuilder = new ProcessBuilder(args);
						pBuilder.redirectErrorStream(true);
						proc = pBuilder.start();
						in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
						stream.println("Running " + packagename + "\n");
						while((line = in.readLine()) != null) {
							stream.println(line);
							if(monitor.isCanceled()) {
								stream.println("Running is cancelled.");
								return Status.CANCEL_STATUS;
							}
						}
						stream.println("\nRuns " + packagename);
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
					} finally {
						if(manifestReader != null) {
							try {
								manifestReader.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							monitor.done();
						}
					}

					id = null;
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		}
		return null;
	}

	private String makeFirstCharUpperCase(String originalString) {
		String resultString = "";

		String firstChar = originalString.substring(0, 1).toUpperCase();
		String restString = originalString.substring(1, originalString.length());

		resultString = firstChar + restString;

		return resultString;
	}

}
