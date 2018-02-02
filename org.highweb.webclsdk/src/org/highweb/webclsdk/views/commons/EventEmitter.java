package org.highweb.webclsdk.views.commons;

import java.util.ArrayList;
import java.util.List;

public class EventEmitter {
	
	private static EventEmitter instance;
	
	public static EventEmitter getInstance()
	{
		if(instance == null)
			return new EventEmitter();
		
		return instance;
	}
	
	public EventEmitter() {
		instance = this;
	}
	
	public interface ShellCloseEvent
	{
		public void shellClose();
	}
	
	private List<ShellCloseEvent> shellCloseList = new ArrayList<>();
	
	public void addShellCloseEvent(ShellCloseEvent target)
	{
		shellCloseList.add(target);
	}
	
	public void deleteShellCloseEvent(ShellCloseEvent target)
	{
		shellCloseList.remove(target);
	}
	
	public void callShellCloseEvnet()
	{
		for(ShellCloseEvent event : shellCloseList)
			event.shellClose();
	}
	
	public interface ViewInitEevent{
		public void init();
	}
	
	private List<ViewInitEevent> viewInitList = new ArrayList<>();
	
	public void addViewInitEevent(ViewInitEevent target)
	{
		viewInitList.add(target);
	}
	
	public void deleteViewInitEevent(ViewInitEevent target)
	{
		viewInitList.remove(target);
	}
	
	public void callViewInitEevent()
	{
		for(ViewInitEevent event : viewInitList)
			event.init();
	}
}
