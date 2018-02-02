package org.highweb.webclsdk.views;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.part.*;
import org.highweb.webclsdk.preferences.WebCLSDKPreferencePage;
import org.highweb.webclsdk.views.commons.SWTApi;
import org.highweb.webclsdk.views.dialog.DeviceSelectDialog;
import org.highweb.webclsdk.views.dialog.MultiExecutorDialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class FpsLookupView extends ViewPart {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "org.highweb.webclsdk.views.FpsLookupView";

    private static final int DATE_START = 0;
    private static final int DATE_END = 1;

    // private TableViewer viewer;
    private FpsLookupChart fpsLookupChart;

    private List<String> DEVICES_ID;
    private List<Device> Devices;
    
    /*
     * The content provider class is responsible for providing objects to the
     * view. It can wrap existing objects in adapters or simply return objects
     * as-is. These objects may be sensitive to the current input of the view,
     * or ignore it and always show the same content (like Task List, for
     * example).
     */

    class ViewContentProvider implements IStructuredContentProvider {
        public void inputChanged(Viewer v, Object oldInput, Object newInput) {
        }

        public void dispose() {
        }

        public Object[] getElements(Object parent) {
            return new String[] { "FPS : " };
        }
    }

    class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
        public String getColumnText(Object obj, int index) {
            return getText(obj);
        }

        public Image getColumnImage(Object obj, int index) {
            return getImage(obj);
        }

        public Image getImage(Object obj) {
            return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
        }
    }

    // class NameSorter extends ViewerSorter {
    // }

    /**
     * The constructor.
     */
    public FpsLookupView() {
    	DEVICES_ID = new ArrayList<>();
    	Devices = new ArrayList<>();
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize
     * it.
     */
    public void createPartControl(Composite parent) {
    	parent.setLayout(new GridLayout(1, true));
    	
    	ScrolledComposite sc = new ScrolledComposite(parent, SWT.NONE | SWT.H_SCROLL | SWT.V_SCROLL);
        sc.setLayout(new GridLayout(1, true));
        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);
        SWTApi.setLayoutData(sc, GridData.FILL, GridData.FILL, true, true, 1, 1, 0, 0);
        
    	Composite rootCompo = new Composite(sc, SWT.NONE);
    	rootCompo.setLayout(new GridLayout(2, true));
    	SWTApi.setLayoutData(rootCompo, GridData.FILL, GridData.FILL, true, true, 1, 1, 0, 0);
        
    	createViewer(rootCompo);
        createViewer(rootCompo);
        
        sc.setContent(rootCompo);
        sc.setMinSize(100, SWT.DEFAULT);
    }
    
    private void createViewer(Composite parent) {
    	
    	Device device = new Device();
    	
    	final Color WHITE = new Color(Display.getDefault(), 255, 255, 255);
    	
        Composite subContainer = new Composite(parent, SWT.NONE);
        subContainer.setBackground(WHITE);
        subContainer.setLayout(new GridLayout(1, true));
        SWTApi.setLayoutData(subContainer, GridData.FILL, GridData.FILL, true, true, 1, 1, 0, 0);
        
        //Control & Info
        Composite compositeTop1 = new Composite(subContainer, SWT.NONE);
        compositeTop1.setBackground(WHITE);
        compositeTop1.setLayout(new GridLayout(6, true));
        SWTApi.setLayoutData(compositeTop1, GridData.FILL, GridData.FILL, false, false, 1, 1, 0, 0);
        
        Label labelDeviceID = new Label(compositeTop1, SWT.CENTER| SWT.BORDER);
        labelDeviceID.setBackground(WHITE);
        labelDeviceID.setText("--");
        SWTApi.setLayoutData(labelDeviceID, GridData.FILL, GridData.FILL, false, false, 1, 1, 0, 0);
        
        Label labelDeviceModel_ = new Label(compositeTop1, SWT.CENTER);
        labelDeviceModel_.setBackground(WHITE);
        labelDeviceModel_.setText("Device Model: ");
        SWTApi.setLayoutData(labelDeviceModel_, GridData.FILL, GridData.FILL, false, false, 1, 1, 0, 0);

        Label labelDeviceModel = new Label(compositeTop1, SWT.LEFT);
        labelDeviceModel.setText("--");
        labelDeviceModel.setBackground(WHITE);
        SWTApi.setLayoutData(labelDeviceModel, GridData.FILL, GridData.FILL, false, false, 1, 1, 0, 0);

        Label labelAndroidVersion_ = new Label(compositeTop1, SWT.CENTER);
        labelAndroidVersion_.setText("Android Version: ");
        labelAndroidVersion_.setBackground(WHITE);
        SWTApi.setLayoutData(labelAndroidVersion_, GridData.FILL, GridData.FILL, false, false, 1, 1, 0, 0);

        Label labelAndroidVersion = new Label(compositeTop1, SWT.LEFT);
        labelAndroidVersion.setText("--");
        labelAndroidVersion.setBackground(WHITE);
        SWTApi.setLayoutData(labelAndroidVersion, GridData.FILL, GridData.FILL, false, false, 1, 1, 0, 0);
        
        Button btnConn = new Button(compositeTop1, SWT.NONE);
        btnConn.setText("Connection");
        btnConn.addSelectionListener(device.getConnectionEvent(labelDeviceID, labelDeviceModel, labelAndroidVersion));
        
        Composite compositeTop2 = new Composite(subContainer, SWT.NONE);
        compositeTop2.setBackground(WHITE);
        compositeTop2.setLayout(new GridLayout(5, false));
        SWTApi.setLayoutData(compositeTop2, GridData.FILL, GridData.FILL, false, false, 1, 1, 0, 0);

        Composite fpsCompo = new Composite(compositeTop2, SWT.CENTER);
        fpsCompo.setBackground(WHITE);
        SWTApi.setLayoutData(fpsCompo, GridData.CENTER, GridData.FILL, false, false, 1, 1, 0, 0);
        fpsCompo.setLayout(new GridLayout(2, false));
        Label labelFps = new Label(fpsCompo, SWT.NONE);
        labelFps.setBackground(WHITE);
        labelFps.setText("Fps on/off ");
        Button btnFps = new Button(fpsCompo, SWT.CHECK);
        btnFps.setSelection(true);
        btnFps.addSelectionListener(device.getOnOffEvent(FpsLookupChart.TIME_SERIES_FPS));

        Composite flopsCompo = new Composite(compositeTop2, SWT.CENTER);
        flopsCompo.setBackground(WHITE);
        SWTApi.setLayoutData(flopsCompo, GridData.CENTER, GridData.FILL, false, false, 1, 1, 0, 0);
        flopsCompo.setLayout(new GridLayout(2, false));
        Label labelFlops = new Label(flopsCompo, SWT.NONE);
        labelFlops.setBackground(WHITE);
        labelFlops.setText("Flops on/off ");
        Button btnFlops = new Button(flopsCompo, SWT.CHECK);
        btnFlops.setSelection(true);
        btnFlops.addSelectionListener(device.getOnOffEvent(FpsLookupChart.TIME_SERIES_FLOPS));

        Composite intervalCompo = new Composite(compositeTop2, SWT.CENTER);
        intervalCompo.setBackground(WHITE);
        SWTApi.setLayoutData(intervalCompo, GridData.CENTER, GridData.FILL, false, false, 1, 1, 0, 0);
        intervalCompo.setLayout(new GridLayout(4, false));
        Label labelInterval = new Label(intervalCompo, SWT.NONE);
        labelInterval.setBackground(WHITE);
        labelInterval.setText("Interval(ms) ");
        Text textInterval = new Text(intervalCompo, SWT.BORDER | SWT.SINGLE | SWT.CENTER);
        textInterval.setText("" + FpsLookupChart.UPDATE_CHART_INTERVAL_DEFAULT);
        textInterval.addVerifyListener(new IntervalEvent());
        Button btnIntervalUp = new Button(intervalCompo, SWT.ARROW | SWT.UP);
        final int TYPE_UP = 1;
        btnIntervalUp.addSelectionListener(device.getInterControlEvent(textInterval, TYPE_UP, fpsLookupChart.UPDATE_CHART_INTERVAL_CHANGE, textInterval));
        Button btnIntervalDown = new Button(intervalCompo, SWT.ARROW | SWT.DOWN);
        final int TYPE_DOWN = -1;
        btnIntervalDown.addSelectionListener(device.getInterControlEvent(textInterval, TYPE_DOWN, fpsLookupChart.UPDATE_CHART_INTERVAL_CHANGE, textInterval));

        Button btnStartAndStop = new Button(compositeTop2, SWT.PUSH);
        btnStartAndStop.setText("Start");
        SWTApi.setLayoutData(btnStartAndStop, GridData.FILL, GridData.FILL, false, false, 1, 1, 0, 0);
        btnStartAndStop.addSelectionListener(device.getStartEvent(btnStartAndStop));

        Button btnSave = new Button(compositeTop2, SWT.PUSH);
        btnSave.setText("Save Fps/Flops");
        SWTApi.setLayoutData(btnSave, GridData.FILL, GridData.FILL, false, false, 1, 1, 0, 0);
        btnSave.addSelectionListener(device.getSaveEvnet());

        //graph
        Composite compositeBottom = new Composite(subContainer, SWT.NONE);
        compositeBottom.setBackground(WHITE);
        compositeBottom.setLayout(new GridLayout(1, true));
        SWTApi.setLayoutData(compositeBottom, GridData.FILL, GridData.FILL, true, true, 1, 1, 0, 0);
        
        FpsLookupChart fpsLookupChart = new FpsLookupChart(30000, compositeBottom, device);
        fpsLookupChart.init();
        device.addFpsLookupChar(fpsLookupChart);
        Devices.add(device);
    }

    protected String openSafeSaveDialog(Shell shell) {
        FileDialog dialog = new FileDialog(shell, SWT.SAVE);
        dialog.setText("Save Fps/Flops");
        dialog.setFilterNames(new String[] { "Text Files", "All Files (*.*)" });
        dialog.setFilterExtensions(new String[] { "*.txt", "*.*" });
        dialog.setFilterPath("c:\\");

        String fileName = null;
        boolean done = false;

        try {
            while (!done) {
                // Open the File Dialog
                fileName = dialog.open();
                if (fileName == null) {
                    // User has cancelled, so quit and return
                    done = true;
                } else {
                    // User has selected a file; see if it already
                    // exists
                    File file = new File(fileName);
                    if (file.exists()) {
                        // The file already exists; asks for
                        // confirmation
                        // If they click Yes, we're done and we drop
                        // out. If
                        // they click No, we redisplay the File Dialog
                        done = MessageDialog.openQuestion(dialog.getParent(), "Save Fps/Flops",
                                fileName + " already exists. Do you want to replace it?");
                    } else {
                        // File does not exist, so drop out
                        done = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileName;
    }

    @Override
    public void init(IViewSite viewSite) throws PartInitException {
        super.init(viewSite);
    }

    @Override
    public void dispose() {
    	for(Device d : Devices) d.dispose();
        super.dispose();
    }

    private void showMessage(Shell shell, String title, String message) {
        if (shell != null) {
            shell.getDisplay().asyncExec(new Runnable() {

                @Override
                public void run() {
                    MessageDialog.openWarning(shell, title, message);
                }
            });
        }
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus() {
        // viewer.getControl().setFocus();
    }

    private class IntervalEvent implements VerifyListener
    {
    	@Override
    	public void verifyText(VerifyEvent e) {
    		try {
                Integer.parseInt(e.text);
            } catch (NumberFormatException ex) {
                e.doit = false;
            }
    	}
    }
    
    public class Device{
    	private FpsLookupChart fpsLookupChart;
    	private boolean isConnected;
    	
    	private Process adbProcess;
    	private Job adbJob;
    	
        private int fps;
        private int flops;
        
        private String deviceModel;
        private String deviceVersion;
    	
    	public void addFpsLookupChar(FpsLookupChart fpsLookupChart){
    		this.fpsLookupChart = fpsLookupChart;
    		this.isConnected = false;
    		
    		adbJob = new Job("ADB logcat for FPS") {

                @Override
                protected IStatus run(IProgressMonitor monitor) {
                    // TODO Auto-generated method stub
                    IWorkbenchWindow window = FpsLookupView.this.getViewSite().getWorkbenchWindow();
                    if (window.getShell().isDisposed())
                        return null;
                    String androidPath = WebCLSDKPreferencePage.getAndroidSDKDirectory();
                    if (androidPath == null || androidPath.isEmpty()) {
                        showMessage(window.getShell(), "HighWeb Warning - Android SDK Path",
                                "Android SDK 寃쎈줈媛� �꽭�똿�릺�뼱 �엳吏� �븡�뒿�땲�떎.\n"
                                        + "Window - Preferences - HighWeb Tool �뿉�꽌 Android SDK 寃쎈줈瑜� �꽭�똿�빐 二쇱꽭�슂");
                        return null;
                    }

                    String[] args = new String[] {
                            androidPath + File.separator + "platform-tools" + File.separator + "adb.exe", "logcat", "chromium:I" };
                    BufferedReader in = null;

                    ProcessBuilder pBuilder = new ProcessBuilder(args);
                    pBuilder.redirectErrorStream(true);
                    try {
                        adbProcess = pBuilder.start();
                        in = new BufferedReader(new InputStreamReader(adbProcess.getInputStream()));
                        Date[] dateFps = new Date[2];
                        dateFps[DATE_START] = new Date();
                        dateFps[DATE_END] = dateFps[DATE_START];
                        Date[] dateFlops = new Date[2];
                        dateFlops[DATE_START] = new Date();
                        dateFlops[DATE_END] = dateFlops[DATE_START];
                        while (adbProcess.isAlive()) {

                            String logline = in.readLine();
                            Pattern patternFps = Pattern.compile("send fps \\d+");
                            Pattern patternFlops = Pattern.compile("send flops \\d+");
                            if (logline == null || patternFps == null)
                                continue;
                            Matcher matcherFps = patternFps.matcher(logline);
                            Matcher matcherFlops = patternFlops.matcher(logline);
                            if (monitor.isCanceled()) {
                                adbProcess.destroy();
                            } else {
                                dateFps = findMacth(matcherFps, dateFps, FpsLookupChart.TIME_SERIES_FPS);
                                dateFlops = findMacth(matcherFlops, dateFlops, FpsLookupChart.TIME_SERIES_FLOPS);
                            }
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } finally {
                        monitor.done();
                        try {
                            if (in != null) {
                                in.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    return Status.OK_STATUS;
                }

            };
    	}
    	
    	public InterControlEvent getInterControlEvent(Text text, int TYPE, int INTERVAL_CHANGE_UNIT, Text intervalText){
    		return new InterControlEvent(text, TYPE, INTERVAL_CHANGE_UNIT, intervalText);
    	}
    	
		private class InterControlEvent implements SelectionListener {
		    private Text text;
		    private final int TYPE;
		    private final int INTERVAL_CHANGE_UNIT;
		    private Text intervalText;
		    public InterControlEvent(Text text, int TYPE, int INTERVAL_CHANGE_UNIT, Text intervalText) {
				this.text = text;
				this.TYPE = TYPE;
				this.INTERVAL_CHANGE_UNIT = INTERVAL_CHANGE_UNIT;
				this.intervalText = intervalText;
			}
		    
		    @Override
		    public void widgetSelected(SelectionEvent e) {
		    	int interVal = Integer.parseInt(intervalText.getText()) + INTERVAL_CHANGE_UNIT*TYPE;
		          	if(interVal < 0)
		          	interVal = 0;
		    		text.setText(String.valueOf(interVal));
		            fpsLookupChart.setInterval(interVal);
		    	}
		    	
		    @Override
		    public void widgetDefaultSelected(SelectionEvent e) {}
		}
		
		public OnOffEvent getOnOffEvent(int timeSeriesNum){
			return new OnOffEvent(timeSeriesNum);
		}
		
	    private class OnOffEvent implements SelectionListener {
	    	private final int timeSeriesNum; 
	    	public OnOffEvent(int timeSeriesNum) {
	    		this.timeSeriesNum = timeSeriesNum;
			}
	    	@Override
	    	public void widgetSelected(SelectionEvent e) {
	            fpsLookupChart.setTimeSeriesVisible(timeSeriesNum, ((Button)e.widget).getSelection());
	    		
	    	}
	    	
	    	@Override
	    	public void widgetDefaultSelected(SelectionEvent e) {}
	    }
	    
	    protected boolean addDivce(String device){
			if(!DEVICES_ID.contains(device)){
				DEVICES_ID.add(device);
				return true;
			}else
				return false;
		}
		
		public ConnectionEvent getConnectionEvent(Label labelID, Label labelModel, Label labelVersion){
			return new ConnectionEvent(labelID, labelModel, labelVersion);
		}
		
		private class ConnectionEvent implements SelectionListener {
			private Label labelModel;
			private Label labelVersion;
			private Label labelID;
			
			public ConnectionEvent(Label labelID, Label labelModel, Label labelVersion) {
				this.labelID = labelID;
				this.labelModel = labelModel;
				this.labelVersion = labelVersion;
			}
			
	    	@Override
	    	public void widgetSelected(SelectionEvent e) {
	    		IWorkbenchWindow window = FpsLookupView.this.getViewSite().getWorkbenchWindow();
		        String androidPath = WebCLSDKPreferencePage.getAndroidSDKDirectory();
		        if (androidPath == null || androidPath.isEmpty()) {
	                showMessage(window.getShell(), "HighWeb Warning - Android SDK Path", "Android SDK 寃쎈줈媛� �꽭�똿�릺�뼱 �엳吏� �븡�뒿�땲�떎.\n"
	                        + "Window - Preferences - HighWeb Tool �뿉�꽌 Android SDK 寃쎈줈瑜� �꽭�똿�빐 二쇱꽭�슂");
		            return;
		        }
		        
		        String[] argsFroUSBMode = new String[] {
		        	androidPath + File.separator + "platform-tools" + File.separator + "adb.exe", "usb"
		        };
		        
		        String[] argsForDeviceId = new String[] {
		        	androidPath + File.separator + "platform-tools" + File.separator + "adb.exe", "devices"
		        };
		        

		        BufferedReader in = null;
		        ProcessBuilder pBuilder = null;
		        Process process = null;
		        try {
		        	//USB MODE
		        	pBuilder = new ProcessBuilder(argsFroUSBMode);
		            pBuilder.redirectErrorStream(true);
		            process = pBuilder.start();
		            process.destroy();
		            
		            //Connection
		            pBuilder = new ProcessBuilder(argsForDeviceId);
		            pBuilder.redirectErrorStream(true);
		            process = pBuilder.start();
		            in = new BufferedReader(new InputStreamReader(process.getInputStream()));
		            String id = in.readLine();
		            List<String> ids = new ArrayList<>();
		            while((id = in.readLine()) != null){
		            	ids.add(id.split("\t")[0].trim());
		            }
		            process.destroy();
		            in.close();
		            
		            DeviceSelectDialog dialog = new DeviceSelectDialog(Display.getDefault().getActiveShell(), ids);
		            if(dialog.open() == 0){
		            	
		            	id = dialog.getSelected_ID();
		            	if(id == null) return;
			            
			            String[] argsForDeviceModel = new String[] {
		    	                androidPath + File.separator + "platform-tools" + File.separator + "adb.exe", "-s", id, "shell", "getprop",
		    	                "ro.product.model" };
		    	        String[] argsForDeviceVersion = new String[] {
		    	                androidPath + File.separator + "platform-tools" + File.separator + "adb.exe", "-s", id, "shell", "getprop",
		    	                "ro.build.version.release" };
			        	
			            pBuilder = new ProcessBuilder(argsForDeviceModel);
			            pBuilder.redirectErrorStream(true);
			            process = pBuilder.start();
			            in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			            String model = in.readLine();
			            process.destroy();
			            in.close();

			            pBuilder = new ProcessBuilder(argsForDeviceVersion);
			            pBuilder.redirectErrorStream(true);
			            process = pBuilder.start();
			            in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			            String version = in.readLine();
			            process.destroy();
			            in.close();

			            if (model.contains("error") || version.contains("error") || model.contains("daemon")
			                    || version.contains("daemon")) {
			            	showMessage(window.getShell(), "FPS Lookup", model);
			                if (adbProcess != null) {
			                    adbProcess.destroy();
			                }
			                return;
			            }
			            
			            labelID.setText(id);
			            deviceModel = model;
			            labelModel.setText(model);
			            deviceVersion = version;
			            labelVersion.setText(version);
			            isConnected = true;
		            }  
		            
		        } catch (Exception err) {
		            err.printStackTrace();
		        } finally {
		            try {
		                if (in != null) {
		                    in.close();
		                }
		                if (process != null) {
		                    process.destroy();
		                }
		            } catch (Exception err) {
		                err.printStackTrace();
		            }
		        }
	    	}
	    	
	    	@Override
	    	public void widgetDefaultSelected(SelectionEvent e) {}
	    }
		
		public StartEvnet getStartEvent(Button btnStartAndStop){
			return new StartEvnet(btnStartAndStop);
		}
		
	    private class StartEvnet implements SelectionListener {
	    	
	    	private Button btnStartAndStop;
	    	private final String START = "Start";
	    	private final String STOP = "Stop";
	    	
	    	public StartEvnet(Button btnStartAndStop) {
	    		this.btnStartAndStop = btnStartAndStop;
			}
	    	
	    	@Override
	    	public void widgetSelected(SelectionEvent e) {
	    		final String text = btnStartAndStop.getText().trim();
	    		new Thread(new Runnable() {

	                @Override
	                public void run() {
	                	if(isConnected){
	                		switch (text) {
							case START:
								adbJob.schedule();
								setUpdateState(true);
								break;
							case STOP:
								adbJob.sleep();
								setUpdateState(false);
								break;
							}
						    
						}
	                }

	            }).start();
	    	}
	    	
	    	@Override
	    	public void widgetDefaultSelected(SelectionEvent e) {}
	    	
	        protected void setUpdateState(boolean state) {
	        	Display.getDefault().syncExec(new Runnable() {
	        		@Override
	        		public void run() {
	        			fpsLookupChart.setChartUpdateState(state);
	        			btnStartAndStop.setText(state == false ? "Start" : "Stop");
					}
				});
	        }
	    }
	    
	    public SaveEvent getSaveEvnet(){
	    	return new SaveEvent();
	    }

	    private class SaveEvent implements SelectionListener {
	    	@Override
	    	public void widgetSelected(SelectionEvent e) {
	    		Shell shell = e.widget.getDisplay().getActiveShell();
	            StringBuilder sbFpsFlopsData = fpsLookupChart.getFpsFlopsData();
	            if (sbFpsFlopsData.length() == 0) {
	                showMessage(shell, "Save Fps/Flops", "There is no DATA!");
	                return;
	            }
	            String fileName = openSafeSaveDialog(shell);
	            if (fileName == null)
	                return;

	            new Thread(new Runnable() {

	                @Override
	                public void run() {
	                	BufferedWriter bw = null;
	                    try {
	                        bw = new BufferedWriter(new FileWriter(new File(fileName)));

	                        bw.write(sbFpsFlopsData.toString());
	                        bw.flush();
	                    } catch (IOException e) {
	                        e.printStackTrace();
	                    }finally {
	                    	try {
	                    		if(bw != null)
	                    			bw.close();
							} catch (IOException ioE) {}
						}
	                }
	            }).start();
	    	}
	    	
	    	@Override
	    	public void widgetDefaultSelected(SelectionEvent e) {}
	    }
	    
	    public void dispose(){
	    	adbJob.cancel();
	    	fpsLookupChart.interrupt();
	    }
	    
	    protected Date[] findMacth(Matcher matcher, Date[] date, int timeSeriesNum) {
	        int num = -1;
	        if (matcher.find()) {
	            date[DATE_START] = new Date();
	            String[] matchGroup = matcher.group().split(" ");
	            try {
	                num = Integer.parseInt(matchGroup[2]);
	            } catch (Exception e) {
	                num = 0;
	                e.printStackTrace();
	            }
	        } else {
	            date[DATE_END] = new Date();
	            if (date[DATE_END].getTime() - date[DATE_START].getTime() >= 3000) {
	                num = 0;
	            } else {
	                return date;
	            }
	        }

	        if (timeSeriesNum == FpsLookupChart.TIME_SERIES_FPS) {
	            fps = num;
	        } else {
	            flops = num;
	        }
	        return date;
	    }
	    
	    public int getFps() {
	        return fps;
	    }

	    public int getFlops() {
	        return flops;
	    }
	    
	    public String getDeviceModel() {
			return deviceModel;
		}
	    
	    public String getDeviceVersion() {
			return deviceVersion;
		}
	}
}
