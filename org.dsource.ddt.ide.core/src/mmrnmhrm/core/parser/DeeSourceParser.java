package mmrnmhrm.core.parser;

import org.dsource.ddt.ide.core.model.DeeModuleDeclaration;
import org.eclipse.dltk.ast.parser.AbstractSourceParser;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.compiler.problem.DefaultProblem;
import org.eclipse.dltk.compiler.problem.DefaultProblemIdentifier;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.dltk.compiler.problem.ProblemSeverities;
import org.eclipse.dltk.core.IModelElement;

import dtool.DeeNamingRules;
import dtool.parser.DeeParser;
import dtool.parser.DeeParserResult;
import dtool.parser.DeeParserSession;
import dtool.parser.ICompileError;

public class DeeSourceParser extends AbstractSourceParser {
	
	@Override
	public DeeModuleDeclaration parse(IModuleSource input, IProblemReporter reporter) {
		DeeParserResult deeParserSession = parseToDeeParseResult(input, reporter);
		DeeModuleDeclaration deeModuleDecl = new DeeModuleDeclaration(deeParserSession.getParsedModule());
		return deeModuleDecl;
	}
	
	public final String[] NOSTRINGS = new String[0];
	
	public DeeParserResult parseToDeeParseResult(IModuleSource input, IProblemReporter reporter) {
		String source = input.getSourceContents();
		
		String defaultModuleName = "_unnamedSource_";
		IModelElement modelElement = input.getModelElement();
		if(modelElement != null) {
			defaultModuleName = DeeNamingRules.getModuleNameFromFileName(modelElement.getElementName());
		}
		DeeParserResult deeParserSession = true ? 
			DeeParserSession.parseSource(defaultModuleName, source) : 
			DeeParser.parseSource(source, defaultModuleName);
		
		for (ICompileError parserError : deeParserSession.errors) {
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
		
		return deeParserSession;
	}
	
}