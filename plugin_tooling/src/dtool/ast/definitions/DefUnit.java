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
import melnorme.lang.tooling.ast.INamedElementNode;
import melnorme.lang.tooling.ast.ParserError;
import melnorme.lang.tooling.ast.SourceRange;
import melnorme.lang.tooling.ast.util.NodeUtil;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ElementDoc;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.context.ModuleFullName;
import melnorme.lang.tooling.engine.INamedElementSemantics;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import descent.core.ddoc.DeeDocAccessor;
import dtool.parser.DeeTokenSemantics;
import dtool.parser.common.Token;

/**
 * Abstract class for all AST elements that define a new named entity.
 */
public abstract class DefUnit extends ASTNode implements INamedElementNode {
	
	public static class ProtoDefSymbol {
		public final String name;
		public final SourceRange nameSourceRange;
		public final ParserError error;
		
		public ProtoDefSymbol(String name, SourceRange nameSourceRange, ParserError error) {
			this.name = name;
			this.nameSourceRange = nameSourceRange;
			this.error = error;
		}

		public boolean isMissing() {
			return error != null;
		}
		
		public int getStartPos() {
			return nameSourceRange.getStartPos();
		}
	}
	
	public final DefSymbol defname; // It may happen that this is not a child of DefUnit
	
	protected DefUnit(DefSymbol defname) {
		this(defname, true);
	}
	
	protected DefUnit(DefSymbol defname, boolean defIdIsChild) {
		assertNotNull(defname);
		this.defname = defIdIsChild ? parentize(defname) : defname;

	}
	
	public DefUnit(ProtoDefSymbol defIdTuple) {
		this(createDefId(defIdTuple));
	}
	
	public static DefSymbol createDefId(ProtoDefSymbol defIdTuple) {
		assertNotNull(defIdTuple);
		DefSymbol defId = new DefSymbol(defIdTuple.name);
		if(defIdTuple.nameSourceRange != null) {
			defId.setSourceRange(defIdTuple.nameSourceRange);
		}
		if(defIdTuple.error == null) {
			defId.setParsedStatus();
		} else {
			defId.setParsedStatusWithErrors(defIdTuple.error);
		}
		return defId;
	}
	
	/** Constructor for synthetic defunits. */
	protected DefUnit(String defName) {
		this(new ProtoDefSymbol(defName, null, null));
	}
	
	@Override
	public String getName() {
		return defname.name;
	}
	
	@Override
	public SourceRange getNameSourceRangeOrNull() {
		return defname.getSourceRange();
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
		INamedElement parentNamespace = getParentNamedElement();
		if(parentNamespace == null) {
			return getName();
		} else {
			return parentNamespace.getFullyQualifiedName() + "." + getName();
		}
	}
	
	@Override
	public ModuleFullName getModuleFullName() {
		return ModuleFullName.fromString(getModuleFullyQualifiedName());
	}
	
	@Override
	public INamedElement getParentNamedElement() {
		return NodeUtil.getParentDefUnit(this);
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
	public INamedElementSemantics getSemantics(ISemanticContext parentContext) {
		return (INamedElementSemantics) super.getSemantics(parentContext);
	}
	@Override
	public abstract INamedElementSemantics createSemantics(ISemanticContext context);
	
	@Override
	public final IConcreteNamedElement resolveConcreteElement(ISemanticContext context) {
		return getSemantics(context).resolveConcreteElement().result;
	}
	
	@Override
	public final void resolveSearchInMembersScope(CommonScopeLookup search) {
		getSemantics(search.modResolver).resolveSearchInMembersScope(search);
	}
	
	@Override
	public final INamedElement resolveTypeForValueContext(ISemanticContext context) {
		return getSemantics(context).resolveTypeForValueContext();
	}
	
}