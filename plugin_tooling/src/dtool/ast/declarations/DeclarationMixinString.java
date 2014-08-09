package dtool.ast.declarations;

import java.util.Iterator;

import melnorme.utilbox.misc.IteratorUtil;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.expressions.ExpMixinString;
import dtool.ast.statements.IStatement;
import dtool.engine.common.INonScopedContainer;

/**
 * Parse exp contents as code (exp must resolve to a string).
 * http://dlang.org/module.html#MixinDeclaration
 */
public class DeclarationMixinString extends ASTNode implements INonScopedContainer, IDeclaration, IStatement {
	
	public final ExpMixinString exp;
	
	public DeclarationMixinString(ExpMixinString exp) {
		this.exp = parentize(exp);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECLARATION_MIXIN_STRING;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, exp);
	}
	
	@Override
	public Iterator<? extends ASTNode> getMembersIterator() {
		// TODO: parse the exp string
		return IteratorUtil.emptyIterator();
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(exp);
		cp.append(";");
	}
	
}