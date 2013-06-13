package mmrnmhrm.core.parser;

import org.dsource.ddt.ide.core.model.DeeModuleDeclaration;
import org.eclipse.dltk.ast.parser.AbstractSourceParser;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.dltk.core.IModelElement;

import descent.core.compiler.IProblem;
import dtool.DeeNamingRules;
import dtool.parser.DeeParserResult;
import dtool.parser.DeeParserSession;

public class DeeSourceParser extends AbstractSourceParser {
	
	private static final class DescentProblemAdapter implements
	descent.internal.compiler.parser.ast.IProblemReporter {
		
		private IProblemReporter reporter;
		
		public DescentProblemAdapter(IProblemReporter reporter) {
			this.reporter = reporter;
		}
		
		@Override
		public void reportProblem(IProblem problem) {
			reporter.reportProblem(DLTKDescentProblemWrapper.createProblemWrapper(problem));
		}
		
		public static descent.internal.compiler.parser.ast.IProblemReporter create(IProblemReporter reporter) {
			if(reporter == null)
				return null;
			return new DescentProblemAdapter(reporter);
		}
	}
	
	@Override
	public DeeModuleDeclaration parse(IModuleSource input, IProblemReporter reporter) {
		DeeParserResult deeParserSession = parseToDeeParseResult(input, reporter);
		DeeModuleDeclaration deeModuleDecl = new DeeModuleDeclaration(deeParserSession.getParsedModule());
		return deeModuleDecl;
	}
	
	public DeeParserResult parseToDeeParseResult(IModuleSource input, IProblemReporter reporter) {
		String source = input.getSourceContents();
		
		String defaultModuleName = "_unnamedSource_";
		IModelElement modelElement = input.getModelElement();
		if(modelElement != null) {
			defaultModuleName = DeeNamingRules.getModuleNameFromFileName(modelElement.getElementName());
		}
		
		DeeParserSession deeParserSession = DeeParserSession.parseSource(defaultModuleName, source,
				DescentProblemAdapter.create(reporter));
		return deeParserSession;
	}
	
}