package org.highweb.webclsdk.wizards;

import java.io.File;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.highweb.webclsdk.Activator;
import org.highweb.webclsdk.Utils;

public class NewProjectWizardStartPage extends WizardPage {
	private static final String DEFAULT_LOCATION_PATH = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString().replace("/", File.separator);
	private String chosenPath = "";
	private String projectPath = "";
	private String projectName = "";
	private String packageName = "";

	public NewProjectWizardStartPage() {
		super("newProjectWizardStart");
		setTitle(NewProjectWizard.PROJECT_WIZARD_TITLE);
		setDescription("Create a HighWeb project for WebCL content.");
		setImageDescriptor(Utils.getImageDescriptor("icons/HighWeb64.png"));
	}

	@Override
	public void createControl(Composite parent) {
		// TODO Auto-generated method stub
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 9;

		Label lblPrjName = new Label(container, SWT.NONE);
		lblPrjName.setText("Project Name: ");
		Text txtPrjName = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		txtPrjName.setLayoutData(gd);
		txtPrjName.setText("");

		Label lblPkgName = new Label(container, SWT.NONE);
		lblPkgName.setText("Package Name: ");
		Text txtPkgName = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		txtPkgName.setLayoutData(gd);
		txtPkgName.setText("");

		Group grpProjectLocation = new Group(container, SWT.NONE);
		grpProjectLocation.setText("Project Location");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		grpProjectLocation.setLayoutData(gd);
		GridLayout locLayout = new GridLayout();
		locLayout.numColumns = 3;
		grpProjectLocation.setLayout(locLayout);

		Button chkUseDefLoc = new Button(grpProjectLocation, SWT.CHECK);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		chkUseDefLoc.setLayoutData(gd);
		chkUseDefLoc.setText("Use default location");
		chkUseDefLoc.setSelection(true);

		Label lblLocation = new Label(grpProjectLocation, SWT.NONE);
		lblLocation.setText("Location:");
		Text txtLocation = new Text(grpProjectLocation, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		txtLocation.setLayoutData(gd);
		txtLocation.setText(DEFAULT_LOCATION_PATH);
		Button btnBrowse = new Button(grpProjectLocation, SWT.NONE);
		btnBrowse.setText("Browse...");
		btnBrowse.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void widgetSelected(SelectionEvent event) {
				// TODO Auto-generated method stub
				DirectoryDialog dialog = new DirectoryDialog(btnBrowse.getShell());
				dialog.setMessage("Select the location directory");
				chosenPath = dialog.open();
				if(chosenPath != null && !chosenPath.isEmpty()) {
					txtLocation.setText(chosenPath);
					projectPath = chosenPath;
				}
			}
			
		});

		chkUseDefLoc.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void widgetSelected(SelectionEvent event) {
				// TODO Auto-generated method stub
				if(chkUseDefLoc.getSelection()) {
					lblLocation.setEnabled(false);
					txtLocation.setEnabled(false);
					txtLocation.setText(DEFAULT_LOCATION_PATH);
					btnBrowse.setEnabled(false);
				} else {
					lblLocation.setEnabled(true);
					txtLocation.setEnabled(true);
					txtLocation.setText(chosenPath);
					btnBrowse.setEnabled(true);
				}
			}
			
		});

		txtPrjName.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent event) {
				// TODO Auto-generated method stub
				if(chkUseDefLoc.getSelection()) {
					if(txtPrjName.getText().length() <= 0) {
						txtLocation.setText(DEFAULT_LOCATION_PATH);
						projectName = "";
					} else {
						projectPath = DEFAULT_LOCATION_PATH + File.separator + txtPrjName.getText();
						txtLocation.setText(projectPath);
						projectName = txtPrjName.getText();
					}
				}
			}
			
		});

		txtPkgName.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				// TODO Auto-generated method stub
				if(txtPkgName.getText().length() <= 0) {
					packageName = "";
				} else {
					packageName = txtPkgName.getText();
				}
			}
			
		});

		lblLocation.setEnabled(false);
		txtLocation.setEnabled(false);
		btnBrowse.setEnabled(false);

		setControl(container);
	}

	public String getProjectPath() {
		return projectPath;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getPackageName() {
		return packageName;
	}
}
