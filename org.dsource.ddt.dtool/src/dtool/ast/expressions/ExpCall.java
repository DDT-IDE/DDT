package dtool.ast.expressions;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.NodeListView;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.DefinitionFunction;
import dtool.resolver.DefUnitSearch;
import dtool.resolver.ReferenceResolver;
import dtool.resolver.api.IModuleResolver;

public class ExpCall extends Expression {
	
	public final Expression callee;
	public final NodeListView<Expression> args;
	
	public ExpCall(Expression callee, NodeListView<Expression> args) {
		this.callee = parentize(callee);
		this.args = parentize(args);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_CALL;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, callee);
		acceptVisitor(visitor, args);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(callee);
		cp.appendNodeList("(", args, ", " , ")"); 
	}
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(IModuleResolver moduleResolver, boolean findFirstOnly) {
		DefUnit defUnit = callee.findTargetDefUnit(moduleResolver);
		if(defUnit == null)
			return null;		
		if (defUnit instanceof DefinitionFunction) {
			DefinitionFunction defOpCallFunc = (DefinitionFunction) defUnit;
			DefUnit targetDefUnit = defOpCallFunc.findReturnTypeTargetDefUnit(moduleResolver);
			return Collections.singleton(targetDefUnit);
		}
		
		DefUnitSearch search = new DefUnitSearch("opCall", null, false, moduleResolver);
		ReferenceResolver.findDefUnitInScope(defUnit.getMembersScope(moduleResolver), search);
		
		for (Iterator<DefUnit> iter = search.getMatchDefUnits().iterator(); iter.hasNext();) {
			DefUnit defOpCall = iter.next();
			if (defOpCall instanceof DefinitionFunction) {
				DefinitionFunction defOpCallFunc = (DefinitionFunction) defOpCall;
				DefUnit targetDefUnit = defOpCallFunc.findReturnTypeTargetDefUnit(moduleResolver);
				return Collections.singleton(targetDefUnit);
			}
		}
		return null;
	}
	
}