package dtool.ast.definitions;

import java.util.ArrayList;
import java.util.List;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.DeclList;
import dtool.ast.IASTVisitor;
import dtool.ast.expressions.Expression;
import dtool.ast.references.Reference;
import dtool.refmodel.IScope;
import dtool.refmodel.pluginadapters.IModuleResolver;
import dtool.util.ArrayView;

/**
 * A definition of a class aggregate.
 */
public class DefinitionClass extends DefinitionAggregate {
	
	public final ArrayView<Reference> baseClasses;
	
	public DefinitionClass(ProtoDefSymbol defId, ArrayView<TemplateParameter> tplParams,
		Expression tplConstraint, ArrayView<Reference> baseClasses, DeclList decls) {
		super(defId, tplParams, tplConstraint, decls);
		this.baseClasses = parentize(baseClasses);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DEFINITION_CLASS;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		acceptNodeChildren(visitor, children);
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		classLikeToStringAsCode(cp, "class ");
	}
	
	public void classLikeToStringAsCode(ASTCodePrinter cp, String keyword) {
		aggregateToStringAsCode(cp, keyword, false);
		cp.appendNodeList(": ", baseClasses, ",", " ");
		cp.appendNode("{\n", decls, "}");
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Class;
	}
	
	@Override
	protected void acceptNodeChildren(IASTVisitor visitor, boolean children) {
		if (children) {
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, tplParams);
			TreeVisitor.acceptChildren(visitor, tplConstraint);
			TreeVisitor.acceptChildren(visitor, baseClasses);
			TreeVisitor.acceptChildren(visitor, decls);
		}
	}
	
	@Override
	public List<IScope> getSuperScopes(IModuleResolver moduleResolver) {
		if(baseClasses == null || baseClasses.size() < 0)
			return null;
		
		List<IScope> scopes = new ArrayList<IScope>();
		for(Reference baseclass: baseClasses) {
			DefUnit defunit = baseclass.findTargetDefUnit(moduleResolver);
			if(defunit == null)
				continue;
			scopes.add(defunit.getMembersScope(moduleResolver));
		}
		return scopes;
		// TODO add Object super scope.
	}
	
}