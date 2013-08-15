package dtool.ast.definitions;

import java.util.Iterator;

import melnorme.utilbox.misc.IteratorUtil;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.declarations.IDeclaration;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;
import dtool.resolver.INonScopedBlock;

/**
 * Declaration of a template mixin with no name:
 * http://dlang.org/template-mixin.html#TemplateMixinDeclaration
 * (without MixinIdentifier)
 */
public class DeclarationMixin extends ASTNode implements INonScopedBlock, IDeclaration, IStatement {
	
	public final Reference templateInstance;
	
	public DeclarationMixin(Reference templateInstance) {
		this.templateInstance = parentize(templateInstance);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECLARATION_MIXIN;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, templateInstance);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("mixin ");
		cp.append(templateInstance);
		cp.append(";");
	}
	
	@Override
	public Iterator<ASTNode> getMembersIterator() {
		return IteratorUtil.emptyIterator();
		// TODO: mixin container
		/*
		DefUnit defunit = type.findTargetDefUnit();
		if(defunit == null)
			return IteratorUtil.getEMPTY_ITERATOR();
		return (Iterator) defunit.getMembersScope().getMembersIterator();
		 */
	}
	
}