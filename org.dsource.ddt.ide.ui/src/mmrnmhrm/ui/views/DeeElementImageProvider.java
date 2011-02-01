package mmrnmhrm.ui.views;

import melnorme.utilbox.tree.IElement;
import mmrnmhrm.ui.DeePluginImages;

import org.eclipse.swt.graphics.Image;

import dtool.ast.ASTNeoNode;
import dtool.ast.declarations.DeclarationImport;
import dtool.ast.declarations.PartialPackageDefUnit;
import dtool.ast.definitions.DefinitionAlias;
import dtool.ast.definitions.DefinitionClass;
import dtool.ast.definitions.DefinitionEnum;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.DefinitionInterface;
import dtool.ast.definitions.DefinitionStruct;
import dtool.ast.definitions.DefinitionTemplate;
import dtool.ast.definitions.DefinitionTypedef;
import dtool.ast.definitions.DefinitionUnion;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.Module.DeclarationModule;
import dtool.ast.references.Reference;
import dtool.ast.references.RefModule.LiteModuleDummy;

public class DeeElementImageProvider {
	
	public static Image getElementImage(IElement element) {
		if (element instanceof ASTNeoNode) {
			return getNodeImage((ASTNeoNode) element);
		} /*else if(element instanceof DeePackageFragment) {
			return getImage(DeePluginImages.ELEM_PACKAGE);
		} else if(element instanceof CompilationUnit) {
			return getImage(DeePluginImages.ELEM_FILE);
		} else if(element instanceof DeeSourceFolder) {
			return getImage(DeePluginImages.ELEM_SOURCEFOLDER);
		} else if(element instanceof DeeSourceLib) {
			return getImage(DeePluginImages.ELEM_LIBRARY);
		} */else  
			return null;
	}
	
	
	public static Image getNodeImage(ASTNeoNode node) {
		if(node instanceof DeclarationImport) { 
			return getImage(DeePluginImages.NODE_IMPORT);
		} else if(node instanceof DeclarationModule 
				|| node instanceof Module || node instanceof LiteModuleDummy) {
			return getImage(DeePluginImages.NODE_MODULE_DEC);
		} else if(node instanceof PartialPackageDefUnit ) {
			return getImage(DeePluginImages.NODE_MODULE_DEC);
		} else if (node instanceof Reference) {
			return getImage(DeePluginImages.NODE_REF);
		} else if (node instanceof DefinitionAlias) {
			return getImage(DeePluginImages.ENT_ALIAS);
		} else if(node instanceof DefinitionTemplate) {
			return getImage(DeePluginImages.ENT_TEMPLATE);
		} else if(node instanceof DefinitionVariable) {
			return getImage(DeePluginImages.ENT_VARIABLE);
		} else if(node instanceof DefinitionFunction) {
			return getImage(DeePluginImages.ENT_FUNCTION);
		} else
			
		if(node instanceof DefinitionInterface) {
			return getImage(DeePluginImages.ENT_INTERFACE);
		} else if(node instanceof DefinitionStruct) {
			return getImage(DeePluginImages.ENT_STRUCT);
		} else if(node instanceof DefinitionUnion) {
			return getImage(DeePluginImages.ENT_UNION);
		} else if(node instanceof DefinitionClass) {
			return getImage(DeePluginImages.ENT_CLASS);
		} else if(node instanceof DefinitionTypedef) {
			return getImage(DeePluginImages.ENT_TYPEDEF);
		} else if(node instanceof DefinitionEnum) {
			return getImage(DeePluginImages.ENT_ENUM);
		} else if(!(node instanceof ASTNeoNode)) {
			return getImage(DeePluginImages.NODE_OLDAST);
		} else
			return getImage(DeePluginImages.NODE_OTHER);
	}


	private static Image getImage(String imageKey) {
		return DeePluginImages.getImage(imageKey);
	}


}
