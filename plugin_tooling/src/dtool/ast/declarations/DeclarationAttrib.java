/*******************************************************************************
 * Copyright (c) 2013 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.declarations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.lang.tooling.EProtection;
import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast.util.NodeList;
import melnorme.lang.tooling.ast.util.NodeVector;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.scoping.INonScopedContainer;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.misc.IteratorUtil;
import dtool.ast.definitions.CommonDefinition;
import dtool.ast.statements.IStatement;

/**
 * Attribute declarations
 *  
 * Technicaly DMD doesn't accept certain attributes as statements (such as protection, align), 
 * but structurally we allow it, even though a syntax or semantic error may still be issued.
 */
public class DeclarationAttrib extends ASTNode implements INonScopedContainer, IDeclaration, IStatement {
	
	public static enum AttribBodySyntax { SINGLE_DECL, BRACE_BLOCK, COLON }
	
	public final NodeVector<Attribute> attributes;
	public final AttribBodySyntax bodySyntax;
	public final ASTNode body; // Note: can be DeclList
	
	public DeclarationAttrib(NodeVector<Attribute> attributes, AttribBodySyntax bodySyntax, ASTNode bodyDecls) {
		this.attributes = parentize(assertNotNull(attributes));
		this.bodySyntax = assertNotNull(bodySyntax);
		this.body = parentize(bodyDecls);
		
		localAnalysis();
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECLARATION_ATTRIB;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, attributes);
		acceptVisitor(visitor, body);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		return new DeclarationAttrib(clone(attributes), bodySyntax, clone(body));
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendList(attributes, " ", true);
		cp.append(bodySyntax == AttribBodySyntax.COLON, " :\n");
		cp.append(body);
	}
	
	@Override
	public Iterable<? extends IASTNode> getMembersIterable() {
		return getBodyIterable(body);
	}
	
	public static Iterable<ASTNode> getBodyIterable(ASTNode body) {
		if(body == null) {
			return IteratorUtil.<ASTNode>emptyIterable();
		}
		if(body instanceof NodeList<?>) {
			return ((NodeList<?>) body).nodes.upcastTypeParameter();
		}
		// TODO save body node collection
		return ArrayList2.create(body);
	}
	
	/** 
	 * If this declaration attrib contains only a single declaration, return it, otherwise return null 
	 */
	public IDeclaration getSingleDeclaration() {
		if(bodySyntax != AttribBodySyntax.SINGLE_DECL) {
			return null;
		}
		if(body instanceof IDeclaration) {
			return (IDeclaration) body;
		}
		return null;
	}
	
	protected void localAnalysis() {
		for (Attribute attribute : attributes) {
			if(attribute instanceof AttribBasic) {
				AttribBasic attribBasic = (AttribBasic) attribute;
				applyBasicAttributes(attribBasic, this);			
			}
		}
		for (int ix = attributes.size() - 1; ix >= 0; ix--) {
			Attribute attribute = attributes.get(ix);
			if(attribute instanceof AttribProtection) {
				AttribProtection attribProtection = (AttribProtection) attribute;
				applyProtectionAttributes(attribProtection.protection, this);
				break; // last atribute takes precedence
			} 
		}
	}
	
	// TODO have CommonDefinition fetch attributes upwards,
	// instead of the other way around
	protected void applyBasicAttributes(AttribBasic attribute, INonScopedContainer block) {
		
		for (IASTNode node : block.getMembersIterable()) {
			
			if(node instanceof CommonDefinition) {
				CommonDefinition def = (CommonDefinition) node;
				def.setAttribute(attribute);
			} else if(node instanceof INonScopedContainer) {
				applyBasicAttributes(attribute, (INonScopedContainer) node);
			}
		}
	}
	
	protected void applyProtectionAttributes(EProtection protection, INonScopedContainer block) {
		for (IASTNode descendantNode : block.getMembersIterable()) {
			
			if(anotherProtectionAttribPresent(descendantNode)) {
				continue; // Do not descend, other attrib takes precedence
			}
			if(descendantNode instanceof CommonDefinition) {
				CommonDefinition def = (CommonDefinition) descendantNode;
				def.setProtection(protection);
			} else if(descendantNode instanceof DeclarationImport && protection == EProtection.PUBLIC) {
				DeclarationImport declImport = (DeclarationImport) descendantNode;
				declImport.isPublicImport = true;
			} else if(descendantNode instanceof INonScopedContainer) {
				applyProtectionAttributes(protection, (INonScopedContainer) descendantNode);
			}
		}
	}
	
	public boolean anotherProtectionAttribPresent(IASTNode node) {
		if(node instanceof DeclarationAttrib) {
			DeclarationAttrib declAttrib = (DeclarationAttrib) node;
			for (Attribute attrib : declAttrib.attributes) {
				if(attrib instanceof AttribProtection) 
					return true;
			}
		}
		return false;
	}
	
}