package org.highweb.webclsdk.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.highweb.webclsdk.Activator;

public class HighWebAppTerminateHandler extends AbstractHandler {

	public HighWebAppTerminateHandler() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		if(Activator.nodejsProcess != null && Activator.nodejsProcess.isAlive()) {
			Activator.nodejsProcess.destroy();
			Activator.nodejsProcess = null;
		}
		return null;
	}

}
