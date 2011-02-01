package mmrnmhrm.core.model;

import melnorme.utilbox.misc.ArrayUtil;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.build.DeeProjectBuilder;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.ScriptNature;

public class DeeNature extends ScriptNature  {

	private static final String NATURE_BASEID = "nature";
	public static final String NATURE_ID = DeeCore.PLUGIN_ID +"."+ NATURE_BASEID;


	/** Configure the project with a Dee nature. */
	@Override
	public void configure() throws CoreException {
		super.configure();
		addToBuildSpec(getProject(), DeeProjectBuilder.BUILDER_ID);
	}
	
	/** Remove the Dee nature from the project. */
	@Override
	public void deconfigure() throws CoreException {
		super.configure();
		removeFromBuildSpec(getProject(), DeeProjectBuilder.BUILDER_ID);
	}
	
	/** Adds a builder to the build spec of this project, if the
	 * builder doesn't exist already. 
	 * @param project */
	public static void addToBuildSpec(IProject project, String builderID) throws CoreException {

		IProjectDescription description = project.getDescription();
		ICommand[] commands = description.getBuildSpec();
	
		if (getBuildCommandIndex(commands, builderID) == -1) {
			// Adds the builder to the build spec.
			ICommand command = description.newCommand();
			command.setBuilderName(builderID);
	
			description.setBuildSpec(ArrayUtil.append(commands, command));
			project.setDescription(description, null);
		}
		
	}
	
	/** Find the specific build command in the given build spec.
	 * Returns it's index or -1 if not found. */
	public static int getBuildCommandIndex(ICommand[] buildSpec, String builderID) {
		for (int i = 0; i < buildSpec.length; ++i) {
			if (buildSpec[i].getBuilderName().equals(builderID)) {
				return i;
			}
		}
		return -1;
	}

	/** Removes the given builder from the build spec in this project. */
	public static void removeFromBuildSpec(IProject project, String builderID) throws CoreException {
	
		IProjectDescription description = project.getDescription();
		ICommand[] commands = description.getBuildSpec();
		for (int i = 0; i < commands.length; ++i) {
			if (commands[i].getBuilderName().equals(builderID)) {
				ICommand[] newCommands = ArrayUtil.removeAt(commands, i); 
				description.setBuildSpec(newCommands);
				project.setDescription(description, null);
				return;
			}
		}
	}

}
