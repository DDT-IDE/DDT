package mmrnmhrm.core.model;

import melnorme.utilbox.misc.ArrayUtil;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.build.DeeProjectBuilder;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.ScriptNature;

public class DeeNature extends ScriptNature  {
	
	protected static final String NATURE_BASEID = "nature";
	public static final String NATURE_ID = DeeCore.PLUGIN_ID +"."+ NATURE_BASEID;
	
	
	/** Configure the project with a Dee nature. */
	@Override
	public void configure() throws CoreException {
		super.configure();
		addToBuildSpec2(DeeProjectBuilder.BUILDER_ID);
	}
	
	/** Remove the Dee nature from the project. */
	@Override
	public void deconfigure() throws CoreException {
		super.configure();
		removeFromBuildSpec(DeeProjectBuilder.BUILDER_ID);
	}
	
	/** Adds a builder with given builderID to the build spec of our project, if the
	 * builder is not there already. */
	protected void addToBuildSpec2(String builderID) throws CoreException {
		IProjectDescription description = getProject().getDescription();
		ICommand[] commands = description.getBuildSpec();
		int buildCommandIndex = getBuildCommandIndex(commands, builderID);
		
		if (buildCommandIndex == -1) {
			// Adds the builder to the build spec.
			ICommand command = description.newCommand();
			command.setBuilderName(builderID);
			
			description.setBuildSpec(ArrayUtil.append(commands, command));
			getProject().setDescription(description, null);
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
	
}
