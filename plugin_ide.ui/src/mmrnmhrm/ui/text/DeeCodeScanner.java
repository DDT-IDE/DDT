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

import static melnorme.utilbox.core.CoreUtil.array;

import java.util.ArrayList;
import java.util.List;

import melnorme.lang.ide.ui.text.coloring.AbstractLangScanner;

import org.eclipse.cdt.ui.text.ITokenStoreFactory;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import dtool.parser.DeeTokenHelper;
import dtool.parser.DeeTokens;

public class DeeCodeScanner extends AbstractLangScanner {
	
	public static String COLOR_TOKENS__PROPERTY_KEYS[] = new String[] {
		DeeColorPreferences.DEFAULT.key,
		DeeColorPreferences.KEYWORDS.key,
		DeeColorPreferences.BASICTYPES.key,
		DeeColorPreferences.ANNOTATIONS.key,
		DeeColorPreferences.LITERALS.key,
		DeeColorPreferences.OPERATORS.key,
	};
	
	public DeeCodeScanner(ITokenStoreFactory factory) {
		super(factory.createTokenStore(COLOR_TOKENS__PROPERTY_KEYS));
		setRules(createRules());
	}
	
	protected List<IRule> createRules() {
		List<IRule> rules = new ArrayList<IRule>();
		
		IToken tkOther = getToken(DeeColorPreferences.DEFAULT.key);
		IToken tkKeyword = getToken(DeeColorPreferences.KEYWORDS.key);
		IToken tkBasics = getToken(DeeColorPreferences.BASICTYPES.key);
		IToken tkLiterals = getToken(DeeColorPreferences.LITERALS.key);
//		IToken tkOperators = getToken(DeeColorConstants.DEE_OPERATORS);
		
		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new LangWhitespaceDetector()));
		
		// Add word rule for keywords, types, and constants.
		WordRule wordRule = new WordRule(new JavaWordDetector(), tkOther);
		addWordsFromTokens(wordRule, DeeTokenHelper.keyWords_control, tkKeyword);
		addWordsFromTokens(wordRule, DeeTokenHelper.keyWords_nativeTypes, tkBasics);
		addWordsFromTokens(wordRule, DeeTokenHelper.keyWords_literalValues, tkLiterals);
		rules.add(wordRule);
		
		// These need special treament because of the '!' character
		rules.add(new FullPatternRule(tkKeyword, array("!in", "!is"), new JavaWordDetector()));
		
		
		IToken tkAnnotation = getToken(DeeColorPreferences.ANNOTATIONS.key);
		WordRule annotationsRule = new WordRule(new AnnotationsWordDetector(), tkAnnotation);
		rules.add(annotationsRule);
		
		setDefaultReturnToken(tkOther);
		return rules;
	}
	
	protected void addWordsFromTokens(WordRule wordRule, List<DeeTokens> tokenTypes, IToken token) {
		for (DeeTokens type : tokenTypes) {
			wordRule.addWord(type.getSourceValue(), token);
		}
	}
	
	public static class AnnotationsWordDetector extends JavaWordDetector {
		@Override
		public boolean isWordStart(char character) {
			return character == '@';
		}
	}
	
}