package dtool.descentadapter;

import static melnorme.utilbox.core.CoreUtil.array;

import java.util.Collections;

import descent.internal.compiler.parser.AsmBlock;
import descent.internal.compiler.parser.AsmStatement;
import descent.internal.compiler.parser.BreakStatement;
import descent.internal.compiler.parser.CaseRangeStatement;
import descent.internal.compiler.parser.CaseStatement;
import descent.internal.compiler.parser.Catch;
import descent.internal.compiler.parser.ConditionalStatement;
import descent.internal.compiler.parser.ContinueStatement;
import descent.internal.compiler.parser.DeclarationExp;
import descent.internal.compiler.parser.DeclarationStatement;
import descent.internal.compiler.parser.DefaultStatement;
import descent.internal.compiler.parser.DoStatement;
import descent.internal.compiler.parser.ExpStatement;
import descent.internal.compiler.parser.ForStatement;
import descent.internal.compiler.parser.ForeachRangeStatement;
import descent.internal.compiler.parser.ForeachStatement;
import descent.internal.compiler.parser.GotoCaseStatement;
import descent.internal.compiler.parser.GotoDefaultStatement;
import descent.internal.compiler.parser.GotoStatement;
import descent.internal.compiler.parser.IfStatement;
import descent.internal.compiler.parser.LabelStatement;
import descent.internal.compiler.parser.OnScopeStatement;
import descent.internal.compiler.parser.PragmaStatement;
import descent.internal.compiler.parser.ReturnStatement;
import descent.internal.compiler.parser.ScopeStatement;
import descent.internal.compiler.parser.StaticAssertStatement;
import descent.internal.compiler.parser.SwitchStatement;
import descent.internal.compiler.parser.SynchronizedStatement;
import descent.internal.compiler.parser.TOK;
import descent.internal.compiler.parser.ThrowStatement;
import descent.internal.compiler.parser.TryCatchStatement;
import descent.internal.compiler.parser.TryFinallyStatement;
import descent.internal.compiler.parser.VolatileStatement;
import descent.internal.compiler.parser.WhileStatement;
import descent.internal.compiler.parser.WithStatement;
import dtool.ast.NodeList;
import dtool.ast.SourceRange;
import dtool.ast.declarations.DeclarationAttrib.AttribBodySyntax;
import dtool.ast.declarations.DeclarationPragma;
import dtool.ast.declarations.DeclarationStaticAssert;
import dtool.ast.definitions.FunctionParameter;
import dtool.ast.definitions.IFunctionParameter;
import dtool.ast.statements.BlockStatement;
import dtool.ast.statements.ForeachRangeExpression;
import dtool.ast.statements.ForeachVariableDef;
import dtool.ast.statements.IStatement;
import dtool.ast.statements.StatementAsm;
import dtool.ast.statements.StatementBreak;
import dtool.ast.statements.StatementCase;
import dtool.ast.statements.StatementCaseRange;
import dtool.ast.statements.StatementContinue;
import dtool.ast.statements.StatementDefault;
import dtool.ast.statements.StatementDoWhile;
import dtool.ast.statements.StatementExp;
import dtool.ast.statements.StatementFor;
import dtool.ast.statements.StatementForeach;
import dtool.ast.statements.StatementGoto;
import dtool.ast.statements.StatementGotoCase;
import dtool.ast.statements.StatementGotoDefault;
import dtool.ast.statements.StatementIf;
import dtool.ast.statements.StatementLabel;
import dtool.ast.statements.StatementOnScope;
import dtool.ast.statements.StatementReturn;
import dtool.ast.statements.StatementSwitch;
import dtool.ast.statements.StatementSynchronized;
import dtool.ast.statements.StatementThrow;
import dtool.ast.statements.StatementTry;
import dtool.ast.statements.StatementTry.CatchClause;
import dtool.ast.statements.StatementVolatile;
import dtool.ast.statements.StatementWhile;
import dtool.ast.statements.StatementWith;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.util.ArrayView;

public class StatementConverterVisitor extends ExpressionConverterVisitor {
	
	@Override
	public boolean visit(ForeachRangeStatement elem) {
		return endAdapt(DefinitionConverter.sourceRange(elem), 
			new StatementForeach(
				elem.op == TOK.TOKforeach_reverse,
				ArrayView.create(new ForeachVariableDef[0] /* WHATEVER*/ ),
//				(IFunctionParameter) DescentASTConverter.convertElem(elem.arg, convContext)),
				new ForeachRangeExpression(
					ExpressionConverter.convert(elem.lwr, convContext),
					ExpressionConverter.convert(elem.upr, convContext)
				),
				StatementConverterVisitor.convertStatement(elem.body, convContext)
			)
		);
	}
	
	@Override
	public boolean visit(AsmBlock elem) {
		return endAdapt(DefinitionConverter.sourceRange(elem),
			new BlockStatement(
				convertStatements(elem)
			)
		);
	}
	@Override
	public boolean visit(descent.internal.compiler.parser.CompoundStatement elem) {
		return endAdapt(DefinitionConverter.sourceRange(elem),
			new BlockStatement(
				convertStatements(elem)
			)
		);
	}
	
	public ArrayView<IStatement> convertStatements(descent.internal.compiler.parser.CompoundStatement elem) {
		ArrayView<IStatement> statements = DescentASTConverter.convertMany(elem.statements, IStatement.class, convContext);
		for(@SuppressWarnings("unused")	IStatement decl : statements) {
			// just check class cast
		}
		return statements;
	}
	
	@Override
	public boolean visit(AsmStatement element) {
		return endAdapt(connect(DefinitionConverter.sourceRange(element), new StatementAsm()));
	}
	
	@Override
	public boolean visit(BreakStatement element) {
		return endAdapt(DefinitionConverter.sourceRange(element), 
			new StatementBreak(
				element.ident == null ? null : DefinitionConverter.convertId(element.ident)
			)
		);
	}

	@Override
	public boolean visit(CaseStatement element) {
		return endAdapt(DefinitionConverter.sourceRange(element), 
			new StatementCase(
				ArrayView.create(array(ExpressionConverter.convert(element.exp, convContext))),
				convertStatement(element.statement, convContext)
			)
		);
	}
	
	@Override
	public boolean visit(CaseRangeStatement element) {
		return endAdapt(DefinitionConverter.sourceRange(element), 
			new StatementCaseRange(
				ExpressionConverter.convert(element.first, convContext),
				ExpressionConverter.convert(element.last, convContext),
				convertStatement(element.statement, convContext)
			)
		);
	}
	
	@Override
	public boolean visit(ConditionalStatement element) {
		return endAdapt(DeclarationConverter.convert(element, convContext));
	}

	@Override
	public boolean visit(ContinueStatement element) {
		return endAdapt(DefinitionConverter.sourceRange(element), 
			new StatementContinue(
				element.ident == null ? null : DefinitionConverter.convertId(element.ident)
			)
		);
	}

	@Override
	public boolean visit(DeclarationStatement element) {
		return visit((DeclarationExp) element.exp);
	}

	@Override
	public boolean visit(DefaultStatement element) {
		return endAdapt(DefinitionConverter.sourceRange(element), 
			new StatementDefault(
				convertStatement(element.statement, convContext)
			)
		);
	}

	@Override
	public boolean visit(DoStatement element) {
		return endAdapt(DefinitionConverter.sourceRange(element), 
			new StatementDoWhile(
				convertStatement(element.body, convContext),
				ExpressionConverter.convert(element.condition, convContext)
			)
		);
	}
	
	@Override
	public boolean visit(ExpStatement element) {
		SourceRange sourceRange = element.hasNoSourceRangeInfo() && element.exp != null
			? DefinitionConverter.sourceRange(element.exp)
			: DefinitionConverter.sourceRange(element);
		return endAdapt(sourceRange,
			new StatementExp(ExpressionConverter.convert(element.exp, convContext)));
	}
	
	@Override
	public boolean visit(ForeachStatement element) {
		return endAdapt(DefinitionConverter.sourceRange(element), 
			new StatementForeach(
				element.op == TOK.TOKforeach_reverse,
				ArrayView.create(new ForeachVariableDef[0] /* WHATEVER*/ ),
				//DescentASTConverter.convertMany(element.arguments, IFunctionParameter.class, convContext),
				ExpressionConverter.convert(element.sourceAggr, convContext),
				convertStatement(element.body, convContext)
			)
		);
	}
	
	@Override
	public boolean visit(ForStatement element) {
		return endAdapt(DefinitionConverter.sourceRange(element), 
			new StatementFor(
				convertStatement(element.init, convContext),
				ExpressionConverter.convert(element.condition, convContext),
				ExpressionConverter.convert(element.increment, convContext),
				convertStatement(element.body, convContext)
			)
		);
	}

	@Override
	public boolean visit(GotoCaseStatement element) {
		return endAdapt(DefinitionConverter.sourceRange(element), 
			new StatementGotoCase(
				ExpressionConverter.convert(element.exp, convContext)
			)
		);
	}

	@Override
	public boolean visit(GotoDefaultStatement element) {
		return endAdapt(DefinitionConverter.sourceRange(element), new StatementGotoDefault());
	}

	@Override
	public boolean visit(GotoStatement element) {
		return endAdapt(DefinitionConverter.sourceRange(element), 
			new StatementGoto(
				DefinitionConverter.convertId(element.ident)
			)
		);
	}

	@Override
	public boolean visit(IfStatement element) {
		return endAdapt(DefinitionConverter.sourceRange(element),
			new StatementIf(
				ExpressionConverter.convert(element.condition, convContext),
				convertStatement(element.ifbody, convContext),
				convertStatement(element.elsebody, convContext)
			)
		);
	}

	@Override
	public boolean visit(LabelStatement element) {
		return endAdapt(DefinitionConverter.sourceRange(element),
			new StatementLabel(
				DefinitionConverter.convertId(element.ident)
			)
		);
	}

	@Override
	public boolean visit(OnScopeStatement element) {
		return endAdapt(DefinitionConverter.sourceRange(element),
			new StatementOnScope(
				StatementOnScope.EventType.ON_EXIT, // TODO: Find out how to access this scope value
				convertStatement(element.statement, convContext)
			)
		);
	}

	@Override
	public boolean visit(PragmaStatement element) {
		NodeList body = DeclarationConverter.createNodeList2(element.body, convContext);
		return endAdapt(DefinitionConverter.sourceRange(element),
			new DeclarationPragma(
				DefinitionConverter.convertId(element.ident),
				ExpressionConverter.convertMany(element.args, convContext),
				AttribBodySyntax.BRACE_BLOCK, body
			)
		);
	}

	@Override
	public boolean visit(ReturnStatement element) {
		return endAdapt(DefinitionConverter.sourceRange(element),
			new StatementReturn(
				ExpressionConverter.convert(element.exp, convContext)
			)
		);
	}

	@Override
	public boolean visit(ScopeStatement element) {
		ArrayView<IStatement> statements = null;
		boolean hasCurlyBraces = false;
		if(element.statement instanceof descent.internal.compiler.parser.CompoundStatement) {
			descent.internal.compiler.parser.CompoundStatement compoundSt = 
				(descent.internal.compiler.parser.CompoundStatement) element.statement;
			statements = DescentASTConverter.convertMany(compoundSt.statements, IStatement.class, convContext);
			hasCurlyBraces = true;
		} else {
			statements = DescentASTConverter.convertMany(Collections.singleton(element.statement), IStatement.class,
					convContext);
		}

		return endAdapt(DefinitionConverter.sourceRange(element),
			new BlockStatement(
				statements
				//,hasCurlyBraces
			)
		);
	}

	@Override
	public boolean visit(StaticAssertStatement element) {
		return endAdapt(DefinitionConverter.sourceRange(element),
			new DeclarationStaticAssert(
				ExpressionConverter.convert(element.sa.exp, convContext),
				ExpressionConverter.convert(element.sa.msg, convContext)
			)
		);
	}

	@Override
	public boolean visit(SwitchStatement element) {
		return endAdapt(DefinitionConverter.sourceRange(element),
			new StatementSwitch(
				false,
				ExpressionConverter.convert(element.condition, convContext),
				convertStatement(element.body, convContext)
			)
		);
	}

	@Override
	public boolean visit(SynchronizedStatement element) {
		return endAdapt(DefinitionConverter.sourceRange(element),
			new StatementSynchronized(
				ExpressionConverter.convert(element.exp, convContext),
				convertStatement(element.body, convContext)
			)
		);
	}

	@Override
	public boolean visit(ThrowStatement element) {
		return endAdapt(DefinitionConverter.sourceRange(element),
			new StatementThrow(
				ExpressionConverter.convert(element.exp, convContext)
			)
		);
	}
	
	@Override
	public boolean visit(TryCatchStatement element) {
		return endAdapt(DefinitionConverter.sourceRange(element),
			new StatementTry(
				convertStatement(element.body, convContext),
				DescentASTConverter.convertMany(element.catches, CatchClause.class, convContext),
				null
			)
		);
	}
	
	@Override
	public boolean visit(TryFinallyStatement element) {
		if (element.body instanceof TryCatchStatement) {
			TryCatchStatement tcs = (TryCatchStatement) element.body;
			return endAdapt(DefinitionConverter.sourceRange(element),
				new StatementTry(
					convertStatement(tcs.body, convContext),
					DescentASTConverter.convertMany(tcs.catches, CatchClause.class, convContext),
					convertStatement(element.finalbody, convContext)
				)
			);
		} else {
			return endAdapt(DefinitionConverter.sourceRange(element),
				new StatementTry(
					convertStatement(element.body, convContext),
					ArrayView.create(new CatchClause[0]),
					convertStatement(element.finalbody, convContext)
				)
			);
		}
	}
	
	@Override
	public boolean visit(VolatileStatement element) {
		return endAdapt(DefinitionConverter.sourceRange(element),
			new StatementVolatile(
				convertStatement(element.statement, convContext)
			)
		);
	}

	@Override
	public boolean visit(WhileStatement element) {
		return endAdapt(DefinitionConverter.sourceRange(element),
			new StatementWhile(
				ExpressionConverter.convert(element.condition, convContext),
				convertStatement(element.body, convContext)
			)
		);
	}

	@Override
	public boolean visit(WithStatement element) {
		return endAdapt(DefinitionConverter.sourceRange(element),
			new StatementWith(
				ExpressionConverter.convert(element.exp, convContext),
				convertStatement(element.body, convContext)
			)
		);
	}
	
	@Override
	public boolean visit(Catch element) {
		IFunctionParameter param;
		if(element.type == null) {
			param = null;
		} else if(element.ident == null) {
			param = DefinitionConverter.convertNamelessParameter(element.type, convContext);
		} else {
			DefUnitTuple dudt = new DefUnitTuple(
				new SourceRange(element.type.getStartPos(), element.ident.getEndPos() - element.type.getStartPos()),
				DefinitionConverter.convertIdToken(element.ident), 
				null
			);
			
			param = connect(dudt.sourceRange, new FunctionParameter(
				null, 
				ReferenceConverter.convertType(element.type, convContext), 
				dudt.defSymbol,
				null, false
			));
		}
		
		return endAdapt(DefinitionConverter.sourceRange(element),
			new StatementTry.CatchClause(
				param,
				convertStatement(element.handler, convContext)
			)
		);
	}

	@Override
	public boolean visit(DeclarationExp elem) {
		return endAdapt(DescentASTConverter.convertElem(elem.declaration, convContext));
	}

	public static IStatement convertStatement(descent.internal.compiler.parser.Statement elem, ASTConversionContext convContext) {
		return DescentASTConverter.convertElem(elem, IStatement.class, convContext);
	}

}