package org.highweb.webclsdk.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.menus.UIElement;
import org.highweb.webclsdk.Activator;
import org.highweb.webclsdk.preferences.WebCLSDKPreferencePage;

public class HighWebAndroidLogcatHandler extends AbstractHandler implements IElementUpdater {
	private ICommandService service;
	private boolean isSelected;
	private IWorkbenchWindow window;

	public HighWebAndroidLogcatHandler() {
		// TODO Auto-generated constructor stub
		service = PlatformUI.getWorkbench().getService(ICommandService.class);
		window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		String androidPath = WebCLSDKPreferencePage.getAndroidSDKDirectory();
		if(androidPath == null || androidPath.isEmpty()) {
			MessageDialog.openWarning(window.getShell(), "HighWeb Warning - Android SDK Path",
                    "Android SDK 경로가 세팅되어 있지 않습니다.\n"
                    + "Window - Preferences - HighWeb Tool 에서 Android SDK 경로를 세팅해 주세요");
			return null;
		}
		String[] args = new String[] {
				androidPath + File.separator + "platform-tools" + File.separator + "adb.exe",
				"logcat",
				"-v",
				"time"
		};

		Command command = event.getCommand();
		State state = command.getState("org.highweb.webclsdk.commands.highWebAndroidLogcatCommand.toggleState");
		isSelected = !(Boolean) state.getValue();
		state.setValue(isSelected);
		service.refreshElements(command.getId(), null);
		final Job job = new Job("Displaying Android Logcat") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				// TODO Auto-generated method stub
				monitor.beginTask("Displaying Android Logcat", 1);
				MessageConsole console = new MessageConsole("Android Logcat", null);
				ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] {console});
				ConsolePlugin.getDefault().getConsoleManager().showConsoleView(console);
				MessageConsoleStream stream = console.newMessageStream();

				ProcessBuilder pBuilder = new ProcessBuilder(args);
				pBuilder.redirectErrorStream(true);
				try {
					Process proc = pBuilder.start();
					Activator.androidLogcatProcess = proc;
					BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
					while(Activator.androidLogcatProcess.isAlive()) {
						stream.println(in.readLine());
						Thread.sleep(10);
					}
					stream.println(in.readLine());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					monitor.worked(1);
					monitor.done();
				}
				return Status.OK_STATUS;
			}
			
		};
		if(isSelected) {
			job.schedule();
		} else {
			if(Activator.androidLogcatProcess != null && Activator.androidLogcatProcess.isAlive()) {
				Activator.androidLogcatProcess.destroy();
			}
		}
		return null;
	}

	@Override
	public void updateElement(UIElement element, Map parameters) {
		// TODO Auto-generated method stub
		element.setChecked(isSelected);
	}

}
