package org.highweb.webclsdk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.osgi.framework.Bundle;

public class Utils {
	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, path);
	}

	public static void copyFromPlugin(IPath source, IFile destination)
			throws CoreException {
        try
        {
        	final Bundle bundle = org.highweb.webclsdk.Activator.getDefault().getBundle();
            final InputStream in = FileLocator.openStream( bundle, source, false );
            destination.create( in, true, null );
        }
        catch( IOException e )
        {
            throw new CoreException( new Status( IStatus.ERROR,
            		Activator.PLUGIN_ID,
            		0, e.getMessage(), e ) );
        }
    }

	public static void copyFromPlugin(IPath source, File destination)
			throws CoreException {
		try {
			final Bundle bundle = Activator.getDefault().getBundle();
			final InputStream in = FileLocator.openStream(bundle, source, false);

			FileOutputStream out = new FileOutputStream(destination);
			byte[] data = new byte[64];
			int len = 0;
			while((len = in.read(data)) != -1) {
				out.write(data, 0, len);
			}
			out.flush();
			out.close();
		} catch(IOException e) {
			throw new CoreException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, 0, e.getMessage(), e));
		}
	}

	public static IFolder getFolder(final IProject project, String foldername) {
		final IVirtualComponent vc = ComponentCore.createComponent( project );
        final IVirtualFolder vf;
        if(foldername == null || foldername.isEmpty()) {
        	vf = vc.getRootFolder();
        } else {
        	vf = vc.getRootFolder().getFolder(foldername);
        }
        return (IFolder) vf.getUnderlyingFolder();
	}

	/*
	 * Source from:
	 * 		http://stackoverflow.com/questions/318239/how-do-i-set-environment-variables-from-java
	 */
	public static void setEnv(Map<String, String> newenv) {
		try {
			Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
			Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
			theEnvironmentField.setAccessible(true);
			Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
			env.putAll(newenv);
			Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
			theCaseInsensitiveEnvironmentField.setAccessible(true);
			Map<String, String> cienv = (Map<String, String>) theCaseInsensitiveEnvironmentField.get(null);
			cienv.putAll(newenv);
		} catch (NoSuchFieldException e) {
			try {
				Class[] classes = Collections.class.getDeclaredClasses();
				Map<String, String> env = System.getenv();
				for (Class cl : classes) {
					if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
						Field field = cl.getDeclaredField("m");
						field.setAccessible(true);
						Object obj = field.get(env);
						Map<String, String> map = (Map<String, String>) obj;
						map.clear();
						map.putAll(newenv);
					}
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	/*
	 * source from:
	 * 		http://www.davidc.net/programming/java/reading-windows-registry-java-without-jni
	 */
	public static class WindowsRegistry {
		/* Windows hives */
		public static final int HKEY_CURRENT_USER = 0x80000001;
		public static final int HKEY_LOCAL_MACHINE = 0x80000002;

		/* Windows security masks */
		private static final int KEY_READ = 0x20019;

		/* Constants used to interpret returns of native functions */
		private static final int NATIVE_HANDLE = 0;
		private static final int ERROR_CODE = 1;

		/* Windows error codes. */
		private static final int ERROR_SUCCESS = 0;
		private static final int ERROR_FILE_NOT_FOUND = 2;

		public static String getKeySz(int hive, String keyName, String valueName) throws BackingStoreException {
			if (hive != HKEY_CURRENT_USER && hive != HKEY_LOCAL_MACHINE) {
				throw new IllegalArgumentException("Invalid hive " + hive);
			}

			final Class clazz = Preferences.userRoot().getClass();

			try {
				final Method openKeyMethod = clazz.getDeclaredMethod("WindowsRegOpenKey", int.class, byte[].class,
						int.class);
				openKeyMethod.setAccessible(true);

				final Method closeKeyMethod = clazz.getDeclaredMethod("WindowsRegCloseKey", int.class);
				closeKeyMethod.setAccessible(true);

				final Method queryValueMethod = clazz.getDeclaredMethod("WindowsRegQueryValueEx", int.class,
						byte[].class);
				queryValueMethod.setAccessible(true);

				int[] result = (int[]) openKeyMethod.invoke(null, hive, stringToByteArray(keyName), KEY_READ);
				if (result[ERROR_CODE] != ERROR_SUCCESS) {
					if (result[ERROR_CODE] == ERROR_FILE_NOT_FOUND) {
						throw new BackingStoreException("Not Found error opening key " + keyName);
					} else {
						throw new BackingStoreException("Error " + result[ERROR_CODE] + " opening key " + keyName);
					}
				}

				int hKey = result[NATIVE_HANDLE];

				byte[] b = (byte[]) queryValueMethod.invoke(null, hKey, stringToByteArray(valueName));
				closeKeyMethod.invoke(null, hKey);

				if (b == null)
					return null;
				else
					return byteArrayToString(b);
			} catch (InvocationTargetException e) {
				throw new BackingStoreException(e.getCause());
			} catch (NoSuchMethodException e) {
				throw new BackingStoreException(e);
			} catch (IllegalAccessException e) {
				throw new BackingStoreException(e);
			}
		}

		/**
		 * Returns this java string as a null-terminated byte array
		 *
		 * @param str
		 *            The string to convert
		 * @return The resulting null-terminated byte array
		 */
		private static byte[] stringToByteArray(String str) {
			byte[] result = new byte[str.length() + 1];
			for (int i = 0; i < str.length(); i++) {
				result[i] = (byte) str.charAt(i);
			}
			result[str.length()] = 0;
			return result;
		}

		/**
		 * Converts a null-terminated byte array to java string
		 *
		 * @param array
		 *            The null-terminated byte array to convert
		 * @return The resulting string
		 */
		private static String byteArrayToString(byte[] array) {
			StringBuilder result = new StringBuilder();
			for (int i = 0; i < array.length - 1; i++) {
				result.append((char) array[i]);
			}
			return result.toString();
		}

		// @SuppressWarnings({ "UseOfSystemOutOrSystemErr",
		// "HardcodedFileSeparator" })
		// private static void testKey(int hive, String keyName, String
		// valueName) {
		// String s;
		//
		// if (hive == HKEY_CURRENT_USER)
		// System.out.print("HKCU\\");
		// if (hive == HKEY_LOCAL_MACHINE)
		// System.out.print("HKLM\\");
		// System.out.println(keyName);
		// System.out.println(" Reading: " + valueName);
		//
		// try {
		// s = getKeySz(hive, keyName, valueName);
		// System.out.println(" >>" + s + "<<");
		// } catch (BackingStoreException e) {
		// System.out.println(" !!" + e.getMessage());
		// }
		// }
		//
		// public static void main(String[] args) {
		// // TODO Auto-generated method stub
		// testKey(HKEY_LOCAL_MACHINE, "SOFTWARE\\Valve\\Steam", "InstallPath");
		// testKey(HKEY_LOCAL_MACHINE, "SOFTWARE\\Valve\\Steam\\Apps\\15660",
		// "");
		// testKey(HKEY_LOCAL_MACHINE, "SOFTWARE\\Valve\\Steam\\Apps\\22000",
		// "");
		// testKey(HKEY_LOCAL_MACHINE, "SOFTWARE\\Valve\\Steam\\Apps\\22010",
		// "");
		// testKey(HKEY_LOCAL_MACHINE,
		// "SOFTWARE\\Classes\\http\\shell\\open\\command", "");
		// }

	}
}
