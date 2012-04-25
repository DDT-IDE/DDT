package mmrnmhrm.ui;

import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.INamespace;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.ui.ScriptElementLabels;

// TODO: customize some of the label strings? Don't print initializers for the methods? 
public class DeeScriptElementLabels extends ScriptElementLabels {
	
	@Override
	public String getElementLabel(IModelElement element, long flags) {
		return super.getElementLabel(element, flags);
	}
	
	@Override
	protected void appendTypeQualification(IType type, long flags, StringBuffer buf) {
		// -------------- Original DLTK 3.0 code:
//		try {
//			final INamespace namespace = type.getNamespace();
//			if (namespace != null) {
//				// TODO customize separator
//				buf.append(namespace.getQualifiedName("."));
//				buf.append(".");
//				return;
//			}
//		} catch (ModelException e) {
//			// ignore
//			return;
//		}
//		IResource resource = type.getResource();
//		IProjectFragment pack = null;
//		if (resource != null) {
//			IScriptProject project = type.getScriptProject();
//			pack = project.getProjectFragment(resource);
//		} else {
//			pack = findProjectFragment(type);
//		}
//		if (pack == null) {
//			pack = findProjectFragment(type);
//		}
//		getScriptFolderLabel(pack, (flags & QUALIFIER_FLAGS), buf);
		
		// -------------- 
		try {
			final INamespace namespace = type.getNamespace();
			if (namespace != null) {
				buf.append(namespace.getQualifiedName("."));
				if(!namespace.isRoot()) {
					buf.append(".");
				}
				return;
			}
		} catch (ModelException e) {
			// ignore
			return;
		}
		IScriptFolder pkg = type.getScriptFolder();
		
		getScriptFolderLabel(pkg, (flags & QUALIFIER_FLAGS), buf);
		buf.append(".");
	}
	
}
