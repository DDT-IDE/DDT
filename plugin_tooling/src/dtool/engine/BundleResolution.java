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


import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.nio.file.Path;
import java.util.HashSet;

import melnorme.lang.tooling.bundles.ModuleFullName;
import melnorme.lang.tooling.bundles.ModuleSourceException;
import melnorme.utilbox.collections.Indexable;
import melnorme.utilbox.misc.StringUtil;
import dtool.dub.BundlePath;

public class BundleResolution extends AbstractBundleResolution {
	
	protected final BundlePath bundlePath;
	protected final StandardLibraryResolution stdLibResolution;
	protected final Indexable<? extends BundleResolution> depResolutions;
	
	public BundleResolution(SemanticManager manager, BundlePath bundlePath, BundleModules bundleModules,
			StandardLibraryResolution stdLibResolution, Indexable<? extends BundleResolution> depResolutions) {
		super(manager, bundleModules);
		this.bundlePath = bundlePath;
		this.stdLibResolution = assertNotNull(stdLibResolution); 
		this.depResolutions = depResolutions;
	}
	
	public BundlePath getBundlePath() {
		return bundlePath;
	}
	
	public Indexable<? extends BundleResolution> getDirectDependencies() {
		return depResolutions;
	}
	
	public StandardLibraryResolution getStdLibResolution() {
		return stdLibResolution;
	}
	
	public Path getCompilerPath() {
		return getStdLibResolution().getCompilerInstall().getCompilerPath();
	}
	
	@Override
	public String toString() {
		if(getBundlePath() == null) {
			return "BundleResolution: [" + StringUtil.collToString(bundleModules.moduleFiles, ":") + "]";
		}
		return "BundleResolution: " + getBundlePath();
	}

	
	/* -----------------  ----------------- */
	
	// As an optimization, we don't check STD_LIB staleness, as its likely to change very rarely.
	protected static boolean CHECK_STD_LIB_STALENESS = false;
	
	@Override
	public boolean checkIsStale() {
		if(checkIsModuleListStale() || checkIsModuleContentsStale()) {
			return true;
		}
		
		if(CHECK_STD_LIB_STALENESS && stdLibResolution.checkIsStale()) {
			return true;
		}
		
		for (BundleResolution bundleRes : depResolutions) {
			if(bundleRes.checkIsStale()) {
				return true;
			}
		}
		return false;
	}
	
	/* ----------------- ----------------- */
	
	public abstract class BundleResolutionVisitor<E extends Exception> {
		
		public BundleResolutionVisitor() throws E {
			visitBundleResolutions();
		}
		
		private void visitBundleResolutions() throws E {
			
			visit(stdLibResolution); // TODO optimize duplicate visits of StdLib
			if(isFinished()) {
				return;
			}
			
			visitSelf(BundleResolution.this);
			if(isFinished()) {
				return;
			}
			
			for (BundleResolution depBundleRes : depResolutions) {
				visit(depBundleRes);
				if(isFinished()) {
					return;
				}
			}
		}
		
		protected abstract void visit(AbstractBundleResolution bundleResolution) throws E;
		
		protected abstract void visitSelf(BundleResolution bundleResolution) throws E;
		
		protected boolean isFinished() {
			return false;
		}
		
	}
	
	@Override
	protected final void findModules(final String fullNamePrefix, final HashSet<String> matchedModules) {
		new BundleResolutionVisitor<RuntimeException>() {
			@Override
			public void visit(AbstractBundleResolution bundleResolution) {
				bundleResolution.findModules(fullNamePrefix, matchedModules);
			}
			@Override
			protected void visitSelf(BundleResolution bundleResolution) throws RuntimeException {
				findBundleModules(fullNamePrefix, matchedModules);
			}
		};
	}
	
	protected abstract class ModuleResolutionVisitor extends BundleResolutionVisitor<ModuleSourceException> {
		public ResolvedModule resolvedModule;
		
		protected ModuleResolutionVisitor() throws ModuleSourceException {
			super();
		}
		
		@Override
		public abstract void visit(AbstractBundleResolution bundleResolution) throws ModuleSourceException;
		
		@Override
		public boolean isFinished() {
			return resolvedModule != null;
		}
	}
	
	@Override
	public final ResolvedModule findResolvedModule(final ModuleFullName moduleFullName) throws ModuleSourceException {
		return new ModuleResolutionVisitor() {
			@Override
			public void visit(AbstractBundleResolution bundleResolution) throws ModuleSourceException {
				resolvedModule = bundleResolution.findResolvedModule(moduleFullName);
			}
			@Override
			protected void visitSelf(BundleResolution bundleResolution) throws ModuleSourceException {
				resolvedModule = bundleResolution.getBundleResolvedModule(moduleFullName);
			}
		}.resolvedModule;
	}
	
	@Override
	public final ResolvedModule findResolvedModule(final Path path) throws ModuleSourceException {
		return new ModuleResolutionVisitor() {
			@Override
			public void visit(AbstractBundleResolution bundleResolution) throws ModuleSourceException {
				resolvedModule = bundleResolution.findResolvedModule(path);
			}
			
			@Override
			protected void visitSelf(BundleResolution bundleResolution) throws ModuleSourceException {
				resolvedModule = bundleResolution.getBundleResolvedModule(path);
			}
		}.resolvedModule;
	}
	
}