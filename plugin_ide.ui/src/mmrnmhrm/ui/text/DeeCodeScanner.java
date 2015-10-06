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

import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import dtool.parser.DeeTokenHelper;
import dtool.parser.DeeTokens;
import melnorme.lang.ide.core.text.FullPatternRule;
import melnorme.lang.ide.ui.text.AbstractLangScanner;
import melnorme.lang.ide.ui.text.coloring.StylingPreferences;
import melnorme.lang.ide.ui.text.coloring.TokenRegistry;
import melnorme.utilbox.collections.ArrayList2;

public class DeeCodeScanner extends AbstractLangScanner {
	
	public DeeCodeScanner(TokenRegistry tokenStore, StylingPreferences stylingPrefs) {
		super(tokenStore, stylingPrefs);
	}
	
	@Override
	protected void initRules(ArrayList2<IRule> rules) {
		
		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new LangWhitespaceDetector()));
		
		IToken tkDefault = getToken(DeeColorPreferences.DEFAULT);
		IToken tkKeyword = getToken(DeeColorPreferences.KEYWORDS);
		IToken tkBasics = getToken(DeeColorPreferences.KW_BASICTYPES);
		IToken tkLiterals = getToken(DeeColorPreferences.KW_LITERALS);
//		IToken tkOperators = getToken(DeeColorConstants.DEE_OPERATORS);
		
		// Add word rule for keywords, types, and constants.
		WordRule wordRule = new WordRule(new JavaWordDetector(), tkDefault);
		addWordsFromTokens(wordRule, DeeTokenHelper.keyWords_control, tkKeyword);
		addWordsFromTokens(wordRule, DeeTokenHelper.keyWords_nativeTypes, tkBasics);
		addWordsFromTokens(wordRule, DeeTokenHelper.keyWords_literalValues, tkLiterals);
		rules.add(wordRule);
		
		// These need special treament because of the '!' character
		rules.add(new FullPatternRule(tkKeyword, array("!in", "!is"), new JavaWordDetector()));
		
		
		IToken tkAnnotation = getToken(DeeColorPreferences.ANNOTATIONS);
		WordRule annotationsRule = new WordRule(new AnnotationsWordDetector(), tkAnnotation);
		rules.add(annotationsRule);
		
		setDefaultReturnToken(tkDefault);
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