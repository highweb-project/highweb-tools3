package org.highweb.webclsdk.views.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.highweb.webclsdk.views.commons.SWTApi;

public class BuildSelectDialog extends Dialog {
	
	private final String [] TYPES = {"General", "Multi Excutor"};
	private String selected_type;
	
	public BuildSelectDialog(Shell parent) {
		super(parent);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Select Build Type");
		newShell.setSize(500, 300);
	}
	
	@Override
	protected void setShellStyle(int newShellStyle) {
		setBlockOnOpen(false);
	}
	
	@Override
	public boolean close() {
		return super.close();
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		parent.setLayout(new GridLayout(1,true));
		
		Table table = new Table(parent, SWT.V_SCROLL| SWT.H_SCROLL | SWT.BORDER);
		SWTApi.setLayoutData(table, GridData.FILL, GridData.FILL, true, true, 1, 1, 0, 0);
		table.setHeaderVisible(true);
		
		TableColumn column = new TableColumn(table, SWT.CENTER);
	    column.setText("Build Type");
	    
	    for(String str : TYPES){
	    	TableItem item = new TableItem(table, SWT.NONE);
	    	item.setText(0, str);
	    }
	    
	    table.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selected_type = table.getSelection()[0].getText();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	    
	    parent.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event event) {
				column.setWidth(parent.getBounds().width - 20);
			}
		});
	    
	    return parent;
	}
	
	public String getSelected_Type(){
		return selected_type;
	}
}