/*******************************************************************************
 * Copyright (c) 2011 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.ide.core_text;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.Token;

import LANG_PROJECT_ID.ide.core_text.NestedDelimiterRule;
import melnorme.lang.ide.core.TextSettings_Actual.LangPartitionTypes;
import melnorme.utilbox.collections.ArrayList2;

public class DeePartitionScanner extends LangPartitionScanner {
	
	// See: http://www.digitalmars.com/d/2.0/lex.html
	@Override
	protected void initPredicateRules(ArrayList2<IPredicateRule> rules) {
		
		rules.add(new PatternRule("`", "`", 
			new Token(LangPartitionTypes.DEE_RAW_STRING.getId()), NO_ESCAPE_CHAR, false, true));
		rules.add(new PatternRule("r\"", "\"", 
			new Token(LangPartitionTypes.DEE_RAW_STRING2.getId()), NO_ESCAPE_CHAR, false, true));
		// TODO: this rule is not accurate, need to use something like HereDocEnabledPartitioner to make it work
		rules.add(new PatternRule("q\"", "\"", 
			new Token(LangPartitionTypes.DEE_DELIM_STRING.getId()), NO_ESCAPE_CHAR, false, true)); 
		rules.add(new PatternRule("\"", "\"", 
			new Token(LangPartitionTypes.DEE_STRING.getId()), '\\', false, true));
		rules.add(new PatternRule("'", "'", 
			new Token(LangPartitionTypes.DEE_CHARACTER.getId()), '\\', true, true));
		
		addStandardRules(rules, 
			LangPartitionTypes.DEE_SINGLE_COMMENT.getId(), 
			LangPartitionTypes.DEE_MULTI_COMMENT.getId(), 
			LangPartitionTypes.DEE_SINGLE_DOCCOMMENT.getId(), 
			LangPartitionTypes.DEE_MULTI_DOCCOMMENT.getId(), 
			null);
		
		rules.add(new NestedDelimiterRule("/++", "/+", "+/", 
			new Token(LangPartitionTypes.DEE_NESTED_DOCCOMMENT.getId()), NO_ESCAPE_CHAR, true));
		rules.add(new NestedDelimiterRule("/+", "/+", "+/", 
			new Token(LangPartitionTypes.DEE_NESTED_COMMENT.getId()), NO_ESCAPE_CHAR, true));
		
	}
	
}