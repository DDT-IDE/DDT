package dtool.descentadapter;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.utilbox.core.Assert;
import descent.internal.compiler.parser.AddAssignExp;
import descent.internal.compiler.parser.AddExp;
import descent.internal.compiler.parser.AddrExp;
import descent.internal.compiler.parser.AndAndExp;
import descent.internal.compiler.parser.AndAssignExp;
import descent.internal.compiler.parser.AndExp;
import descent.internal.compiler.parser.ArrayExp;
import descent.internal.compiler.parser.ArrayInitializer;
import descent.internal.compiler.parser.ArrayLengthExp;
import descent.internal.compiler.parser.ArrayLiteralExp;
import descent.internal.compiler.parser.AssertExp;
import descent.internal.compiler.parser.AssignExp;
import descent.internal.compiler.parser.AssocArrayLiteralExp;
import descent.internal.compiler.parser.BinExp;
import descent.internal.compiler.parser.CallExp;
import descent.internal.compiler.parser.CastExp;
import descent.internal.compiler.parser.CatAssignExp;
import descent.internal.compiler.parser.CatExp;
import descent.internal.compiler.parser.CmpExp;
import descent.internal.compiler.parser.ComExp;
import descent.internal.compiler.parser.CommaExp;
import descent.internal.compiler.parser.CompileDeclaration;
import descent.internal.compiler.parser.CompileExp;
import descent.internal.compiler.parser.CompileStatement;
import descent.internal.compiler.parser.CondExp;
import descent.internal.compiler.parser.DeleteExp;
import descent.internal.compiler.parser.DivAssignExp;
import descent.internal.compiler.parser.DivExp;
import descent.internal.compiler.parser.DollarExp;
import descent.internal.compiler.parser.EqualExp;
import descent.internal.compiler.parser.ErrorExp;
import descent.internal.compiler.parser.ExpInitializer;
import descent.internal.compiler.parser.FileExp;
import descent.internal.compiler.parser.FileInitExp;
import descent.internal.compiler.parser.FuncExp;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.IdentityExp;
import descent.internal.compiler.parser.InExp;
import descent.internal.compiler.parser.IndexExp;
import descent.internal.compiler.parser.IntegerExp;
import descent.internal.compiler.parser.IsExp;
import descent.internal.compiler.parser.LineInitExp;
import descent.internal.compiler.parser.MinAssignExp;
import descent.internal.compiler.parser.MinExp;
import descent.internal.compiler.parser.ModAssignExp;
import descent.internal.compiler.parser.ModExp;
import descent.internal.compiler.parser.MulAssignExp;
import descent.internal.compiler.parser.MulExp;
import descent.internal.compiler.parser.NegExp;
import descent.internal.compiler.parser.NewAnonClassExp;
import descent.internal.compiler.parser.NewExp;
import descent.internal.compiler.parser.NotExp;
import descent.internal.compiler.parser.NullExp;
import descent.internal.compiler.parser.OrAssignExp;
import descent.internal.compiler.parser.OrExp;
import descent.internal.compiler.parser.OrOrExp;
import descent.internal.compiler.parser.PostExp;
import descent.internal.compiler.parser.PtrExp;
import descent.internal.compiler.parser.RealExp;
import descent.internal.compiler.parser.ScopeExp;
import descent.internal.compiler.parser.ShlAssignExp;
import descent.internal.compiler.parser.ShlExp;
import descent.internal.compiler.parser.ShrAssignExp;
import descent.internal.compiler.parser.ShrExp;
import descent.internal.compiler.parser.SliceExp;
import descent.internal.compiler.parser.StringExp;
import descent.internal.compiler.parser.StructInitializer;
import descent.internal.compiler.parser.SuperExp;
import descent.internal.compiler.parser.TOK;
import descent.internal.compiler.parser.TY;
import descent.internal.compiler.parser.ThisExp;
import descent.internal.compiler.parser.TraitsExp;
import descent.internal.compiler.parser.TypeBasic;
import descent.internal.compiler.parser.TypeFunction;
import descent.internal.compiler.parser.TypeidExp;
import descent.internal.compiler.parser.UAddExp;
import descent.internal.compiler.parser.UnaExp;
import descent.internal.compiler.parser.UshrAssignExp;
import descent.internal.compiler.parser.UshrExp;
import descent.internal.compiler.parser.VoidInitializer;
import descent.internal.compiler.parser.XorAssignExp;
import descent.internal.compiler.parser.XorExp;
import dtool.ast.ASTNode;
import dtool.ast.SourceRange;
import dtool.ast.declarations.DeclarationMixinString;
import dtool.ast.definitions.IFunctionParameter;
import dtool.ast.expressions.ExpArrayLength;
import dtool.ast.expressions.ExpAssert;
import dtool.ast.expressions.ExpCast;
import dtool.ast.expressions.ExpConditional;
import dtool.ast.expressions.ExpDefaultInit;
import dtool.ast.expressions.ExpFunctionLiteral;
import dtool.ast.expressions.ExpIftype;
import dtool.ast.expressions.ExpImportString;
import dtool.ast.expressions.ExpIndex;
import dtool.ast.expressions.ExpInfix;
import dtool.ast.expressions.ExpInfix.InfixOpType;
import dtool.ast.expressions.ExpLiteralBool;
import dtool.ast.expressions.ExpLiteralFloat;
import dtool.ast.expressions.ExpLiteralInteger;
import dtool.ast.expressions.ExpLiteralMapArray;
import dtool.ast.expressions.ExpLiteralString;
import dtool.ast.expressions.ExpMixinString;
import dtool.ast.expressions.ExpNew;
import dtool.ast.expressions.ExpNewAnonClass;
import dtool.ast.expressions.ExpNull;
import dtool.ast.expressions.ExpPostfixOperator;
import dtool.ast.expressions.ExpPostfixOperator.PostfixOpType;
import dtool.ast.expressions.ExpPrefix;
import dtool.ast.expressions.ExpPrefix.PrefixOpType;
import dtool.ast.expressions.ExpReference;
import dtool.ast.expressions.ExpSlice;
import dtool.ast.expressions.ExpSuper;
import dtool.ast.expressions.ExpThis;
import dtool.ast.expressions.ExpTraits;
import dtool.ast.expressions.ExpTypeId;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.Initializer;
import dtool.ast.expressions.InitializerArray_Old;
import dtool.ast.expressions.InitializerExp;
import dtool.ast.expressions.InitializerStruct_Old;
import dtool.ast.expressions.InitializerVoid;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.Reference;
import dtool.ast.statements.BlockStatement;
import dtool.ast.statements.IFunctionBody;
import dtool.ast.statements.IStatement;
import dtool.ast.statements.InOutFunctionBody;
import dtool.parser.DeeTokens;
import dtool.util.ArrayView;

abstract class ExpressionConverterVisitor extends DeclarationConverterVisitor {
	
	@Override
	public boolean visit(FileExp node) {
		return endAdapt(connect(DefinitionConverter.sourceRange(node),
			new ExpImportString(
				ExpressionConverter.convert(node.e1, convContext)
			))
		);
	}
	
	@Override
	public boolean visit(TraitsExp node) {
		return endAdapt(connect(DefinitionConverter.sourceRange(node),
			new ExpTraits(
				node.ident.ident,
				DescentASTConverter.convertMany(node.args, ASTNode.class, convContext))
			)
		);
	}
	
	@Override
	public boolean visit(AssocArrayLiteralExp node) {
		Assert.isTrue(node.keys.size() == node.values.size());
		return endAdapt(connect(DefinitionConverter.sourceRange(node), 
			new ExpLiteralMapArray(
				/*DescentASTConverter.convertMany(node.keys, Resolvable.class, convContext),*/
				/*DescentASTConverter.convertMany(node.values, Resolvable.class, convContext),*/
				ArrayView.create(new ExpLiteralMapArray.MapArrayLiteralKeyValue[0])
			)
		));
	}
	
	@Override
	public boolean visit(CompileDeclaration node) {
		return endAdapt(connect(DefinitionConverter.sourceRange(node),
			new DeclarationMixinString(
				ExpressionConverter.convert(node.exp, convContext))
			)
		);
	}

	@Override
	public boolean visit(CompileExp node) {
		return endAdapt(connect(DefinitionConverter.sourceRange(node),
			new ExpMixinString(
				ExpressionConverter.convert(node.e1, convContext))
			)
		);
	}

	@Override
	public boolean visit(CompileStatement node) {
		return endAdapt(connect(DefinitionConverter.sourceRange(node),
			new DeclarationMixinString(
				ExpressionConverter.convert(node.exp, convContext))
			)
		);
	}

	
	/* Initializers */
	
	@Override
	public boolean visit(ArrayInitializer element) {
		return endAdapt(connect(DefinitionConverter.sourceRange(element),
			new InitializerArray_Old(
				ExpressionConverter.convertMany(element.index, convContext),
				DescentASTConverter.convertMany(element.value, Initializer.class, convContext))
			)
		);
	}

	@Override
	public boolean visit(ExpInitializer element) {
		return endAdapt(connect(DefinitionConverter.sourceRange(element, !(element.exp instanceof ErrorExp)),
			new InitializerExp(
				ExpressionConverter.convert(element.exp, convContext))
			)
		);
	}

	@Override
	public boolean visit(StructInitializer element) {
		RefIdentifier[] indices = new RefIdentifier[element.field.size()];
		for(int i = 0; i < element.field.size(); ++i) {
			IdentifierExp id = element.field.get(i);
			ExpReference expref = (ExpReference) DescentASTConverter.convertElem(id, convContext);
			if(expref == null) {
				indices[i] = null;
			} else {
				indices[i] = (RefIdentifier) expref.ref;
				indices[i].parent = null; //reset parent
			}
		}
		
		return endAdapt(connect(DefinitionConverter.sourceRange(element),
			new InitializerStruct_Old(
				ArrayView.create(indices),
				DescentASTConverter.convertMany(element.value, Initializer.class, convContext))
			)
		);
	}

	@Override
	public boolean visit(VoidInitializer element) {
		return endAdapt(connect(DefinitionConverter.sourceRange(element), new InitializerVoid()));
	}

	
	/* ===================== Special ===================== */
	@Override
	public boolean visit(ArrayExp elem) {
		Expression array = ExpressionConverter.convert(elem.e1, convContext);
		ArrayView<Expression> args = DescentASTConverter.convertMany(elem.arguments, Expression.class, convContext);
		return endAdapt(connect(DefinitionConverter.sourceRange(elem), new ExpIndex(array, args)));
	}
	
	@Override
	public boolean visit(ArrayLengthExp element) {
		return endAdapt(connect(DefinitionConverter.sourceRange(element), new ExpArrayLength()));
	}
	
	@Override
	public boolean visit(ArrayLiteralExp element) {
		return endAdapt(ExpressionConverter.createExpArrayLiteral(element, convContext));
	}
	
	@Override
	public boolean visit(AssertExp elem) {
		Expression exp = ExpressionConverter.convert(elem.e1, convContext);
		Expression msg = ExpressionConverter.convert(elem.msg, convContext);
		return endAdapt(connect(DefinitionConverter.sourceRange(elem), new ExpAssert(exp, msg)));
	}
	
	@Override
	public boolean visit(CallExp elem) {
		return endAdapt(ExpressionConverter.createExpCall(elem, convContext));
	}
	
	@Override
	public boolean visit(CastExp elem) {
		Expression exp = ExpressionConverter.convert(elem.sourceE1, convContext);
		Reference type = ReferenceConverter.convertType(elem.sourceTo, convContext);
		return endAdapt(DefinitionConverter.sourceRange(elem), new ExpCast(type, exp));
	}
	
	@Override
	public boolean visit(CondExp elem) {
		Resolvable predExp = ExpressionConverter.convert(elem.econd, convContext); 
		Resolvable trueExp = ExpressionConverter.convert(elem.e1, convContext);
		Resolvable falseExp = ExpressionConverter.convert(elem.e2, convContext); 
		
		return endAdapt(connect(DefinitionConverter.sourceRange(elem),
				new ExpConditional(predExp, trueExp, falseExp)));
	}
	
	@Override
	public boolean visit(DeleteExp element) {
		return endAdapt(DefinitionConverter.sourceRange(element),
			new ExpPrefix(
				PrefixOpType.DELETE,
				ExpressionConverter.convert(element.e1, convContext)
			)
		);
	}
	
	@Override
	public boolean visit(DollarExp element) {
		return endAdapt(DefinitionConverter.sourceRange(element), new ExpArrayLength());
	}
	
	@Override
	public boolean visit(FileInitExp element) {
		return endAdapt(DefinitionConverter.sourceRange(element),
			new ExpDefaultInit(
				ExpDefaultInit.DefaultInit.FILE
			)
		);
	}
	
	@Override
	public boolean visit(LineInitExp element) {
		return endAdapt(DefinitionConverter.sourceRange(element),
			new ExpDefaultInit(
				ExpDefaultInit.DefaultInit.LINE
			)
		);
	}
	
	@Override
	public boolean visit(FuncExp element) {
		TypeFunction elemTypeFunc = (TypeFunction) element.fd.type;
		
		IStatement frequire = StatementConverterVisitor.convertStatement(element.fd.frequire, convContext);
		IStatement fensure = StatementConverterVisitor.convertStatement(element.fd.fensure, convContext);
		BlockStatement fbody = (BlockStatement) 
			StatementConverterVisitor.convertStatement(element.fd.fbody, convContext);
		
		IFunctionBody fnBody;
		if(frequire == null && fensure == null) {
			if(fbody == null) {
				fnBody = null;
			} else {
				fnBody =  fbody;
			}
		} else {
			/*WATHEVAR*/
			fnBody = new InOutFunctionBody(false, null, null, fbody);
		}
		
		return endAdapt(connect(DefinitionConverter.sourceRange(element), 
			new ExpFunctionLiteral(
				Boolean.TRUE,
				ReferenceConverter.convertType(elemTypeFunc.next, convContext),
				DescentASTConverter.convertMany(elemTypeFunc.parameters, IFunctionParameter.class, convContext),
				null,
				fnBody
			)) 
		);
	}
	
	@Override
	public boolean visit(IdentifierExp element) {
		return endAdapt(ReferenceConverter.createExpReference(element));
	}
	
	@Override
	public boolean visit(IsExp element) {
		return endAdapt(DefinitionConverter.sourceRange(element),
			new ExpIftype(
				ReferenceConverter.convertType(element.targ, convContext),
				element.tok,
				ReferenceConverter.convertType(element.tspec, convContext)
			)
		);
	}
	
	@Override
	public boolean visit(IntegerExp element) {
		if (((TypeBasic) element.type).ty == TY.Tbool) {
			return endAdapt(DefinitionConverter.sourceRange(element),
				new ExpLiteralBool(
					element.value.intValue() != 0
				)
			);
		} else {
			return endAdapt(DefinitionConverter.sourceRange(element, !(element instanceof ErrorExp)),
				new ExpLiteralInteger(makeToken(DeeTokens.INTEGER_DECIMAL, element.str, element.getStart())
				)
			);
		}
	}
	
	@Override
	public boolean visit(NewAnonClassExp element) {
		return endAdapt(DefinitionConverter.sourceRange(element),
			new ExpNewAnonClass(
				DescentASTConverter.convertMany(element.newargs, Expression.class, convContext),
				DescentASTConverter.convertMany(element.arguments, Expression.class, convContext),
				DescentASTConverter.convertMany(element.cd.sourceBaseclasses, Reference.class, convContext),
				createDeclList(DescentASTConverter.convertMany(element.cd.members, ASTNode.class, convContext))
			)
		);
	}
	
	@Override
	public boolean visit(NewExp element) {
		Reference type = element.newtype == null ? 
				new Reference.InvalidSyntaxReference() : 
				ReferenceConverter.convertType(element.newtype, convContext);
		return endAdapt(DefinitionConverter.sourceRange(element),
			new ExpNew(
				DescentASTConverter.convertMany(element.newargs, Expression.class, convContext),
				type,
				DescentASTConverter.convertMany(element.arguments, Expression.class, convContext)
			)
		);
	}
	
	@Override
	public boolean visit(NullExp element) {
		return endAdapt(DefinitionConverter.sourceRange(element), new ExpNull());
	}
	
	@Override
	public boolean visit(RealExp element) {
		return endAdapt(DefinitionConverter.sourceRange(element), 
			new ExpLiteralFloat(makeToken(DeeTokens.FLOAT_DECIMAL, element.str, element.getStartPos())));
	}
	
	@Override
	public boolean visit(ScopeExp element) {
		return endAdapt(ReferenceConverter.createExpReference(element, convContext));
	}
	
	@Override
	public boolean visit(SliceExp element) {
		return endAdapt(connect(DefinitionConverter.sourceRange(element),
			new ExpSlice(
				ExpressionConverter.convert(element.e1, convContext),
				ExpressionConverter.convert(element.lwr, convContext),
				ExpressionConverter.convert(element.upr, convContext)
			)
		));
	}
	
	@Override
	public boolean visit(StringExp element) {
// TODO: AST CONV: deal with elem.allStringExps
//		this.strings = new char[elem.strings.size()][];
//		for (int i = 0; i < elem.strings.size(); i++) {
//			this.strings[i] = elem.strings.get(i).string;
//		}
		return endAdapt(DefinitionConverter.sourceRange(element),
			new ExpLiteralString(makeToken(DeeTokens.STRING_DQ, element.sourceString, element.getStartPos())));
	}
	
	@Override
	public boolean visit(SuperExp element) {
		return endAdapt(DefinitionConverter.sourceRange(element), new ExpSuper());
	}
	
	@Override
	public boolean visit(ThisExp element) {
		return endAdapt(DefinitionConverter.sourceRange(element), new ExpThis());
	}
	
	@Override
	public boolean visit(TypeidExp element) {
		assertTrue(element.typeidType != null || element.argumentExp__DDT_ADDITION != null);
		return endAdapt(DefinitionConverter.sourceRange(element), 
			element.typeidType != null
			? new ExpTypeId(
				ReferenceConverter.convertType(element.typeidType, convContext)
			)
			: new ExpTypeId(
				ExpressionConverter.convert(element.argumentExp__DDT_ADDITION, convContext)
			)
		);
	}	

	/* ===================== Unary ===================== */
	

	@Override
	public boolean visit(UnaExp element) {
		return assertFailABSTRACT_NODE();
	}
	
	@Override
	public boolean visit(AddrExp element) {
		return endAdapt(DefinitionConverter.sourceRange(element),
			new ExpPrefix(
				ExpPrefix.PrefixOpType.ADDRESS,
				ExpressionConverter.convert(element.e1, convContext)
			)
		);
	}
	
	@Override
	public boolean visit(ComExp element) {
		return endAdapt(DefinitionConverter.sourceRange(element),
			new ExpPrefix(
				ExpPrefix.PrefixOpType.COMPLEMENT,
				ExpressionConverter.convert(element.e1, convContext)
			)
		);
	}	
	
	@Override
	public boolean visit(NegExp element) {
		return endAdapt(DefinitionConverter.sourceRange(element),
			new ExpPrefix(
				ExpPrefix.PrefixOpType.NEGATIVE,
				ExpressionConverter.convert(element.e1, convContext)
			)
		);
	}
	
	@Override
	public boolean visit(NotExp element) {
		return endAdapt(DefinitionConverter.sourceRange(element),
			new ExpPrefix(
				ExpPrefix.PrefixOpType.NOT,
				ExpressionConverter.convert(element.e1, convContext)
			)
		);
	}	

	
	@Override
	public boolean visit(IndexExp node) {
		return assertFailFAKENODE();
	}

	@Override
	public boolean visit(PostExp node) {
		int type = 0;
		switch (node.op) {
		case TOKplusplus: type = 1; break;
		case TOKminusminus: type = 1; break;
		default: Assert.fail();
		}
		
		return endAdapt(DefinitionConverter.sourceRange(node),
			new ExpPostfixOperator(
				(Resolvable) DescentASTConverter.convertElem(node.e1, convContext),
				PostfixOpType.POST_DECREMENT
			)
		);
	}

	@Override
	public boolean visit(PtrExp element) {
		return endAdapt(DefinitionConverter.sourceRange(element),
			new ExpPrefix(
				ExpPrefix.PrefixOpType.REFERENCE,
				ExpressionConverter.convert(element.e1, convContext)
			)
		);
	}
	
	@Override
	public boolean visit(UAddExp element) {
		return endAdapt(DefinitionConverter.sourceRange(element),
			new ExpPrefix(
				ExpPrefix.PrefixOpType.POSITIVE,
				ExpressionConverter.convert(element.e1, convContext)
			)
		);
	}	

	/* ===================== Binary ===================== */
	
	@Override
	public boolean visit(BinExp element) {
		Assert.fail("Error visited abstract class."); return false;
	}
	
	public ExpInfix newExpInfix(Resolvable left, DeeTokens kind, Resolvable right, SourceRange sourceRange) {
		return connect(sourceRange,
			new ExpInfix((Expression) left, InfixOpType.tokenToInfixOpType(kind), (Expression) right));
	}

	@Override
	public boolean visit(AddAssignExp element) {
		Expression exp1 = ExpressionConverter.convert(element.e1, convContext);
		SourceRange sourceRange = DefinitionConverter.sourceRange(element);
		Resolvable newelem = element.isPreIncrement
				? connect(sourceRange, new ExpPrefix(ExpPrefix.PrefixOpType.PRE_INCREMENT, exp1))
				: newExpInfix(
					exp1,
					DeeTokens.PLUS_ASSIGN,
					ExpressionConverter.convert(element.e2, convContext),
					sourceRange
				);
		return endAdapt(newelem);
	}
	

	@Override
	public boolean visit(MinAssignExp element) {
		Expression exp1 = ExpressionConverter.convert(element.e1, convContext);
		SourceRange sourceRange = DefinitionConverter.sourceRange(element);
		Resolvable newelem = element.isPreDecrement
				? connect(sourceRange, new ExpPrefix(ExpPrefix.PrefixOpType.PRE_DECREMENT, exp1))
				: newExpInfix(
					exp1, 
					DeeTokens.MINUS_ASSIGN,
					ExpressionConverter.convert(element.e2, convContext),
					sourceRange
				);
		return endAdapt(newelem);
	}
	
	
	@Override
	public boolean visit(AddExp element) {
		return endAdapt(
			newExpInfix(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				DeeTokens.PLUS,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(AndAndExp element) {
		return endAdapt(
			newExpInfix(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				DeeTokens.LOGICAL_AND,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(AndAssignExp element) {
		return endAdapt(
			newExpInfix(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				DeeTokens.AND_ASSIGN,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(AndExp element) {
		return endAdapt(
			newExpInfix(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				DeeTokens.AND,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(AssignExp element) {
		return endAdapt(
			newExpInfix(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				DeeTokens.ASSIGN,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(CatAssignExp element) {
		return endAdapt(
			newExpInfix(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				DeeTokens.CONCAT_ASSIGN,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(CatExp element) {
		return endAdapt(
			newExpInfix(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				DeeTokens.CONCAT,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(CmpExp element) {
		return endAdapt(
			newExpInfix(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				DeeTokens.EQUALS,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(CommaExp element) {
		return endAdapt(
			newExpInfix(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				DeeTokens.COMMA,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(DivAssignExp element) {
		return endAdapt(
			newExpInfix(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				DeeTokens.DIV_ASSIGN,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(DivExp element) {
		return endAdapt(
			newExpInfix(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				DeeTokens.DIV,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(EqualExp element) {
		return endAdapt(
			newExpInfix(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				DeeTokens.ASSIGN,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(IdentityExp element) {
		Assert.isTrue(element.op == TOK.TOKis || element.op == TOK.TOKnotis);
		
		return endAdapt(
			newExpInfix(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				element.op == TOK.TOKis ? DeeTokens.KW_IS : DeeTokens.KW_IS,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(InExp element) {
		return endAdapt(
			newExpInfix(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				DeeTokens.KW_IN,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(MinExp element) {
		return endAdapt(
			newExpInfix(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				DeeTokens.MINUS,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(ModAssignExp element) {
		return endAdapt(
			newExpInfix(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				DeeTokens.MOD_ASSIGN,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(ModExp element) {
		return endAdapt(
			newExpInfix(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				DeeTokens.MOD,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(MulAssignExp element) {
		return endAdapt(
			newExpInfix(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				DeeTokens.MULT_ASSIGN,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(MulExp element) {
		return endAdapt(
			newExpInfix(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				DeeTokens.STAR,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(OrAssignExp element) {
		return endAdapt(
			newExpInfix(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				DeeTokens.OR_ASSIGN,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(OrExp element) {
		return endAdapt(
			newExpInfix(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				DeeTokens.OR,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(OrOrExp element) {
		return endAdapt(
			newExpInfix(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				DeeTokens.LOGICAL_OR,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(ShlAssignExp element) {
		return endAdapt(
			newExpInfix(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				DeeTokens.LEFT_SHIFT_ASSIGN,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(ShlExp element) {
		return endAdapt(
			newExpInfix(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				DeeTokens.LEFT_SHIFT,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(ShrAssignExp element) {
		return endAdapt(
			newExpInfix(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				DeeTokens.RIGHT_SHIFT_ASSIGN,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(ShrExp element) {
		return endAdapt(
			newExpInfix(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				DeeTokens.RIGHT_SHIFT,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(UshrAssignExp element) {
		return endAdapt(
			newExpInfix(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				DeeTokens.TRIPLE_RSHIFT_ASSIGN,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(UshrExp element) {
		return endAdapt(
			newExpInfix(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				DeeTokens.TRIPLE_RSHIFT,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(XorAssignExp element) {
		return endAdapt(
			newExpInfix(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				DeeTokens.XOR_ASSIGN,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(XorExp element) {
		return endAdapt(
			newExpInfix(
				(Resolvable) DescentASTConverter.convertElem(element.e1, convContext),
				DeeTokens.XOR,
				(Resolvable) DescentASTConverter.convertElem(element.e2, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
}
