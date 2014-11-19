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
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.INamedElementNode;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.INamedElementSemantics;
import melnorme.lang.tooling.engine.resolver.TypeSemantics;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.utilbox.collections.Indexable;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.DefinitionTemplate;
import dtool.ast.definitions.EArcheType;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.ReferenceResolver;

public class TemplateInstance extends DefUnit implements IConcreteNamedElement {
	
	public final Indexable<INamedElementNode> tplArguments;
	protected final DefinitionTemplate template;
	
	public TemplateInstance(DefinitionTemplate template, Indexable<INamedElementNode> tplArguments) {
		super(assertNotNull(template).defname.createCopy());
		this.template = template;
		this.tplArguments = tplArguments;
		
		setSourceRange(template.getSourceRange());
		setParsedStatus();
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendList("【", tplArguments, ",", "】 ");
		// TODO
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		// TODO: need to clone template
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DEFINITION_TEMPLATE;
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Template;
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public INamedElementSemantics getNodeSemantics() {
		return semantics;
	}
	
	protected final TypeSemantics semantics = new TypeSemantics(this) {
		
		@Override
		public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
			boolean isSequentialLookup = search.isSequentialLookup();
			/* FIXME: need to refactor this */
			ReferenceResolver.findInNodeList(search, tplArguments, isSequentialLookup);
		}
		
	};
	
}