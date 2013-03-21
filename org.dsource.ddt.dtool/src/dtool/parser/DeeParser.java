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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import dtool.ast.definitions.Module;

/**
 * Concrete D Parser class
 */
public class DeeParser extends DeeParser_Decls {
	
	public DeeParser(String source) {
		this(new DeeLexer(source));
	}
	
	public DeeParser(DeeLexer deeLexer) {
		super(new LexerElementSource(deeLexer));
	}
	
	public DeeParser(LexerElementSource lexSource) {
		super(lexSource);
	}
	
	public static DeeParserResult parseSource(String source) {
		DeeParser deeParser = new DeeParser(source);
		Module module = deeParser.parseModule();
		return new DeeParserResult(module, deeParser.errors);
	}
	
	public DeeParserResult parseUsingRule(String parseRule) {
		DeeParserResult result;
		if(parseRule == null) {
			result = new DeeParserResult(parseModule(), this.errors);
		} else if(parseRule.equalsIgnoreCase(DeeParser.RULE_EXPRESSION.name)) {
			result = new DeeParserResult(parseExpression(), this.errors);
		} else if(parseRule.equalsIgnoreCase(DeeParser.RULE_REFERENCE.name)) {
			result = new DeeParserResult(parseReference(), this.errors);
		} else if(parseRule.equalsIgnoreCase(DeeParser.RULE_DECLARATION.name)) {
			result = new DeeParserResult(parseDeclaration(), this.errors);
		} else if(parseRule.equals("DeclarationImport")) {
			result = new DeeParserResult(parseImportDeclaration(), this.errors);
		} else {
			throw assertFail();
		}
		return result;
	}
	
}