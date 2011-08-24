package mmrnmhrm.ui.preferences;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.swtutil.GridComposite;
import melnorme.util.ui.fields.FieldUtil;
import melnorme.util.ui.fields.ProjectContainerSelectionDialog;
import melnorme.util.ui.fields.SelectionComboDialogField;
import melnorme.util.ui.fields.StringDialogField;
import melnorme.util.ui.swt.SWTLayoutUtil;
import mmrnmhrm.core.build.DeeBuildOptions;
import mmrnmhrm.core.build.DeeBuilder;
import mmrnmhrm.core.projectmodel.DeeProjectModel;
import mmrnmhrm.core.projectmodel.DeeProjectOptions;
import mmrnmhrm.ui.actions.OperationsManager;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class DeeProjectOptionsBlock implements IDialogFieldListener  {
	
	DeeProjectOptions fDeeProjOptions;
	DeeProjectOptions fOverlayOptions;

	
	protected SelectionComboDialogField<DeeBuildOptions.EBuildTypes> fBuildType;
	protected StringDialogField fArtifactName;
	protected StringButtonDialogField fOutputDir;
	protected StringButtonDialogField fCompilerTool;
	//protected IPath fCompilerToolPath;
	protected StringDialogField fExtraOptions;

	protected StringDialogField fOptionsPreview;



	private Shell shell;
	
	public DeeProjectOptionsBlock() {

		fBuildType = new SelectionComboDialogField<DeeBuildOptions.EBuildTypes>();
		fBuildType.setLabelText("Build Type:");
		fBuildType.setObjectItems(DeeBuildOptions.EBuildTypes.values());
		fBuildType.setDialogFieldListener(this);
		
		fArtifactName = new StringDialogField(SWT.BORDER | SWT.SINGLE);
		fArtifactName.setLabelText("Target name:");
		fArtifactName.setDialogFieldListener(this);
		
		fOutputDir = new StringButtonDialogField(new IStringButtonAdapter() {
			@Override
			public void changeControlPressed(DialogField field) {
				ProjectContainerSelectionDialog containerDialog;
				containerDialog	= new ProjectContainerSelectionDialog(getShell(), fDeeProjOptions.getProject());
				containerDialog.dialog.setTitle("Folder Selection"); 
				containerDialog.dialog.setMessage("Choose the output location folder.");

				IResource initSelection = null;
				if (fOutputDir != null) {
					initSelection = fDeeProjOptions.getProject().findMember(new Path(fOutputDir.getText()));
					containerDialog.dialog.setInitialSelection(initSelection);
				}

				IContainer container = containerDialog.chooseContainer();
				if (container != null) {
					fOutputDir.setText(container.getProjectRelativePath().toString());
					//fCompilerToolPath = container.getProjectRelativePath();
				}
				
			}
		});
		fOutputDir.setLabelText("Output folder:");
		fOutputDir.setButtonLabel("Browse");
		fOutputDir.setDialogFieldListener(this);
		
		fCompilerTool = new StringButtonDialogField(new IStringButtonAdapter() {
			@Override
			public void changeControlPressed(DialogField field) {
				FileDialog dialog = new FileDialog(getShell());
				dialog.setFilterPath(fCompilerTool.getText());
				if (Platform.getOS().equals(Platform.OS_WIN32)) {
					dialog.setFilterExtensions(new String[] { "*.exe;*.bat" });
				} else {
					dialog.setFilterExtensions(new String[] { "*" });
				}
				dialog.setFilterNames(new String[] { "Executables" });
				String newPath = dialog.open();
				if (newPath != null) {
					fCompilerTool.setText((new Path(newPath)).toString());
				}
			}
		});
		fCompilerTool.setLabelText("Build Command:");
		fCompilerTool.setButtonLabel("Browse");
		fCompilerTool.setDialogFieldListener(this);
		
		fExtraOptions = new StringDialogField(SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
				);
		fExtraOptions.setLabelText("Managed response file:");
		fExtraOptions.setDialogFieldListener(this);

		fOptionsPreview = new StringDialogField(SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL 
				| SWT.READ_ONLY);
		fOptionsPreview.setLabelText("Generated 'build.rf' response file preview:");
		
	}
	
	protected void internalInit(DeeProjectOptions projectInfo) {
		assertNotNull(projectInfo);
		fDeeProjOptions = projectInfo;
		fOverlayOptions = fDeeProjOptions.clone();
		assertTrue(fOverlayOptions.getBuildCommands().indexOf('\r') == -1);
		updateView();
	}
	
	public void init2(IScriptProject scriptProject) {
		internalInit(DeeProjectModel.getDeeProjectInfo(scriptProject));
	}

	
	public Composite createControl(Composite parent) {
		Composite content = parent;
		shell = parent.getShell();
		content = new GridComposite(parent);
		
		GridComposite rowComposite = new GridComposite(content);
		SWTLayoutUtil.setWidthHint(rowComposite, 200);
		SWTLayoutUtil.enableDiagonalExpand(rowComposite);

		
		//LayoutUtil.enableHorizontalGrabbing(content);
/*		FieldUtil.doDefaultLayout2(topcontent, false, 
				fArtifactName, fOutputDir, fCompilerTool);
		LayoutUtil.enableHorizontalGrabbing(fOutputDir.getTextControl(null));
		LayoutUtil.setHorizontalSpan(fArtifactName.getTextControl(null), 1);
		*/
		Composite comp;


/*		comp = FieldUtil.createCompose(rowComposite, false, fBuildType);
		//LayoutUtil.setHorizontalSpan(fBuildType.getLabelControl(null), 1);
		SWTLayoutUtil.setWidthHint(fBuildType.getLabelControl(null), 100);
		SWTLayoutUtil.setWidthHint(fBuildType.getComboControl(null), 80);
	*/	
		comp = FieldUtil.createCompose(rowComposite, false, fArtifactName);
		SWTLayoutUtil.setWidthHint(fArtifactName.getLabelControl(null), 100);
		SWTLayoutUtil.setWidthHint(fArtifactName.getTextControl(null), 120);

		
		comp = FieldUtil.createCompose(rowComposite, false, fOutputDir);
		SWTLayoutUtil.setWidthHint(fOutputDir.getLabelControl(null), 100);
		SWTLayoutUtil.enableHorizontalGrabbing(fOutputDir.getTextControl(null));

		
		comp = FieldUtil.createCompose(rowComposite, false, fCompilerTool);
		SWTLayoutUtil.setWidthHint(fCompilerTool.getLabelControl(null), 100);
		SWTLayoutUtil.enableHorizontalGrabbing(fCompilerTool.getTextControl(null));

		
		comp = FieldUtil.createCompose(rowComposite, true, fExtraOptions);
		SWTLayoutUtil.enableDiagonalExpand(comp);
		SWTLayoutUtil.enableDiagonalExpand(fExtraOptions.getTextControl(null));
		SWTLayoutUtil.setHeightHint(fExtraOptions.getTextControl(null), 200);

		comp = FieldUtil.createCompose(rowComposite, true, fOptionsPreview);
		SWTLayoutUtil.enableDiagonalExpand(comp);
		SWTLayoutUtil.enableDiagonalExpand(fOptionsPreview.getTextControl(null));
		SWTLayoutUtil.setHeightHint(fOptionsPreview.getTextControl(null), 200);

		return content;
	}

	private Shell getShell() {
		return shell;
	}
	
	private void updateView() {
		DeeBuildOptions options = fOverlayOptions.getCompilerOptions();
		fBuildType.setTextWithoutUpdate(options.buildType.toString());
		fArtifactName.setTextWithoutUpdate(options.artifactName);
		fOutputDir.setTextWithoutUpdate(options.outputDir.toString());
		fCompilerTool.setTextWithoutUpdate(options.buildToolCmdLine);
		fExtraOptions.setTextWithoutUpdate(options.buildCommands);

		updateBuildPreview();
	}
	
	@Override
	public void dialogFieldChanged(DialogField field) {
		DeeBuildOptions options = fOverlayOptions.getCompilerOptions();
		options.buildType = fBuildType.getSelectedObject();
		options.artifactName = fArtifactName.getText();
		options.outputDir = new Path(fOutputDir.getText());
		options.buildToolCmdLine = fCompilerTool.getText().replace("\r", "");
		options.buildCommands = fExtraOptions.getText().replace("\r", "");
		
		updateBuildPreview();
	}

	private void updateBuildPreview() {
		String cmds = DeeBuilder.getPreviewBuildCommands(fDeeProjOptions.dltkProj,
				fOverlayOptions, new NullProgressMonitor());
		assertTrue(cmds.indexOf('\r') == -1);
		fOptionsPreview.setText(cmds);
	}

	public boolean performOk() {
		return OperationsManager.executeOperation(new IWorkspaceRunnable() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				fDeeProjOptions.changeProjectOptions(fOverlayOptions.getCompilerOptions());
			}
		}, "Saving Project Compile Option");
	}
	
}
