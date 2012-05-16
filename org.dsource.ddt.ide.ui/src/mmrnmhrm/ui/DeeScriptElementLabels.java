package mmrnmhrm.ui;

import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.ILocalVariable;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.ui.ScriptElementLabels;

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
		
		IScriptFolder pkg = type.getScriptFolder();
		if(pkg.isRootFolder()) {
			return;
		}
		buf.append(pkg.getElementName().replaceAll("/", ".") + ".");
	}
	
	@Override
	protected void getFieldLabel(IField field, long flags, StringBuffer buf) {
		super.getFieldLabel(field, flags, buf);
	}
	
	@Override
	protected void getLocalVariableLabel(ILocalVariable field, long flags, StringBuffer buf) {
		super.getLocalVariableLabel(field, flags, buf);
	}
	
	@Override
	protected void getSourceModule(ISourceModule module, long flags, StringBuffer buf) {
		if (getFlag(flags, CU_QUALIFIED)) {
			IScriptFolder pack = (IScriptFolder) module.getParent();
			if(!pack.isRootFolder()) {
				getScriptFolderLabel(pack, (flags & QUALIFIER_FLAGS), buf);
				buf.append("/");
			}
		}
		buf.append(module.getElementName());
		
		if (getFlag(flags, CU_POST_QUALIFIED) && !((IScriptFolder) module.getParent()).isRootFolder()) {
			IScriptFolder pack = (IScriptFolder) module.getParent();
			buf.append(CONCAT_STRING);
			getScriptFolderLabel(pack, flags & QUALIFIER_FLAGS, buf);
		}
	}
	
}
