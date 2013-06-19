package dtool.descentadapter;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
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
import descent.internal.compiler.parser.TypeBasic;
import descent.internal.compiler.parser.TypeFunction;
import dtool.ast.ASTNode;
import dtool.ast.SourceRange;
import dtool.ast.TokenInfo;
import dtool.ast.declarations.DeclarationAllocatorFunction;
import dtool.ast.declarations.DeclarationSpecialFunction;
import dtool.ast.declarations.DeclarationSpecialFunction.SpecialFunctionKind;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.DefinitionConstructor;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.EnumMember;
import dtool.ast.definitions.FunctionParameter;
import dtool.ast.definitions.IFunctionParameter;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.Module.DeclarationModule;
import dtool.ast.definitions.NamelessParameter;
import dtool.ast.definitions.Symbol;
import dtool.ast.references.Reference;
import dtool.ast.statements.BlockStatement;
import dtool.ast.statements.IFunctionBody;
import dtool.ast.statements.IStatement;
import dtool.ast.statements.InOutFunctionBody;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.parser.DeeTokens;
import dtool.parser.LexElement;
import dtool.parser.Token;
import dtool.util.ArrayView;

public class DefinitionConverter extends BaseDmdConverter {
	
	public static TokenInfo convertIdToken(IdentifierExp id) {
		assertTrue(id.getClass() == IdentifierExp.class);
		assertTrue(id.hasNoSourceRangeInfo() || id.getLength() == id.ident.length);
		return new TokenInfo(new String(id.ident), id.getStartPos());
	}
	
	public static Token convertIdToken2(IdentifierExp id) {
		assertTrue(id.getClass() == IdentifierExp.class);
		assertTrue(id.hasNoSourceRangeInfo() || id.getLength() == id.ident.length);
		return new Token(DeeTokens.IDENTIFIER, new String(id.ident), id.getStartPos());
	}
	
	public static Symbol convertId(IdentifierExp idExp) {
		TokenInfo idTokenInfo = DefinitionConverter.convertIdToken(idExp);
		return connect(idTokenInfo.getSourceRange(), new Symbol(idTokenInfo.value));
	}
	
	public static DefUnitTuple convertDsymbol(Dsymbol elem, ASTConversionContext convContext) {
		final SourceRange sourceRange = sourceRange(elem);
		
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
			TokenInfo defName = new TokenInfo(TokenInfo.SYNTAX_ERROR, -1);
			return new DefUnitTuple(sourceRange, defName, newComments);
		} else {
			TokenInfo defName = convertIdToken(ident);
			return new DefUnitTuple(sourceRange, defName, newComments);
		}
	}
	
	/*---------------------------------------------------------*/
	
	public static Module createModule(descent.internal.compiler.parser.Module elem, ASTConversionContext convContext,
			String defaultModuleName) {
		
		ArrayView<ASTNode> members = 
				DescentASTConverter.convertManyNoNulls(elem.members, ASTNode.class, convContext);
		
		SourceRange sourceRange = sourceRange(elem, false);
		if(elem.md == null) {
			return connect(sourceRange, Module.createModuleNoModuleDecl(defaultModuleName, members));
		} else  {
			LexElement defnameInfo = new LexElement(null, DefinitionConverter.convertIdToken2(elem.md.id));
			SourceRange declRange = sourceRange(elem.md);
			
			Token[] packages = ArrayUtil.newSameSize(elem.md.packages, Token.class);
			
			for (int i = 0; i < packages.length; i++) {
				IdentifierExp packageId = elem.md.packages.get(i);
				packages[i] = new Token(DeeTokens.IDENTIFIER, new String(packageId.ident), packageId.start);
			}
			
			// Remove comments of other defunits (DMD parser quirk)
			Comment[] comments = filterComments(elem, elem.md.start); 
			DeclarationModule md = connect(declRange, 
				new DeclarationModule(null, ArrayView.create(packages), defnameInfo));
			return connect(sourceRange, new Module(md.getModuleSymbol(), md, members));
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
		DefUnitTuple defunitInfo = convertDsymbol(elem, convContext);
		return connect(defunitInfo.sourceRange, 
			new EnumMember(null, defunitInfo.defSymbol, ExpressionConverter.convert(elem.value, convContext)));
	}
	
	public static NamelessParameter convertNamelessParameter(Type type, ASTConversionContext convContext) {
		return connect(sourceRange(type), new NamelessParameter(null,
			ReferenceConverter.convertType(type, convContext), null, false));
	}

	public static NamelessParameter convertNamelessParameter(Argument elem, IdentifierExp ident,
			@SuppressWarnings("unused") ASTConversionContext convContext) {
		assertTrue(!(ident instanceof TemplateInstanceWrapper));
		return connect(sourceRange(elem), 
			new NamelessParameter(null, ReferenceConverter.convertToRefIdentifier(ident), null, false));
	}

	public static NamelessParameter convertNamelessParameter(Argument elem, ASTConversionContext convContext) {
		return connect(sourceRange(elem), new NamelessParameter(null, 
			ReferenceConverter.convertType(elem.type, convContext),  
			ExpressionConverter.convert(elem.defaultArg, convContext), false));
	}
	
	public static boolean isSingleSymbolDeclaration(ASTDmdNode parent) {
		if(!(parent instanceof AttribDeclaration)) {
			return false;
		}
		int length = 0;
		for(descent.internal.compiler.parser.ast.ASTNode child : parent.getChildren()) {
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
				null : 
				ReferenceConverter.convertType(elemTypeFunc.next, convContext);
		
		DefUnitTuple defunitData = DefinitionConverter.convertDsymbol(elem, convContext);
		return connect(defunitData.sourceRange, 
			new DefinitionFunction(
				defunitData.commentsToToken(),
				rettype, 
				defunitData.defSymbol, 
				null, 
				DescentASTConverter.convertMany(elemTypeFunc.parameters, IFunctionParameter.class, convContext), 
				null, 
				null, 
				convertFnBody(elem, convContext)
		));
	}

	public static IFunctionBody convertFnBody(descent.internal.compiler.parser.FuncDeclaration elem,
		ASTConversionContext convContext) {
		return convertFnBody(
			StatementConverterVisitor.convertStatement(elem.frequire, convContext), 
			StatementConverterVisitor.convertStatement(elem.fensure, convContext), 
			(BlockStatement) StatementConverterVisitor.convertStatement(elem.fbody, convContext));
	}
	
	public static IFunctionBody convertFnBody(IStatement frequire, IStatement fensure, BlockStatement fbody) {
		IFunctionBody fnBody;
		if(frequire == null && fensure == null) {
			if(fbody == null) {
				fnBody = null;
			} else {
				fnBody = fbody;
			}
		} else {
			/*WATHEVAR*/
			fnBody = new InOutFunctionBody(false, null, null, fbody);
		}
		return fnBody;
	}
	
	public static int convertVarArgs(int varargs) {
		Assert.isTrue(varargs >= 0 && varargs <= 2);
		return varargs;
	}
	
	public static ASTNode convertFunctionParameter(Argument elem, ASTConversionContext convContext) {
		if(elem.ident != null) {
			if(elem.type != null) {
				Reference type;
				if(elem.type instanceof TypeBasic && ((TypeBasic)elem.type).ty.name == null) {
					assertFail();
					type = null;
				} else
					type = ReferenceConverter.convertType(elem.type, convContext);
				
				DefUnitTuple dudt = new DefUnitTuple(
					DefinitionConverter.sourceRange(elem), DefinitionConverter.convertIdToken(elem.ident), null
				);
				
				return connect(dudt.sourceRange, 
					new FunctionParameter(
					null, 
					type,
					dudt.defSymbol,
					ExpressionConverter.convert(elem.defaultArg, convContext),
					false
				));
			} else {
				// strange case, likely from a syntax error
				return convertNamelessParameter(elem, elem.ident, convContext);
			}
		} else {
			return convertNamelessParameter(elem, convContext);
		}
	}
	
	public static DefinitionConstructor createDefinitionCtor(CtorDeclaration elem, ASTConversionContext convContext) {
		return connect(DefinitionConverter.sourceRange(elem),  
			new DefinitionConstructor(
				null,
				new DefUnit.ProtoDefSymbol("this", new SourceRange(elem.thisStart, "this".length()), null),
				null,
				nullToEmpty(DescentASTConverter.convertMany(elem.arguments, IFunctionParameter.class, convContext)),
				null,
				null,
				convertFnBody(elem, convContext)
			)
		);
	}
	
	protected static ArrayView<IFunctionParameter> nullToEmpty(ArrayView<IFunctionParameter> arrayView) {
		return arrayView != null ? arrayView : ArrayView.create(new IFunctionParameter[0]);
	}
	
	public static DeclarationSpecialFunction createDefinitionCtor(DtorDeclaration elem, 
		ASTConversionContext convContext) {
		return connect(DefinitionConverter.sourceRange(elem), new DeclarationSpecialFunction(
			SpecialFunctionKind.STATIC_DESTRUCTOR,
			convertFnBody(elem, convContext)
			)
		);
	}
	
	public static DeclarationSpecialFunction createDefinitionCtor(StaticCtorDeclaration elem, 
		ASTConversionContext convContext) {
		return connect(DefinitionConverter.sourceRange(elem), new DeclarationSpecialFunction(
			SpecialFunctionKind.STATIC_CONSTRUCTOR,
			convertFnBody(elem, convContext)
			)
		);
	}
	
	public static DeclarationSpecialFunction createDefinitionCtor(StaticDtorDeclaration elem, 
		ASTConversionContext convContext) {
		return connect(DefinitionConverter.sourceRange(elem), new DeclarationSpecialFunction(
			DeclarationSpecialFunction.SpecialFunctionKind.STATIC_DESTRUCTOR,
			convertFnBody(elem, convContext)
			)
		);
	}
	
	public static DeclarationAllocatorFunction createDefinitionCtor(NewDeclaration elem, ASTConversionContext convContext) {
		return connect(DefinitionConverter.sourceRange(elem), new DeclarationAllocatorFunction(
			true,
			nullToEmpty(DescentASTConverter.convertMany(elem.parameters, IFunctionParameter.class, convContext)),
			convertFnBody(elem, convContext)
			)
		);
	}
	
	public static DeclarationAllocatorFunction createDefinitionCtor(DeleteDeclaration elem, 
		ASTConversionContext convContext) {
		return connect(DefinitionConverter.sourceRange(elem),
			new DeclarationAllocatorFunction(
			false,
			nullToEmpty(DescentASTConverter.convertMany(elem.parameters, IFunctionParameter.class, convContext)),
			convertFnBody(elem, convContext)
		));
	}
	
}