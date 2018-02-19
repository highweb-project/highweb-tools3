package org.highweb.webclsdk.handlers;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Display;
import org.highweb.webclsdk.views.dialog.multiExecutor.MultiExecutorDialog;

public class MultiExcutorHandler extends AbstractHandler {
	
	private MultiExecutorDialog multiExecutorDialog;

	public MultiExcutorHandler() {}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		
		try {
			if(multiExecutorDialog == null){
				multiExecutorDialog = new MultiExecutorDialog(Display.getDefault().getActiveShell(), this);
				multiExecutorDialog.open();
			}else{
				multiExecutorDialog.getShell().forceActive();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return null;
	}
	
	public void init(){
		multiExecutorDialog = null;
	}

}