package org.highweb.webclsdk.util;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class MessageUtil {
    public static void showMessage(String message) {
        showMessage(MessageDialog.INFORMATION, "", message);
    }
    public static void showMessage(int type, String title, String message) {
        PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
            public void run() {
                Shell activeShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                switch (type) {
                case MessageDialog.ERROR:
                    MessageDialog.openError(activeShell, title, message);
                    break;
                case MessageDialog.INFORMATION:
                    MessageDialog.openInformation(activeShell, title, message);
                    break;
                case MessageDialog.CONFIRM:
                    MessageDialog.openConfirm(activeShell, title, message);
                    break;
                default:
                    MessageDialog.openInformation(activeShell, title, message);
                    break;
                }
            }
        });
    }
}
