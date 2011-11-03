package dtool.tests.ref.cc;

import melnorme.utilbox.core.ExceptionAdapter;

import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;

public class ModuleSourceTestUtils {

	public static IModuleSource moduleToIModuleSource(final ISourceModule srcModule) {
		return new IModuleSource() {
			@Override
			public String getFileName() {
				return srcModule.getPath().toString();
			}
			
			@Override
			public String getSourceContents() {
				try {
					return srcModule.getSource();
				} catch(ModelException e) {
					throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
				}
			}
			
			@Override
			public IModelElement getModelElement() {
				return srcModule;
			}
			
			@Override
			public char[] getContentsAsCharArray() {
				try {
					return srcModule.getSourceAsCharArray();
				} catch(ModelException e) {
					throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
				}
			}
		};
	}
	
}
