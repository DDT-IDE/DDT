package dtool.ast.statements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.ScopeStatement;
import descent.internal.compiler.parser.ast.ASTNode;
import dtool.ast.IASTNeoVisitor;
import dtool.descentadapter.DescentASTConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

/**
 * A compound statement. Allways introduces a new Scope.
 */
public class BlockStatement extends Statement implements IScopeNode {
	
	public List<IStatement> statements;
	public boolean hasCurlyBraces; // syntax-structural?

	public BlockStatement(Collection<IStatement> statements, boolean hasCurlyBraces) {
		this.statements = new ArrayList<IStatement>(statements); 
		this.hasCurlyBraces = hasCurlyBraces;
	}
	
	public BlockStatement(descent.internal.compiler.parser.CompoundStatement elem, ASTConversionContext convContext) {
		convertNode(elem);
		this.statements = DescentASTConverter.convertManyL(elem.statements, statements, convContext);
		
		for(@SuppressWarnings("unused")	IStatement decl : statements) {
			// just check class cast
		}
	}

	public BlockStatement(ScopeStatement elem, ASTConversionContext convContext) {
		convertNode(elem);
		if(elem.statement instanceof descent.internal.compiler.parser.CompoundStatement) {
			descent.internal.compiler.parser.CompoundStatement compoundSt = 
				(descent.internal.compiler.parser.CompoundStatement) elem.statement;
			this.statements = DescentASTConverter.convertManyL(compoundSt.statements, statements, convContext);
			this.hasCurlyBraces = true;
		} else {
			this.statements = DescentASTConverter.convertManyL(
					new ASTNode[] {elem.statement}, statements, convContext);
			setSourceRange(elem.statement);
		}
	}


	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, statements);
		}
		visitor.endVisit(this);
	}


	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Iterator<ASTNode> getMembersIterator() {
		return (Iterator) statements.iterator();
	}
	@Override
	public List<IScope> getSuperScopes() {
		return null;
	}
	@Override
	public boolean hasSequentialLookup() {
		return true;
	}
	
	//@Override
	/*public IScope getAdaptedScope() {
		return this;
	}*/

}
