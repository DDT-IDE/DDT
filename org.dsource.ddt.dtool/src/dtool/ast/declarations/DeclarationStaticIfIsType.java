package dtool.ast.declarations;

import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.misc.IteratorUtil;
import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.TOK;
import descent.internal.compiler.parser.ast.IASTNode;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.ast.references.Reference;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

/**
 * Declaration of a static if, with an is type condition, which creates
 * a new DefUnit.
 * In this DeclarationConditional the thendecls are not a direct children
 * of this node. The direct children is an IsTypeScope, which in turn is the 
 * parent of the the thendecls. This is so that the thendecls can see the
 * DefUnit of the node. However, the {@link #getMembersIterator()} will 
 * still return thendecls + elsedecls as normal, bypassing the IsTypeScope.
 */
public class DeclarationStaticIfIsType extends DeclarationConditional {

	public class IsTypeDefUnit extends DefUnit {

		public IsTypeDefUnit(DefSymbol id) {
			super(
				new SourceRange(id.getOffset(), id.getLength()),
				id.name,
				new SourceRange(id.getOffset(), id.getLength()),
				null
			);
		}

		@Override
		public EArcheType getArcheType() {
			return EArcheType.Alias;
		}

		@Override
		public IScopeNode getMembersScope() {
			if(specType != null)
				return specType.getTargetScope();
			else
				return arg.getTargetScope();
		}

		@Override
		public void accept0(IASTNeoVisitor visitor) {
			if (visitor.visit(this)) {	
			}
			visitor.endVisit(this);
		}
	}
	
	/** This is a special scope, where the IsTypeDefUnit is available. */
	public class IsTypeScope extends ASTNeoNode implements IScopeNode {
		public final NodeList nodelist;
		
		public IsTypeScope(NodeList nodes, SourceRange sourceRange) {
			initSourceRange(sourceRange);
			this.nodelist = nodes; parentize(this.nodelist);
		}

		@Override
		public void accept0(IASTNeoVisitor visitor) {
			if (visitor.visit(this)) {
				TreeVisitor.acceptChildren(visitor, NodeList.getNodes(nodelist));
			}
			visitor.endVisit(this);
		}
		
		@Override
		public Iterator<? extends IASTNode> getMembersIterator() {
			return IteratorUtil.singletonIterator(defUnit);
		}
		
		@Override
		public List<IScope> getSuperScopes() {
			return null;
		}
		
		@Override
		public boolean hasSequentialLookup() {
			return false;
		}
		
	}
	
	public final IsTypeScope thendeclsScope;

	public final Reference arg;
	public final IsTypeDefUnit defUnit;
	public final TOK tok;
	public final Reference specType;
	
	public DeclarationStaticIfIsType(Reference arg, DefSymbol id, TOK tok, Reference specType, NodeList thenDecls, NodeList elseDecls, SourceRange innerRange, SourceRange sourceRange) {
		super(thenDecls, elseDecls, sourceRange);
		this.arg = arg; parentize(this.arg);
		id.setParent(this); this.defUnit = new IsTypeDefUnit(id); parentize(this.defUnit);
		this.tok = tok;
		this.specType = specType; parentize(this.specType);
		this.thendeclsScope = new IsTypeScope(this.thendecls, innerRange); parentize(this.thendeclsScope);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, arg);
			TreeVisitor.acceptChildren(visitor, defUnit);
			TreeVisitor.acceptChildren(visitor, specType);
			TreeVisitor.acceptChildren(visitor, thendeclsScope);
			TreeVisitor.acceptChildren(visitor, NodeList.getNodes(elsedecls));
		}
		visitor.endVisit(this);
	}

}
