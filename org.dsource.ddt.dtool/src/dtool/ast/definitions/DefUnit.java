package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.List;

import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.Comment;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Module;
import dtool.ast.ASTNeoNode;
import dtool.ast.NeoSourceRange;
import dtool.ast.TokenInfo;
import dtool.descentadapter.DefinitionConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
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
	public EArcheType archeType;
	
	public static DefUnitDataTuple convertDsymbol(Dsymbol elem, ASTConversionContext convContext) {
		Module module = convContext.module;
		
		// The following code is a workaround for the way the DMD AST is created.
		ASTDmdNode nodeWithComments = elem;
		List<Comment> preDdocs;
		Comment postDdoc;
		while(true) {
			preDdocs = module.getPreComments(nodeWithComments);
			postDdoc = module.getPostComment(nodeWithComments);
			if(preDdocs != null || postDdoc != null) {
				break;
			}
			ASTDmdNode parent = nodeWithComments.getParent();
			if(parent == null) {
				break;
			}
			if(DefinitionConverter.isSingleSymbolDeclaration(parent)) {
				nodeWithComments = parent;
			} else {
				break;
			}
		}
		
		
		int commentsSize = 0;
		if(preDdocs != null) {
			commentsSize = preDdocs.size();
		}
		if(postDdoc != null) {
			commentsSize = commentsSize+1;
		}
		
		Comment[] newComments = (commentsSize == 0) ? null : new Comment[commentsSize];
		
		if(preDdocs != null) {
			for (int i = 0; i < preDdocs.size(); i++) {
				newComments[i] = preDdocs.get(i);
			}
		}
		if(postDdoc != null) {
			newComments[commentsSize-1] = postDdoc;
		}
		
		NeoSourceRange sourceRange = DefinitionConverter.convertSourceRange(elem);
		IdentifierExp ident = elem.ident;
		if(ident == null) {
			TokenInfo defName = new TokenInfo("<syntax_error>");
			return new DefUnitDataTuple(sourceRange, defName, newComments);
		} else {
			TokenInfo defName = DefinitionConverter.convertId(ident);
			return new DefUnitDataTuple(sourceRange, defName, newComments);
		}
	}
	
	public static final class DefUnitDataTuple {
		public NeoSourceRange sourceRange;
		public TokenInfo defName;
		public Comment[] comments;
		public DefUnitDataTuple(NeoSourceRange sourceRange, TokenInfo defName, Comment[] comments) {
			this.sourceRange = sourceRange;
			this.defName = defName;
			this.comments = comments;
		}
	}
	
	public DefUnit(DefUnitDataTuple defunit) {
		this(defunit.sourceRange, defunit.defName, defunit.comments);
	}
	
	public DefUnit(NeoSourceRange sourceRange, TokenInfo defName, Comment[] comments) {
		this(sourceRange, defName.value, defName.getRange(), comments);
	}
	
	public DefUnit(NeoSourceRange sourceRange, String defName, NeoSourceRange defNameSourceRange, Comment[] comments) {
		maybeSetSourceRange(sourceRange);
		this.defname = new DefSymbol(defName, defNameSourceRange, this);
		this.comments = comments;
	}
	
	@Deprecated
	public DefUnit(IdentifierExp id) {
		this.defname = new DefSymbol(DefinitionConverter.convertId(id), this);
		this.comments = null;
	}
	
	@Deprecated
	public DefUnit(NeoSourceRange sourceRange, DefSymbol defname, Comment[] comments) {
		maybeSetSourceRange(sourceRange);
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