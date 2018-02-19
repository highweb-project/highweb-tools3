package org.highweb.webclsdk.views.commons;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.highweb.webclsdk.preferences.WebCLSDKPreferencePage;

public class ADBSendCMD {
	
	private static ADBSendCMD instance;
	
	public static ADBSendCMD getInstance(){
		if(instance == null)
			instance = new ADBSendCMD();
		
		return instance;
	}

	private final String ADB_PATH =  WebCLSDKPreferencePage.getAndroidSDKDirectory() + File.separator + "platform-tools" + File.separator + "adb.exe";
	private String[] commands;
	private ExecutorService executorService;
	
	public ADBSendCMD() {
		 executorService = Executors.newSingleThreadExecutor();
	}
	
	public String[] commandADB(String ...command) {
		
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
		        			if(logline != null){
		        				result += logline +"/";
		        			}
		        			
		        		}
						
					} catch (IOException e) {
						return e.getMessage();
					}
					finally {
						if(process != null)
							process.destroy();
					}
					return result;
				}
			});
			
			return new String [] {"SUCCESS" ,future.get()};
		} catch (ExecutionException e) {
			return new String [] {"ERROR", e.getMessage()};
		} catch (InterruptedException e2){
			return new String [] {"ERROR", e2.getMessage()};
		}
	}
}
