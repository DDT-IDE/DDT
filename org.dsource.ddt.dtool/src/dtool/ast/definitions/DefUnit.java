package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import descent.internal.compiler.parser.Comment;
import dtool.ast.ASTNeoNode;
import dtool.ast.SourceRange;
import dtool.ast.TokenInfo;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;

/**
 * Abstract class for all AST elements that define a new named entity.
 */
public abstract class DefUnit extends ASTNeoNode {
	
	public static final class DefUnitTuple {
		public SourceRange sourceRange;
		public TokenInfo defName;
		public Comment[] comments;
		public DefUnitTuple(SourceRange sourceRange, TokenInfo defName, Comment[] comments) {
			this.sourceRange = sourceRange;
			this.defName = defName;
			this.comments = comments;
		}
	}
	
	public final Comment[] comments;
	public final DefSymbol defname;
	
	public DefUnit(DefUnitTuple defunit) {
		this(defunit.sourceRange, defunit.defName.getString(), defunit.defName.getSourceRange(), defunit.comments);
	}
	
	public DefUnit(SourceRange sourceRange, String defName, SourceRange defNameSourceRange, Comment[] comments) {
		initSourceRange(sourceRange);
		this.defname = new DefSymbol(defName, defNameSourceRange, this);
		this.comments = comments;
//		if(hasSourceRangeInfo()) {
//			assertTrue(defname.hasSourceRangeInfo());
//		}
	}
	
	/** Constructor for synthetic defunits. */
	protected DefUnit(String defName) {
		this(null, defName, null, null);
	}
	
	/** Constructor for Module defunit. */
	protected DefUnit(SourceRange sourceRange, DefSymbol defname, Comment[] comments) {
		initSourceRange(sourceRange);
		assertNotNull(defname);
		this.defname = defname;
		this.comments = comments;
	}
	
	public String getName() {
		return defname.name;
	}
	
	public boolean isSynthetic() {
		// TODO need to define this properly
		return getModuleNode() == null;
	}
	
	public String getCombinedDocComments() {
		if(comments == null || comments.length == 0) {
			return null;
		}
		String str = new String(comments[0].string);
		for (int i = 1; i < comments.length; i++) {
			str = str + "\n" + comments[i].toString();
		}
		return str;
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
	public String toStringForHoverSignature() {
		String str = getModuleScope().toStringAsElement() + "." + getName();
		//if(getMembersScope() != this)str += " : " + getMembersScope();
		return str;
	}
	
	/** Returns completion proposal oriented String representation. */
	//public abstract String toStringForCodeCompletion() ;
	public String toStringForCodeCompletion() {
		return getName() + " - " + getModuleScope().toStringAsElement();
	}
	
}