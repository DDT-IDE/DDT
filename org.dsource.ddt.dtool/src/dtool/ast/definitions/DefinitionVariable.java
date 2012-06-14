package dtool.ast.definitions;

import java.util.Collection;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.PROT;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.expressions.Initializer;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;
import dtool.refmodel.IDefUnitReference;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;

/**
 * A definition of a variable
 */
public class DefinitionVariable extends Definition implements IStatement {
	
	public final Reference type;
	public final Initializer init;
	
	public DefinitionVariable(DefUnitDataTuple dudt, PROT prot, Reference type, Initializer init) {
		super(dudt, prot);
		this.type = parentize(type);
		this.init = parentize(init);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, init);
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
	
	private String getTypeString() {
		if(type != null)
			return type.toStringAsElement();
		return "auto";
	}
	
	@Override
	public String toStringForHoverSignature() {
		String str = getTypeString() + " " + getName();
		return str;
	}
	
	@Override
	public String toStringForCodeCompletion() {
		return defname.toStringAsElement() + "   " + getTypeString() + " - " + getModuleScope().toStringAsElement();
	}
	
}