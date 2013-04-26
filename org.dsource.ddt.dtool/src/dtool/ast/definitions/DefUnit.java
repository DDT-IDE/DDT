package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import descent.internal.compiler.parser.Comment;
import dtool.ast.ASTNeoNode;
import dtool.ast.SourceRange;
import dtool.ast.TokenInfo;
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
	public static final class DefUnitTuple {
		public final SourceRange sourceRange;
		public final Comment[] comments;
		public final ProtoDefSymbol defSymbol;
		
		public DefUnitTuple(Comment[] comments, String name, SourceRange nameSourceRange, 
			@Deprecated SourceRange sourceRange) {
			this.defSymbol = new ProtoDefSymbol(name, nameSourceRange, null);
			this.comments = comments;
			this.sourceRange = sourceRange;
		}
		
		public DefUnitTuple(SourceRange sourceRange, TokenInfo defName, Comment[] comments) {
			this(comments, defName.getString(), defName.getSourceRange(), sourceRange);
		}
		
	}
	
	public final Comment[] comments;
	public final DefSymbol defname; // This may not be a child of DefUnit
	
	protected DefUnit(DefSymbol defname, Comment[] comments) {
		this(defname, comments, true);
	}
	
	protected DefUnit(DefSymbol defname, Comment[] comments, boolean defIdIsChild) {
		assertNotNull(defname);
		this.defname = defIdIsChild ? parentize(defname) : defname;
		this.comments = comments;
	}
	
	public DefUnit(ProtoDefSymbol defIdTuple) {
		this(createDefId(defIdTuple), null /*TODO comments*/);
	}
	
	public static DefSymbol createDefId(ProtoDefSymbol defIdTuple) {
		DefSymbol defId = new DefSymbol(defIdTuple.name);
		defId.initSourceRange(defIdTuple.nameSourceRange);
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