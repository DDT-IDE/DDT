/*******************************************************************************
 * Copyright (c) 2012 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.expressions;

import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast.util.NodeVector;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.ErrorElement.NotAValueErrorElement;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.ExpSemantics;
import melnorme.lang.tooling.engine.resolver.TypeReferenceResult;
import melnorme.lang.tooling.engine.scoping.ResolutionLookup;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.references.Reference;

public class ExpCall extends Expression {
	
	public final Expression callee;
	public final NodeVector<Expression> args;
	
	public ExpCall(Expression callee, NodeVector<Expression> args) {
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
	protected CommonASTNode doCloneTree() {
		return new ExpCall(clone(callee), clone(args));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(callee);
		cp.appendNodeList("(", args, ", " , ")"); 
	}
	
	/* -----------------  ----------------- */
	
	@Override
	protected ExpSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new ExpSemantics(this, pickedElement) {
		
			@Override
			public TypeReferenceResult doCreateExpResolution() {
				INamedElement calleeElem = callee.resolveTypeOfUnderlyingValue_(context);
				if(calleeElem == null)
					return null;
				
				if (calleeElem instanceof DefinitionFunction) {
					DefinitionFunction defOpCallFunc = (DefinitionFunction) calleeElem;
					return resolveTypeReference(defOpCallFunc.retType);
				}
				
				if(calleeElem instanceof NotAValueErrorElement) {
					NotAValueErrorElement notAValueErrorElement = (NotAValueErrorElement) calleeElem;
					calleeElem = notAValueErrorElement.invalidElement; 
				}
				
				ResolutionLookup search = new ResolutionLookup("opCall", context);
				search.evaluateInMembersScope(calleeElem);
				INamedElement matchedElement = search.getMatchedElement();
				
				for (INamedElement possibleFunctionElement : Reference.resolveResultToCollection(matchedElement)) {
					if (possibleFunctionElement instanceof DefinitionFunction) {
						DefinitionFunction defOpCallFunc = (DefinitionFunction) possibleFunctionElement;
						return resolveTypeReference(defOpCallFunc.retType);
					}
				}
				return null;
			}
			
		};
	}
	
}