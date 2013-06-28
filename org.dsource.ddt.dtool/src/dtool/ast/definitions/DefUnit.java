package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import dtool.ast.ASTNode;
import dtool.ast.SourceRange;
import dtool.ast.references.Reference;
import dtool.parser.DeeTokenSemantics;
import dtool.parser.ParserError;
import dtool.parser.Token;
import dtool.resolver.IScopeNode;
import dtool.resolver.api.IModuleResolver;

/**
 * Abstract class for all AST elements that define a new named entity.
 */
public abstract class DefUnit extends ASTNode {
	
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
	
	public String getName() {
		return defname.name;
	}
	
	public boolean isSynthetic() {
		// TODO need to define this properly
		return getModuleNode() == null;
	}
	
	/** @return the comments that define the DDoc for this defUnit. Can be null  */
	public Token[] getDocComments() {
		return null;
	}
	public void getDocComments_invariant() {
		for (Token token : getDocComments()) {
			assertTrue(DeeTokenSemantics.tokenIsDocComment(token));
		}
	}
	
	/** Gets the archetype (the kind) of this DefUnit. */
	public abstract EArcheType getArcheType() ;
	
	/** Gets the scope which contains the members of this DefUnit. 
	 * In the case of aggregate like DefUnits the members scope is contained
	 * in the DefUnit node, but on other cases the scope is somewhere else.
	 * May be null if the scope is not found. */
	public abstract IScopeNode getMembersScope(IModuleResolver moduleResolver);
	
	@Override
	public String toStringAsElement() {
		return getName();
	}
	
	/** Returns signature-oriented String representation. TODO: this should move to UI code */
	@Deprecated
	public String toStringForHoverSignature() {
		String str = getModuleScope().toStringAsElement() + "." + getName();
		//if(getMembersScope() != this)str += " : " + getMembersScope();
		return str;
	}
	
	/** Returns completion proposal oriented String representation. TODO: this should move to UI code */
	//public abstract String toStringForCodeCompletion() ;
	@Deprecated
	public String toStringForCodeCompletion() {
		return getName() + " - " + getModuleScope().toStringAsElement();
	}
	
	// TODO: move this to UI code
	public static String typeRefToUIString(Reference typeReference) {
		if(typeReference == null) {
			return "<auto>";
		}
		return typeReference.toStringAsElement();
	}
	
}