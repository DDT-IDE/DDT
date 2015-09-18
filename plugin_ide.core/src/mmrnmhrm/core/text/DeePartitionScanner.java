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
package mmrnmhrm.core.text;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.Token;

import melnorme.lang.ide.core.text.LangPartitionScanner;
import melnorme.utilbox.collections.ArrayList2;

public class DeePartitionScanner extends LangPartitionScanner implements DeePartitions {
	
	// See: http://www.digitalmars.com/d/2.0/lex.html
	@Override
	protected void initPredicateRules(ArrayList2<IPredicateRule> rules) {
		
		rules.add(new PatternRule("`", "`", new Token(DEE_RAW_STRING), NO_ESCAPE_CHAR, false, true));
		rules.add(new PatternRule("r\"", "\"", new Token(DEE_RAW_STRING2), NO_ESCAPE_CHAR, false, true));
		// TODO: this rule is not accurate, need to use something like HereDocEnabledPartitioner to make it work
		rules.add(new PatternRule("q\"", "\"", new Token(DEE_DELIM_STRING), NO_ESCAPE_CHAR, false, true)); 
		rules.add(new PatternRule("\"", "\"", new Token(DEE_STRING), '\\', false, true));
		rules.add(new PatternRule("'", "'", new Token(DEE_CHARACTER), '\\', true, true));
		
		addStandardRules(rules, 
			DeePartitions.DEE_SINGLE_COMMENT, 
			DeePartitions.DEE_MULTI_COMMENT, 
			DeePartitions.DEE_SINGLE_DOCCOMMENT, 
			DeePartitions.DEE_MULTI_DOCCOMMENT, 
			null);
		
		rules.add(new NestedDelimiterRule("/++", "/+", "+/", new Token(DEE_NESTED_DOCCOMMENT), NO_ESCAPE_CHAR, true));
		rules.add(new NestedDelimiterRule("/+", "/+", "+/", new Token(DEE_NESTED_COMMENT), NO_ESCAPE_CHAR, true));
		
	}
	
}