package dtool.ast.definitions;

import java.util.ArrayList;
import java.util.List;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.PROT;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.refmodel.IScope;
import dtool.refmodel.pluginadapters.IModuleResolver;
import dtool.util.ArrayView;

/**
 * A definition of a class aggregate.
 */
public class DefinitionClass extends DefinitionAggregate {
	
	public final ArrayView<BaseClass> baseClasses;
	
	public DefinitionClass(DefUnitDataTuple dudt, PROT prot, ArrayView<TemplateParameter> tplParams,
			ArrayView<BaseClass> baseClasses, ArrayView<ASTNeoNode> members) {
		super(dudt, prot, tplParams, members);
		this.baseClasses = parentize(baseClasses);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		acceptNodeChildren(visitor, children);
		visitor.endVisit(this);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Class;
	}
	
	@Override
	protected void acceptNodeChildren(IASTNeoVisitor visitor, boolean children) {
		if (children) {
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, templateParams);
			TreeVisitor.acceptChildren(visitor, baseClasses);
			TreeVisitor.acceptChildren(visitor, members);
		}
	}
	
	@Override
	public List<IScope> getSuperScopes(IModuleResolver moduleResolver) {
		if(baseClasses == null || baseClasses.size() < 0)
			return null;
		
		List<IScope> scopes = new ArrayList<IScope>();
		for(BaseClass baseclass: baseClasses) {
			DefUnit defunit = baseclass.type.findTargetDefUnit(moduleResolver);
			if(defunit == null)
				continue;
			scopes.add(defunit.getMembersScope(moduleResolver));
		}
		return scopes;
		// TODO add Object super scope.
	}
	
}