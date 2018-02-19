package org.highweb.webclsdk.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.highweb.webclsdk.Activator;
import org.eclipse.ui.IWorkbench;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class WebCLSDKPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public WebCLSDKPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Settings for HighWeb Tool development.");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
//		addField(new DirectoryFieldEditor(PreferenceConstants.P_FIREFOX_PATH, 
//				"&Firefox directory:", getFieldEditorParent()));

		addField(new FileFieldEditor(PreferenceConstants.P_NODEJS_PATH, 
				"&NodeJS path:", getFieldEditorParent()));

		addField(new FileFieldEditor(PreferenceConstants.P_PYTHON_PATH, 
				"&Python path:", getFieldEditorParent()));

		addField(new DirectoryFieldEditor(PreferenceConstants.P_ANT_PATH, 
				"&Ant directory:", getFieldEditorParent()));

		addField(new DirectoryFieldEditor(PreferenceConstants.P_ANDROID_SDK_PATH, 
				"&Android SDK directory:", getFieldEditorParent()));

		addField(new DirectoryFieldEditor(PreferenceConstants.P_CROSSWALK_PATH, 
				"&Crosswalk directory:", getFieldEditorParent()));

		addField(new DirectoryFieldEditor(PreferenceConstants.P_HTTRACK_PATH, 
				"&Httrack directory:", getFieldEditorParent()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}


	public static String getFirefoxPath() {
		String firefoxPath = "";
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setValue(PreferenceConstants.P_FIREFOX_PATH, PreferenceInitializer.getDefaultFirefoxPath());
		firefoxPath = store.getString(PreferenceConstants.P_FIREFOX_PATH);
		System.out.println(firefoxPath);
//		firefoxPath = PreferenceInitializer.getDefaultFirefoxPath();
		return firefoxPath;
	}
	
	public static String getNodeJSPath() {
		String nodejsPath = "";
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		nodejsPath = store.getString(PreferenceConstants.P_NODEJS_PATH);

		return nodejsPath;
	}
	
	public static String getPythonPath() {
		String pythonsPath = "";
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		pythonsPath = store.getString(PreferenceConstants.P_PYTHON_PATH);

		return pythonsPath;
	}
	
	public static String getAntDirectory() {
		String antDirectory = "";
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		antDirectory = store.getString(PreferenceConstants.P_ANT_PATH);

		return antDirectory;
	}
	
	public static String getAndroidSDKDirectory() {
		String androidSDKDirectory = "";
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		androidSDKDirectory = store.getString(PreferenceConstants.P_ANDROID_SDK_PATH);

		return androidSDKDirectory;
	}
	
	public static String getCrosswalkDirectory() {
		String crosswalkDirectory = "";
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		crosswalkDirectory = store.getString(PreferenceConstants.P_CROSSWALK_PATH);

		return crosswalkDirectory;
	}

	public static String getHttrackDirectory() {
		String httrackDirectory = "";
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		httrackDirectory = store.getString(PreferenceConstants.P_HTTRACK_PATH);

		return httrackDirectory;
	}
	
	@Override
	public void dispose() {
		System.out.println("xxxxxxxxxxxxx");
		super.dispose();
	}
}