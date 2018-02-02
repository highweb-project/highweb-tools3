package org.highweb.webclsdk.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Display;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.operation.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.core.resources.*;

import java.io.*;
import org.eclipse.ui.*;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.IDE;
import org.highweb.webclsdk.Activator;
import org.highweb.webclsdk.preferences.WebCLSDKPreferencePage;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This is a sample new wizard. Its role is to create a new file resource in the
 * provided container. If the container resource (a folder or a project) is
 * selected in the workspace when the wizard is opened, it will accept it as the
 * target container. The wizard creates one file with the extension "cl". If a
 * sample multi-page editor (also available as a template) is registered for the
 * same extension, it will be able to open it.
 */

public class NewScrapeWebContentWizard extends Wizard implements INewWizard {
    private NewScrapeWebContentWizardPage page;
    private ISelection selection;
    private IProject project;
    private String currentProject;

    /**
     * Constructor for NewCLWizard.
     */
    public NewScrapeWebContentWizard() {
        super();
        setNeedsProgressMonitor(true);
    }

    public NewScrapeWebContentWizard(IProject project, String currentProject) {
        super();
        setNeedsProgressMonitor(true);
        this.project = project;
        this.currentProject = currentProject;
    }

    /**
     * Adding the page to the wizard.
     */

    public void addPages() {
        page = new NewScrapeWebContentWizardPage(selection, currentProject);
        addPage(page);
    }

    /**
     * This method is called when 'Finish' button is pressed in the wizard. We
     * will create an operation and run it using wizard as execution context.
     */
    public boolean performFinish() {
        final String url = page.getUrl();
        final String containerName = page.getContainerName();
        final String folderName = page.getFolderName();
        IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                try {
                    doFinish(url, containerName, folderName, monitor);
                } catch (CoreException e) {
                    throw new InvocationTargetException(e);
                } finally {
                    monitor.done();
                }
            }
        };
        try {
            getContainer().run(true, false, op);
        } catch (InterruptedException e) {
            return false;
        } catch (InvocationTargetException e) {
            Throwable realException = e.getTargetException();
            e.printStackTrace();
            MessageDialog.openError(getShell(), "Error", realException.getMessage());
            return false;
        }
        return true;
    }

    /**
     * The worker method. It will find the container, create the file if missing
     * or just replace its contents, and open the editor on the newly created
     * file.
     */

    private void doFinish(String url, String containerName, String folderName, IProgressMonitor monitor)
            throws CoreException {

        final Job job = new Job("Scrape Web Content") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                // TODO Auto-generated method stub
                monitor.beginTask("Scrape Web Content", 3);
                System.out.println("================================");
                System.out.println("Scrape Web Content");
                // String path = System.getenv("path");
                // String tempPath = new String(path);
                // StringBuffer pathbuf = new StringBuffer(path);
                // if(!path.endsWith(";")) {
                // pathbuf.append(';');
                // }
                // pathbuf.append(antDirectory + File.separator + "bin")
                // .append(';').append(androidSDKDirectory + File.separator +
                // "tools")
                // .append(';').append(androidSDKDirectory + File.separator +
                // "platform-tools");
                // path = pathbuf.toString();
                // monitor.worked(1);

                String strSelectedProject = new StringTokenizer(containerName, "/").nextToken();
                System.out.println("SelectedProject: " + strSelectedProject);
                project = ResourcesPlugin.getWorkspace().getRoot().getProject(strSelectedProject);
                if (project == null)
                    return Status.CANCEL_STATUS;
                String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
                System.out.println("workspacePath: " + workspacePath);
                String httrackDirectory = WebCLSDKPreferencePage.getHttrackDirectory();
                String[] args = new String[] { httrackDirectory + File.separator + "httrack.exe", url, "-O",
                        workspacePath + containerName + File.separator + folderName, "+*", "--depth=2", "-v" };
                System.out.print("args: ");
                for (String str : args)
                    System.out.print(str + " ");
                System.out.println();

                monitor.worked(1);

                try {
                    // 콘솔에 메시지 입력 할 때
                    MessageConsole console = new MessageConsole("Scrape Web Content", null);
                    ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { console });
                    ConsolePlugin.getDefault().getConsoleManager().showConsoleView(console);
                    MessageConsoleStream stdStream = console.newMessageStream();
                    MessageConsoleStream errStream = console.newMessageStream();
                    Display display = getShell().getDisplay().getCurrent();
                    if (display != null) {
                        Color red = new Color(display, 255, 0, 0);
                        errStream.setColor(red);
                    }

                    ProcessBuilder pBuilder = new ProcessBuilder(args);
                    Process process = pBuilder.start();

                    monitor.setTaskName("Scraping web content...");
                    System.out.println("Scraping web content...");
                    BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));

                    while (process.isAlive()) {
                        if (monitor.isCanceled()) {
                            System.out.println("monitor.isCanceled...");
                            System.out.println("process.destroy...");
                            process.destroy();
                            File path = new File(workspacePath + containerName + File.separator + folderName);
                            while (path.exists()) {
                                System.out.println("deleteDirectory: " + deleteDirectory(path));
                            }
                            System.out.println("Refreshing local... " + project.getFullPath().toOSString());
                            project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
                            errStream.println("Scraping web content is cancelled.");
                            return Status.CANCEL_STATUS;
                        }
                        String str = in.readLine();
                        if (str != null) {
                            if (str.contains("Error:"))
                                errStream.println(str);
                            else {
                                stdStream.println(str);
                            }
                        }
                    }

                    monitor.worked(1);

                    monitor.setTaskName("Refreshing local...");
                    System.out.println("Refreshing local... " + project.getFullPath().toOSString());
                    project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());

                    monitor.worked(1);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (CoreException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    monitor.done();
                    System.out.println("Done");
                    System.out.println("================================");
                }
                return Status.OK_STATUS;
            }
        };

        job.setUser(true);
        job.schedule();

    }

    protected boolean deleteDirectory(File path) {
        if (!path.exists()) {
            return false;
        }
        File[] files = path.listFiles();
        for (File file : files) {

            if (file.isDirectory()) {
                deleteDirectory(file);
            } else {
                file.delete();
            }
        }
        return path.delete();
    }

    /**
     * We will initialize file contents with a sample text.
     */

    private InputStream openContentStream() {
        String contents = "//\n" + "// Book:      OpenCL(R) Programming Guide\n"
                + "// Authors:   Aaftab Munshi, Benedict Gaster, Timothy Mattson, James Fung, Dan Ginsburg\n"
                + "// ISBN-10:   0-321-74964-2\n" + "// ISBN-13:   978-0-321-74964-2\n"
                + "// Publisher: Addison-Wesley Professional\n"
                + "// URLs:      http://safari.informit.com/9780132488006/\n"
                + "//			  http://www.openclprogrammingguide.com\n" + "//\n\n"
                + "__kernel void sample_kernel(__global const float *a,\n" + "\t\t\t\t\t\t\t__global const float *b,\n"
                + "\t\t\t\t\t\t\t__global float *result)\n" + "{\n" + "\tint gid = get_global_id(0);\n\n"
                + "\tresult[gid] = a[gid] + b[gid];\n" + "}";
        return new ByteArrayInputStream(contents.getBytes());
    }

    private void throwCoreException(String message) throws CoreException {
        IStatus status = new Status(IStatus.ERROR, "org.highweb.webclsdk", IStatus.OK, message, null);
        throw new CoreException(status);
    }

    /**
     * We will accept the selection in the workbench to see if we can initialize
     * from it.
     * 
     * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
     */
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
    }
}