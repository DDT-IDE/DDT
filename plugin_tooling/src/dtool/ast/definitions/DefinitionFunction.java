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
package dtool.ast.definitions;

import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast.util.NodeVector;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.NamedElementSemantics;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.collections.ArrayView;
import dtool.ast.declarations.IDeclaration;
import dtool.ast.expressions.Expression;
import dtool.ast.references.RefTemplateInstance;
import dtool.ast.references.Reference;
import dtool.ast.statements.IFunctionBody;
import dtool.ast.statements.IStatement;
import dtool.parser.common.Token;

/**
 * A definition of a function.
 */
public class DefinitionFunction extends AbstractFunctionDefinition implements IDeclaration, IStatement, 
	IConcreteNamedElement {
	
	public final Reference retType;
	
	public DefinitionFunction(Token[] comments, Reference retType, DefSymbol defName,
		NodeVector<ITemplateParameter> tplParams, NodeVector<IFunctionParameter> fnParams,
		NodeVector<IFunctionAttribute> fnAttributes, Expression tplConstraint, IFunctionBody fnBody)
	{
		super(comments, defName, tplParams, fnParams, fnAttributes, tplConstraint, fnBody);
		this.retType = parentize(retType);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DEFINITION_FUNCTION;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, retType);
		visitChildren_common(visitor);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new DefinitionFunction(comments, clone(retType), clone(defName), clone(tplParams), clone(fnParams), 
			clone(fnAttributes), clone(tplConstraint), clone(fnBody));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(retType, " ");
		toStringAsCode_fromDefId(cp);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Function;
	}
	
	@Override
	public DefUnit cloneTemplateElement(final RefTemplateInstance templateRef) {
		return setParsedFromOther(
			new DefinitionFunction(comments, clone(retType), clone(defName), null, clone(fnParams), 
				clone(fnAttributes), null, clone(fnBody)) {
				
				@Override
				public String getExtendedName() {
					return getName() + templateRef.normalizedArgsToString() + toStringParametersForSignature();
				}
			}
			,this);
	}
	
	@Override
	public String getExtendedName() {
		return getName() + toStringParametersForSignature();
	}
	
	public String toStringParametersForSignature() {
		return toStringParametersForSignature(fnParams);
	}
	
	public static String toStringParametersForSignature(ArrayView<IFunctionParameter> params) {
		if(params == null) 
			return "";
		String strParams = "(";
		for (int i = 0; i < params.size(); i++) {
			if(i != 0)
				strParams += ", ";
			strParams += params.get(i).toStringForFunctionSignature();
		}
		return strParams + ")";
	}
	
	/* -----------------  ----------------- */
	
	@Override
	protected NamedElementSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new FunctionElementSemantics(this, pickedElement);
	}
	
	public static abstract class AbstractFunctionElementSemantics extends NamedElementSemantics {
		
		public AbstractFunctionElementSemantics(INamedElement element, PickedElement<?> pickedElement) {
			super(element, pickedElement);
		}
		
		@SuppressWarnings("unused")
		public static void resolveSearchInMembersScopeForFunction(CommonScopeLookup search, Reference retType) {
			// Do nothing, a function has no members scope
			// TODO: except for implicit function calls syntax, that needs to be implemented
		}
		
		@Override
		public INamedElement getTypeForValueContext_do() {
			// TODO implicit function call
			return null;
		}
		
	}
	
	public static class FunctionElementSemantics extends AbstractFunctionElementSemantics {
		
		protected final DefinitionFunction function;
		
		public FunctionElementSemantics(DefinitionFunction defFunction, PickedElement<?> pickedElement) {
			super(defFunction, pickedElement);
			this.function = defFunction;
		}
		
		@Override
		protected IConcreteNamedElement doResolveConcreteElement() {
			return function;
		}
		
		@Override
		public void resolveSearchInMembersScope(CommonScopeLookup search) {
			resolveSearchInMembersScopeForFunction(search, function.retType);
		}
		
		@Override
		public INamedElement getTypeForValueContext_do() {
			return function; // Not entirely, we need to extract the function type.
		}
	}
	
}