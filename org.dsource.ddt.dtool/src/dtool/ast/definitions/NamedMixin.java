package dtool.ast.definitions;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.TemplateMixin;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.NeoSourceRange;
import dtool.ast.references.RefTemplateInstance;
import dtool.ast.statements.IStatement;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.refmodel.IScopeNode;


public class NamedMixin extends DefUnit implements IStatement {
	
	public final RefTemplateInstance type;
	
	public NamedMixin(TemplateMixin elem, RefTemplateInstance tplInstance, ASTConversionContext convContext, NeoSourceRange sourceRange) {
		super(elem, convContext);
		this.type = tplInstance;
		setSourceRange(sourceRange);
	}
	
//	public NamedMixin(RefTemplateInstance tplInstance, NeoSourceRange sourceRange) {
//		super(elem, convContext);
//		this.type = tplInstance;
//		if(sourceRange != null) {
//			setSourceRange(sourceRange);
//		}
//	}
	
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
