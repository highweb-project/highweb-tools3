package org.highweb.webclsdk.views.dialog;

import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.highweb.webclsdk.views.commons.ADBSendCMD;
import org.highweb.webclsdk.views.commons.EventEmitter;
import org.highweb.webclsdk.views.commons.SWTApi;

public class DeviceAddDialog extends Dialog implements EventEmitter.ShellCloseEvent{
	
	private Table table;
	
	public DeviceAddDialog(Shell parent) {
		super(parent);
		EventEmitter.getInstance().addShellCloseEvent(this);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Add Device");
		newShell.setSize(500, 300);
	}
	
	@Override
	protected void setShellStyle(int newShellStyle) {
		setBlockOnOpen(false);
	}
	
	@Override
	public boolean close() {
		EventEmitter.getInstance().deleteShellCloseEvent(this);
		return super.close();
	}
	
	@Override
	public void shellClose() {
		super.close();
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		parent.setLayout(new GridLayout(2,false));
		
		Text ipText = new Text(parent, SWT.BORDER);
		SWTApi.setLayoutData(ipText, GridData.FILL, GridData.FILL, true, false, 1, 1, 0, 0);
		
		Button addBtn = new Button(parent, SWT.NONE);
		addBtn.setText("ADD");
		SWTApi.setLayoutData(addBtn, GridData.FILL, GridData.FILL, false, false, 1, 1, 0, 0);
		
		
		table = new Table(parent, SWT.V_SCROLL| SWT.H_SCROLL | SWT.BORDER);
		SWTApi.setLayoutData(table, GridData.FILL, GridData.FILL, true, true, 2, 1, 0, 0);
		table.setHeaderVisible(true);
		
		TableColumn column = new TableColumn(table, SWT.CENTER);
	    column.setText("Device ID");
	    
	    parent.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event event) {
				column.setWidth(parent.getBounds().width - 20);
			}
		});
	    
	    addBtn.addSelectionListener(new AddEvent(ipText));
	    
	    tableRefresh();
	    
	    return parent;
	}
	
	private void tableRefresh(){
		final String ipRegex = "^([0-9]{1,3})(\\.([0-9]{1,3})){3}:([1-9]{1,4})$";
		String [] message = ADBSendCMD.getInstance().commandADB("devices");
		if(message[0].equals("SUCCESS")){
			table.removeAll();
			for(String str : message[1].split("/")) {
				str = str.split("\t")[0];
				if(Pattern.matches(ipRegex, str)) new TableItem(table, SWT.NONE).setText(str.split("\t")[0]);
			}
		}		
	}
	
	
	private class AddEvent implements SelectionListener{
		
		private Text text;
		
		public AddEvent(Text text) {
			this.text = text;
		}
		
		@Override
		public void widgetSelected(SelectionEvent e) {
			String [] message = ADBSendCMD.getInstance().commandADB("connect", text.getText().trim());
			if(message[0].equals("SUCCESS")) tableRefresh();
		}
		
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {}
	}
	
}
