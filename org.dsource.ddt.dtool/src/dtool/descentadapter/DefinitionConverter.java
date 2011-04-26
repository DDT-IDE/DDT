package dtool.descentadapter;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.List;

import melnorme.utilbox.misc.ArrayUtil;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.AttribDeclaration;
import descent.internal.compiler.parser.Comment;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.TemplateInstanceWrapper;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.ast.ASTNode;
import dtool.DToolBundle;
import dtool.ast.ASTNeoNode;
import dtool.ast.SourceRange;
import dtool.ast.TokenInfo;
import dtool.ast.declarations.Declaration;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.NamelessParameter;
import dtool.ast.definitions.Symbol;
import dtool.ast.expressions.Expression;
import dtool.ast.references.ReferenceConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

public class DefinitionConverter extends BaseDmdConverter {
	
	public static SourceRange convertSourceRange(ASTDmdNode elem) {
		return BaseDmdConverter.sourceRangeForced(elem);
	}
	
	public static TokenInfo convertIdToken(IdentifierExp id) {
		assertTrue(id.getClass() == IdentifierExp.class);
		if(DToolBundle.UNSUPPORTED_DMD_CONTRACTS)
			assertTrue(id.getLength() == id.ident.length); // TODO check this
		return new TokenInfo(new String(id.ident), id.getStartPos());
	}
	
	public static Symbol convertId(IdentifierExp idExp) {
		return new Symbol(DefinitionConverter.convertIdToken(idExp));
	}
	
	public static DefUnit.DefUnitDataTuple convertDsymbol(Dsymbol elem, ASTConversionContext convContext) {
		descent.internal.compiler.parser.Module module = convContext.module;
		
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
			if(isSingleSymbolDeclaration(parent)) {
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
		
		SourceRange sourceRange = convertSourceRange(elem);
		IdentifierExp ident = elem.ident;
		if(ident == null) {
			TokenInfo defName = new TokenInfo("<syntax_error>");
			return new DefUnit.DefUnitDataTuple(sourceRange, defName, newComments);
		} else {
			TokenInfo defName = convertIdToken(ident);
			return new DefUnit.DefUnitDataTuple(sourceRange, defName, newComments);
		}
	}
	
	/*---------------------------------------------------------*/
	
	public static Module createModule(descent.internal.compiler.parser.Module elem, ASTConversionContext convContext) {
		
		ASTNeoNode[] members = Declaration.convertMany(elem.members, convContext);
		
		SourceRange sourceRange = BaseDmdConverter.sourceRangeForced(elem);
		if(elem.md == null) {
			return Module.createModule(sourceRange, members);
		} else  {
			TokenInfo defName = DefinitionConverter.convertIdToken(elem.md.id);
			SourceRange declRange = DefinitionConverter.convertSourceRange(elem.md);
			
			String[] packages = ArrayUtil.newSameSize(elem.md.packages, String.class);
			
			for (int i = 0; i < packages.length; i++) {
				packages[i] = new String(elem.md.packages.get(i).ident);
			}
			
			// Remove comments of other defunits (DMD parser quirk)
			Comment[] comments = filterComments(elem, elem.md.start); 
			return Module.createModule(sourceRange, comments, packages, defName, declRange, members);
		}
	}
	
	private static Comment[] filterComments(descent.internal.compiler.parser.Module elem, int modDeclOffset) {
		Comment[] moduleComments = elem.comments;
		for (int i = 0; i < elem.comments.length; i++) {
			Comment comment = elem.comments[i];
			if(comment.start > modDeclOffset) {
				moduleComments = ArrayUtil.copyOfRange(elem.comments, 0, i);
				break;
			}
		}
		return moduleComments;
	}
	
	
	
	public static NamelessParameter convertNamelessParameter(Type type, ASTConversionContext convContext) {
		return new NamelessParameter(ReferenceConverter.convertType(type, convContext), 0, null, sourceRange(type));
	}

	public static NamelessParameter convertNamelessParameter(Argument elem, IdentifierExp ident,
			@SuppressWarnings("unused") ASTConversionContext convContext) {
		assertTrue(!(ident instanceof TemplateInstanceWrapper));
		return new NamelessParameter(ReferenceConverter.convertToRefIdentifier(ident), 0, null, sourceRange(elem));
	}

	public static NamelessParameter convertNamelessParameter(Argument elem, ASTConversionContext convContext) {
		return new NamelessParameter(ReferenceConverter.convertType(elem.type, convContext), elem.storageClass, 
				Expression.convert(elem.defaultArg, convContext), sourceRange(elem));
	}
	
	public static boolean isSingleSymbolDeclaration(ASTDmdNode parent) {
		if(!(parent instanceof AttribDeclaration)) {
			return false;
		}
		int length = 0;
		for(ASTNode child : parent.getChildren()) {
			if(child instanceof Dsymbol) {
				length++;
				if(length > 1) {
					return false;
				}
			}
		}
		return length == 1;
	}
	
}
