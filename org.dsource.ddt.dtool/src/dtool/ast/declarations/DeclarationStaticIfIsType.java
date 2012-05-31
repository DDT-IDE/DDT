package dtool.ast.declarations;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.misc.IteratorUtil;
import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.IsExp;
import descent.internal.compiler.parser.TOK;
import descent.internal.compiler.parser.ast.IASTNode;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.TokenInfo;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.ast.references.Reference;
import dtool.ast.references.ReferenceConverter;
import dtool.ast.statements.IStatement;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
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

		public IsTypeDefUnit(IdentifierExp ident) {
			super(ident);
			setSourceRange(ident);
		}
		
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

		public NodeList nodelist;
		
		public IsTypeScope(NodeList nodes) {
			this.nodelist = nodes;
			if (this.nodelist != null) {
				for (ASTNeoNode n : this.nodelist.nodes) {
					n.setParent(this);
				}
			}
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
	
	public IsTypeScope thendeclsScope;

	public Reference arg;
	public IsTypeDefUnit defUnit;
	public TOK tok;
	public Reference specType;
	
	public DeclarationStaticIfIsType(ASTDmdNode  elem, IsExp isExp, NodeList thendecls, NodeList elsedecls
			, ASTConversionContext convContext) {
		convertNode(elem);
		this.arg = ReferenceConverter.convertType(isExp.targ, convContext);
		this.defUnit = new IsTypeDefUnit(isExp.id);
		this.tok = isExp.tok;
		this.specType = ReferenceConverter.convertType(isExp.tspec, convContext);
		this.thendecls = thendecls;
		this.elsedecls = elsedecls;
		this.thendeclsScope = new IsTypeScope(thendecls);
		this.thendeclsScope.setSourceRange(isExp.getStartPos(), 
				elem.getEndPos() - isExp.getStartPos());
	}
	
	public DeclarationStaticIfIsType(Reference arg, DefSymbol id, Reference specType, Collection<IStatement> thenDecls, Collection<IStatement> elseDecls) {
		super(
			NodeList.createNodeList(thenDecls, false),
			NodeList.createNodeList(elseDecls, false)
		);
		
		this.arg = arg;
		if (this.arg != null)
			this.arg.setParent(this);
		
		this.defUnit = new IsTypeDefUnit(id);
		this.defUnit.setParent(this);
		
		this.specType = specType;
		if (this.specType != null)
			this.specType.setParent(this);
		
		this.thendeclsScope = new IsTypeScope(this.thendecls);
		this.thendeclsScope.setParent(this);
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
