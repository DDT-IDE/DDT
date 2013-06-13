package dtool.parser;

import java.util.ArrayList;
import java.util.List;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.Parser;
import descent.internal.compiler.parser.Token;
import descent.internal.compiler.parser.ast.IProblemReporter;
import dtool.ast.definitions.Module;
import dtool.descentadapter.DescentASTConverter;

public class DeeParserSession extends DeeParserResult {
	
	@Deprecated
	public static DeeParserResult parseSource(String defaultModuleName, String source) {
		return parseSource(defaultModuleName, source, null);
	}
	
	public static DeeParserResult parseSource(String defaultModuleName, String source, 
		IProblemReporter problemReporter) {
		DescentParserAdapter parserAdapter = DescentParserAdapter.parseSource(source, Parser.D2, problemReporter);
		Module module = DescentASTConverter.convertModule(parserAdapter.mod, defaultModuleName);
		return new DeeParserSession(module, defaultModuleName, parserAdapter);
	}
	
	public static DeeParserResult parseWithRecovery(String defaultModuleName, String source, 
			final int offset, Token lastTokenNonWS) {
		DescentParserAdapter parserAdapter = DescentParserAdapter.parseSource(source, Parser.D2, null);
		parserAdapter.recoverForCompletion(source, offset, lastTokenNonWS);
		Module module = DescentASTConverter.convertModule(parserAdapter.mod, defaultModuleName);
		return new DeeParserSession(module, defaultModuleName, parserAdapter);
	}
	
	protected final String defaultModuleName;
	protected final DescentParserAdapter parserAdapter;
	
	public DeeParserSession(Module module, String defaultModuleName, DescentParserAdapter parserAdapter) {
		super(null, module, false, initErrors(parserAdapter));
		this.defaultModuleName = defaultModuleName;
		this.parserAdapter = parserAdapter;
	}
	
	@Override
	public boolean hasSyntaxErrors() {
		return parserAdapter.mod.problems.size() != 0;
	}
	
	@Override
	public boolean isQualifiedDotFix() {
		return parserAdapter.isQualifiedDotFix;
	}
	
	public descent.internal.compiler.parser.Module getDMDModule() {
		return parserAdapter.mod;
	}
	
	public static List<ICompileError> initErrors(DescentParserAdapter parserAdapter) {
		ArrayList<ICompileError> errors = new ArrayList<ICompileError>();
		List<IProblem> problems = parserAdapter.parser.problems;
		if(problems == null)
			return errors;
		
		for (final IProblem problem : problems) {
			// TODO: originating file ?
			errors.add(new ICompileError() {

				@Override
				public String getUserMessage() {
					return problem.getMessage();
				}

				@Override
				public int getStartPos() {
					return problem.getSourceStart();
				}

				@Override
				public int getEndPos() {
					return problem.getSourceEnd();
				}

				@Override
				public int getLineNumber() {
					return problem.getSourceLineNumber();
				}
			});
		}
		return errors;
	}
}
