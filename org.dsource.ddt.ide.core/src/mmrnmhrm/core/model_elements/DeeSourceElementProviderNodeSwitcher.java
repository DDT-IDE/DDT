package mmrnmhrm.core.model_elements;


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
import dtool.ast.definitions.DefinitionMixinInstance;
import dtool.ast.definitions.DefinitionStruct;
import dtool.ast.definitions.DefinitionTemplate;
import dtool.ast.definitions.DefinitionUnion;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.EnumMember;
import dtool.ast.definitions.Module;
import dtool.ast.references.NamedReference;

public abstract class DeeSourceElementProviderNodeSwitcher {

	// NOTE: make sure preVisit code is matches endVisit
	
	public final boolean preVisit(ASTNode node) {
		switch (node.getNodeType()) {
		case MODULE:
			return visit((Module) node);
		case DEFINITION_VARIABLE:
		case DEFINITION_AUTO_VARIABLE:
			return visit((DefinitionVariable) node);
		case DEFINITION_VAR_FRAGMENT:
			return visit((DefVarFragment) node);
		case DEFINITION_FUNCTION:
			return visit((DefinitionFunction) node);
		case DEFINITION_CONSTRUCTOR:
			return visit((DefinitionConstructor) node);
		case DEFINITION_STRUCT:
			return visit((DefinitionStruct) node);
		case DEFINITION_UNION:
			return visit((DefinitionUnion) node);
		case DEFINITION_CLASS:
			return visit((DefinitionClass) node);
		case DEFINITION_INTERFACE:
			return visit((DefinitionInterface) node);
		case DEFINITION_TEMPLATE:
			return visit((DefinitionTemplate) node);
		case DEFINITION_MIXIN_INSTANCE:
			return visit((DefinitionMixinInstance) node);
		case DEFINITION_ENUM:
			return visit((DefinitionEnum) node);
		case ENUM_MEMBER:
			return visit((EnumMember) node);
		case DEFINITION_ALIAS_VAR_DECL:
			return visit((DefinitionAliasVarDecl) node);
		case DEFINITION_ALIAS_FRAGMENT:
			return visit((DefinitionAliasFragment) node);
		case DEFINITION_ALIAS_FUNCTION_DECL:
			return visit((DefinitionAliasFunctionDecl) node);
		case REF_IDENTIFIER:
		case REF_IMPORT_SELECTION:
		case REF_MODULE_QUALIFIED:
		case REF_QUALIFIED:
		case REF_PRIMITIVE:
		case REF_MODULE:
			return visit((NamedReference) node);
		default:
			return true;
		}
		
	}
	
	public final void postVisit(ASTNode node) {
		switch (node.getNodeType()) {
		case MODULE:
			endVisit((Module) node); return;
		case DEFINITION_VARIABLE:
		case DEFINITION_AUTO_VARIABLE:
			endVisit((DefinitionVariable) node); return;
		case DEFINITION_VAR_FRAGMENT:
			endVisit((DefVarFragment) node); return;
		case DEFINITION_FUNCTION:
			endVisit((DefinitionFunction) node); return;
		case DEFINITION_CONSTRUCTOR:
			endVisit((DefinitionConstructor) node); return;
		case DEFINITION_STRUCT:
			endVisit((DefinitionStruct) node); return;
		case DEFINITION_UNION:
			endVisit((DefinitionUnion) node); return;
		case DEFINITION_CLASS:
			endVisit((DefinitionClass) node); return;
		case DEFINITION_INTERFACE:
			endVisit((DefinitionInterface) node); return;
		case DEFINITION_TEMPLATE:
			endVisit((DefinitionTemplate) node); return;
		case DEFINITION_MIXIN_INSTANCE:
			endVisit((DefinitionMixinInstance) node); return;
		case DEFINITION_ENUM:
			endVisit((DefinitionEnum) node); return;
		case ENUM_MEMBER:
			endVisit((EnumMember) node); return;
		case DEFINITION_ALIAS_VAR_DECL:
			endVisit((DefinitionAliasVarDecl) node); return;
		case DEFINITION_ALIAS_FRAGMENT:
			endVisit((DefinitionAliasFragment) node); return;
		case DEFINITION_ALIAS_FUNCTION_DECL:
			endVisit((DefinitionAliasFunctionDecl) node); return;
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
	
	public abstract boolean visit(DefinitionMixinInstance node);
	public abstract void endVisit(DefinitionMixinInstance node);
	
	public abstract boolean visit(DefinitionEnum node);
	public abstract void endVisit(DefinitionEnum node);
	
	public abstract boolean visit(EnumMember node);
	public abstract void endVisit(EnumMember node);
	
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