package dtool.descentadapter;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.List;

import melnorme.utilbox.core.Assert;
import melnorme.utilbox.misc.ArrayUtil;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.AttribDeclaration;
import descent.internal.compiler.parser.Comment;
import descent.internal.compiler.parser.CtorDeclaration;
import descent.internal.compiler.parser.DeleteDeclaration;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.DtorDeclaration;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.NewDeclaration;
import descent.internal.compiler.parser.StaticCtorDeclaration;
import descent.internal.compiler.parser.StaticDtorDeclaration;
import descent.internal.compiler.parser.TemplateInstanceWrapper;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeFunction;
import descent.internal.compiler.parser.ast.ASTNode;
import dtool.ast.ASTNeoNode;
import dtool.ast.SourceRange;
import dtool.ast.TokenInfo;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.DefUnit.DefUnitDataTuple;
import dtool.ast.definitions.DefinitionCtor;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.DefinitionFunction.AutoFunctionReturnReference;
import dtool.ast.definitions.ArrayView;
import dtool.ast.definitions.EnumMember;
import dtool.ast.definitions.FunctionParameter;
import dtool.ast.definitions.IFunctionParameter;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.NamelessParameter;
import dtool.ast.definitions.Symbol;
import dtool.ast.references.Reference;
import dtool.ast.references.ReferenceConverter;
import dtool.ast.statements.Statement;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

public class DefinitionConverter extends BaseDmdConverter {
	
	public static TokenInfo convertIdToken(IdentifierExp id) {
		assertTrue(id.getClass() == IdentifierExp.class);
		assertTrue(id.hasNoSourceRangeInfo() || id.getLength() == id.ident.length);
		return new TokenInfo(new String(id.ident), id.getStartPos());
	}
	
	public static Symbol convertId(IdentifierExp idExp) {
		return new Symbol(DefinitionConverter.convertIdToken(idExp));
	}
	
	public static DefUnit.DefUnitDataTuple convertDsymbol(Dsymbol elem, ASTConversionContext convContext) {
		SourceRange sourceRange = sourceRange(elem);
		
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
	
	public static Module createModule(descent.internal.compiler.parser.Module elem, ASTConversionContext convContext,
			String moduleName) {
		
		ArrayView<ASTNeoNode> members = DescentASTConverter.convertManyNoNulls(elem.members, convContext);
		
		SourceRange sourceRange = sourceRange(elem, false);
		if(elem.md == null) {
			return Module.createModule(sourceRange, members, moduleName);
		} else  {
			TokenInfo defnameInfo = DefinitionConverter.convertIdToken(elem.md.id);
			SourceRange declRange = sourceRange(elem.md);
			
			String[] packages = ArrayUtil.newSameSize(elem.md.packages, String.class);
			
			for (int i = 0; i < packages.length; i++) {
				packages[i] = new String(elem.md.packages.get(i).ident);
			}
			
			// Remove comments of other defunits (DMD parser quirk)
			Comment[] comments = filterComments(elem, elem.md.start); 
			return Module.createModule(sourceRange, comments, packages, defnameInfo, declRange, members);
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
	
	/* ------------------- */
	
	public static EnumMember createEnumMember(descent.internal.compiler.parser.EnumMember elem, ASTConversionContext convContext) {
		assertNotNull(elem.ident);
		elem.ident.length = elem.ident.ident.length; // Fix a parser source range issue
		elem.length = Math.max(elem.length, elem.ident.length);
		DefUnitDataTuple defunitInfo = convertDsymbol(elem, convContext);
		return new EnumMember(defunitInfo, ExpressionConverter.convert(elem.value, convContext));
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
				ExpressionConverter.convert(elem.defaultArg, convContext), sourceRange(elem));
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
	
	/* ------------------- */
	
	public static DefinitionFunction createDefinitionFunction(descent.internal.compiler.parser.FuncDeclaration elem, ASTConversionContext convContext) {
		TypeFunction elemTypeFunc = ((TypeFunction) elem.type);
		
		/*if(elem.templateParameters != null)
			this.templateParams = TemplateParameter.convertMany(elem.templateParameters);*/
		Assert.isTrue(elem.parameters == null);
		
		Reference rettype = (elemTypeFunc.next == null) ? 
				new AutoFunctionReturnReference() : 
				ReferenceConverter.convertType(elemTypeFunc.next, convContext);
		
		DefinitionFunction definitionFunction = new DefinitionFunction(
				DefinitionConverter.convertDsymbol(elem, convContext), 
				elem.prot(),
				rettype,
				DescentASTConverter.convertManyToView(elemTypeFunc.parameters, IFunctionParameter.class, convContext).getInternalArray(),
				convertVarArgs(elemTypeFunc.varargs),
				Statement.convert(elem.frequire, convContext),
				Statement.convert(elem.fensure, convContext),
				Statement.convert(elem.fbody, convContext)
				);
		
		return definitionFunction;
	}
	
	public static int convertVarArgs(int varargs) {
		Assert.isTrue(varargs >= 0 && varargs <= 2);
		return varargs;
	}
	
	public static ASTNeoNode convertFunctionParameter(Argument elem, ASTConversionContext convContext) {
		if(elem.ident != null) {
			if(elem.type != null) {
				return new FunctionParameter(elem, convContext);
			} else {
				// strange case, likely from a syntax error
				return convertNamelessParameter(elem, elem.ident, convContext);
			}
		} else {
			return convertNamelessParameter(elem, convContext);
		}
	}
	
	
	public static DefinitionCtor createDefinitionCtor(CtorDeclaration elem, ASTConversionContext convContext) {
		return new DefinitionCtor(
			DefinitionCtor.SpecialFunctionKind.CONSTRUCTOR, 
			DescentASTConverter.convertMany(elem.arguments, IFunctionParameter.class, convContext),
			convertVarArgs(elem.varargs),
			Statement.convert(elem.fbody, convContext),
			elem.thisStart, DefinitionConverter.sourceRange(elem)
		);
	}
	
	public static DefinitionCtor createDefinitionCtor(DtorDeclaration elem, ASTConversionContext convContext) {
		return new DefinitionCtor(
			DefinitionCtor.SpecialFunctionKind.DESTRUCTOR, 
			DescentASTConverter.convertMany(elem.parameters, IFunctionParameter.class, convContext),
			0,
			Statement.convert(elem.fbody, convContext),
			elem.thisStart - 1, DefinitionConverter.sourceRange(elem)
		);
	}
	
	public static DefinitionCtor createDefinitionCtor(StaticCtorDeclaration elem, ASTConversionContext convContext) {
		return new DefinitionCtor(
			DefinitionCtor.SpecialFunctionKind.CONSTRUCTOR,
			DescentASTConverter.convertMany(elem.parameters, IFunctionParameter.class, convContext),
			0,
			Statement.convert(elem.fbody, convContext),
			elem.thisStart, DefinitionConverter.sourceRange(elem)
		);
	}
	
	public static DefinitionCtor createDefinitionCtor(StaticDtorDeclaration elem, ASTConversionContext convContext) {
		return new DefinitionCtor(
			DefinitionCtor.SpecialFunctionKind.DESTRUCTOR,
			DescentASTConverter.convertMany(elem.parameters, IFunctionParameter.class, convContext),
			0,
			Statement.convert(elem.fbody, convContext),
			elem.thisStart - 1, DefinitionConverter.sourceRange(elem)
		);
	}
	
	public static DefinitionCtor createDefinitionCtor(NewDeclaration elem, ASTConversionContext convContext) {
		return new DefinitionCtor(
			DefinitionCtor.SpecialFunctionKind.ALLOCATOR,
			DescentASTConverter.convertMany(elem.parameters, IFunctionParameter.class, convContext),
			convertVarArgs(elem.varargs),
			Statement.convert(elem.fbody, convContext),
			elem.newStart, DefinitionConverter.sourceRange(elem)
		);
	}
	
	public static DefinitionCtor createDefinitionCtor(DeleteDeclaration elem, ASTConversionContext convContext) {
		return new DefinitionCtor(
			DefinitionCtor.SpecialFunctionKind.DEALLOCATOR,
			DescentASTConverter.convertMany(elem.parameters, IFunctionParameter.class, convContext),
			0,
			Statement.convert(elem.fbody, convContext),
			elem.deleteStart, DefinitionConverter.sourceRange(elem)
		);
	}
	
}
