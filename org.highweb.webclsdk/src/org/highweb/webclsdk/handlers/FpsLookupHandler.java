package org.highweb.webclsdk.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

public class FpsLookupHandler extends AbstractHandler {

	public FpsLookupHandler() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		try {
			HandlerUtil.getActiveWorkbenchWindowChecked(event).getActivePage().showView("org.highweb.webclsdk.views.FpsLookupView");
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
