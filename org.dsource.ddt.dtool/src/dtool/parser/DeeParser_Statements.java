/*******************************************************************************
 * Copyright (c) 2013, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.parser;

import java.util.ArrayList;

import dtool.ast.definitions.Symbol;
import dtool.ast.statements.BlockStatement;
import dtool.ast.statements.EmptyStatement;
import dtool.ast.statements.IStatement;
import dtool.ast.statements.StatementLabel;
import dtool.util.ArrayView;


public abstract class DeeParser_Statements extends DeeParser_Decls {
	
	/* ----------------------------------------------------------------- */
	
	public static final ParseRuleDescription RULE_BLOCK = new ParseRuleDescription("Block");
	
	@Override
	protected NodeResult<BlockStatement> parseBlockStatement(boolean createMissing, boolean brokenIfMissing) {
		if(!tryConsume(DeeTokens.OPEN_BRACE)) {
			if(createMissing) {
				return result(brokenIfMissing, createMissingBlock(RULE_BLOCK));
			}
			return nullResult(); 
		}
		ParseHelper parse = new ParseHelper();
		
		ArrayView<IStatement> body = parseStatements(DeeTokens.CLOSE_BRACE);
		parse.consumeRequired(DeeTokens.CLOSE_BRACE);
		
		return parse.resultConclude(new BlockStatement(body, true));
	}
	
	protected ArrayView<IStatement> parseStatements(DeeTokens nodeListTerminator) {
		ArrayList<IStatement> nodeList = new ArrayList<>();
		while(true) {
			if(lookAhead() == nodeListTerminator) {
				break;
			}
			IStatement st = parseStatement().node;
			if(st == null) { 
				break;
			}
			nodeList.add(st);
		}
		
		return arrayViewI(nodeList);
	}
	
	public static final ParseRuleDescription RULE_STATEMENT = new ParseRuleDescription("Statement");
	
	protected NodeResult<? extends IStatement> parseStatement() {
		switch (lookAhead()) {
		case SEMICOLON: 
			consumeLookAhead();
			return resultConclude(false, srOf(lastLexElement(), new EmptyStatement()));
		case OPEN_BRACE:
			return parseBlockStatement(true, true);
		default:
			break;
		}
		
		if(lookAhead() == DeeTokens.IDENTIFIER && lookAhead(1) == DeeTokens.COLON) {
			return parseStatementLabel_start();
		}
		
		NodeResult<? extends IStatement> decl = parseStatementDeclaration();
		
		return decl;
	}
	
	protected NodeResult<StatementLabel> parseStatementLabel_start() {
		LexElement labelId = consumeLookAhead(DeeTokens.IDENTIFIER);
		consumeLookAhead(DeeTokens.COLON);
		
		Symbol label = createIdSymbol(labelId);
		return resultConclude(false, srBounds(labelId.getStartPos(), getLexPosition(), new StatementLabel(label)));
	}
	
}