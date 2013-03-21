package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import descent.internal.compiler.parser.Comment;
import dtool.ast.ASTNeoNode;
import dtool.ast.ASTSemantics;
import dtool.ast.SourceRange;
import dtool.ast.TokenInfo;
import dtool.parser.LexElement;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;

/**
 * Abstract class for all AST elements that define a new named entity.
 */
public abstract class DefUnit extends ASTNeoNode {
	
	public static final class DefUnitTuple {
		public SourceRange sourceRange;
		public String name;
		public SourceRange nameSourceRange;
		public Comment[] comments;
		
		public DefUnitTuple(SourceRange sourceRange, TokenInfo defName, Comment[] comments) {
			this.sourceRange = sourceRange;
			this.name = defName.getString();
			this.nameSourceRange = defName.getSourceRange();
			this.comments = comments;
		}
		
		public DefUnitTuple(SourceRange sourceRange, LexElement id, Comment[] comments) {
			this.sourceRange = sourceRange;
			this.name = id.token.getSourceValue();
			this.nameSourceRange = id.token.getSourceRange();
			this.comments = comments;
		}
	}
	
	public final Comment[] comments;
	public final DefSymbol defname;
	
	public DefUnit(DefUnitTuple defunit) {
		this(defunit.name, defunit.nameSourceRange, defunit.comments, defunit.sourceRange);
	}
	
	public DefUnit(String defName, SourceRange defNameSourceRange, Comment[] comments, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.defname = new DefSymbol(defName, defNameSourceRange, this);
		this.defname.setData(ASTSemantics.PARSED_STATUS);
		this.comments = comments;
	}
	
	/** Constructor for synthetic defunits. */
	protected DefUnit(String defName) {
		this(defName, null, null, null);
	}
	
	/** Constructor for Module defunit. */
	protected DefUnit(DefSymbol defname, Comment[] comments, SourceRange sourceRange) {
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
	
}