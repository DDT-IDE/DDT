package mmrnmhrm.ui.views;

import melnorme.utilbox.tree.IElement;
import mmrnmhrm.ui.DeePluginImages;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import dtool.ast.ASTNode;
import dtool.ast.declarations.DeclarationImport;
import dtool.ast.declarations.PartialPackageDefUnit;
import dtool.ast.definitions.DefinitionAliasVarDecl;
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
		if (element instanceof ASTNode) {
			return getNodeImage((ASTNode) element);
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
	
	
	public static Image getNodeImage(ASTNode node) {
		return DeePluginImages.getManagedImage(getNodeImageDescriptorKey(node));
	}
	
	public static ImageDescriptor getNodeImageDescriptor(ASTNode node) {
		return DeePluginImages.getManagedDescriptor(getNodeImageDescriptorKey(node));
	}
	
	private static String getNodeImageDescriptorKey(ASTNode node) {
		// TODO refactor using getNodeType
		if(node instanceof DeclarationImport) { 
			return DeePluginImages.NODE_IMPORT;
		} else if(node instanceof DeclarationModule 
				|| node instanceof Module || node instanceof LiteModuleDummy) {
			return DeePluginImages.NODE_MODULE_DEC;
		} else if(node instanceof PartialPackageDefUnit ) {
			return DeePluginImages.NODE_MODULE_DEC;
		} else if (node instanceof Reference) {
			return DeePluginImages.NODE_REF;
		} else if (node instanceof DefinitionAliasVarDecl) {
			return DeePluginImages.ENT_ALIAS;
		} else if(node instanceof DefinitionTemplate) {
			return DeePluginImages.ENT_TEMPLATE;
		} else if(node instanceof DefinitionVariable) {
			return DeePluginImages.ENT_VARIABLE;
		} else if(node instanceof DefinitionFunction) {
			return DeePluginImages.ENT_FUNCTION;
		} else
		
		if(node instanceof DefinitionInterface) {
			return DeePluginImages.ENT_INTERFACE;
		} else if(node instanceof DefinitionStruct) {
			return DeePluginImages.ENT_STRUCT;
		} else if(node instanceof DefinitionUnion) {
			return DeePluginImages.ENT_UNION;
		} else if(node instanceof DefinitionClass) {
			return DeePluginImages.ENT_CLASS;
		} else if(node instanceof DefinitionTypedef) {
			return DeePluginImages.ENT_TYPEDEF;
		} else if(node instanceof DefinitionEnum) {
			return DeePluginImages.ENT_ENUM;
		} else if(!(node instanceof ASTNode)) {
			return DeePluginImages.NODE_OLDAST;
		} else {
			return DeePluginImages.NODE_OTHER;
		}
	}
	
}
