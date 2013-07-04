package org.dsource.ddt.ide.core.model.engine;


import dtool.ast.ASTNode;
import dtool.ast.definitions.DefVarFragment;
import dtool.ast.definitions.DefinitionAlias.DefinitionAliasFragment;
import dtool.ast.definitions.DefinitionAliasFunctionDecl;
import dtool.ast.definitions.DefinitionAliasVarDecl;
import dtool.ast.definitions.DefinitionClass;
import dtool.ast.definitions.DefinitionConstructor;
import dtool.ast.definitions.DefinitionEnum;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.DefinitionInterface;
import dtool.ast.definitions.DefinitionStruct;
import dtool.ast.definitions.DefinitionTemplate;
import dtool.ast.definitions.DefinitionUnion;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.Module;
import dtool.ast.references.NamedReference;

public abstract class DeeSourceElementProviderNodeSwitcher {
	
	public final boolean preVisit(ASTNode node) {
		switch (node.getNodeType()) {
		case MODULE:
			visit((Module) node); break;
		case DEFINITION_STRUCT:
			visit((DefinitionStruct) node); break;
		case DEFINITION_UNION:
			visit((DefinitionUnion) node); break;
		case DEFINITION_CLASS:
			visit((DefinitionClass) node); break;
		case DEFINITION_INTERFACE:
			visit((DefinitionInterface) node); break;
		case DEFINITION_TEMPLATE:
			visit((DefinitionTemplate) node); break;
		case DEFINITION_ENUM:
			visit((DefinitionEnum) node); break;
		case DEFINITION_ALIAS_VAR_DECL:
			visit((DefinitionAliasVarDecl) node); break;
		case DEFINITION_ALIAS_FRAGMENT:
			visit((DefinitionAliasFragment) node); break;
		case DEFINITION_ALIAS_FUNCTION_DECL:
			visit((DefinitionAliasFunctionDecl) node); break;
		case DEFINITION_FUNCTION:
			visit((DefinitionFunction) node); break;
		case DEFINITION_CONSTRUCTOR:
			visit((DefinitionConstructor) node); break;
		case DEFINITION_VARIABLE:
		//case DEFINITION_AUTO_VARIABLE: /*BUG here*/
			visit((DefinitionVariable) node); break;
		case DEFINITION_VAR_FRAGMENT:
			visit((DefVarFragment) node); break;
		case REF_IDENTIFIER:
		case REF_IMPORT_SELECTION:
		case REF_MODULE_QUALIFIED:
		case REF_QUALIFIED:
		case REF_PRIMITIVE:
		case REF_MODULE:
			visit((NamedReference) node);
			break;
		default:
			break;
		}
		
		return true;
	}
	
	public final void postVisit(ASTNode node) {
		switch (node.getNodeType()) {
		case MODULE:
			endVisit((Module) node); break;
		case DEFINITION_STRUCT:
			endVisit((DefinitionStruct) node); break;
		case DEFINITION_UNION:
			endVisit((DefinitionUnion) node); break;
		case DEFINITION_CLASS:
			endVisit((DefinitionClass) node); break;
		case DEFINITION_INTERFACE:
			endVisit((DefinitionInterface) node); break;
		case DEFINITION_TEMPLATE:
			endVisit((DefinitionTemplate) node); break;
		case DEFINITION_ENUM:
			endVisit((DefinitionEnum) node); break;
		case DEFINITION_ALIAS_VAR_DECL:
			endVisit((DefinitionAliasVarDecl) node); break;
		case DEFINITION_ALIAS_FRAGMENT:
			endVisit((DefinitionAliasFragment) node); break;
		case DEFINITION_ALIAS_FUNCTION_DECL:
			endVisit((DefinitionAliasFunctionDecl) node); break;
		case DEFINITION_FUNCTION:
			endVisit((DefinitionFunction) node); break;
		case DEFINITION_CONSTRUCTOR:
			endVisit((DefinitionConstructor) node); break;
		case DEFINITION_VARIABLE:
			endVisit((DefinitionVariable) node); break;
		case DEFINITION_VAR_FRAGMENT:
			endVisit((DefVarFragment) node); break;
		default:
			break;
		}
	}
	
	public abstract boolean visit(Module node);
	public abstract void endVisit(Module node);
	
	public abstract boolean visit(DefinitionStruct node);
	public abstract void endVisit(DefinitionStruct node);
	
	public abstract boolean visit(DefinitionUnion node);
	public abstract void endVisit(DefinitionUnion node);
	
	public abstract boolean visit(DefinitionClass node);
	public abstract void endVisit(DefinitionClass node);
	
	public abstract boolean visit(DefinitionInterface node);
	public abstract void endVisit(DefinitionInterface node);
	
	
	public abstract boolean visit(DefinitionTemplate node);
	public abstract void endVisit(DefinitionTemplate node);
	
	public abstract boolean visit(DefinitionEnum node);
	public abstract void endVisit(DefinitionEnum node);
	
	public abstract boolean visit(DefinitionAliasVarDecl node);
	public abstract void endVisit(DefinitionAliasVarDecl node);
	
	public abstract boolean visit(DefinitionAliasFragment node);
	public abstract void endVisit(DefinitionAliasFragment node);
	
	public abstract boolean visit(DefinitionAliasFunctionDecl node);
	public abstract void endVisit(DefinitionAliasFunctionDecl node);
	
	/* ---------------------------------- */
	
	public abstract boolean visit(DefinitionFunction node);
	public abstract void endVisit(DefinitionFunction node);
	
	public abstract boolean visit(DefinitionConstructor node);
	public abstract void endVisit(DefinitionConstructor node);
	
	/* ---------------------------------- */
	
	public abstract boolean visit(DefinitionVariable node);
	public abstract void endVisit(DefinitionVariable node);
	
	public abstract boolean visit(DefVarFragment node);
	public abstract void endVisit(DefVarFragment node);
	
	/* ---------------------------------- */
	
	public abstract boolean visit(NamedReference elem);
	
}