package org.highweb.webclsdk.views.dialog;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.highweb.webclsdk.views.commons.SWTApi;

import org.highweb.webclsdk.views.commons.EventEmitter;

public class MultiExecutorDialog extends MultiExecutorFunction implements EventEmitter.ShellCloseEvent{

	private final String LOCAL_IP; 
	private final String ADB_PORT = "5555";
	
	public MultiExecutorDialog(Shell parent) throws UnknownError, UnknownHostException{
		super(parent);
		
		LOCAL_IP = InetAddress.getLocalHost().getHostAddress();
		EventEmitter.getInstance().addShellCloseEvent(this);
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Muti Device Execution Property");
		newShell.setSize(800, 600);
	}
	
	@Override
	protected void setShellStyle(int newShellStyle) {
		setBlockOnOpen(false);
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) 
	{
		GridLayout layout = (GridLayout)parent.getLayout();
		layout.marginHeight = 0;
	}
	
	@Override
	public boolean close() {
		return super.close();
	}
	
	@Override
	public void shellClose() {
		EventEmitter.getInstance().deleteShellCloseEvent(this);
		super.close();
	}

	@Override
	protected Control createDialogArea(Composite parent) 
	{
		Composite rootCompo= new Composite(parent, SWT.NONE);
		rootCompo.setLayout(new GridLayout(3, false));
		SWTApi.setLayoutData(rootCompo, GridData.FILL, GridData.FILL, true, true, 1, 1, 0, 0);
		
		createServerControl(rootCompo);
		createDeviceControl(rootCompo);
		createConsole(rootCompo);
		
		return rootCompo;
	}
	
	private void createServerControl(Composite parent){
		Label nodeServer = new Label(parent, SWT.CENTER);
		nodeServer.setText("Node Server");
		SWTApi.setLayoutData(nodeServer, GridData.FILL, SWT.NONE, true, false, 1, 1, 0, 0);
		
		Label serverState = new Label(parent, SWT.CENTER);
		SWTApi.setLayoutData(serverState, GridData.FILL, SWT.NONE, true, false, 1, 1, 0, 0);
		
		Button startBtn = new Button(parent, SWT.NONE);
		SWTApi.setLayoutData(startBtn, GridData.FILL, SWT.NONE, true, false, 1, 1, 0, 0);
		startBtn.addSelectionListener(new NodeServerEvnet(startBtn, serverState));
		
	}
	
	private void createDeviceControl(Composite parent){
		
		Composite deviceCompo = new Composite(parent, SWT.NONE);
		deviceCompo.setLayout(new GridLayout(5, true));
		SWTApi.setLayoutData(deviceCompo, GridData.FILL, SWT.NONE, false, false, 3, 1, 0, 0);
		
		Label labelDevice = new Label(deviceCompo, SWT.CENTER);
		labelDevice.setText("Device IP");
		SWTApi.setLayoutData(labelDevice, GridData.FILL, SWT.NONE, true, false, 1, 1, 0, 0);
		
		Label labelDeviceState = new Label(deviceCompo, SWT.CENTER);
		labelDeviceState.setText("Device State");
		SWTApi.setLayoutData(labelDeviceState, GridData.FILL, SWT.NONE, true, false, 1, 1, 0, 0);
		
		Label labelDetect = new Label(deviceCompo, SWT.CENTER);
		labelDetect.setText("Device Detect");
		SWTApi.setLayoutData(labelDetect, GridData.FILL, SWT.NONE, true, false, 1, 1, 0, 0);
		
		Label labelProject = new Label(deviceCompo, SWT.CENTER);
		labelProject.setText("Property");
		SWTApi.setLayoutData(labelProject, GridData.FILL, SWT.NONE, true, false, 1, 1, 0, 0);
		
		Label labelConn = new Label(deviceCompo, SWT.CENTER);
		labelConn.setText("Send Conn CMD");
		SWTApi.setLayoutData(labelConn, GridData.FILL, SWT.NONE, true, false, 1, 1, 0, 0);
		
		final String[] devices = {"None", "None"};
		for(String str : devices)
			createExecutor(deviceCompo, str);
		
		Label labelText = new Label(deviceCompo, SWT.CENTER);
		labelText.setText("Send Text:");
		SWTApi.setLayoutData(labelText, GridData.FILL, SWT.NONE, true, false, 1, 1, 0, 0);
		
		Text sendText = new Text(deviceCompo, SWT.BORDER);
		SWTApi.setLayoutData(sendText, GridData.FILL, SWT.NONE, true, false, 3, 1, 0, 0);
		
		Button sendBtn = new Button(deviceCompo, SWT.CENTER);
		sendBtn.setText("Send");
		SWTApi.setLayoutData(sendBtn, GridData.FILL, SWT.NONE, false, false, 1, 1, 0, 0);
		sendBtn.addSelectionListener(new SendDataEvent(sendText));
	}
	
	private void createExecutor(Composite parent, String deviceTitle)
	{
		Text textDeviceIP = new Text(parent, SWT.CENTER | SWT.BORDER);
		SWTApi.setLayoutData(textDeviceIP, GridData.FILL, SWT.NONE, true, false, 1, 1, 0, 0);
		
		Label deviceState = new Label(parent, SWT.CENTER);
		deviceState.setText("---");
		SWTApi.setLayoutData(deviceState, GridData.FILL, SWT.NONE, true, false, 1, 1, 0, 0);
		
		Button detectBtn = new Button(parent, SWT.NONE);
		detectBtn.setText("Detach");
		SWTApi.setLayoutData(detectBtn, GridData.FILL, SWT.NONE, false, false, 1, 1, 0, 0);
		
		Combo comboProperty = new Combo(parent, SWT.READ_ONLY);
		SWTApi.setLayoutData(comboProperty, GridData.FILL, SWT.NONE, true, false, 1, 1, 0, 0);
		
		Button connectionBtn = new Button(parent, SWT.NONE);
		connectionBtn.setText("Connection");
		SWTApi.setLayoutData(connectionBtn, GridData.FILL, SWT.NONE, false, false, 1, 1, 0, 0);
		
		DetactEvent deviceEvent = new DetactEvent(deviceState, textDeviceIP, comboProperty, detectBtn, connectionBtn);
		detectBtn.addSelectionListener(deviceEvent);
		connectionBtn.addSelectionListener(deviceEvent);
	}
	
	private void createConsole(Composite parent){
		ConsoleTable table = new ConsoleTable(parent, SWT.V_SCROLL| SWT.H_SCROLL | SWT.BORDER);
		SWTApi.setLayoutData(table, GridData.FILL, GridData.FILL, true, true, 3, 1, 0, 0);
		table.setHeaderVisible(false);
		
		TableColumn column = new TableColumn(table, SWT.CENTER);
	    column.setText("Console");
	    
	    parent.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event event) {
				column.setWidth(parent.getBounds().width - 20);
			}
		});
	}
	
	
	private class NodeServerEvnet implements SelectionListener, EventEmitter.ViewInitEevent{
		
		private final Button control;
		private final Label state;
		
		private final String START = "Start";
		private final String STOP = "Stop";
		private final String CONNECTION = "Connection";
		private final String DISCONNECTION = "Disconnection";
		
		public NodeServerEvnet(Button control, Label state) {
			this.control = control;
			this.state = state;
			EventEmitter.getInstance().addViewInitEevent(this);
			init();
		}
		
		public void init(){
			control.setText(START);
			state.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
			state.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
			state.setFont(new Font(null, "Arial", 11, SWT.BOLD));
			state.setText(DISCONNECTION); 
		}
		
		@Override
		public void widgetSelected(SelectionEvent e) {
			try {
				switch (control.getText().trim()) {
					case START:
						if(NodeServerStart()){
							control.setText(STOP);
							state.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));
							state.setText(CONNECTION);
							setInfo("Started Node Server PORT:8080!");
							setInfo(commandADB("tcpip", ADB_PORT));	//adb tcpip mode·Î ½ÇÇà
						}
						break;
					case STOP:
						if(NodeServerStop()){
							setInfo("Stoped Node Server PORT:8080!");
							setInfo(commandADB("kill-server"));
							EventEmitter.getInstance().callViewInitEevent();
						}
						break;
				}
			} catch (InterruptedException error1) {
			} catch(IOException error2){
			}
		}
		
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {}
	}
	
	private class DetactEvent implements SelectionListener, EventEmitter.ViewInitEevent{

		private Text ipText;
		private Combo property;
		private Button btnDetact;
		private Button btnConn;
		private Label state;
		
		private final String DETACT = "Detact";
		private final String DETACT_STATE = "Detacted";
		private final String RELEASE = "Release";
		private final String RELEASE_STATE = "Released";
		private final String CONNECTION = "Connection";
		
		private final String SENDER = "Project Sender";
		private final String RECIVER ="Project Reciver";
		
		private String IP;
		
		public DetactEvent(Label state, Text ipText, Combo property, Button btnDetact, Button btnConn) {
			this.state = state;
			this.ipText = ipText;
			this.property = property;
			this.btnDetact = btnDetact;
			this.btnConn = btnConn;
			
			EventEmitter.getInstance().addViewInitEevent(this);
			
			init();
		}
		
		@Override
		public void init(){
			btnDetact.setText(DETACT);
			state.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
			state.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
			state.setFont(new Font(null, "Arial", 11, SWT.BOLD));
			state.setText(DETACT_STATE); 
		
			btnConn.setText(CONNECTION);
			btnConn.setEnabled(false);
			
			final String [] comboTitles = {"None", SENDER, RECIVER};
			if(property.getItems().length < 1)
				for(String str : comboTitles) property.add(str);
			property.select(0);
		}
		
		@Override
		public void widgetSelected(SelectionEvent e) {
			switch (((Button)e.getSource()).getText().trim()){
				case DETACT:
					IP =  ipText.getText().trim()+":" + ADB_PORT;
					setInfo(commandADB("connect", IP));
					
					btnDetact.setText(RELEASE);
					
					btnConn.setEnabled(true);
					
					state.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));
					state.setText(DETACT_STATE);
					
				break;
				case RELEASE:
					setInfo(commandADB("disconnect", IP));
					
					btnDetact.setText(DETACT);
					
					btnConn.setEnabled(false);
					
					state.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
					state.setText(RELEASE_STATE); 
					IP = null;
					
				break;
				case CONNECTION:
						String pro = null;
						switch (property.getText()) {
							case SENDER:
								pro = "sender.html";
								break;
							case RECIVER:
								pro = "reciver.html";
							default: break;
						}
						if(pro != null)
							commandADB("-s", IP, "shell", "am", "start", "-a", "android.intent.action.VIEW", "-d", "http://"+LOCAL_IP+":2018/"+pro);
				break;
			}
		}
		
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {}
	}
	
	private class SendDataEvent implements SelectionListener{
		
		private Text text;
		
		public SendDataEvent(Text text) {
			this.text = text;
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			sendText(LOCAL_IP+":2018/", text.getText().trim());
		}
		
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {}
	}
}