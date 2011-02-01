package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.ChainedIterator;
import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.TemplateDeclaration;
import descent.internal.compiler.parser.ast.IASTNode;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.declarations.Declaration;
import dtool.ast.statements.IStatement;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

/**
 * Note, ATM only valid as a statement in the shorthand syntax for an eponymous template, like class(T) { ...
 */
public class DefinitionTemplate extends Definition implements IScopeNode, IStatement {

	public final TemplateParameter[] templateParams; 
	public final ASTNeoNode[] decls;
	public final boolean wrapper;

	
	public DefinitionTemplate(TemplateDeclaration elem, ASTConversionContext convContext) {
		super(elem, convContext);
		this.decls = Declaration.convertMany(elem.members, convContext);
		this.templateParams = TemplateParameter.convertMany(elem.parameters, convContext);
		this.wrapper = elem.wrapper;
		if(wrapper) {
			assertTrue(decls.length == 1);
		}
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, templateParams);
			TreeVisitor.acceptChildren(visitor, decls);
		}
		visitor.endVisit(this);
	}

	@Override
	public EArcheType getArcheType() {
		return EArcheType.Template;
	}

	@Override
	public IScopeNode getMembersScope() {
		return this;
	}
	
	@Override
	public List<IScope> getSuperScopes() {
		// TODO: template super scope
		return null;
	}
	
	@Override
	public boolean hasSequentialLookup() {
		return false;
	}

	@Override
	public Iterator<? extends IASTNode> getMembersIterator() {
		// TODO: check if in a template invocation
		if(wrapper) {
			// Go straight to decls member's members
			IScopeNode scope = ((DefUnit)decls[0]).getMembersScope();
			Iterator<? extends IASTNode> tplIter = Arrays.asList(templateParams).iterator();
			return ChainedIterator.create(tplIter, scope.getMembersIterator());
		}
		ASTNeoNode[] newar = ArrayUtil.concat(templateParams, decls, ASTNeoNode.class);
		return Arrays.asList(newar).iterator();
/*		List<ASTNode> list = new ArrayList<ASTNode>(decls.length + templateParams.length);
		list.addAll(Arrays.asList(decls));
		list.addAll(Arrays.asList(templateParams));
		return 	list.iterator();*/
	}

}
