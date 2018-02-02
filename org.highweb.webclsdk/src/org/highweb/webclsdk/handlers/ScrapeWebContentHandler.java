package org.highweb.webclsdk.handlers;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.highweb.webclsdk.preferences.WebCLSDKPreferencePage;
import org.highweb.webclsdk.wizards.NewScrapeWebContentWizard;

public class ScrapeWebContentHandler extends AbstractHandler {

    public ScrapeWebContentHandler() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        String httrackDirectory = WebCLSDKPreferencePage.getHttrackDirectory();
        if (httrackDirectory == null || httrackDirectory.isEmpty()) {
            MessageDialog.openWarning(window.getShell(), "HighWeb Warning - Httrack Directory",
                    "Httrack 디렉토리가 세팅되어 있지 않습니다.\n" + "Window - Preferences - HighWeb Tool 에서 Httrack 디렉토리를 세팅해 주세요");
            return null;
        }

        // selection project ���� ���� ���
        // container ��θ� �Է� ���� ���� ���
        IWorkbenchWindow win = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        IWorkbenchPage activePage = win.getActivePage();
        ISelection selection = activePage.getSelection();
        IProject project = null;
        String currentProject = null;
        if (selection != null && selection instanceof ITreeSelection) {
            TreeSelection treeSelection = (TreeSelection) selection;
            TreePath[] treePaths = treeSelection.getPaths();
            if (treePaths.length > 0) {
                TreePath treePath = treePaths[0];

                Object firstSegmentObj = treePath.getFirstSegment();

                project = (IProject) ((IAdaptable) firstSegmentObj).getAdapter(IProject.class);
                String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
                File projectDir = new File(workspacePath + File.separator + project.getName());
                currentProject = project.getFullPath().toOSString();
                currentProject = currentProject.replace("\\", "/");
            }
        }

        Shell activeShell = HandlerUtil.getActiveShell(event);
        IWizard wizard = new NewScrapeWebContentWizard(project, currentProject);
        WizardDialog dialog = new WizardDialog(activeShell, wizard);
        dialog.open();
        return null;
    }

}
