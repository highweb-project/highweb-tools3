package org.highweb.webclsdk.views;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.resource.Resource;

/**
 * The main plugin class to be used in the desktop.
 */
public class TreeViewerPlugin extends AbstractUIPlugin {
    // The shared instance.
    private static TreeViewerPlugin plugin;
    // Resource bundle.
    private ResourceBundle resourceBundle;

    /**
     * The constructor.
     */
    public TreeViewerPlugin(IPluginDescriptor descriptor) {
        super(descriptor);
        plugin = this;
        try {
            resourceBundle = ResourceBundle.getBundle("cbg.article.treeviewer.TreeviewerPluginResources");
        } catch (MissingResourceException x) {
            resourceBundle = null;
        }
    }

    /**
     * Returns the shared instance.
     */
    public static TreeViewerPlugin getDefault() {
        return plugin;
    }

    /**
     * Returns the workspace instance.
     */
    public static IWorkspace getWorkspace() {
        return ResourcesPlugin.getWorkspace();
    }

    /**
     * Returns the string from the plugin's resource bundle, or 'key' if not
     * found.
     */
    public static String getResourceString(String key) {
        ResourceBundle bundle = TreeViewerPlugin.getDefault().getResourceBundle();
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }

    public static ImageDescriptor getImageDescriptor(String name) {
        String iconPath = "icons/";
        try {
            // URL installURL = getDefault().getDescriptor().getInstallURL();
            // URL url = new URL(installURL, iconPath + name);
            URL url = FileLocator.find(Platform.getBundle("org.highweb.webclsdk"), new Path(iconPath + name), null);
            return ImageDescriptor.createFromURL(url);
        } catch (Exception e) {
            // should not happen
            return ImageDescriptor.getMissingImageDescriptor();
        }
    }

    /**
     * Returns the plugin's resource bundle,
     */
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }
    
}
