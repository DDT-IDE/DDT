/*******************************************************************************
 * Copyright (c) 2012, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.expressions;

import java.util.Collection;

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast.util.NodeListView;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.PickedElement;
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
	protected ResolvableSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new ResolvableSemantics(this, pickedElement) {
		
		@Override
		public INamedElement doResolveTargetElement() {
			// TODO: should use #resolveTypeOfUnderlyingValue():
			INamedElement calleeElem = callee.resolveTargetElement(context);
			if(calleeElem == null)
				return null;
			
			if (calleeElem instanceof DefinitionFunction) {
				DefinitionFunction defOpCallFunc = (DefinitionFunction) calleeElem;
				return defOpCallFunc.findReturnTypeTargetDefUnit(context);
			}
			
			Module moduleNode = null;
			if(calleeElem instanceof ASTNode) {
				ASTNode astNode = (ASTNode) calleeElem;
				moduleNode = astNode.getModuleNode_();
			}
			if(moduleNode == null) {
				return null;
			}
			
			ResolutionLookup search = new ResolutionLookup("opCall", context);
			search.evaluateInMembersScope(calleeElem);
			INamedElement matchedElement = search.getMatchedElement();
			
			for (INamedElement possibleFunctionElement : Resolvable.resolveResultToCollection(matchedElement)) {
				if (possibleFunctionElement instanceof DefinitionFunction) {
					DefinitionFunction defOpCallFunc = (DefinitionFunction) possibleFunctionElement;
					return defOpCallFunc.findReturnTypeTargetDefUnit(context);
				}
			}
			return null;
		}
		
		@Override
		public Collection<INamedElement> resolveTypeOfUnderlyingValue() {
			return resultToColl(doResolveTargetElement()); // TODO
		}
		
	};
	}
	
}