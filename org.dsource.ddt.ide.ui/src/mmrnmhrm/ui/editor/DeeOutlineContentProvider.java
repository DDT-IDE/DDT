package mmrnmhrm.ui.editor;

import java.util.ArrayList;

import melnorme.util.ui.jface.ElementContentProvider;
import melnorme.utilbox.tree.IElement;

import org.eclipse.jface.viewers.Viewer;

import descent.internal.compiler.parser.ast.IASTNode;
import dtool.ast.ASTNode;
import dtool.ast.declarations.DeclarationImport;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.Module.DeclarationModule;
import dtool.refmodel.INonScopedBlock;

public class DeeOutlineContentProvider extends ElementContentProvider {

	//private CompilationUnit root;

	public static Object[] filterElements(IElement[] elements) {
		ArrayList<IElement> deeElems = new ArrayList<IElement>();
		for(IElement element : elements) {
			if(element instanceof DefUnit 
					|| element instanceof DeclarationImport 
					|| element instanceof DeclarationModule
					|| element instanceof INonScopedBlock) {
				deeElems.add(element);
			} 
		}
		return deeElems.toArray();
	}

	@Override
	public Object[] getElements(Object inputElement) {
		//return filterElements(root.getModule().getChildren());
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object element) {
		if(element instanceof Module || isDeclarationWithDefUnits(element)) {
			ASTNode node = (ASTNode) element;
			return filterElements(node.getChildren());
		} else {
			return ASTNode.NO_ELEMENTS;
		}
	}

	public static boolean isDeclarationWithDefUnits(Object element) {
		return (!(element instanceof DefUnit) 
				&& element instanceof INonScopedBlock
				&& !(element instanceof DeclarationImport));
	}
	
	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof Module || isDeclarationWithDefUnits(element)) {
			IASTNode node = (IASTNode) element;
			return filterElements(node.getChildren()).length > 0;
		} else {
			return false;
		}
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		/*if(newInput instanceof IEditorInput) {
			IEditorInput input = (IEditorInput) newInput;
	    	DeeDocumentProvider docProvider = DeePlugin.getDeeDocumentProvider();
	    	root = docProvider.getCompilationUnit(input);
	    } else {
	    	root = null;
	    }*/
	}

}
