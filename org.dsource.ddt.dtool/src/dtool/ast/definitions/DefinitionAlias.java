package dtool.ast.definitions;


import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.AliasDeclaration;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;
import dtool.descentadapter.DescentASTConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.refmodel.IScopeNode;

/**
 * A definition of an alias.
 */
public class DefinitionAlias extends Definition implements IStatement {
	
	public Reference target;
	
	public DefinitionAlias(AliasDeclaration elem, ASTConversionContext convContext) {
		super(elem, convContext);
		target = (Reference) DescentASTConverter.convertElem(elem.type, convContext);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Alias;
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, target);
			TreeVisitor.acceptChildren(visitor, defname);
		}
		visitor.endVisit(this);
	}

	@Override
	public IScopeNode getMembersScope() {
		return target.getTargetScope();
	}
	
	
	@Override
	public String toStringForCodeCompletion() {
		return getName() + " -> " + target.toStringAsElement();
	}
	
}