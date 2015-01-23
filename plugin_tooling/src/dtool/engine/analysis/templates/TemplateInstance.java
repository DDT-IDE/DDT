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
import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;
import melnorme.lang.tooling.ast.CommonASTNode;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.INamedElementNode;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast.util.NodeElementUtil;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.NamedElementSemantics;
import melnorme.lang.tooling.engine.resolver.NonValueConcreteElementSemantics;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.engine.scoping.IScopeElement;
import melnorme.lang.tooling.engine.scoping.ScopeTraverser;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.collections.Indexable;
import dtool.ast.declarations.DeclBlock;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.DefinitionTemplate;
import dtool.ast.definitions.EArcheType;
import dtool.ast.references.RefTemplateInstance;

public class TemplateInstance extends DefUnit implements IConcreteNamedElement, IScopeElement {
	
	public final RefTemplateInstance templateRef;
	public final ISemanticContext context;
	public final DefinitionTemplate templateDef;
	
	public final Indexable<INamedElementNode> tplArguments;
	public final DeclBlock body;
	
	public TemplateInstance(RefTemplateInstance templateRef, ISemanticContext context, 
			DefinitionTemplate templateDef, Indexable<INamedElementNode> tplArguments) {
		super(assertNotNull(templateDef).defName.createCopy());
		this.templateRef = assertNotNull(templateRef);
		this.context = context;
		this.templateDef = assertNotNull(templateDef);
		
		this.tplArguments = parentize(assertNotNull(tplArguments));
		this.body = parentize(clone(templateDef.decls));
		
		setSourceRange(templateDef.getSourceRange());
		setParsedStatus();
		completeLocalAnalysisOnNodeTree();
		
		setParent(templateDef);
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, defName);
		acceptVisitor(visitor, tplArguments);
		acceptVisitor(visitor, body);
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
		cp.append(getExtendedName());
		cp.appendList("{", tplArguments, " ", "}");
		cp.append(body);
	}
	
	/* -----------------  ----------------- */ 
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Template;
	}
	
	/** return null since this element cannot be referenced to directly by name. 
	 * Only through {@link RefTemplateInstance} 
	 */
	@Override
	public String getNameInRegularNamespace() {
		return null; 
	}
	
	@Override
	public String getExtendedName() {
		return getName() + templateRef.normalizedArgsToString();
	}
	
	@Override
	public INamedElement getParentNamespace() {
		return NodeElementUtil.getOuterNamedElement(templateDef);
	}
	
	@Override
	public INamedElement getModuleElement() {
		return templateDef.getModuleElement();
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public ScopeTraverser getScopeTraverser() {
		return new ScopeTraverser(tplArguments, true);
	}
	
	@Override
	public NamedElementSemantics doCreateSemantics(PickedElement<?> pickedElement) {
		return new NonValueConcreteElementSemantics(this, pickedElement) {
			
			@Override
			public void resolveSearchInMembersScope(CommonScopeLookup search) {
				search.evaluateScope(body);
			}
			
		};
	}
	
}