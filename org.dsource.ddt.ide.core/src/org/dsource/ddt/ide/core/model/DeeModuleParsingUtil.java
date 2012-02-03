package org.dsource.ddt.ide.core.model;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.parser.IModuleDeclaration;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.SourceParserUtil;

import dtool.ast.definitions.Module;

public abstract class DeeModuleParsingUtil {
	
	/** Gets a DeeModuleDeclaration from given sourceModule, either by parsing or retrieving a cached version.
	 * Parentizes the returned DeeModuleDeclaration
	 * TODO: define proper behavior for sourceModule is not from a DDT DLTK nature */
	public static DeeModuleDeclaration getParsedDeeModule(ISourceModule sourceModule) {
		ModuleDeclaration moduleDeclaration = SourceParserUtil.getModuleDeclaration(sourceModule, null);
		return parentizeDeeModuleDeclaration(moduleDeclaration, sourceModule);
	}
	
	// TODO: investigate if we can get rid of parentizeDeeModuleDeclaration
	
	/** If given moduleDeclaration is a DeeModuleDeclaration, parentizes it to given sourceModule and returns it, 
	 * otherwise return null. */
	public static DeeModuleDeclaration parentizeDeeModuleDeclaration(
			IModuleDeclaration moduleDeclaration, ISourceModule sourceModule) {
		if (moduleDeclaration instanceof DeeModuleDeclaration) {
			DeeModuleDeclaration deeModuleDecl = (DeeModuleDeclaration) moduleDeclaration;
			if (deeModuleDecl.neoModule != null) {
				deeModuleDecl.neoModule.setModuleUnit(sourceModule);
			}
			return deeModuleDecl;
		}
		return null;
	}
	
	/** Parses the module and returns an AST. Returns null if given module is not the DDT nature. 
	 * This operation uses caching for the created AST. */
	public static Module parseAndGetAST(final ISourceModule module) {
		IModuleDeclaration moduleDeclaration = SourceParserUtil.parse(module, null);
		
		if (moduleDeclaration instanceof DeeModuleDeclaration) {
			DeeModuleDeclaration deeModuleDecl = (DeeModuleDeclaration) moduleDeclaration;
			return deeModuleDecl.neoModule;
		}
		return null;
	}
	
}
