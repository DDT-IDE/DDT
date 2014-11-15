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
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.bundles.IModuleResolver;
import melnorme.lang.tooling.engine.resolver.DefElementCommon;
import melnorme.lang.tooling.engine.scoping.IScopeProvider;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.collections.ArrayList2;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.DefinitionTemplate;
import dtool.ast.definitions.EArcheType;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.ReferenceResolver;

public class TemplateInstance extends DefUnit {
	
	public final ArrayList2<ASTNode> tplArguments; /* FIXME: make read only. */
	protected final DefinitionTemplate template;
	
	public TemplateInstance(DefinitionTemplate template, ArrayList2<ASTNode> tplArguments) {
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
	
	@Override
	public INamedElement resolveTypeForValueContext(IModuleResolver mr) {
		return DefElementCommon.returnError_ElementIsNotAValue(this);
	}
	
	@Override
	public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
		boolean isSequentialLookup = search.isSequentialLookup();
		/* FIXME: need to refactor this */
		ReferenceResolver.findInNodeList(search, tplArguments, isSequentialLookup);
	}
	
}