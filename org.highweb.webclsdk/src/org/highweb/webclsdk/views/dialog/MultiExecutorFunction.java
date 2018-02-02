package org.highweb.webclsdk.views.dialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.highweb.webclsdk.preferences.WebCLSDKPreferencePage;

public class MultiExecutorFunction extends Dialog{
	
	private final String PROJECT_PATH = System.getProperty("user.dir") + File.separator + "NodeServer";
	private final NodeSever nodeServer;
	private ConsoleTable console;
	private final ADBSendCMD adSendCMD;
	private final SendData sendData;
	
	public MultiExecutorFunction(Shell parent) {
		super(parent);
		nodeServer = new NodeSever();
		adSendCMD = new ADBSendCMD();
		sendData = new SendData();
	}
	
	@Override
	public boolean close() {
		if(nodeServer!= null)
			nodeServer.close();
		
		commandADB("kill-server");
		
		return super.close();
	}
	
	protected boolean NodeServerStart() throws IOException, InterruptedException {
		return nodeServer.NodeServerStart();
	}
	
	protected boolean NodeServerStop() throws IOException, InterruptedException {
		return nodeServer.NodeServerStop();
	}
	
	protected void setInfo(String message){
		message = message.trim();
		if(message != null)
			if(!message.equals(""))
				console.setInfo(message);
	}
	
	protected void setError(String message){
		message = message.trim();
		if(message != null)
			if(!message.equals(""))
				console.setError(message);
	}
	
	protected void setSend(String message){
		message = message.trim();
		if(message != null)
			if(!message.equals(""))
				console.setSend(message);
	}
	
	protected void setRecive(String message){
		message = message.trim();
		if(message != null)
			if(!message.equals(""))
				console.setRecive(message);
	}
	
	protected String commandADB(String ...command){
		return adSendCMD.commandADB(command);
	}
	
	protected void sendText(String address, String text) {
		sendData.sendText(address, text);
	}
	
	public class NodeSever extends Thread{
		private final String [] CHAT_SERVER_COMMAND = {"node", "server.js"};
		//private final String [] CHAT_SERVER_COMMAND = {"cmd", "/c", "cd", PROJECT_PATH+"\\", "&", "node", "server.js"};
		private Process chatServer;
		
		public boolean NodeServerStart() throws IOException, InterruptedException {
			
			try {
				Thread thread = new Thread(this);
				thread.setDaemon(true);
				thread.start();
			} catch (Exception e) {
				return false;
			}
			
			return true;
		}
		
		public boolean NodeServerStop() throws IOException, InterruptedException {
			return close();
		}
		
        @Override
        public void run() {
        	try {
        		ProcessBuilder processBuilder = new ProcessBuilder(CHAT_SERVER_COMMAND);
        		processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
        		processBuilder.directory(new File(PROJECT_PATH +"\\"));
        		chatServer = processBuilder.start();

        		BufferedReader in = new BufferedReader(new InputStreamReader(chatServer.getInputStream()));
        		while(chatServer.isAlive()){
        			String logline = in.readLine();
        			if(logline != null)
        				System.out.println(logline);
        		}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
        }
        
        public boolean close(){
        	if(chatServer != null){
        		try {
        			chatServer.destroy();
				} catch (Exception e) {
					return false;
				}
        	}
        	return true;
        }
	}
	
	public class ConsoleTable extends Table{
		
		private final int TABLE_ROW_MAX = 100;
		private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmmss");
		private final String ERROR = " [Error] ";
		private final String INFO = " [Info] ";
		private final String SEND = " [Send]";
		private final String RECIVE = " [Recive]";
		
		public ConsoleTable(Composite parent, int type) {
			super(parent, type);
			console = this;
		}
		
		@Override
		protected void checkSubclass(){}
		
		public void setError(String message){
			message = simpleDateFormat.format(new Date()) + ERROR + message;
			addTableItem(message, Display.getDefault().getSystemColor(SWT.COLOR_RED));
		}
		
		public void setInfo(String message){
			message = simpleDateFormat.format(new Date()) + INFO + message;
			addTableItem(message, Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
		}
		
		public void setSend(String message){
			message = simpleDateFormat.format(new Date()) + SEND + message;
			addTableItem(message, Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN));
		}
		
		public void setRecive(String message){
			message = simpleDateFormat.format(new Date()) + RECIVE + message;
			addTableItem(message, Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
		}
		
		private void addTableItem(String message, Color color){
			TableItem item = new TableItem(this, SWT.NULL);
			item.setForeground(0, color);
			item.setText(message);
			
			if(getItemCount() > TABLE_ROW_MAX)
				remove(0);
			
			setTopIndex(getItemCount() - 1);
		}
	}
	
	public class ADBSendCMD {
		private final String ADB_PATH =  WebCLSDKPreferencePage.getAndroidSDKDirectory() + File.separator + "platform-tools" + File.separator + "adb.exe";
		private String[] commands;
		private ExecutorService executorService;
		
		public ADBSendCMD() {
			 executorService = Executors.newSingleThreadExecutor();
		}
		
		public String commandADB(String[] command) {
			
			commands = new String [command.length + 3];
			commands[0] = "cmd";
			commands[1] = "/C";
			commands[2] = ADB_PATH;
			for(int i=0; i<command.length; i++) 
				commands[i+3] = command[i];
			
			try {
				Future<String> future = executorService.submit(new Callable<String>() {
					@Override
					public String call() throws Exception {
						Process process = null;
						String result = "";
						try {
							ProcessBuilder pb = new ProcessBuilder(commands);
							process = pb.start();
							
							BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
							String logline;
							while((logline = in.readLine()) != null){
			        			if(logline != null)
			        				result += logline;
			        		}
							
						} catch (IOException e) {
							setError(e.getMessage());
							return null;
						}
						finally {
							if(process != null)
								process.destroy();
						}
						return result;
					}
				});
				
				return future.get();
			} catch (ExecutionException e) {
				setError(e.getMessage());
				return null;
			} catch (InterruptedException e2){
				setError(e2.getMessage());
				return null;
			}
		}
	}
	
	
	public class SendData{
		
		public void sendText(String address, String text){
			final String [] command = {"node", PROJECT_PATH + "//sendCMD.js", address, text};
			
			Process process = null;
			try {
				ProcessBuilder pb = new ProcessBuilder(command);
				
				process = pb.start();
				
				BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String logline;
				while((logline = in.readLine()) != null){
        			if(logline != null){
        				String[] str = logline.split("/");
        				switch (str[0]) {
						case "RECIVE":
							setRecive(str[1]);
							break;
						case "SEND":
							setSend(str[1]);
							break;
						default:
							setInfo(str[0]);
							break;
						}
        			}
        				
        		}
			} catch (IOException e1){
				
			}finally {
				if(process != null)
					process.destroy();
			}
		}
	}
	
}
