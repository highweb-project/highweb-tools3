package org.highweb.webclsdk.preferences;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.highweb.webclsdk.Activator;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.P_FIREFOX_PATH, "");
		store.setDefault(PreferenceConstants.P_BOOLEAN, true);
		store.setDefault(PreferenceConstants.P_CHOICE, "choice2");
		store.setDefault(PreferenceConstants.P_STRING,
				"Default value");
	}

    public static String getDefaultFirefoxPath() {
        String path = Platform.getInstallLocation().getURL().getPath() + "utils\\FirefoxPortable\\";
        path = path.replace("/", "\\");
        if (path.startsWith("\\")) {
            System.out.println("startsWith \\");
            path = path.substring(1, path.length());
        }
        return path;
    }

}
