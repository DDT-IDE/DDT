package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.tooling.ast.INamedElementNode;
import melnorme.lang.tooling.ast.SourceRange;
import melnorme.lang.tooling.ast.util.NodeUtil;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.bundles.IModuleResolver;
import melnorme.lang.tooling.bundles.ModuleFullName;
import melnorme.lang.tooling.engine.INamedElementSemantics;
import melnorme.lang.tooling.engine.resolver.IResolvable;
import melnorme.lang.tooling.engine.resolver.NullElementSemantics;
import melnorme.lang.tooling.engine.resolver.TypeSemanticsHelper;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import descent.core.ddoc.Ddoc;
import descent.core.ddoc.DeeDocAccessor;
import dtool.parser.DeeTokenSemantics;
import dtool.parser.ParserError;
import dtool.parser.common.Token;
import dtool.resolver.CommonDefUnitSearch;

/**
 * Abstract class for all AST elements that define a new named entity.
 */
public abstract class DefUnit extends ASTNode implements INamedElement, INamedElementNode {
	
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
	public ModuleFullName getModuleFullName() {
		return ModuleFullName.fromString(getModuleFullyQualifiedName());
	}
	
	@Override
	public INamedElement getParentElement() {
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
	
	public Ddoc getDDoc() {
		return DeeDocAccessor.getDdocFromDocComments(getDocComments());
	}
	
	@Override
	public final Ddoc resolveDDoc() {
		return getDDoc();
	}
	
	/* -----------------  ----------------- */
	
	protected static final TypeSemanticsHelper typeSemantics = new TypeSemanticsHelper(); 
	
	@Override
	public INamedElementSemantics getNodeSemantics() {
		/* FIXME: remove default implementation, implement in subclasses */
		return new NullElementSemantics();
	}
	
	@Override
	public final IConcreteNamedElement resolveConcreteElement() {
		return getNodeSemantics().resolveConcreteElement();
	}
	
	/* FIXME: TODO make these final */
	
	@Override
	public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
		getNodeSemantics().resolveSearchInMembersScope(search);
	}
	
	@Override
	public INamedElement resolveTypeForValueContext(IModuleResolver mr) {
		return getNodeSemantics().resolveTypeForValueContext(mr);
	}
	
	public static void resolveSearchInReferredContainer(CommonDefUnitSearch search, IResolvable resolvable) {
		TypeSemanticsHelper.resolveSearchInReferredContainer(search, resolvable);
	}
	
}