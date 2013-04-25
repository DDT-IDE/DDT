package dtool.ast.declarations;

import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.misc.ChainedIterator;
import melnorme.utilbox.misc.IteratorUtil;
import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.TOK;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTVisitor;
import dtool.ast.NodeList;
import dtool.ast.SourceRange;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;
import dtool.refmodel.INonScopedBlock;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;

/**
 * Declaration of a static if, with an is type condition, which creates
 * a new DefUnit.
 * In this DeclarationConditional the thendecls are not a direct children
 * of this node. The direct children is an IsTypeScope, which in turn is the 
 * parent of the the thendecls. This is so that the thendecls can see the
 * DefUnit of the node. However, the {@link #getMembersIterator()} will 
 * still return thendecls + elsedecls as normal, bypassing the IsTypeScope.
 */
public class DeclarationStaticIfIsType extends ASTNeoNode implements IStatement, INonScopedBlock {
	
	public class IsTypeDefUnit extends DefUnit {
		
		public IsTypeDefUnit(String id, SourceRange idSourceRange) {
			super(id, idSourceRange, null, idSourceRange, null);
			setParent(DeclarationStaticIfIsType.this);
		}
		
		@Override
		public EArcheType getArcheType() {
			return EArcheType.Alias;
		}
		
		@Override
		public IScopeNode getMembersScope(IModuleResolver moduleResolver) {
			if(specType != null)
				return specType.getTargetScope(moduleResolver);
			else
				return arg.getTargetScope(moduleResolver);
		}
		
		@Override
		public void accept0(IASTVisitor visitor) {
			if (visitor.visit(this)) {	
			}
			visitor.endVisit(this);
		}
	}
	
	/** This is a special scope, where the IsTypeDefUnit is available. */
	public class IsTypeScope extends ASTNeoNode implements IScopeNode {
		
		public final NodeList nodelist;
		
		public IsTypeScope(NodeList nodes) {
			this.nodelist = NodeList.parentizeNodeList(nodes, this);
			setParent(DeclarationStaticIfIsType.this);
		}
		
		@Override
		public void accept0(IASTVisitor visitor) {
			if (visitor.visit(this)) {
				TreeVisitor.acceptChildren(visitor, NodeList.getNodes(nodelist));
			}
			visitor.endVisit(this);
		}
		
		@Override
		public Iterator<? extends ASTNeoNode> getMembersIterator(IModuleResolver moduleResolver) {
			return IteratorUtil.singletonIterator(defUnit);
		}
		
		@Override
		public List<IScope> getSuperScopes(IModuleResolver moduleResolver) {
			return null;
		}
		
		@Override
		public boolean hasSequentialLookup() {
			return false;
		}
		
	}
	
	public final Reference arg;
	public final IsTypeDefUnit defUnit;
	public final TOK tok;
	public final Reference specType;
	public final IsTypeScope thenScope;
	public final NodeList elseDecls;
	
	public DeclarationStaticIfIsType(Reference arg, String id, SourceRange idSourceRange, TOK tok, Reference specType,
			NodeList thenDecls, NodeList elseDecls, SourceRange innerRange) {
		this.arg = arg; parentize(this.arg);
		this.defUnit = new IsTypeDefUnit(id, idSourceRange);
		this.tok = tok;
		this.specType = parentize(specType);
		this.thenScope = new IsTypeScope(thenDecls);
		this.thenScope.setSourceRange(innerRange);
		this.elseDecls = NodeList.parentizeNodeList(elseDecls, this);
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, arg);
			TreeVisitor.acceptChildren(visitor, defUnit);
			TreeVisitor.acceptChildren(visitor, specType);
			TreeVisitor.acceptChildren(visitor, thenScope);
			TreeVisitor.acceptChildren(visitor, NodeList.getNodes(elseDecls));
		}
		visitor.endVisit(this);
	}
	
	@Override
	public Iterator<ASTNeoNode> getMembersIterator() {
		if(elseDecls == null)
			return thenScope.nodelist.getNodesIterator();
		
		return new ChainedIterator<ASTNeoNode>(thenScope.nodelist.getNodesIterator(), elseDecls.getNodesIterator()); 
	}
	
}