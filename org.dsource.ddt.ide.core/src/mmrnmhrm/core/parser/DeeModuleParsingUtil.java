package mmrnmhrm.core.parser;

import mmrnmhrm.core.DeeCore;

import org.eclipse.dltk.ast.parser.IModuleDeclaration;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.SourceParserUtil;

import dtool.ast.definitions.Module;

public abstract class DeeModuleParsingUtil {
	
	public static Module getParsedDeeModule(ISourceModule sourceModule) {
		if(sourceModule.exists() == false) {
			DeeCore.logWarning("#getParsedDeeModule with module that does not exist: " + 
					sourceModule.getElementName());
		}
		IModuleDeclaration moduleDeclaration = SourceParserUtil.parse(sourceModule, null);
		if (moduleDeclaration instanceof DeeModuleDeclaration) {
			return ((DeeModuleDeclaration) moduleDeclaration).getModule();
		}
		//TODO: consider proper behavior for sourceModule is not from our DLTK nature
		return null;
	}
	
	public static DeeModuleDeclaration getParsedDeeModuleDecl(ISourceModule sourceModule) {
		IModuleDeclaration moduleDeclaration = SourceParserUtil.parse(sourceModule, null);
		if (moduleDeclaration instanceof DeeModuleDeclaration) {
			return (DeeModuleDeclaration) moduleDeclaration;
		}
		return null;
	}
	
	/** Parses the module and returns an AST. Returns null if given module is not the DDT nature. 
	 * This operation uses caching for the created AST. */
	public static Module parseAndGetAST(ISourceModule sourceModule) {
		return getParsedDeeModule(sourceModule);
	}
	
}