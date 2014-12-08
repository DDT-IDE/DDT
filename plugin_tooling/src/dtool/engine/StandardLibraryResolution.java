/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.engine;

import java.nio.file.Path;
import java.util.List;

import melnorme.lang.tooling.context.BundleModules;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.utilbox.misc.MiscUtil;
import dtool.engine.compiler_installs.CompilerInstall;
import dtool.engine.compiler_installs.CompilerInstall.ECompilerType;
import dtool.parser.DeeParser;
import dtool.parser.DeeParserResult.ParsedModule;

public class StandardLibraryResolution extends AbstractBundleResolution implements ISemanticContext {
	
	protected final CompilerInstall compilerInstall;
	
	public StandardLibraryResolution(SemanticManager manager, CompilerInstall compilerInstall) {
		super(manager, compilerInstall.getLibrarySourceFolders());
		this.compilerInstall = compilerInstall;
	}
	
	protected StandardLibraryResolution(SemanticManager manager, CompilerInstall compilerInstall, 
			BundleModules bundleModules) {
		super(manager, bundleModules);
		this.compilerInstall = compilerInstall;
	}
	
	protected CompilerInstall getCompilerInstall() {
		return compilerInstall;
	}
	
	protected ECompilerType getCompilerType() {
		return compilerInstall.getCompilerType();
	}
	
	protected List<Path> getLibrarySourceFolders() {
		return compilerInstall.getLibrarySourceFolders();
	}
	
	@Override
	public StandardLibraryResolution getStdLibResolution() {
		return this;
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public <E extends Exception> void visitBundleResolutions(BundleResolutionVisitor<?, E> visitor) throws E {
		visitor.visit(this);
	}
	
	@Override
	public <E extends Exception> void visitBundleResolutionsAfterStdLib(BundleResolutionVisitor<?, E> visitor) {
		// Do nothing, already has been visited
	}
	
	/* ----------------- synthetic install ----------------- */
	
	public static final Path NULL_COMPILER_INSTALL_PATH = 
			MiscUtil.createValidPath("###DTOOL_SPECIAL###/Synthetic_StdLib");
	
	public static final CompilerInstall NULL_COMPILER_INSTALL = new CompilerInstall(
		NULL_COMPILER_INSTALL_PATH, ECompilerType.OTHER);
	
	protected static final String SYNTHETIC_Module_Object = "module object; class TypeInfo_Class { }";
	
	/**
	 * Fall-back synthetic StandardLibraryResolution for when no real compiler installs could be found.
	 */
	public static class MissingStandardLibraryResolution extends StandardLibraryResolution {
		
		protected static final Path objectPath = NULL_COMPILER_INSTALL_PATH.resolve("object.di");
		
		public MissingStandardLibraryResolution(SemanticManager manager) {
			super(manager, NULL_COMPILER_INSTALL, BundleModules.createSyntheticBundleModules(objectPath));
			
			ParsedModule parsedModule = DeeParser.parseSource(SYNTHETIC_Module_Object, objectPath);
			ResolvedModule resolvedModule = new ResolvedModule(parsedModule, this);
			resolvedModules.put(objectPath, resolvedModule);
		}
		
		@Override
		public boolean checkIsModuleListStale() {
			return false;
		}
		
		@Override
		public synchronized boolean checkIsModuleContentsStale() {
			return false;
		}
		
	}
	
}