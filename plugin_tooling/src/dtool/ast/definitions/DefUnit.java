/*******************************************************************************
 * Copyright (c) 2010, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.tooling.ast.CommonLanguageElement;
import melnorme.lang.tooling.ast.INamedElementNode;
import melnorme.lang.tooling.ast.SourceRange;
import melnorme.lang.tooling.ast.util.NodeElementUtil;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ElementDoc;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.resolver.INamedElementSemanticData;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import descent.core.ddoc.DeeDocAccessor;
import dtool.parser.DeeTokenSemantics;
import dtool.parser.ProtoDefSymbol;
import dtool.parser.common.Token;

/**
 * Abstract class for all AST elements that define a new named entity.
 */
public abstract class DefUnit extends ASTNode implements INamedElementNode {
	
	public final DefSymbol defName; // It may happen that this is not a child of DefUnit
	
	protected DefUnit(DefSymbol defname) {
		this(defname, true);
	}
	
	protected DefUnit(DefSymbol defname, boolean defIdIsChild) {
		assertNotNull(defname);
		this.defName = defIdIsChild ? parentize(defname) : defname;

	}
	
	/** Constructor for synthetic defunits. */
	protected DefUnit(String defName) {
		this(new ProtoDefSymbol(defName, null, null).createDefId());
	}
	
	@Override
	public String getName() {
		return defName.name;
	}
	
	@Override
	public SourceRange getNameSourceRangeOrNull() {
		return defName.getSourceRange();
	}
	
	public boolean syntaxIsMissingName() {
		return getName().isEmpty();
	}
	
	@Override
	public String getNameInRegularNamespace() {
		return getName();
	}
	
	@Override
	public abstract EArcheType getArcheType() ;
	
	@Override
	public String getExtendedName() {
		return getName();
	}
	
	@Override
	public String getFullyQualifiedName() {
		return CommonLanguageElement.getFullyQualifiedName(this);
	}
	
	@Override
	public INamedElement getParentNamespace() {
		return NodeElementUtil.getOuterNamedElement(this);
	}
	
	@Override
	public DefUnit resolveUnderlyingNode() {
		return this;
	}
	
	/** @return the comments that define the DDoc for this defUnit. Can be null  */
	public Token[] getDocComments() {
		return null;
	}
	public void getDocComments_$invariant() {
		for (Token token : getDocComments()) {
			assertTrue(DeeTokenSemantics.tokenIsDocComment(token));
		}
	}
	
	public ElementDoc getDDoc() {
		return DeeDocAccessor.getDdocFromDocComments(getDocComments());
	}
	
	@Override
	public final ElementDoc resolveDDoc() {
		return getDDoc();
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public INamedElementSemanticData getSemantics(ISemanticContext parentContext) {
		return (INamedElementSemanticData) super.getSemantics(parentContext);
	}
	@Override
	protected abstract INamedElementSemanticData doCreateSemantics(PickedElement<?> pickedElement);
	
	@Override
	public final IConcreteNamedElement resolveConcreteElement(ISemanticContext context) {
		return getSemantics(context).resolveConcreteElement().result;
	}
	
	@Override
	public final void resolveSearchInMembersScope(CommonScopeLookup search) {
		getSemantics(search.context).resolveSearchInMembersScope(search);
	}
	
}