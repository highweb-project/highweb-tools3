package org.highweb.webclsdk.wizards;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.internal.Workbench;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (cl).
 */

public class NewScrapeWebContentWizardPage extends WizardPage {
    private Text containerText;
    private Text folderText;
    private Text urlText;

    private ISelection selection;

    private String currentProject;

    /**
     * Constructor for NewCLWizardPage.
     * 
     * @param pageName
     */
    public NewScrapeWebContentWizardPage(ISelection selection, String currentProject) {
        super("newScrapeWebContentWizardPage");
        setTitle("New Scrape Web Content");
        setDescription("This wizard creates a new Scrape Web Content.");
        // setImageDescriptor(Utils.getImageDescriptor("icons/HighWeb64.png"));
        this.selection = selection;
        this.currentProject = currentProject;
    }

    /**
     * @see IDialogPage#createControl(Composite)
     */
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 3;
        layout.verticalSpacing = 9;
        Label label = new Label(container, SWT.NULL);
        label.setText("&Container:");

        containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        containerText.setLayoutData(gd);
        containerText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                dialogChanged();
            }
        });
        containerText.setEditable(false);

        Button button = new Button(container, SWT.PUSH);
        button.setText("Browse...");
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                handleBrowse();
            }
        });
        label = new Label(container, SWT.NULL);
        label.setText("&Folder name:");

        folderText = new Text(container, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        folderText.setLayoutData(gd);
        folderText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                dialogChanged();
            }
        });
        label = new Label(container, SWT.NULL);

        label = new Label(container, SWT.NULL);
        label.setText("&Web Address(URL):");

        urlText = new Text(container, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        urlText.setLayoutData(gd);
        urlText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                dialogChanged();
            }
        });
        initialize();
        dialogChanged();
        setControl(container);
    }

    /**
     * Tests if the current workbench selection is a suitable container to use.
     */

    private void initialize() {
        if (selection != null && selection.isEmpty() == false && selection instanceof IStructuredSelection) {
            IStructuredSelection ssel = (IStructuredSelection) selection;
            if (ssel.size() > 1)
                return;
            Object obj = ssel.getFirstElement();
            if (obj instanceof IResource) {
                IContainer container;
                if (obj instanceof IContainer)
                    container = (IContainer) obj;
                else
                    container = ((IResource) obj).getParent();
                containerText.setText(container.getFullPath().toString());
            }
        }
        if (currentProject != null && !currentProject.equals("")) {
            containerText.setText(currentProject);
        }
    }

    /**
     * Uses the standard container selection dialog to choose the new value for
     * the container field.
     */

    private void handleBrowse() {
        ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(),
                ResourcesPlugin.getWorkspace().getRoot(), false, "Select new folder container");
        if (dialog.open() == ContainerSelectionDialog.OK) {
            Object[] result = dialog.getResult();
            if (result.length == 1) {
                containerText.setText(((Path) result[0]).toString());
            }
        }
    }

    /**
     * Ensures that both text fields are set.
     */

    private void dialogChanged() {
        IResource container = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(getContainerName()));
        String folderName = getFolderName();
        String url = getUrl();

        if (getContainerName().length() == 0) {
            updateStatus("Container must be specified");
            return;
        }
        if (container == null || (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
            updateStatus("Container must exist");
            return;
        }
        if (!container.isAccessible()) {
            updateStatus("Project must be writable");
            return;
        }

        if (folderName.length() == 0) {
            updateStatus("Folder name must be specified");
            return;
        }
        if (folderName.replace('\\', '/').indexOf('/', 1) > 0) {
            updateStatus("Folder name must be valid");
            return;
        }
        int dotLoc = folderName.lastIndexOf('.');
        if (dotLoc != -1) {
            // String ext = fileName.substring(dotLoc + 1);
            // if (ext.equalsIgnoreCase("cl") == false) {
            // updateStatus("File extension must be \"cl\"");
            // return;
            // }
        } else {
            updateStatus("Folder name must be valid");
        }

        if (url.length() == 0) {
            updateStatus("Web Address must be specified");
            return;
        }

        updateStatus(null);
    }

    private void updateStatus(String message) {
        setErrorMessage(message);
        setPageComplete(message == null);
    }

    public String getContainerName() {
        return containerText.getText();
    }

    public String getFolderName() {
        return folderText.getText();
    }

    public String getUrl() {
        return urlText.getText();
    }
}