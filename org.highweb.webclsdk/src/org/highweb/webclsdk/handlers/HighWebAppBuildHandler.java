package org.highweb.webclsdk.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.eclipse.cdt.internal.core.envvar.BuildSystemEnvironmentSupplier;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.handlers.HandlerUtil;
import org.highweb.webclsdk.preferences.WebCLSDKPreferencePage;
import org.highweb.webclsdk.views.dialog.BuildSelectDialog;
import org.json.JSONException;
import org.json.JSONObject;

import sun.security.provider.DSAPublicKeyImpl;

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
			//String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
			//File projectDir = new File(workspacePath + File.separator + project.getName());
			
			BuildSelectDialog buildSelectDialog = new BuildSelectDialog(Display.getDefault().getActiveShell());
			if(buildSelectDialog.open() == 0){
				Job job = null;
				switch (buildSelectDialog.getSelected_Type()) {
					case "General": job = generalBuild(project); break;
					case "Multi Excutor": job = nodeBuild(project); break;
					default: break;
				}
				
				if(job != null){
					job.setUser(true);
					job.schedule();
				}
			}
			
		}
		return null;
	}
	
	private Job generalBuild(IProject project){
		
		Shell shell = Display.getDefault().getActiveShell();
		File projectDir = new File(project.getLocation().toString());
		
		String pythonPath = WebCLSDKPreferencePage.getPythonPath();
		if(pythonPath == null || pythonPath.isEmpty()) {
			MessageDialog.openWarning(shell, "HighWeb Warning - Python Path",
                    "Python 寃쎈줈媛� �꽭�똿�릺�뼱 �엳吏� �븡�뒿�땲�떎.\n"
                    + "Window - Preferences - HighWeb Tool �뿉�꽌 Python 寃쎈줈瑜� �꽭�똿�빐 二쇱꽭�슂");
			return null;
		}
		
		String antDirectory = WebCLSDKPreferencePage.getAntDirectory();
		if(antDirectory == null || antDirectory.isEmpty()) {
			MessageDialog.openWarning(shell, "HighWeb Warning - Ant Directory",
                    "Ant �뵒�젆�넗由ш� �꽭�똿�릺�뼱 �엳吏� �븡�뒿�땲�떎.\n"
                    + "Window - Preferences - HighWeb Tool �뿉�꽌 Ant �뵒�젆�넗由щ�� �꽭�똿�빐 二쇱꽭�슂");
			return null;
		}

		String androidSDKDirectory = WebCLSDKPreferencePage.getAndroidSDKDirectory();
		if(androidSDKDirectory == null || androidSDKDirectory.isEmpty()) {
			MessageDialog.openWarning(shell, "HighWeb Warning - Android SDK Directory",
                    "Android SDK �뵒�젆�넗由ш� �꽭�똿�릺�뼱 �엳吏� �븡�뒿�땲�떎.\n"
                    + "Window - Preferences - HighWeb Tool �뿉�꽌 Android SDK �뵒�젆�넗由щ�� �꽭�똿�빐 二쇱꽭�슂");
			return null;
		}

		String crosswalkDirectory = WebCLSDKPreferencePage.getCrosswalkDirectory();
		if(crosswalkDirectory == null || crosswalkDirectory.isEmpty()) {
			MessageDialog.openWarning(shell, "HighWeb Warning - Android SDK Directory",
                    "Android SDK �뵒�젆�넗由ш� �꽭�똿�릺�뼱 �엳吏� �븡�뒿�땲�떎.\n"
                    + "Window - Preferences - HighWeb Tool �뿉�꽌 Android SDK �뵒�젆�넗由щ�� �꽭�똿�빐 二쇱꽭�슂");
			return null;
		}
		
		return new Job("Build HighWeb Application") {

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
	}
	
	private Job nodeBuild(IProject project){
		
		final String nodePath = System.getProperty("user.dir") + "\\NodeServer\\builds";
		
		return new Job("Build HighWeb Application"){
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				// TODO Auto-generated method stub
				monitor.beginTask("Build HighWeb Application", 4);
				monitor.worked(1);
				
				monitor.worked(1);

				monitor.subTask("Building Web app to Mobile Web app");
				monitor.worked(1);
				
				copy(new File(project.getLocationURI()),  new File(nodePath));
				monitor.worked(1);
				monitor.done();

				return Status.OK_STATUS;
			}
		};
	}
	
	private void copy(File copy, File pastePath){
		if(!copy.exists() || !copy.isDirectory()) return; //error
		if(!pastePath.exists() || !pastePath.isDirectory()) return; //error
		
		pastePath = copyDirectory(copy, pastePath);
		
		Queue<File> queue = new LinkedList<>();
		do{
			File [] children = copy.listFiles();
			for(File child : children){
				if(child.isDirectory()){
					queue.add(child);
					queue.add(copyDirectory(child, pastePath));
				}
				else{
					copyFile(child, pastePath);
				}
			}
			
			copy = queue.poll();
			pastePath = queue.poll();
		}while(copy != null);
		 
	}
	
	private File copyDirectory(File copy, File pastePath){
		File paste = new File(pastePath.getAbsolutePath() + "\\" + copy.getName());
		if(paste.exists()) paste.delete();
		paste.mkdir();
		return paste;
	}
	
	private void copyFile(File copy, File pastePath){
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(copy);
			fos = new FileOutputStream(new File(pastePath.getAbsolutePath() + "\\" + copy.getName()));
			
			int data = 0;
			while((data = fis.read()) > 0) fos.write(data);
		} catch (IOException e) {
			
		}finally {
			try {
				if(fis != null) fis.close();
				if(fos != null) fos.close();
			} catch (Exception e2) {}
		}
	}

}
