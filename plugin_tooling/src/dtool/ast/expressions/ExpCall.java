package dtool.ast.expressions;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast.util.NodeListView;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.bundles.ISemanticContext;
import melnorme.lang.tooling.engine.resolver.IResolvableSemantics;
import melnorme.lang.tooling.engine.resolver.ResolvableSemantics;
import melnorme.lang.tooling.engine.scoping.ResolutionLookup;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.Module;

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
	
	/* -----------------  ----------------- */
	
	@Override
	public IResolvableSemantics getSemantics() {
		return semantics;
	}
	
	protected final IResolvableSemantics semantics = new ResolvableSemantics(this) {
		
		@Override
		public Collection<INamedElement> findTargetDefElements(ISemanticContext mr, boolean findOneOnly) {
			INamedElement calleeElem = callee.findTargetDefElement(mr);
			if(calleeElem == null)
				return null;
			
			if (calleeElem instanceof DefinitionFunction) {
				DefinitionFunction defOpCallFunc = (DefinitionFunction) calleeElem;
				INamedElement calleeResult = defOpCallFunc.findReturnTypeTargetDefUnit(mr);
				return Collections.singleton(calleeResult);
			}
			
			Module moduleNode = null;
			if(calleeElem instanceof ASTNode) {
				ASTNode astNode = (ASTNode) calleeElem;
				moduleNode = astNode.getModuleNode2();
			}
			if(moduleNode == null) {
				return null;
			}
			
			ResolutionLookup search = new ResolutionLookup("opCall", moduleNode, false, mr);
			calleeElem.resolveSearchInMembersScope(search);
			
			for (Iterator<INamedElement> iter = search.getMatchedElements().iterator(); iter.hasNext();) {
				INamedElement defOpCall = iter.next();
				if (defOpCall instanceof DefinitionFunction) {
					DefinitionFunction defOpCallFunc = (DefinitionFunction) defOpCall;
					INamedElement targetDefUnit = defOpCallFunc.findReturnTypeTargetDefUnit(mr);
					return Collections.singleton(targetDefUnit);
				}
			}
			return null;
		}
		
		@Override
		public Collection<INamedElement> resolveTypeOfUnderlyingValue(ISemanticContext mr) {
			return findTargetDefElements(mr, true); // TODO
		}
		
	};
	
}