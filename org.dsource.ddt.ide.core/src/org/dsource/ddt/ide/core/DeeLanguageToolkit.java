package org.dsource.ddt.ide.core;

import melnorme.utilbox.core.DevelopmentCodeMarkers;
import mmrnmhrm.core.DeeCore;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.AbstractLanguageToolkit;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.environment.IEnvironment;

import dtool.engine.modules.ModuleNamingRules;

public class DeeLanguageToolkit extends AbstractLanguageToolkit  {
	
	private static final String DEE_LANGUAGE_CONTENT_DSOURCE = DeeCore.PLUGIN_ID + ".content.dsource";
	
	private static final IDLTKLanguageToolkit instance = new DeeLanguageToolkit();
	
	public static IDLTKLanguageToolkit getDefault() {
		return instance ;
	}
	
	@Override
	public String getLanguageName() {
		return "D";
	}

	@Override
	public String getNatureId() {
		return DeeNature.NATURE_ID;
	}
	
	@Override
	public String getLanguageContentType() {
		return DEE_LANGUAGE_CONTENT_DSOURCE;
	}
	
	@Override
	public String getPreferenceQualifier() {
		return DeeCore.PLUGIN_ID;
	}
	
	@Override
	public boolean languageSupportZIPBuildpath() {
		return false;
	}
	
	@Override
	public IStatus validateSourceModule(IResource resource) {
		String name = resource.getName();
		if(ModuleNamingRules.isValidCompilationUnitName(name, false)) {
			return Status.OK_STATUS;
		} else {
			return new Status(IStatus.ERROR, DeeCore.PLUGIN_ID, "Invalid resource name:" + name);
		}
	}
	
	@Override
	public boolean validateSourcePackage(IPath path, IEnvironment environment) {
		if(DevelopmentCodeMarkers.UNIMPLEMENTED_FUNCTIONALITY) {
			return ModuleNamingRules.isValidPackagePathName(path.toString());	
		}
		// We always return true because DLTK gives us path as an absolute path! 
		// Thus we have no way to determine where the package starts, so we cant use isValidPackagePathName
		return true;
	}
	
}
