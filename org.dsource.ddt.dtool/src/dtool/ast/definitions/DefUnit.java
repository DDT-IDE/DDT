package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.List;

import descent.internal.compiler.parser.Comment;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Module;
import dtool.ast.ASTNeoNode;
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
	
	public /*final*/ Comment[] comments;
	public final Symbol defname;
	public EArcheType archeType;
	
	public DefUnit(Dsymbol elem, ASTConversionContext convContext) {
		convertNode(elem, false);
		IdentifierExp ident = elem.ident;
		this.defname = (ident == null) ? new DefSymbol("<syntax_error>", this) : new DefSymbol(ident, this);
		int size = 0;
		Module module = convContext.module;
		List<Comment> preDdocs = module.getPreComments(elem);
		Comment postDdoc = module.getPostComment(elem);
		if(preDdocs != null)
			size = preDdocs.size();
		if(postDdoc != null)
			size = size+1;
		
		if(size != 0)
			this.comments = new Comment[size];
		
		if(preDdocs != null) {
			for (int i = 0; i < preDdocs.size(); i++) {
				this.comments[i] = preDdocs.get(i);
			}
		}
		if(postDdoc != null)
			this.comments[size-1] = postDdoc;
	}

	public DefUnit(IdentifierExp id) {
		this.defname = new DefSymbol(id, this);
		this.comments = null;
	}

	public DefUnit(Symbol defname) {
		assertNotNull(defname);
		this.defname = defname;
		this.comments = null;
	}
	
	public String getName() {
		return defname.name;
	}
	
	
	public String getCombinedDocComments() {
		if(comments == null || comments.length == 0)
			return null;
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