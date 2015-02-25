/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.engine.analysis.templates;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;
import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.INamedElementNode;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.scoping.IScopeElement;
import melnorme.lang.tooling.engine.scoping.ScopeTraverser;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.collections.Indexable;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.ITemplatableElement;
import dtool.ast.references.RefTemplateInstance;

public class TemplateInstance extends ASTNode implements IScopeElement {
	
	public final RefTemplateInstance templateRef;
	public final ISemanticContext refContext;
	public final ITemplatableElement templateDef;
	
	public final Indexable<INamedElementNode> tplArguments;
	
	public final DefUnit instantiatedElement;
	
	public TemplateInstance(RefTemplateInstance templateRef, ISemanticContext refContext, 
			ITemplatableElement templateDef, Indexable<INamedElementNode> tplArguments) {
		super();
		this.templateRef = assertNotNull(templateRef);
		this.refContext = refContext;
		this.templateDef = assertNotNull(templateDef);
		assertTrue(templateDef instanceof ASTNode);
		
		this.tplArguments = parentize(assertNotNull(tplArguments));
		
		assertTrue(tplArguments.size() == templateDef.getTemplateParameters().size());
		
		for (INamedElementNode _node : tplArguments) {
			ASTNode node = (ASTNode) _node;
			node.setSourceRange(templateDef.getStartPos(), 0);
			node.setParsedStatus();
		}
		
		
		this.instantiatedElement = parentize(templateDef.cloneTemplateElement(templateRef));
		setParsedFromOther(this, templateDef.asNode());
		
		completeLocalAnalysisOnNodeTree();
		
		setParent(templateDef.asNode().getLexicalParent());
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, tplArguments);
		acceptVisitor(visitor, instantiatedElement);
	}
	
	@Override
	protected CommonASTNode doCloneTree() {
		// Because this is not actually a source element (an element that is parser),
		// this method is not reachable.
		throw assertUnreachable();
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DEFINITION_TEMPLATE;
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendList("@{", tplArguments, " ", "}");
		
		cp.append(instantiatedElement);
	}
	
	/* -----------------  ----------------- */ 
	
	@Override
	public INamedElement getModuleElement() {
		return templateDef.asNode().getModuleElement();
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public ScopeTraverser getScopeTraverser() {
		return new ScopeTraverser(tplArguments, true);
	}
	
}