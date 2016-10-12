/*******************************************************************************
 * Copyright (c) 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Konstantin Salikhov - number lexing rule implementation
 *******************************************************************************/
package dtool.parser;

import melnorme.lang.tooling.parser.lexer.NumberLexingRule;
import melnorme.lang.utils.parse.ICharacterReader;

// This code is duplicated with in DeeLexer
public class DeeNumberLexingRule extends NumberLexingRule {
	
	@Override
	protected void tryConsumeFractionalPart(ICharacterReader reader, int radix) {
		consumeFractionalPart(reader, radix);
	}
	
	@Override
	protected boolean consumeIntSuffix(ICharacterReader reader) {
		if(reader.tryConsume("L")) {
			if(reader.tryConsume("u") || reader.tryConsume("U")) {}
			return true;
		}
		
		if(reader.tryConsume("u") || reader.tryConsume("U")) {
			if(reader.tryConsume("L")) {}
			return true;
		}
		
		return false;
	}
	
	@Override
	protected boolean consumeFloatSuffix(ICharacterReader reader) {
		return reader.tryConsume("f") || reader.tryConsume("F") || reader.tryConsume("L");
	}
	
}