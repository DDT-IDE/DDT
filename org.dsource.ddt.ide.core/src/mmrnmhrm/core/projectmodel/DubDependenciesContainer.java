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

import java.util.ArrayList;

import melnorme.utilbox.misc.ArrayUtil;
import dtool.dub.DubBundle;
import dtool.dub.DubBundle.DubDependecyRef;
import dtool.dub.DubBundleDescription;

public class DubDependenciesContainer extends CommonDubElement {
	
	protected final DubBundleDescription bundleInfo;
	protected final CommonDubElement[] depElements;
	
	public DubDependenciesContainer(DubBundleDescription bundleInfo) {
		super(null);
		this.bundleInfo = assertNotNull(bundleInfo);
		depElements = createChildren();
	}
	
	protected CommonDubElement[] createChildren() {
		ArrayList<CommonDubElement> newChildren = new ArrayList<>();
		
		if(bundleInfo.isResolved()) {
			for (DubBundle dubBundle : bundleInfo.getBundleDependencies()) {
				newChildren.add(new DubDependencyElement(this, dubBundle));
			}
		} else {
			for (DubDependecyRef dubBundleRef : bundleInfo.getMainBundle().getDependencyRefs()) {
				newChildren.add(new DubRawDependencyElement(this, dubBundleRef));
			}
		}
		if(bundleInfo.getMainBundle().error != null) {
			newChildren.add(new DubErrorElement(this, bundleInfo.getMainBundle().error.getMessage()));
		}
		return ArrayUtil.createFrom(newChildren, CommonDubElement.class);
	}
	
	@Override
	public DubElementType getElementType() {
		return DubElementType.DUB_DEP_CONTAINER;
	}
	
	public DubBundleDescription getBundleInfo() {
		return bundleInfo;
	}
	
	@Override
	public boolean hasChildren() {
		return depElements.length > 0;
	}
	
	@Override
	public CommonDubElement[] getChildren() {
		return depElements;
	}
	
	public static class DubErrorElement extends CommonDubElement {
		
		public final String errorDescription;
		
		public DubErrorElement(CommonDubElement parent, String errorDescription) {
			super(parent);
			this.errorDescription = assertNotNull(errorDescription);
		}
		
		@Override
		public DubElementType getElementType() {
			return DubElementType.DUB_ERROR_ELEMENT;
		}
	}
	
	public interface ICommonDepElement {
		public String getBundleName();
	}
	
	public static class DubDependencyElement extends CommonDubElement implements ICommonDepElement {
		
		protected DubBundle dubBundle;
		
		public DubDependencyElement(CommonDubElement parent, DubBundle dubBundle) {
			super(parent);
			this.dubBundle = dubBundle;
		}
		
		@Override
		public DubElementType getElementType() {
			return DubElementType.DUB_RESOLVED_DEP;
		}
		
		@Override
		public String getBundleName() {
			return dubBundle.name;
		}
	}
	
	public static class DubRawDependencyElement extends CommonDubElement implements ICommonDepElement {
		
		protected DubDependecyRef dubBundleRef;
		
		public DubRawDependencyElement(CommonDubElement parent, DubDependecyRef dubBundleRef) {
			super(parent);
			this.dubBundleRef = dubBundleRef;
		}
		
		@Override
		public DubElementType getElementType() {
			return DubElementType.DUB_RAW_DEP;
		}
		
		@Override
		public String getBundleName() {
			return dubBundleRef.bundleName;
		}
	}
	
}