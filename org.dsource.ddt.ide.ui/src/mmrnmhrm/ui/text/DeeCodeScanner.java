/*******************************************************************************
 * Copyright (c) 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.text;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import mmrnmhrm.ui.text.color.IDeeColorConstants;

import org.eclipse.dltk.ui.text.AbstractScriptScanner;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import descent.internal.compiler.parser.TOK;
import descent.internal.compiler.parser.ast.TokenUtil;

public class DeeCodeScanner extends AbstractScriptScanner {
	
	public DeeCodeScanner(IColorManager manager, IPreferenceStore store) {
		super(manager, store);
		initialize();
	}
	
	private static String fgTokenProperties[] = new String[] {
		IDeeColorConstants.DEE_DEFAULT,
		IDeeColorConstants.DEE_KEYWORDS,
		IDeeColorConstants.DEE_BASICTYPES,
		IDeeColorConstants.DEE_LITERALS,
		IDeeColorConstants.DEE_OPERATORS,
	};
	
	@Override
	protected String[] getTokenProperties() {
		return fgTokenProperties;
	}
	
	@Override
	protected List<IRule> createRules() {
		List<IRule> rules = new ArrayList<IRule>();
		
		IToken tkOther = getToken(IDeeColorConstants.DEE_DEFAULT);
		IToken tkKeyword = getToken(IDeeColorConstants.DEE_KEYWORDS);
		IToken tkBasics = getToken(IDeeColorConstants.DEE_BASICTYPES);
		IToken tkLiterals = getToken(IDeeColorConstants.DEE_LITERALS);
//		IToken tkOperators = getToken(DeeColorConstants.DEE_OPERATORS);
		
		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new LangWhitespaceDetector()));
		
		// Add word rule for keywords, types, and constants.
		WordRule wordRule = new WordRule(new JavaWordDetector(), tkOther);
		addWordsFromTokens(wordRule, TokenUtil.keywords, tkKeyword);
		addWordsFromTokens(wordRule, TokenUtil.basicTypes, tkBasics);
		addWordsFromTokens(wordRule, TokenUtil.specialNamedLiterals, tkLiterals);
		rules.add(wordRule);
		
		setDefaultReturnToken(tkOther);
		return rules;
	}
	
	protected void addWordsFromTokens(WordRule wordRule, TOK[] toks, IToken token) {
		for (TOK tok : toks) {
			assertNotNull(tok.value);
			wordRule.addWord(tok.toString(), token);
		}
	}
	
	public static class LangWhitespaceDetector implements IWhitespaceDetector {
		@Override
		public boolean isWhitespace(char character) {
			return Character.isWhitespace(character);
		}
	}
	
	public static class JavaWordDetector implements IWordDetector {
		
		@Override
		public boolean isWordPart(char character) {
			return Character.isJavaIdentifierPart(character);
		}
		
		@Override
		public boolean isWordStart(char character) {
			return Character.isJavaIdentifierPart(character);
		}
	}
}
