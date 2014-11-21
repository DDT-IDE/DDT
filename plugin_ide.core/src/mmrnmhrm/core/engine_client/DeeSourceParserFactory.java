/*******************************************************************************
 * Copyright (c) 2010, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.engine_client;

import melnorme.lang.tooling.ast.ParserError;

import org.eclipse.dltk.ast.parser.AbstractSourceParser;
import org.eclipse.dltk.ast.parser.IModuleDeclaration;
import org.eclipse.dltk.ast.parser.ISourceParser;
import org.eclipse.dltk.ast.parser.ISourceParserFactory;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.compiler.problem.DefaultProblem;
import org.eclipse.dltk.compiler.problem.DefaultProblemIdentifier;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.dltk.compiler.problem.ProblemSeverities;

import dtool.parser.DeeParserResult.ParsedModule;

public class DeeSourceParserFactory implements ISourceParserFactory {
	
	@Override
	public ISourceParser createSourceParser() {
		return new DeeSourceParser();
	}
	
	public static class DeeSourceParser extends AbstractSourceParser {

		@Override
		public IModuleDeclaration parse(IModuleSource input, IProblemReporter reporter) {
			DToolClient.getDefault().doParseForBuildStructureOrIndex(input, reporter);
			return new IModuleDeclaration() { }; // Return an empty type, its not used anyways
		}
		
	}
	
	public static final String[] NOSTRINGS = new String[0];
	
	protected static void reportErrors(IProblemReporter reporter, ParsedModule parsedModule) {
		if(reporter == null || parsedModule == null) {
			return;
		}
		for (ParserError parserError : parsedModule.errors) {
			reporter.reportProblem(new DefaultProblem(
				parserError.getUserMessage(),
				DefaultProblemIdentifier.decode(org.eclipse.dltk.compiler.problem.IProblem.Syntax),
				NOSTRINGS, 
				ProblemSeverities.Error,
				parserError.getStartPos(),
				parserError.getEndPos(),
				0 //TODO: review if we actually need end line
				)
			);
		}
	}
	
}