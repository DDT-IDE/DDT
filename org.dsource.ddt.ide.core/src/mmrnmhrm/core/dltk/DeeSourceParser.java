package mmrnmhrm.core.dltk;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import mmrnmhrm.core.DeeCorePreferences;
import mmrnmhrm.core.LangCore;

import org.eclipse.dltk.ast.parser.AbstractSourceParser;
import org.eclipse.dltk.ast.parser.ISourceParser;
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

		public static descent.internal.compiler.parser.ast.IProblemReporter create(
				IProblemReporter reporter) {
			if(reporter == null)
				return null;
			return new DescentProblemAdapter(reporter);
		}
	}

	private static final DeeSourceParser instance = new DeeSourceParser();
	
	public static ISourceParser getInstance() {
		return instance;
	}
	
	@Override
	public DeeModuleDeclaration parse(IModuleSource input, IProblemReporter reporter) {
		int langVersion = DeeCorePreferences.getInt(DeeCorePreferences.LANG_VERSION);
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
		if(dmdModule.hasSyntaxErrors()
				&& !DeeCorePreferences.getBoolean(DeeCorePreferences.ADAPT_MALFORMED_DMD_AST)) {
			// DontLet's try to convert a malformed AST
			return deeModuleDecl;
		}
		dtool.ast.definitions.Module neoModule = DescentASTConverter.convertModule(dmdModule);
		deeModuleDecl.setNeoModule(neoModule);
		//setModuleDeclModuleUnit(fileName, deeModuleDecl);
		return deeModuleDecl;
		
	}

	/*
	private static void setModuleDeclModuleUnit(char[] filename, DeeModuleDeclaration deeModuleDecl) {
		if(filename == null)
			return;
		IPath path = new Path(new String(filename));
		ISourceModule module = null;
		
		if(EnvironmentPathUtils.isFull(path)) {
			path = EnvironmentPathUtils.getLocalPath(path);
		}
		
		if(!path.isAbsolute()) {
			IFile file = DeeCore.getWorkspaceRoot().getFile(path);
			module = DLTKCore.createSourceModuleFrom(file);
		} else {
			IFile file = DeeCore.getWorkspaceRoot().getFile(path);
			boolean exists = file.exists();
			module = DLTKCore.createSourceModuleFrom(file);
		}
		//IFile file = DeeCore.getWorkspaceRoot().getFileForLocation(path);
		if(module.exists()) {
			deeModuleDecl.neoModule.setModuleUnit(module);
		} else {
			module.exists();
		}
	}
	*/

}