package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Collection;

import descent.core.ddoc.Ddoc;
import descent.core.ddoc.DeeDocAccessor;
import dtool.ast.ASTNode;
import dtool.ast.SourceRange;
import dtool.ast.references.CommonQualifiedReference;
import dtool.ast.util.NodeUtil;
import dtool.engine.modules.IModuleResolver;
import dtool.parser.DeeTokenSemantics;
import dtool.parser.ParserError;
import dtool.parser.Token;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.IResolvable;

/**
 * Abstract class for all AST elements that define a new named entity.
 */
public abstract class DefUnit extends ASTNode implements INamedElement {
	
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
	
	public boolean syntaxIsMissingName() {
		return getName().isEmpty();
	}
	
	public boolean availableInRegularNamespace() {
		return true;
	}
	
	@Override
	public boolean isLanguageIntrinsic() {
		return false;
	}
	
	@Override
	public abstract EArcheType getArcheType() ;
	
	@Override
	public String getExtendedName() {
		return getName();
	}
	
	@Override
	public String getFullyQualifiedName() {
		INamedElement parentNamespace = getParentElement();
		if(parentNamespace == null) {
			return getName();
		} else {
			return parentNamespace.getFullyQualifiedName() + "." + getName();
		}
	}
	
	@Override
	public INamedElement getParentElement() {
		return NodeUtil.getParentDefUnit(this);
	}
	
	@Override
	public DefUnit resolveDefUnit() {
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
	
	public Ddoc getDDoc() {
		return DeeDocAccessor.getDdocFromDocComments(getDocComments());
	}
	
	@Override
	public final Ddoc resolveDDoc() {
		return getDDoc();
	}
	
	@Override
	public abstract void resolveSearchInMembersScope(CommonDefUnitSearch search);
	
	
	public static void resolveSearchInReferredContainer(CommonDefUnitSearch search, IResolvable resolvable) {
		if(resolvable == null) {
			return;
		}
		
		IModuleResolver mr = search.getModuleResolver();
		Collection<INamedElement> containers = resolvable.findTargetDefElements(mr, true);
		CommonQualifiedReference.resolveSearchInMultipleContainers(containers, search);
	}
	
}