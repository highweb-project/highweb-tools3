package org.highweb.webclsdk.wizards;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.highweb.webclsdk.Activator;
import org.highweb.webclsdk.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

public class NewProjectWizard extends Wizard implements INewWizard {
	public static final String PROJECT_WIZARD_TITLE = "HighWeb Project";
	private static final int MANIFEST_INDENT_FACTOR = 4;

	public NewProjectWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	private NewProjectWizardStartPage firstPage = new NewProjectWizardStartPage();

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		final String projectPath = firstPage.getProjectPath();
		final Job job = new Job("Create new HighWeb Project") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				// TODO Auto-generated method stub
				monitor.beginTask("Create new HighWeb Project", 3);
				if(projectPath != null && !projectPath.isEmpty()) {
					File projectFolder = new File(projectPath);
					if(!projectFolder.exists()) {
						projectFolder.mkdirs();
					}

					File webcontentFolder = new File(projectFolder, "WebContent");
					webcontentFolder.mkdir();

					File assetFolder = new File(webcontentFolder, "asset");
					assetFolder.mkdir();

					File jsFolder = new File(webcontentFolder, "js");
					jsFolder.mkdir();

					File imagesFolder = new File(webcontentFolder, "images");
					imagesFolder.mkdir();

					try {
						Utils.copyFromPlugin(new Path("asset/index.html"), new File(webcontentFolder, "index.html"));
						Utils.copyFromPlugin(new Path("asset/fpslookup.js"), new File(webcontentFolder, "js" + File.separator + "fpslookup.js"));
						Utils.copyFromPlugin(new Path("asset/new_file.cl"), new File(assetFolder, "new_file.cl"));
						Utils.copyFromPlugin(new Path("icons/HighWeb128.png"), new File(imagesFolder, "icon.png"));
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					monitor.worked(1);

					JSONObject manifest = new JSONObject();
					manifest.put("name", firstPage.getProjectName());
					manifest.put("package", firstPage.getPackageName());
					manifest.put("xwalk_version", "0.0.1");
					manifest.put("start_url", "index.html");

					JSONObject iconInfo = new JSONObject();
					iconInfo.put("src", "images/icon.png");
					iconInfo.put("sizes", "128x128");
					iconInfo.put("type", "image/png");
					iconInfo.put("density", "4.0");
					JSONArray icons = new JSONArray();
					icons.put(iconInfo);
					manifest.put("icons", icons);

					String manifestContent = manifest.toString(MANIFEST_INDENT_FACTOR).replace("\\/", "/");
					FileWriter manifestWriter = null;
					try {
						manifestWriter = new FileWriter(new File(webcontentFolder, "manifest.json"));
						manifestWriter.write(manifestContent);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						if(manifestWriter != null) {
							try {
								manifestWriter.flush();
								manifestWriter.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					monitor.worked(1);

					IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
					IProject project = root.getProject(firstPage.getProjectName());
					IPath projectpath = new Path(firstPage.getProjectPath());

					IWorkspace workspace = ResourcesPlugin.getWorkspace();
					IProjectDescription desc = workspace.newProjectDescription(project.getName());
					desc.setLocation(projectpath);
					try {
						project.create(desc, monitor);
						project.open(monitor);
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					monitor.worked(1);

					monitor.done();
				}

				return Status.OK_STATUS;
			}
			
		};
		job.setUser(true);
		job.schedule();
//		IRunnableWithProgress op = new IRunnableWithProgress() {
//
//			@Override
//			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
//				// TODO Auto-generated method stub
//				monitor.beginTask("Create new HighWeb Project", 3);
//				if(projectPath != null && !projectPath.isEmpty()) {
//					File projectFolder = new File(projectPath);
//					if(!projectFolder.exists()) {
//						projectFolder.mkdirs();
//					}
//
//					File webcontentFolder = new File(projectFolder, "WebContent");
//					webcontentFolder.mkdir();
//
//					File assetFolder = new File(webcontentFolder, "asset");
//					assetFolder.mkdir();
//
//					File jsFolder = new File(webcontentFolder, "js");
//					jsFolder.mkdir();
//
//					File imagesFolder = new File(webcontentFolder, "images");
//					imagesFolder.mkdir();
//
//					try {
//						Utils.copyFromPlugin(new Path("asset/index.html"), new File(webcontentFolder, "index.html"));
//						Utils.copyFromPlugin(new Path("asset/new_file.cl"), new File(assetFolder, "new_file.cl"));
//						Utils.copyFromPlugin(new Path("icons/HighWeb128.png"), new File(imagesFolder, "icon.png"));
//					} catch (CoreException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					monitor.worked(1);
//
//					JSONObject manifest = new JSONObject();
//					manifest.put("name", firstPage.getProjectName());
//					manifest.put("package", firstPage.getPackageName());
//					manifest.put("xwalk_version", "0.0.1");
//					manifest.put("start_url", "index.html");
//
//					JSONObject iconInfo = new JSONObject();
//					iconInfo.put("src", "images/icon.png");
//					iconInfo.put("sizes", "128x128");
//					iconInfo.put("type", "image/png");
//					iconInfo.put("density", "4.0");
//					JSONArray icons = new JSONArray();
//					icons.add(iconInfo);
//					manifest.put("icons", icons);
//
//					String manifestContent = manifest.toJSONString().replace("\\/", "/");
//					FileWriter manifestWriter = null;
//					try {
//						manifestWriter = new FileWriter(new File(webcontentFolder, "manifest.json"));
//						manifestWriter.write(manifestContent);
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} finally {
//						if(manifestWriter != null) {
//							try {
//								manifestWriter.flush();
//								manifestWriter.close();
//							} catch (IOException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						}
//					}
//					monitor.worked(1);
//
//					IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
//					IProject project = root.getProject(firstPage.getProjectName());
//					IPath projectpath = new Path(firstPage.getProjectPath());
//
//					IWorkspace workspace = ResourcesPlugin.getWorkspace();
//					IProjectDescription desc = workspace.newProjectDescription(project.getName());
//					desc.setLocation(projectpath);
//					try {
//						project.create(desc, monitor);
//						project.open(monitor);
//					} catch (CoreException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					monitor.worked(1);
//
//					monitor.done();
//				}
//			}
//			
//		};
//
//		try {
//			getContainer().run(true, false, op);
//		} catch (InvocationTargetException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			Throwable realException = e.getTargetException();
//			MessageDialog.openError(getShell(), "Error", realException.getMessage());
//			return false;
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false;
//		}

		return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub
		
	}

	public void addPages() {
		addPage(firstPage);
	}

}
