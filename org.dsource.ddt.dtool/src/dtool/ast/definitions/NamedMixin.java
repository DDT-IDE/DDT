package dtool.ast.definitions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.references.RefTemplateInstance;
import dtool.ast.statements.IStatement;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.refmodel.IScopeNode;

public class NamedMixin extends DefUnit implements IStatement {
	
	public final RefTemplateInstance type;
	
	public NamedMixin(DefUnitDataTuple dudt, RefTemplateInstance tplInstance, ASTConversionContext convContext, SourceRange sourceRange) {
		super(dudt);
		this.type = tplInstance; parentize(this.type);
		setSourceRange(sourceRange);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, type);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Mixin;
	}
	
	@Override
	public IScopeNode getMembersScope() {
		return type.getTargetScope();
	}
	
}
