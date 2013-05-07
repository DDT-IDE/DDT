package dtool.ast.definitions;

import static dtool.util.NewUtils.assertNotNull_;

import java.util.Collection;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.expressions.Initializer;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;
import dtool.refmodel.IDefUnitReference;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;
import dtool.util.ArrayView;

/**
 * A variable definition. 
 * Optionally has multiple variables defined with the multi-identifier syntax.
 * TODO fragments semantic visibility
 */
public class DefinitionVariable extends Definition implements IStatement { 
	
	public static final ArrayView<DefVarFragment> NO_FRAGMENTS = ArrayView.create(new DefVarFragment[0]);
	
	public final Reference type;
	public final Initializer init;
	public final ArrayView<DefVarFragment> fragments;
	
	public DefinitionVariable(ProtoDefSymbol defId, Reference type, Initializer init,
		ArrayView<DefVarFragment> fragments) {
		this(defId, assertNotNull_(type), init, fragments, false);
	}
	
	protected DefinitionVariable(ProtoDefSymbol defId, Reference type, Initializer init,
		ArrayView<DefVarFragment> fragments, @SuppressWarnings("unused") boolean dummy)
	{
		super(defId);
		this.type = parentize(type);
		this.init = parentize(init);
		this.fragments = fragments != null ? parentize(fragments) : NO_FRAGMENTS;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DEFINITION_VARIABLE;
	}
	
	// TODO refactor this into own class?
	public static class DefinitionAutoVariable extends DefinitionVariable {
		
		public DefinitionAutoVariable(ProtoDefSymbol defId, Initializer init, 
			ArrayView<DefVarFragment> fragments) {
			super(defId, null, init, fragments, false);
		}
		
		@Override
		public ASTNodeTypes getNodeType() {
			return ASTNodeTypes.DEFINITION_AUTO_VARIABLE;
		}
		
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, init);
			
			TreeVisitor.acceptChildren(visitor, fragments);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Variable;
	}
	
	public IDefUnitReference getTypeReference() {
		return determineType();
	}
	
	private IDefUnitReference determineType() {
		if(type != null)
			return type;
		return NativeDefUnit.nullReference; // TODO: auto references
	}
	
	@Override
	public IScopeNode getMembersScope(IModuleResolver moduleResolver) {
		Collection<DefUnit> defunits = determineType().findTargetDefUnits(moduleResolver, true);
		if(defunits == null || defunits.isEmpty())
			return null;
		return defunits.iterator().next().getMembersScope(moduleResolver);
		//return defunit.getMembersScope();
	}
	
	@Deprecated
	private String getTypeString() {
		if(type != null)
			return type.toStringAsElement();
		return "auto";
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(type, " ");
		cp.append(defname);
		cp.append(" = ", init);
		for (DefVarFragment varFragment : fragments) {
			cp.append(", ", varFragment);
		}
		cp.append(";");
	}
	
	@Override
	public String toStringForHoverSignature() {
		String str = getTypeString() + " " + getName();
		return str;
	}
	
	@Override
	public String toStringForCodeCompletion() {
		return defname.toStringAsCode() + "   " + getTypeString() + " - " + getModuleScope().toStringAsElement();
	}
	
}