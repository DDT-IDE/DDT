package dtool.ast.declarations;

import java.util.Iterator;

import melnorme.utilbox.misc.ChainedIterator;
import melnorme.utilbox.misc.IteratorUtil;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNeoNode;
import dtool.ast.DeclList;
import dtool.ast.definitions.Symbol;
import dtool.ast.statements.IStatement;
import dtool.refmodel.INonScopedBlock;

public abstract class AbstractConditionalDeclaration extends DeclarationAttrib implements IStatement, INonScopedBlock {
	
	// Note: value can be an integer or keyword
	public static class VersionSymbol extends Symbol {
		public VersionSymbol(String value) {
			super(value);
		}
	}
	
	public final ASTNeoNode elseBody;
	
	public AbstractConditionalDeclaration(AttribBodySyntax bodySyntax, ASTNeoNode thenDecls, ASTNeoNode elseDecls) {
		super(bodySyntax, thenDecls);
		this.elseBody = parentize(elseDecls);
	}
	
	@Override
	public Iterator<? extends ASTNeoNode> getMembersIterator() {
		if(body == null && elseBody == null)
			return IteratorUtil.getEMPTY_ITERATOR();
		if(elseBody == null)
			return getBodyIterator(body);
		if(body == null)
			return getBodyIterator(elseBody);
		
		return new ChainedIterator<ASTNeoNode>(getBodyIterator(body), getBodyIterator(elseBody)); 
	}
	
	public void toStringAsCodeBodyAndElseBody(ASTCodePrinter cp) {
		toStringAsCode_body(cp);
		if(elseBody != null) {
			cp.append("else ");
			cp.append(elseBody instanceof DeclList, "{\n");
			cp.appendNode(elseBody);
			cp.append(elseBody instanceof DeclList, "}");
		}
	}
	
}