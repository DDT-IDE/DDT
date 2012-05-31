package dtool.ast.declarations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Collection;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.DVCondition;
import descent.internal.compiler.parser.DebugCondition;
import descent.internal.compiler.parser.VersionCondition;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.definitions.Symbol;
import dtool.ast.statements.IStatement;

public class DeclarationConditionalDV extends DeclarationConditional {
	
	public final Symbol ident;
	public final boolean isDebug;
	
	public DeclarationConditionalDV(ASTDmdNode elem, DVCondition condition, NodeList thendecls, NodeList elsedecls) {
		convertNode(elem);
		if(condition.ident != null) {
			this.ident = new Symbol(new String(condition.ident));
			this.ident.setSourceRange(condition);
		} else {
			ident = null;
		}
		isDebug = condition instanceof DebugCondition;
		if(!isDebug) {
			assertTrue(condition instanceof VersionCondition);
		}
		this.thendecls = thendecls; 
		this.elsedecls = elsedecls;
	}
	
	public DeclarationConditionalDV(boolean isDebug, Symbol id, Collection<IStatement> thenDecls,  Collection<IStatement> elseDecls) {
		super(
			NodeList.createNodeList(thenDecls, false),
			NodeList.createNodeList(elseDecls, false)
		);
				
		this.isDebug = isDebug;
		this.ident = id;
		if (this.ident != null)
			this.ident.setParent(this);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, NodeList.getNodes(thendecls));
			TreeVisitor.acceptChildren(visitor, NodeList.getNodes(elsedecls));
		}
		visitor.endVisit(this);
	}
	
	@Override
	public String toStringAsElement() {
		if(ident!= null) {
			return "["+ (isDebug?"debug":"version") + "("+ident.toStringAsElement()+")]";
		} else {
			return "["+ (isDebug?"debug":"version")+"()]";
		}
	}
}
