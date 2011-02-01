package mmrnmhrm.ui.launch;

import java.util.ArrayList;
import java.util.List;

import mmrnmhrm.core.launch.DeeLaunchConfigurationConstants;
import mmrnmhrm.core.model.DeeModel;
import mmrnmhrm.core.model.DeeNature;
import mmrnmhrm.core.model.DeeProjectOptions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.internal.debug.ui.launcher.AbstractScriptLaunchShortcut;
import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

public class DeeLaunchShortcut extends AbstractScriptLaunchShortcut {
	@Override
	protected ILaunchConfigurationType getConfigurationType() {
		return getLaunchManager().getLaunchConfigurationType(
				DeeLaunchConfigurationConstants.ID_DEE_SCRIPT);
	}

	@Override
	protected String getNatureId() {
		return DeeNature.NATURE_ID;
	}
	
	@Override
	protected IResource[] findScripts(Object[] elements,
			IRunnableContext context) throws InterruptedException,
			CoreException {
		
		List<IResource> list = new ArrayList<IResource>(elements.length);
		for (int i = 0; i < elements.length; i++) {
			Object object = elements[i];
				if (object instanceof IFile) {
					IFile f = (IFile) object;
					if (!f.getName().startsWith("."))
						list.add(f);
				} else if (object instanceof IProject) {
					IProject proj = (IProject) object;
					list.add(getScriptResources(proj));
				} else if (object instanceof IScriptProject) {
					IScriptProject deeProj = (IScriptProject) object;
					list.add(getScriptResources(deeProj.getProject()));
				}
		}
		return list.toArray(new IResource[list.size()]);
	}
	
	
	private IFile getScriptResources(IProject proj) {
		DeeProjectOptions projectInfo = DeeModel.getDeeProjectInfo(DLTKCore.create(proj));
		return projectInfo.getOutputFolder().getFile(projectInfo.getArtifactName());
	}

	@Override
	protected ILaunchConfiguration findLaunchConfiguration(IResource script,
			ILaunchConfigurationType configType) {
		return super.findLaunchConfiguration(script, configType);
	}
	
	@Override
	public void launch(IEditorPart editor, String mode) {
		IEditorInput editorInput = editor.getEditorInput();
		if (editorInput == null)
			return;
		IModelElement element = EditorUtility.getEditorInputModelElement(editor, false);
		IFile file = getScriptResources(element.getScriptProject().getProject());
		launch(file, mode);
	}
	
}
