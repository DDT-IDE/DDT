package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import descent.core.ddoc.Ddoc;
import descent.core.ddoc.DeeDocAccessor;
import dtool.ast.ASTNode;
import dtool.ast.SourceRange;
import dtool.ast.declarations.ModuleProxy;
import dtool.ast.util.NodeUtil;
import dtool.parser.DeeTokenSemantics;
import dtool.parser.ParserError;
import dtool.parser.Token;
import dtool.resolver.CommonDefUnitSearch;

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
	
	public boolean isSynthetic() {
		return false; // reimplement method as appropriate
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
	public INamedElement getParentNamespace() {
		return NodeUtil.getParentDefUnit(this);
	}
	
	@Override
	public List<String> getQualificationList() {
		LinkedList<String> qualications = new LinkedList<String>();
		
		INamedElement defUnitIter = this;
		
		while(true) {
			INamedElement parentDefUnit = defUnitIter.getParentNamespace();
			// TODO: fix this code
			if(parentDefUnit == null) {
				if(defUnitIter instanceof ModuleProxy) {
					ModuleProxy lightweightModuleProxy = (ModuleProxy) defUnitIter;
					defUnitIter = lightweightModuleProxy.resolveDefUnit();
				}
				
				if(defUnitIter instanceof Module) {
					Module module = ((Module) defUnitIter);
					
					String[] packageNames = module.getDeclaredPackages();
					qualications.addAll(0, Arrays.asList(packageNames));
				}
				
				return qualications;
			} else {
				qualications.add(0, parentDefUnit.getName());
				defUnitIter = parentDefUnit;
			}
		}
	}
	
	@Override
	public String getFullyQualifiedName() {
		INamedElement parentNamespace = getParentNamespace();
		if(parentNamespace == null) {
			return getName();
		} else {
			return parentNamespace.getFullyQualifiedName() + "." + getName();
		}
	}
	
	@Override
	public DefUnit asDefUnit() {
		return this;
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
	public Ddoc resolveDDoc() {
		return getDDoc();
	}
	
	@Override
	public abstract void resolveSearchInMembersScope(CommonDefUnitSearch search);
	
}