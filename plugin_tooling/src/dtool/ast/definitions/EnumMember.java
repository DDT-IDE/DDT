/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.definitions;

import static dtool.util.NewUtils.assertCast;
import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.ILanguageElement;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.NamedElementSemantics;
import dtool.ast.definitions.DefinitionEnum.EnumBody;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.IInitializer;
import dtool.ast.references.Reference;
import dtool.engine.analysis.CommonDefVarSemantics;
import dtool.engine.analysis.IVarDefinitionLike;

public class EnumMember extends DefUnit implements IVarDefinitionLike {
	
	public final Reference type;
	public final Expression value;
	
	public EnumMember(Reference type, DefSymbol defName, Expression value) {
		super(defName);
		this.type = parentize(type);
		this.value = parentize(value);
	}
	
	@Override
	protected EnumBody getParent_Concrete() {
		return assertCast(parent, EnumBody.class);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.ENUM_MEMBER;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, type);
		acceptVisitor(visitor, defName);
		acceptVisitor(visitor, value);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new EnumMember(clone(type), clone(defName), clone(value));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(type, " ");
		cp.append(defName);
		cp.append(" = ", value);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.EnumMember;
	}
	
	protected Reference getEffectiveTypeReference() {
		return type != null ? type : getEnumParentType();
	}
	
	public Reference getEnumParentType() {
		EnumBody enumBody = getParent_Concrete();
		ILanguageElement parentEnum = enumBody.getLexicalParent();
		if(parentEnum instanceof DeclarationEnum) {
			DeclarationEnum declarationEnum = (DeclarationEnum) parentEnum;
			return declarationEnum.type; 
		}
		if(parentEnum instanceof DefinitionEnum) {
			DefinitionEnum definitionEnum = (DefinitionEnum) parentEnum;
			return definitionEnum.type; 
		} 
		return null;
	}
	
	/* -----------------  ----------------- */
	
	@Override
	protected NamedElementSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new CommonDefVarSemantics(this, pickedElement) {
			
			@Override
			protected Reference getTypeReference() {
				return getEffectiveTypeReference();
			}
		};
	}
	
	@Override
	public Reference getDeclaredType() {
		return type;
	}
	
	@Override
	public IInitializer getDeclaredInitializer() {
		return value;
	}
	
}