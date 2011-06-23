package dtool.ast.statements;

import static melnorme.utilbox.core.CoreUtil.array;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.ScopeStatement;
import descent.internal.compiler.parser.ast.ASTNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.definitions.ArrayView;
import dtool.descentadapter.DescentASTConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

/**
 * A compound statement. Allways introduces a new Scope.
 */
public class BlockStatement extends Statement implements IScopeNode {
	
	public ArrayView<IStatement> statements;
	public boolean hasCurlyBraces; // syntax-structural?
	
	public BlockStatement(Collection<IStatement> statements, boolean hasCurlyBraces) {
		this.statements = ArrayView.create(ArrayUtil.createFrom(statements, IStatement.class)); 
		this.hasCurlyBraces = hasCurlyBraces;
	}
	
	public BlockStatement(descent.internal.compiler.parser.CompoundStatement elem, ASTConversionContext convContext) {
		convertNode(elem);
		this.statements = DescentASTConverter.convertManyToView(elem.statements, IStatement.class, convContext);
		
		for(@SuppressWarnings("unused")	IStatement decl : statements) {
			// just check class cast
		}
	}

	public BlockStatement(ScopeStatement elem, ASTConversionContext convContext) {
		convertNode(elem);
		if(elem.statement instanceof descent.internal.compiler.parser.CompoundStatement) {
			descent.internal.compiler.parser.CompoundStatement compoundSt = 
				(descent.internal.compiler.parser.CompoundStatement) elem.statement;
			this.statements = DescentASTConverter.convertManyToView(compoundSt.statements, IStatement.class, 
					convContext);
			this.hasCurlyBraces = true;
		} else {
			this.statements = DescentASTConverter.convertManyToView(array(elem.statement), IStatement.class, 
					convContext);
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
