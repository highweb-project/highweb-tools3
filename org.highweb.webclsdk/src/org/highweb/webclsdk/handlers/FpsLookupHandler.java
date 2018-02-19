package org.highweb.webclsdk.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.highweb.webclsdk.views.dialog.fpsLookup.FpsLookupView;

public class FpsLookupHandler extends AbstractHandler {

	public FpsLookupHandler() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			new FpsLookupView(null).open();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return null;
	}

}
