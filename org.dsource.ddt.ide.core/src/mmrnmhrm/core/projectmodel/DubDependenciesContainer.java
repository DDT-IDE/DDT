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
package mmrnmhrm.core.projectmodel;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.CoreUtil.arrayFrom;

import java.nio.file.Path;
import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;

import melnorme.lang.ide.core.utils.EclipseUtils;
import melnorme.utilbox.misc.ArrayUtil;
import mmrnmhrm.core.DLTKUtils;
import dtool.dub.DubBundle;
import dtool.dub.DubBundle.DubDependecyRef;
import dtool.dub.DubBundleDescription;

public class DubDependenciesContainer extends CommonDubElement<IProject> {
	
	protected final DubBundleDescription bundleInfo;
	protected final IDubElement[] depElements;
	
	public DubDependenciesContainer(DubBundleDescription bundleInfo, IProject project) {
		super(project);
		this.bundleInfo = assertNotNull(bundleInfo);
		depElements = createChildren();
	}
	
	protected IDubElement[] createChildren() {
		ArrayList<IDubElement> newChildren = new ArrayList<>();
		
		if(bundleInfo.isResolved()) {
			for (DubBundle dubBundle : bundleInfo.getBundleDependencies()) {
				newChildren.add(new DubDependencyElement(this, dubBundle));
			}
		} else {
			for (DubDependecyRef dubBundleRef : bundleInfo.getMainBundle().getDependencyRefs()) {
				newChildren.add(new DubRawDependencyElement(this, dubBundleRef));
			}
		}
		if(bundleInfo.getError() != null) {
			newChildren.add(new DubErrorElement(this, bundleInfo.getError().getMessage()));
		}
		return ArrayUtil.createFrom(newChildren, IDubElement.class);
	}
	
	@Override
	public DubElementType getElementType() {
		return DubElementType.DUB_DEP_CONTAINER;
	}
	
	public DubBundleDescription getBundleInfo() {
		return bundleInfo;
	}
	
	public IProject getProject() {
		return getParent();
	}
	
	public IScriptProject getScriptProject() {
		return DLTKCore.create(getProject());
	}
	
	@Override
	public String getElementName() {
		return "{Dependencies}";
	}
	
	@Override
	public String getPathString() {
		return getProject().getFullPath().toPortableString() + "/" + getElementName();
	}
	
	@Override
	public boolean hasChildren() {
		return depElements.length > 0;
	}
	
	@Override
	public IDubElement[] getChildren() {
		return depElements;
	}
	
	public static class DubErrorElement extends CommonDubElement<IDubElement> {
		
		public final String errorDescription;
		
		public DubErrorElement(IDubElement parent, String errorDescription) {
			super(parent);
			this.errorDescription = assertNotNull(errorDescription);
		}
		
		@Override
		public DubElementType getElementType() {
			return DubElementType.DUB_ERROR_ELEMENT;
		}
		
		@Override
		public String getElementName() {
			return "<error>";
		}
		
		@Override
		public String getPathString() {
			return getParent().getPathString() + "/" + getElementName();
		}
	}
	
	public static class DubRawDependencyElement extends CommonDubElement<DubDependenciesContainer> {
		
		protected DubDependecyRef dubBundleRef;
		
		public DubRawDependencyElement(DubDependenciesContainer parent, DubDependecyRef dubBundleRef) {
			super(parent);
			this.dubBundleRef = dubBundleRef;
		}
		
		@Override
		public DubElementType getElementType() {
			return DubElementType.DUB_RAW_DEP;
		}
		
		public String getBundleName() {
			return dubBundleRef.bundleName;
		}
		
		@Override
		public String getElementName() {
			return getBundleName();
		}
		
		@Override
		public String getPathString() {
			return getParent().getPathString() + "/@" + getElementName();
		}
	}
	
	public static class DubDependencyElement extends CommonDubElement<DubDependenciesContainer> {
		
		protected final DubBundle dubBundle;
		protected final DubDependencySourceFolderElement[] children;
		
		public DubDependencyElement(DubDependenciesContainer parent, DubBundle dubBundle) {
			super(parent);
			this.dubBundle = dubBundle;
			this.children = createChildren();
		}
		
		@Override
		public DubElementType getElementType() {
			return DubElementType.DUB_RESOLVED_DEP;
		}
		
		public String getBundleName() {
			return dubBundle.name;
		}
		
		@Override
		public String getElementName() {
			return getBundleName();
		}
		
		@Override
		public String getPathString() {
			return getParent().getPathString() + "/["+getBundleName()+"]";
		}
		
		public DubBundle getDubBundle() {
			return dubBundle;
		}
		
		protected DubDependencySourceFolderElement[] createChildren() {
			ArrayList<DubDependencySourceFolderElement> sourceContainers = new ArrayList<>();
			IScriptProject scriptProject = getParent().getScriptProject();
			
			for (Path localPath : dubBundle.getEffectiveSourceFolders()) {
				sourceContainers.add(new DubDependencySourceFolderElement(this, localPath, scriptProject));
			}
			return arrayFrom(sourceContainers, DubDependencySourceFolderElement.class);
		}
		
		@Override
		public boolean hasChildren() {
			return children.length > 0;
		}
		
		@Override
		public Object[] getChildren() {
			return children;
		}
		
	}
	
	public static class DubDependencySourceFolderElement extends CommonDubElement<DubDependencyElement> {
		
		protected final Path srcFolderPath;
		protected final IScriptProject scriptProject;
		
		public DubDependencySourceFolderElement(DubDependencyElement parent, Path srcFolderPath, 
				IScriptProject scriptProject) {
			super(parent);
			this.srcFolderPath = assertNotNull(srcFolderPath);
			this.scriptProject = assertNotNull(scriptProject);
		}
		
		@Override
		public DubElementType getElementType() {
			return DubElementType.DUB_DEP_SRC_FOLDER;
		}
		
		public Path getSourceFolderLocalPath() {
			return srcFolderPath;
		}
		
		@Override
		public String getElementName() {
			return srcFolderPath.toString();
		}
		
		@Override
		public String getPathString() {
			return getParent().getPathString() + "/"+getElementName()+"::";
		}
		
		public IProjectFragment getUnderlyingProjectFragment() {
			Path path = getParent().getDubBundle().location.resolve(srcFolderPath);
			IPath bpPath = DLTKUtils.localEnvPath(EclipseUtils.getPath(path));
			try {
				return scriptProject.findProjectFragment(bpPath);
			} catch (ModelException e) {
				return null;
			}
		}
		
		@Override
		public boolean hasChildren() {
			IProjectFragment projectFragment = getUnderlyingProjectFragment();
			try {
				return projectFragment != null && projectFragment.hasChildren();
			} catch (ModelException e) {
				return false;
			}
		}
		
		@Override
		public Object[] getChildren() {
			IProjectFragment projectFragment = getUnderlyingProjectFragment();
			try {
				return projectFragment == null ? NO_CHILDREN : projectFragment.getChildren();
			} catch (ModelException e) {
				return NO_CHILDREN;
			}
		}
	}
	
}