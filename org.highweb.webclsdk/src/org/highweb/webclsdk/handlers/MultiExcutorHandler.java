package org.highweb.webclsdk.handlers;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Display;
import org.highweb.webclsdk.views.dialog.MultiExecutorDialog;

public class MultiExcutorHandler extends AbstractHandler {

	public MultiExcutorHandler() {}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		
		try {
			new MultiExecutorDialog(Display.getDefault().getActiveShell()).open();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return null;
	}

}