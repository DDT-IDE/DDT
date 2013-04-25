package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import descent.internal.compiler.parser.Comment;
import dtool.ast.ASTNeoNode;
import dtool.ast.SourceRange;
import dtool.ast.TokenInfo;
import dtool.parser.LexElement;
import dtool.parser.ParserError;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;

/**
 * Abstract class for all AST elements that define a new named entity.
 */
public abstract class DefUnit extends ASTNeoNode {
	
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
	
	@Deprecated
	public static final class DefUnitTuple extends ProtoDefSymbol {
		public final SourceRange sourceRange;
		public final Comment[] comments;
		
		public DefUnitTuple(Comment[] comments, String name, SourceRange nameSourceRange, ParserError error) {
			super(name, nameSourceRange, error);
			this.comments = comments;
			this.sourceRange = null;
		}
		
		public DefUnitTuple(Comment[] comments, String name, SourceRange nameSourceRange, 
			@Deprecated SourceRange sourceRange) {
			super(name, nameSourceRange, null);
			this.comments = comments;
			this.sourceRange = sourceRange;
		}
		
		public DefUnitTuple(SourceRange sourceRange, TokenInfo defName, Comment[] comments) {
			this(comments, defName.getString(), defName.getSourceRange(), sourceRange);
		}
		
		public DefUnitTuple(SourceRange sourceRange, LexElement id, Comment[] comments) {
			this(comments, id.getSourceValue(), id.getSourceRange(), sourceRange);
		}
	}
	
	public final Comment[] comments;
	public final DefSymbol defname;
	
	public DefUnit(DefUnitTuple defunit) {
		this(defunit.name, defunit.nameSourceRange, defunit.comments, defunit.sourceRange, defunit.error);
	}
	
	public DefUnit(ProtoDefSymbol defId) {
		this(defId.name, defId.nameSourceRange, null, null, defId.error);
	}
	
	public DefUnit(String defName, SourceRange defNameSourceRange, Comment[] comments, SourceRange sourceRange, 
		ParserError error) {
		initSourceRange(sourceRange);
		this.defname = new DefSymbol(defName, this);
		this.defname.setSourceRange(defNameSourceRange);
		if(error == null) {
			this.defname.setParsedStatus();
		} else {
			this.defname.setParsedStatusWithErrors(error);
		}
		this.comments = comments;
	}
	
	/** Constructor for synthetic defunits. */
	protected DefUnit(String defName) {
		this(defName, null, null, null, null);
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