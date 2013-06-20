package dtool.descentadapter;

import java.util.ArrayList;
import java.util.List;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.Parser;
import descent.internal.compiler.parser.TOK;
import descent.internal.compiler.parser.Token;
import descent.internal.compiler.parser.ast.IProblemReporter;
import descent.internal.compiler.parser.ast.TokenUtil;
import dtool.ast.definitions.Module;
import dtool.parser.DeeParserResult;
import dtool.parser.ICompileError;

@Deprecated
public class DeeParserSession extends DeeParserResult {
	
//	public static DeeParserResult parseSource(String defaultModuleName, String source) {
//		return parseSource(defaultModuleName, source, null);
//	}
//	
//	public static DeeParserResult parseSource(String defaultModuleName, String source, 
//		IProblemReporter problemReporter) {
//		DescentParserAdapter parserAdapter = DescentParserAdapter.parseSource(source, Parser.D2, problemReporter);
//		Module module = DescentASTConverter.convertModule(parserAdapter.mod, defaultModuleName);
//		return new DeeParserSession(module, defaultModuleName, parserAdapter);
//	}
	
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
	
	public static boolean isValidReferenceToken(Token token) {
		return token.value == TOK.TOKidentifier
		|| token.value == TOK.TOKbool
		|| token.value == TOK.TOKchar
		|| token.value == TOK.TOKdchar
		|| token.value == TOK.TOKfloat32
		|| token.value == TOK.TOKfloat64
		|| token.value == TOK.TOKfloat80
		|| token.value == TOK.TOKint8
		|| token.value == TOK.TOKint16
		|| token.value == TOK.TOKint32
		|| token.value == TOK.TOKint64
		//|| token.value == TOK.TOKnull
		//|| token.value == TOK.TOKthis
		//|| token.value == TOK.TOKsuper
		|| token.value == TOK.TOKuns8
		|| token.value == TOK.TOKuns16
		|| token.value == TOK.TOKuns32
		|| token.value == TOK.TOKuns64
		|| token.value == TOK.TOKvoid
		|| token.value == TOK.TOKwchar
		;
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
	
	public static DeeParserResult parseWithRecovery(String source, String defaultModuleName, final int offset) {
		Token tokenList = DescentParserAdapter.tokenizeSource(source);
		
		Token lastTokenNonWS = null;
		Token lastToken = null;
		
		// : Find last non-white token before offset
		Token newtoken = tokenList;
		while (newtoken.ptr < offset) {
			lastToken = newtoken;
			if(!TokenUtil.isWhiteToken(newtoken.value)) {
				lastTokenNonWS = newtoken;
			}
			
			newtoken = newtoken.next;
		}
		
		// : Check if completion request is *inside* the token
		if(lastToken != null && lastToken.ptr < offset && (lastToken.ptr + lastToken.sourceLen) > offset) {
			// if so then check if it's an allowed token
			if(!DeeParserSession.isValidReferenceToken(lastToken)) {
				return null;
			}
		}
		
		// : Parse source and do syntax error recovery
		DeeParserResult parseResult = parseWithRecovery(defaultModuleName, source,  
				offset, lastTokenNonWS);
		return parseResult;
	}
}
