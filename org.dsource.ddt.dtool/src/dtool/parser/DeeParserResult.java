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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import dtool.ast.ASTNeoNode;
import dtool.ast.definitions.Module;
import dtool.parser.AbstractParser.NodeResult;
import dtool.util.NewUtils;

public class DeeParserResult {
	
	public final String source;
	public final ASTNeoNode node;
	public final boolean ruleBroken;
	public final Module module;
	public final ArrayList<ParserError> errors;
	
	public DeeParserResult(NodeResult<? extends ASTNeoNode> result, DeeParser parser) {
		this(parser.getSource(), result.getNode(), result.ruleBroken, parser.errors);
	}
	
	public DeeParserResult(String source, ASTNeoNode node, boolean ruleBroken, ArrayList<ParserError> errors) {
		this.source = source;
		this.node = node;
		this.ruleBroken = ruleBroken;
		this.module = node instanceof Module ? (Module) node : null;
		this.errors = errors;
		if(errors != null) {
			Collections.sort(errors, new ParserErrorComparator());
		}
	}
	
	public boolean hasSyntaxErrors() {
		return errors != null && errors.size() > 0;
	}
	
	public Module getParsedModule() {
		assertNotNull(module);
		return module;
	}
	
	public static final class ParserErrorComparator implements Comparator<ParserError> {
		@Override
		public int compare(ParserError o1, ParserError o2) {
			int compareResult = o1.sourceRange.compareTo(o2.sourceRange);
			if(compareResult == 0) {
				compareResult = o1.errorType.ordinal() - o2.errorType.ordinal(); 
			}
			if(compareResult == 0) {
				compareResult = NewUtils.compareStrings(o1.msgErrorSource, o2.msgErrorSource); 
			}
			return compareResult;
		}
	}
	
}