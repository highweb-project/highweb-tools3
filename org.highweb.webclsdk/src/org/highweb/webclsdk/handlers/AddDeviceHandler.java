package org.highweb.webclsdk.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.highweb.webclsdk.views.dialog.DeviceAddDialog;

public class AddDeviceHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		
		try {
			new DeviceAddDialog(null).open();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return null;
	}

}
