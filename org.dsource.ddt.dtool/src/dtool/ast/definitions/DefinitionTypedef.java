package dtool.ast.definitions;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.PROT;
import dtool.ast.IASTVisitor;
import dtool.ast.expressions.IInitializer;
import dtool.ast.expressions.Initializer;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;
import dtool.descentadapter.DefUnitTuple;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;

public class DefinitionTypedef extends Definition implements IStatement {
	
	public final Reference type;
	public final IInitializer initializer;
	
	public DefinitionTypedef(DefUnitTuple dudt, PROT prot, Reference type, IInitializer initializer) {
		super(dudt.commentsToToken(), dudt.defSymbol);
		initSourceRange(dudt.sourceRange);
		this.type = parentize(type);
		this.initializer = parentizeI(initializer);
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, initializer);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Typedef;
	}
	
	@Override
	public IScopeNode getMembersScope(IModuleResolver moduleResolver) {
		return type.getTargetScope(moduleResolver);
	}
	
	@Override
	public String toStringForCodeCompletion() {
		return getName() +" -> "+ type.toStringAsElement() +" - "+ getModuleScope().toStringAsElement();
	}
	
}