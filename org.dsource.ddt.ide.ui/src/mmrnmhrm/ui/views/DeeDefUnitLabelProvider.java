package mmrnmhrm.ui.views;

import dtool.ast.ASTCodePrinter;
import dtool.ast.declarations.PartialPackageDefUnit;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.DefVarFragment;
import dtool.ast.definitions.DefinitionAggregate;
import dtool.ast.definitions.DefinitionAlias.DefinitionAliasFragment;
import dtool.ast.definitions.DefinitionAliasFunctionDecl;
import dtool.ast.definitions.DefinitionAliasVarDecl;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.FunctionParameter;
import dtool.ast.definitions.Module;
import dtool.ast.references.RefModule.LiteModuleDummy;
import dtool.ast.references.Reference;

public class DeeDefUnitLabelProvider {
	
	public static String getLabelForHoverSignature(DefUnit defUnit) {
		ASTCodePrinter cp = new ASTCodePrinter();
		
		if(defUnit instanceof LiteModuleDummy) {
			LiteModuleDummy module = (LiteModuleDummy) defUnit;
			return module.getFullyQualifiedName();
		}
		
		switch (defUnit.getNodeType()) {
		case MODULE: {
			Module module = (Module) defUnit;
			return module.getFullyQualifiedName();
		}
		case DEFINITION_VARIABLE: {
			DefinitionVariable var = (DefinitionVariable) defUnit;
			
			return typeRefToUIString(var.type) + " " + var.getName();
		}
		case DEFINITION_VAR_FRAGMENT: {
			DefVarFragment fragment = (DefVarFragment) defUnit;
			
			return typeRefToUIString(fragment.getDeclaredTypeReference()) + " " + fragment.getName();
		}
		
		case DEFINITION_FUNCTION: {
			DefinitionFunction function = (DefinitionFunction) defUnit; 
			cp.appendStrings(typeRefToUIString(function.retType), " ");
			cp.append(function.getName());
			cp.appendList("(", function.tplParams, ",", ") ");
			cp.appendList("(", function.getParams_asNodes(), ",", ") ");
			return cp.toString();
		}
		
		default: break;
		}
		
		if(defUnit instanceof DefinitionAggregate) {
			DefinitionAggregate defAggr = (DefinitionAggregate) defUnit;
			cp.append(defAggr.getName());
			cp.appendList("(", defAggr.tplParams, ",", ") ");
			return cp.toString();
		}
		
		// Default hover signature:
		return defUnit.getName();
	}
	
	// TODO: there are no tests for this code
	public static String getLabelForContentAssistPopup(DefUnit defUnit) {
		
		if(defUnit instanceof LiteModuleDummy) {
			LiteModuleDummy module = (LiteModuleDummy) defUnit;
			return module.getFullyQualifiedName();
		}
		
		if(defUnit instanceof PartialPackageDefUnit) {
			PartialPackageDefUnit elem = (PartialPackageDefUnit) defUnit;
			return elem.getName();
		}
		
		ASTCodePrinter cp = new ASTCodePrinter();
		
		switch (defUnit.getNodeType()) {
		case MODULE: {
			Module elem = (Module) defUnit;
			return elem.getName();
		}
		
		case DEFINITION_VARIABLE: {
			DefinitionVariable elem = (DefinitionVariable) defUnit;
			return elem.getName() + getTypeSegment(elem.type) + getDefUnitContainerSuffix(defUnit);
		}
		case DEFINITION_VAR_FRAGMENT: {
			DefVarFragment elem = (DefVarFragment) defUnit;
			Reference type = elem.getDeclaredTypeReference();
			return elem.getName() + getTypeSegment(type) + getDefUnitContainerSuffix(defUnit);
		}
		
		case DEFINITION_FUNCTION: {
			DefinitionFunction elem = (DefinitionFunction) defUnit; 
			cp.append(elem.getName());
			cp.appendList("(", elem.tplParams, ",", ") ");
			cp.append(elem.toStringParametersForSignature());
			cp.append(getTypeSegment(elem.retType));
			cp.append(getDefUnitContainerSuffix(defUnit));
			return cp.toString();
		}
		
		case DEFINITION_ALIAS_FRAGMENT: {
			DefinitionAliasFragment elem = (DefinitionAliasFragment) defUnit;
			return elem.getName() + getAliasSegment(elem.target) + getDefUnitContainerSuffix(defUnit);
		}
		case DEFINITION_ALIAS_VAR_DECL: {
			DefinitionAliasVarDecl elem = (DefinitionAliasVarDecl) defUnit;
			return elem.getName() + getAliasSegment(elem.target) + getDefUnitContainerSuffix(defUnit);
		}
		case DEFINITION_ALIAS_FUNCTION_DECL: {
			DefinitionAliasFunctionDecl elem = (DefinitionAliasFunctionDecl) defUnit;
			// TODO: print a proper alias segment
			return elem.getName() + getAliasSegment(elem.target) + getDefUnitContainerSuffix(defUnit);
		}
		
		case FUNCTION_PARAMETER: {
			FunctionParameter elem = (FunctionParameter) defUnit;
			return elem.getName() + getTypeSegment(elem.type) + getDefUnitContainerSuffix(defUnit);
		}
		
		default: break;
		}
		
		if(defUnit instanceof DefinitionAggregate) {
			return defUnit.getName() + getDefUnitContainerSuffix(defUnit);
		}
		
		return defUnit.getName() + getDefUnitContainerSuffix(defUnit);
	}

	public static String getTypeSegment(Reference typeRef) {
		return " : " + typeRefToUIString(typeRef);
	}
	
	public static String getAliasSegment(Reference target) {
		return " -> " + target.toStringAsElement();
	}
	
	public static String getDefUnitContainerSuffix(DefUnit defUnit) {
		/* BUG here can be null with synthetic defUnits */
		return " - " + defUnit.getModuleScope().toStringAsElement();
	}
	
	public static String typeRefToUIString(Reference typeReference) {
		if(typeReference == null) {
			return "auto";
		}
		return typeReference.toStringAsElement();
	}
	
}