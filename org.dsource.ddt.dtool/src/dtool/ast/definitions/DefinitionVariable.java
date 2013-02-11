package dtool.ast.definitions;

import java.util.Collection;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.expressions.Initializer;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;
import dtool.refmodel.IDefUnitReference;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;
import dtool.util.ArrayView;

/**
 * A variable definition. 
 * Optionally has multiple symbols defined with the multi-identifier syntax.
 * TODO fragments semantic visibility
 */
public class DefinitionVariable extends Definition implements IStatement { 
	
	public static ArrayView<DefinitionVarFragment> emptyFrags = ArrayView.create(new DefinitionVarFragment[0]);
	
	public final Reference type;
	public final Initializer init;
	public final ArrayView<DefinitionVarFragment> fragments;
	
	public DefinitionVariable(DefUnitTuple dudt, Reference type, Initializer init, 
		ArrayView<DefinitionVarFragment> fragments, SourceRange sourceRange) {
		super(dudt, null);
		this.type = parentize(type);
		this.init = parentize(init);
		this.fragments = fragments != null ? parentize(fragments) : emptyFrags;
		initSourceRange(sourceRange);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
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
	
	public Initializer getInitializer() {
		return init;
	}
	
	public Reference getTypeReference() {
		return type;
	}
	
	private IDefUnitReference determineType() {
		if(type != null)
			return type;
		return NativeDefUnit.nullReference;
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
		cp.appendNode(type, " ");
		cp.appendNode(defname);
		cp.appendNode(" = ", init);
		for (DefinitionVarFragment varFragment : fragments) {
			cp.appendNode(", ", varFragment);
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