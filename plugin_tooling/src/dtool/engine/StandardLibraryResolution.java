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

import java.util.List;

import melnorme.lang.tooling.ast.CommonLanguageElement;
import melnorme.lang.tooling.context.BundleModules;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.utilbox.misc.Location;
import melnorme.utilbox.misc.MiscUtil;
import melnorme.utilbox.misc.PathUtil;
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
	
	protected List<Location> getLibrarySourceFolders() {
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
	
	/** Use a fake Location for the null compiler install path. 
	 * The path doesn't actually exists, but that's fine */
	public static final Location NULL_COMPILER_INSTALL_PATH = 
			PathUtil.DEFAULT_ROOT_LOC.resolve_fromValid("###INTERNAL_PATH###/org.dsource.dtool/Missing_StdLib");
	
	protected static final Location NULL_COMPILER_INSTALL_PATH_objectPath = 
			NULL_COMPILER_INSTALL_PATH.resolve_fromValid("object.di");
	
	public static final CompilerInstall NULL_COMPILER_INSTALL = new CompilerInstall(
		NULL_COMPILER_INSTALL_PATH, ECompilerType.OTHER);
	
	protected static final String SYNTHETIC_Module_Object =
			MiscUtil.getClassResourceAsString(MissingStandardLibraryResolution.class, "object.di");
	
	/**
	 * Fall-back synthetic StandardLibraryResolution for when no real compiler installs could be found.
	 */
	public static class MissingStandardLibraryResolution extends StandardLibraryResolution {
		
		protected final ResolvedModule fakeObjectModule;
		
		public MissingStandardLibraryResolution(SemanticManager manager) {
			super(manager, NULL_COMPILER_INSTALL, BundleModules.createSyntheticBundleModules(
				NULL_COMPILER_INSTALL_PATH_objectPath));
			
			ParsedModule parsedModule = DeeParser.parseSourceModule(SYNTHETIC_Module_Object, "object");
			fakeObjectModule = new ResolvedModule(parsedModule, this);
			resolvedModules.put(NULL_COMPILER_INSTALL_PATH_objectPath, fakeObjectModule);
		}
		
		@Override
		public boolean checkIsModuleListStale() {
			return false;
		}
		
		@Override
		public synchronized boolean checkIsModuleContentsStale() {
			return false;
		}
		
		@Override
		public boolean bundleContainsElement(CommonLanguageElement languageElement, Location path) {
			if(languageElement.getContainingModuleNamespace() == fakeObjectModule.getModuleNode()) {
				return true;
			}
			return false;
		}
		
	}
	
}