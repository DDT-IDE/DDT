package dtool.descentadapter;

import static melnorme.utilbox.core.CoreUtil.array;
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
import dtool.ast.SourceRange;
import dtool.ast.declarations.DeclarationPragma;
import dtool.ast.declarations.DeclarationStaticAssert;
import dtool.ast.declarations.NodeList;
import dtool.ast.definitions.FunctionParameter;
import dtool.ast.definitions.IFunctionParameter;
import dtool.ast.statements.BlockStatement;
import dtool.ast.statements.IStatement;
import dtool.ast.statements.Statement;
import dtool.ast.statements.StatementAsm;
import dtool.ast.statements.StatementBreak;
import dtool.ast.statements.StatementCase;
import dtool.ast.statements.StatementCaseRange;
import dtool.ast.statements.StatementContinue;
import dtool.ast.statements.StatementDefault;
import dtool.ast.statements.StatementDo;
import dtool.ast.statements.StatementExp;
import dtool.ast.statements.StatementFor;
import dtool.ast.statements.StatementForeach;
import dtool.ast.statements.StatementForeachRange;
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

public class StatementConverterVisitor extends ExpressionConverterVisitor {
	
	@Override
	public boolean visit(ForeachRangeStatement elem) {
		return endAdapt(
			new StatementForeachRange(
				(IFunctionParameter) DescentASTConverter.convertElem(elem.arg, convContext), // this used to be null b4 refactoring POSSIBLE BUG HERE
				ExpressionConverter.convert(elem.lwr, convContext),
				ExpressionConverter.convert(elem.upr, convContext),
				Statement.convert(elem.body, convContext),
				elem.op == TOK.TOKforeach_reverse,
				DefinitionConverter.sourceRange(elem)
			)
		);
	}
	
	@Override
	public boolean visit(AsmBlock elem) {
		return endAdapt(
			new BlockStatement(
				convertStatements(elem),
				true, // TODO: How do we know if it is curly or not?
				DefinitionConverter.sourceRange(elem)
			)
		);
	}
	@Override
	public boolean visit(descent.internal.compiler.parser.CompoundStatement elem) {
		return endAdapt(
			new BlockStatement(
				convertStatements(elem),
				false, // TODO: How do we know if it is curly or not?
				DefinitionConverter.sourceRange(elem)
			)
		);
	}
	
	public IStatement[] convertStatements(descent.internal.compiler.parser.CompoundStatement elem) {
		IStatement[] statements = DescentASTConverter.convertMany(elem.statements, IStatement.class, convContext);
		for(@SuppressWarnings("unused")	IStatement decl : statements) {
			// just check class cast
		}
		return statements;
	}
	
	@Override
	public boolean visit(AsmStatement element) {
		return endAdapt(new StatementAsm(DefinitionConverter.sourceRange(element)));
	}
	
	@Override
	public boolean visit(BreakStatement element) {
		return endAdapt(
			new StatementBreak(
				element.ident == null ? null : DefinitionConverter.convertId(element.ident),
				DefinitionConverter.sourceRange(element)
			)
		);
	}

	@Override
	public boolean visit(CaseStatement element) {
		return endAdapt(
			new StatementCase(
				ExpressionConverter.convert(element.exp, convContext),
				Statement.convert(element.statement, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(CaseRangeStatement element) {
		return endAdapt(
			new StatementCaseRange(
				ExpressionConverter.convert(element.first, convContext),
				ExpressionConverter.convert(element.last, convContext),
				Statement.convert(element.statement, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(ConditionalStatement element) {
		return endAdapt(DeclarationConverter.convert(element, convContext));
	}

	@Override
	public boolean visit(ContinueStatement element) {
		return endAdapt(
			new StatementContinue(
				//element.ident == null ? null : DefinitionConverter.convertId(element.ident), 
				DefinitionConverter.convertId(element.ident), // BUG HERE (temporary)
				DefinitionConverter.sourceRange(element)
			)
		);
	}

	@Override
	public boolean visit(DeclarationStatement element) {
		return visit((DeclarationExp) element.exp);
	}

	@Override
	public boolean visit(DefaultStatement element) {
		return endAdapt(
			new StatementDefault(
				Statement.convert(element.statement, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}

	@Override
	public boolean visit(DoStatement element) {
		return endAdapt(
			new StatementDo(
				ExpressionConverter.convert(element.condition, convContext),
				Statement.convert(element.body, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(ExpStatement element) {
		SourceRange sourceRange = element.hasNoSourceRangeInfo() && element.exp != null
			? DefinitionConverter.sourceRange(element.exp)
			: DefinitionConverter.sourceRange(element);
		return endAdapt(new StatementExp(ExpressionConverter.convert(element.exp, convContext), sourceRange));
	}
	
	@Override
	public boolean visit(ForeachStatement element) {
		return endAdapt(
			new StatementForeach(
				DescentASTConverter.convertMany(element.arguments.toArray(), IFunctionParameter.class, convContext), /*Used to be null*/ // POSSIBLE BUG HERE
				ExpressionConverter.convert(element.sourceAggr, convContext),
				Statement.convert(element.body, convContext),
				element.op == TOK.TOKforeach_reverse,
				DefinitionConverter.sourceRange(element)
			)
		);
	}

	@Override
	public boolean visit(ForStatement element) {
		return endAdapt(
			new StatementFor(
				Statement.convert(element.init, convContext),
				ExpressionConverter.convert(element.condition, convContext),
				ExpressionConverter.convert(element.increment, convContext),
				Statement.convert(element.body, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}

	@Override
	public boolean visit(GotoCaseStatement element) {
		return endAdapt(
			new StatementGotoCase(
				ExpressionConverter.convert(element.exp, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}

	@Override
	public boolean visit(GotoDefaultStatement element) {
		return endAdapt(new StatementGotoDefault(DefinitionConverter.sourceRange(element)));
	}

	@Override
	public boolean visit(GotoStatement element) {
		return endAdapt(
			new StatementGoto(
				DefinitionConverter.convertId(element.ident),
				DefinitionConverter.sourceRange(element)
			)
		);
	}

	@Override
	public boolean visit(IfStatement element) {
		return endAdapt(
			new StatementIf(
				ExpressionConverter.convert(element.condition, convContext),
				Statement.convert(element.ifbody, convContext),
				Statement.convert(element.elsebody, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}

	@Override
	public boolean visit(LabelStatement element) {
		return endAdapt(
			new StatementLabel(
				DefinitionConverter.convertId(element.ident),
				DefinitionConverter.sourceRange(element)
			)
		);
	}

	@Override
	public boolean visit(OnScopeStatement element) {
		return endAdapt(
			new StatementOnScope(
				StatementOnScope.EventType.ON_EXIT, // TODO: Find out how to access this scope value
				Statement.convert(element.statement, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}

	@Override
	public boolean visit(PragmaStatement element) {
		NodeList body = NodeList.createNodeList(element.body, convContext);
		return endAdapt(
			new DeclarationPragma(
				DefinitionConverter.convertId(element.ident),
				element.args != null ? ExpressionConverter.convertMany(element.args, convContext) : null,
				body,
				DefinitionConverter.sourceRange(element)
			)
		);
	}

	@Override
	public boolean visit(ReturnStatement element) {
		return endAdapt(
			new StatementReturn(
				ExpressionConverter.convert(element.exp, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}

	@Override
	public boolean visit(ScopeStatement element) {
		IStatement[] statements = null;
		boolean hasCurlyBraces = false;
		if(element.statement instanceof descent.internal.compiler.parser.CompoundStatement) {
			descent.internal.compiler.parser.CompoundStatement compoundSt = 
				(descent.internal.compiler.parser.CompoundStatement) element.statement;
			statements = DescentASTConverter.convertMany(compoundSt.statements, IStatement.class, 
					convContext);
			hasCurlyBraces = true;
		} else {
			statements = DescentASTConverter.convertMany(array(element.statement), IStatement.class, 
					convContext);
		}

		return endAdapt(
			new BlockStatement(
				statements,
				hasCurlyBraces,
				DefinitionConverter.sourceRange(element)
			)
		);
	}

	@Override
	public boolean visit(StaticAssertStatement element) {
		return endAdapt(
			new DeclarationStaticAssert(
				ExpressionConverter.convert(element.sa.exp, convContext),
				ExpressionConverter.convert(element.sa.msg, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}

	@Override
	public boolean visit(SwitchStatement element) {
		return endAdapt(
			new StatementSwitch(
				ExpressionConverter.convert(element.condition, convContext),
				Statement.convert(element.body, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}

	@Override
	public boolean visit(SynchronizedStatement element) {
		return endAdapt(
			new StatementSynchronized(
				ExpressionConverter.convert(element.exp, convContext),
				Statement.convert(element.body, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}

	@Override
	public boolean visit(ThrowStatement element) {
		return endAdapt(
			new StatementThrow(
				ExpressionConverter.convert(element.exp, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}
	
	@Override
	public boolean visit(TryCatchStatement element) {
		Object[] catches = element.catches.toArray();
		return endAdapt(
			new StatementTry(
				Statement.convert(element.body, convContext),
				DescentASTConverter.convertMany(catches, CatchClause.class, convContext),
				null, 
				DefinitionConverter.sourceRange(element)
			)
		);
	}

	@Override
	public boolean visit(TryFinallyStatement element) {
		if (element.body instanceof TryCatchStatement) {
			TryCatchStatement tcs = (TryCatchStatement) element.body;
			Object[] catches = tcs.catches.toArray();
			return endAdapt(
				new StatementTry(
					Statement.convert(tcs.body, convContext),
					DescentASTConverter.convertMany(catches, CatchClause.class, convContext),
					Statement.convert(element.finalbody, convContext), 
					DefinitionConverter.sourceRange(element)
				)
			);
		} else {
			return endAdapt(
				new StatementTry(
					Statement.convert(element.body, convContext),
					new CatchClause[0],
					Statement.convert(element.finalbody, convContext), 
					DefinitionConverter.sourceRange(element)
				)
			);
		}
	}

	@Override
	public boolean visit(VolatileStatement element) {
		return endAdapt(
			new StatementVolatile(
				Statement.convert(element.statement, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}

	@Override
	public boolean visit(WhileStatement element) {
		return endAdapt(
			new StatementWhile(
				ExpressionConverter.convert(element.condition, convContext),
				Statement.convert(element.body, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}

	@Override
	public boolean visit(WithStatement element) {
		return endAdapt(
			new StatementWith(
				ExpressionConverter.convert(element.exp, convContext),
				Statement.convert(element.body, convContext),
				DefinitionConverter.sourceRange(element)
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
			param = new FunctionParameter(element.type, element.ident, convContext);
		}
		
		return endAdapt(
			new StatementTry.CatchClause(
				param,
				Statement.convert(element.handler, convContext),
				DefinitionConverter.sourceRange(element)
			)
		);
	}

	@Override
	public boolean visit(DeclarationExp elem) {
		return endAdapt(DescentASTConverter.convertElem(elem.declaration, convContext));
	}

}
