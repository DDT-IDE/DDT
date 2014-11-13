package dtool.ast.expressions;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast.util.NodeListView;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.Module;
import dtool.engine.common.IDeeNamedElement;
import dtool.engine.modules.IModuleResolver;
import dtool.resolver.DefUnitSearch;

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
	public Collection<IDeeNamedElement> findTargetDefElements(IModuleResolver moduleResolver, boolean findFirstOnly) {
		IDeeNamedElement calleeElem = callee.findTargetDefElement(moduleResolver);
		if(calleeElem == null)
			return null;		
		if (calleeElem instanceof DefinitionFunction) {
			DefinitionFunction defOpCallFunc = (DefinitionFunction) calleeElem;
			IDeeNamedElement calleeResult = defOpCallFunc.findReturnTypeTargetDefUnit(moduleResolver);
			return Collections.singleton(calleeResult);
		}
		
		Module moduleNode = null;
		if(calleeElem instanceof ASTNode) {
			ASTNode astNode = (ASTNode) calleeElem;
			moduleNode = astNode.getModuleNode();
		}
		if(moduleNode == null) {
			return null;
		}
		
		DefUnitSearch search = new DefUnitSearch("opCall", moduleNode, false, moduleResolver);
		calleeElem.resolveSearchInMembersScope(search);
		
		for (Iterator<IDeeNamedElement> iter = search.getMatchedElements().iterator(); iter.hasNext();) {
			IDeeNamedElement defOpCall = iter.next();
			if (defOpCall instanceof DefinitionFunction) {
				DefinitionFunction defOpCallFunc = (DefinitionFunction) defOpCall;
				IDeeNamedElement targetDefUnit = defOpCallFunc.findReturnTypeTargetDefUnit(moduleResolver);
				return Collections.singleton(targetDefUnit);
			}
		}
		return null;
	}
	
}