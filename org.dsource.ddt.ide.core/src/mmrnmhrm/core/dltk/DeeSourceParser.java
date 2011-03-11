package mmrnmhrm.core.dltk;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import mmrnmhrm.core.LangCore;

import org.eclipse.dltk.ast.parser.AbstractSourceParser;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.compiler.problem.IProblemReporter;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.Parser;
import dtool.descentadapter.DescentASTConverter;

public class DeeSourceParser extends AbstractSourceParser {
	
	private static final class DescentProblemAdapter implements
	descent.internal.compiler.parser.ast.IProblemReporter {
		
		private IProblemReporter reporter;
		
		public DescentProblemAdapter(IProblemReporter reporter) {
			this.reporter = reporter;
		}
		
		@Override
		public void reportProblem(IProblem problem) {
			reporter.reportProblem(new DLTKDescentProblemWrapper(problem));
		}
		
		public static descent.internal.compiler.parser.ast.IProblemReporter create(IProblemReporter reporter) {
			if(reporter == null)
				return null;
			return new DescentProblemAdapter(reporter);
		}
	}
	
	@Override
	public DeeModuleDeclaration parse(IModuleSource input, IProblemReporter reporter) {
		int langVersion = 2; // TODO we should use default from project configured interpreter version
		
		char[] source = input.getContentsAsCharArray();
		Parser parser = new Parser(langVersion, source);
		parser.setProblemReporter(DescentProblemAdapter.create(reporter));
		Module dmdModule = null;
		try {
			dmdModule = parser.parseModuleObj();
		} catch (RuntimeException e) {
			LangCore.log(e);
			throw e;
		}
		
		assertTrue(dmdModule.length == source.length);
		DeeModuleDeclaration deeModuleDecl = new DeeModuleDeclaration(dmdModule);
		boolean adaptMalformedAST = true;
		if(dmdModule.hasSyntaxErrors() && !adaptMalformedAST) {
			// DontLet's try to convert a malformed AST
			return deeModuleDecl;
		}
		dtool.ast.definitions.Module neoModule = DescentASTConverter.convertModule(dmdModule);
		deeModuleDecl.setNeoModule(neoModule);
		//setModuleDeclModuleUnit(fileName, deeModuleDecl);
		return deeModuleDecl;
		
	}
	
}