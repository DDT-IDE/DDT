package mmrnmhrm.core.codeassist;

import mmrnmhrm.core.DeeCore;

import org.eclipse.dltk.codeassist.ScriptSelectionEngine;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;

// TODO: understand, implement and use this?
public class DeeSelectionEngine extends ScriptSelectionEngine {
	
	@Override
	public IModelElement[] select(IModuleSource sourceUnit, int offset, int i) {
		ISourceModule sourceModule = (ISourceModule) sourceUnit.getModelElement();
		//String source = sourceUnit.getSourceContents();
		
		
		IModelElement elementAt = null;
		try {
			elementAt = sourceModule.getElementAt(offset);
			//elementAt = sourceModule.getElementAt(methodDeclaration.sourceStart() + 1);
		} catch (ModelException e) {
			DeeCore.log(e);
		}
		
		return new IModelElement[]{elementAt};
	}
}