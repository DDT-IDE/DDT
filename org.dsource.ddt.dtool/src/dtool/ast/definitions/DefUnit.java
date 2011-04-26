package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;


import descent.internal.compiler.parser.Comment;
import descent.internal.compiler.parser.IdentifierExp;
import dtool.ast.ASTNeoNode;
import dtool.ast.SourceRange;
import dtool.ast.TokenInfo;
import dtool.descentadapter.DefinitionConverter;
import dtool.refmodel.IScopeNode;

/**
 * Abstract class for all AST elements that define a new named entity.
 */
public abstract class DefUnit extends ASTNeoNode {
	
	static public enum EArcheType {
		Module,
		Package,
		Native,
		Aggregate, 
		Enum,
		EnumMember, // same as var?
		Variable,
		Function,
		Alias,
		Typedef,
		Template,
		Mixin,
		Tuple,   
		;
	}
	
	
	public final Comment[] comments;
	public final DefSymbol defname;
	
	public static final class DefUnitDataTuple {
		public SourceRange sourceRange;
		public TokenInfo defName;
		public Comment[] comments;
		public DefUnitDataTuple(SourceRange sourceRange, TokenInfo defName, Comment[] comments) {
			this.sourceRange = sourceRange;
			this.defName = defName;
			this.comments = comments;
		}
	}
	
	public DefUnit(DefUnitDataTuple defunit) {
		this(defunit.sourceRange, defunit.defName, defunit.comments);
	}
	
	public DefUnit(SourceRange sourceRange, TokenInfo defName, Comment[] comments) {
		this(sourceRange, defName.value, defName.getRange(), comments);
	}
	
	public DefUnit(SourceRange sourceRange, String defName, SourceRange defNameSourceRange, Comment[] comments) {
		initSourceRange(sourceRange);
		this.defname = new DefSymbol(defName, defNameSourceRange, this);
		this.comments = comments;
	}
	
	@Deprecated
	public DefUnit(IdentifierExp id) {
		this.defname = new DefSymbol(DefinitionConverter.convertIdToken(id), this);
		this.comments = null;
	}
	
	protected DefUnit(SourceRange sourceRange, DefSymbol defname, Comment[] comments) {
		initSourceRange(sourceRange);
		assertNotNull(defname);
		this.defname = defname;
		this.comments = comments;
	}
	
	public String getName() {
		return defname.name;
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
	
	/** Gets the archtype (the kind) of this DefUnit. */
	public abstract EArcheType getArcheType() ;
	
	/** Gets the scope which contains the members of this DefUnit. 
	 * In the case of aggregate like DefUnits the members scope is contained
	 * in the DefUnit node, but on other cases the scope is somewhere else.
	 * May be null if the scope is not found. */
	public abstract IScopeNode getMembersScope();
	
	@Override
	public String toStringAsElement() {
		return getName();
	}
	
	/** Returns signature-oriented String representation. */
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