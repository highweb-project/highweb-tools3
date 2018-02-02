package org.highweb.webclsdk.handlers;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.highweb.webclsdk.Activator;
import org.highweb.webclsdk.Utils;
import org.highweb.webclsdk.preferences.WebCLSDKPreferencePage;
import org.highweb.webclsdk.util.MessageUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class HighWebAppRunHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public HighWebAppRunHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
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
			File projectDir = new File(workspacePath + File.separator + project.getName());

			//System.out.println("FireFox path: " + WebCLSDKPreferencePage.getFirefoxPath());
			//"C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe";
			String nodejsPath = WebCLSDKPreferencePage.getNodeJSPath();
			if(nodejsPath == null || nodejsPath.isEmpty()) {
				MessageDialog.openWarning(window.getShell(), "HighWeb Warning - NodeJS Path",
                        "NodeJS 경로가 세팅되어 있지 않습니다.\n"
                        + "Window - Preferences - HighWeb Tool 에서 NodeJS 경로를 세팅해 주세요");
				return null;
			}

//			String defaultWebBrwoserRegKey = "";
//			try {
//				defaultWebBrwoserRegKey = Utils.WindowsRegistry.getKeySz(Utils.WindowsRegistry.HKEY_LOCAL_MACHINE, "SOFTWARE\\Mozilla\\Firefox", "");
//			} catch(java.util.prefs.BackingStoreException e) {
//				try {
//					defaultWebBrwoserRegKey = Utils.WindowsRegistry.getKeySz(Utils.WindowsRegistry.HKEY_CURRENT_USER, "SOFTWARE\\Mozilla\\Firefox", "");
//				} catch(java.util.prefs.BackingStoreException e1) {
//					MessageDialog.openWarning(window.getShell(), "HighWeb Warning - Mozilla Firefox Installation",
//            "Mozilla Firefox 브라우저가 설치되어 있지 않습니다.\n"
//            + "Mozilla Firefox 3.3 버전 및 Nokia WebCL Extension을 설치해 주세요");
//					return null;
//				}
//			}

			String firefoxPath = WebCLSDKPreferencePage.getFirefoxPath() + File.separator + "FirefoxPortable.exe";
			if(firefoxPath == null || firefoxPath.isEmpty()) {
				MessageDialog.openWarning(window.getShell(), "HighWeb Warning - Firefox Path",
                        "Firefox 경로가 세팅되어 있지 않습니다.\n"
                        + "Window - Preferences - HighWeb Tool 에서 Firefox 경로를 세팅해 주세요");
				return null;
			}

			ProcessBuilder pBuilder = null;

//			 helper.exe -> default browser 瑜� 諛붽퓭二쇰뒗 �뿭�븷
			try {
//				String progFilesx86Dir = System.getenv("ProgramFiles(x86)");
				StringBuilder helper = new StringBuilder();
				helper.append(WebCLSDKPreferencePage.getFirefoxPath()).append(File.separator)
				.append("App").append(File.separator)
				.append("Firefox").append(File.separator)
				.append("uninstall").append(File.separator)
				.append("helper.exe");
				pBuilder = new ProcessBuilder(new String[] {helper.toString(), "/SetAsDefaultAppUser"});
				pBuilder.redirectErrorStream(true);
				Process proc = pBuilder.start();
				proc.waitFor();
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			try {
				Process proc = null;
				if(Activator.nodejsProcess != null && Activator.nodejsProcess.isAlive()) {
				    Activator.nodejsProcess.destroy();
//					String url = "http://localhost:4400/?enableripple=true";
//					pBuilder = new ProcessBuilder(new String[] {firefoxPath, url});
//					pBuilder.redirectErrorStream(true);
//					proc = pBuilder.start();
				}
				String webContentPath = projectDir.getAbsolutePath() + File.separator + "WebContent";
                String appdata = System.getenv("APPDATA");
                String ripple = appdata + File.separator + "npm" + File.separator
                        + "node_modules" + File.separator + "ripple-emulator" + File.separator
                        + "bin" + File.separator + "ripple";
                String rippleArg = "emulate";
                pBuilder = new ProcessBuilder(new String[] {nodejsPath, ripple, rippleArg});
                System.out.println("ripple: "+nodejsPath+ ripple+ rippleArg);
                pBuilder.redirectErrorStream(true);
                pBuilder.directory(new File(webContentPath));
                proc = pBuilder.start();
                Activator.nodejsProcess = proc;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				MessageUtil.showMessage(MessageDialog.ERROR, "", e.toString());
			}
		}
		return null;
	}
}
