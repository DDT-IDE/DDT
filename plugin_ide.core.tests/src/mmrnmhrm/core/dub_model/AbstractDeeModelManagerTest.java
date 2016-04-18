/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.dub_model;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import dtool.dub.CommonDubTest;
import dtool.dub.CommonDubTest.DubBundleChecker;
import dtool.dub.DubBundle.DubBundleException;
import dtool.dub.DubBundleDescription;
import dtool.dub.DubDescribeParserTest;
import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.core.project_model.view.BundleErrorElement;
import melnorme.lang.ide.core.project_model.view.DependenciesContainer;
import melnorme.lang.ide.core.project_model.view.IBundleModelElement;
import melnorme.lang.ide.core.utils.ResourceUtils;
import melnorme.lang.tooling.BundlePath;
import melnorme.lang.tooling.bundle.BundleInfo;
import melnorme.utilbox.concurrency.ITaskAgent;
import melnorme.utilbox.concurrency.LatchRunnable;
import melnorme.utilbox.misc.CollectionUtil;
import melnorme.utilbox.misc.Location;
import mmrnmhrm.core.dub_model.DeeBundleModelManager.DeeBundleModel;
import mmrnmhrm.tests.CommonDeeWorkspaceTest;


abstract class JsHelpers extends CommonDeeWorkspaceTest {
	
	public static String jsObject(String... entries) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (String entry : entries) {
			sb.append(entry);
			if(!entry.endsWith(",")) {
				sb.append(",");
			}
		}
		sb.append("\"dummyEndKey\" : null } ");
		return sb.toString();
	}
	
	public static String jsEntry(String key, String value) {
		return "\""+key+"\" : \""+value+"\",";
	}
	
	public static String jsEntryValue(String key, Object value) {
		return "\""+key+"\" : "+value.toString()+",";
	}
	
	public static String jsFileEnd() {
		return "\"dummyEndKey\" : null } ";
	}
	
	public static String jsEntry(String key, CharSequence value) {
		return "\"" + key + "\" : " + jsToString(value) + ",";
	}
	
	private static String jsToString(CharSequence value) {
		if(value instanceof String) {
			return "\""+value+"\"";
		} else {
			return value.toString();
		}
	}
	
	public static StringBuilder jsArray(CharSequence... objs) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (CharSequence obj : objs) {
			sb.append(jsToString(obj));
			sb.append(",");
		}
		sb.append("]");
		return sb;
	}
	
}
/**
 * Utilities for manipulation of Dub projects
 */
public abstract class AbstractDeeModelManagerTest extends JsHelpers {
	
	protected static final Location ECLIPSE_WORKSPACE_PATH = ResourceUtils.getWorkspaceLocation();
	
	static {
		initDubRepositoriesPath();
	}
	
	private static void initDubRepositoriesPath() {
		DubDescribeParserTest.initDubRepositoriesPath();
		DubDescribeParserTest.dubAddPath(ECLIPSE_WORKSPACE_PATH);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
	    		System.out.println("BaseDubModelManagerTest shutdown hook.");
	    		DubDescribeParserTest.dubRemovePath(ECLIPSE_WORKSPACE_PATH);
				DubDescribeParserTest.cleanupDubRepositoriesPath();
			}
		});
		_awaitModelUpdates_();
	}
	
	
	/* -----------------  ----------------- */
	
	protected static Location loc(IProject project) {
		return loc(project.getLocation().toFile().toPath());
	}
	
	protected static BundlePath bpath(IProject project) {
		return BundlePath.create(project.getLocation().toFile().toPath());
	}
	
	protected static ITaskAgent getModelAgent() {
		return LangCore.getBundleModelManager().internal_getModelAgent();
	}
	
	protected static void _awaitModelUpdates_() {
		LangCore.getBundleModelManager().syncPendingUpdates();
	}
	
	protected static final DeeBundleModel model = getModelManager().getModel();
	
	protected static DeeBundleModelManager getModelManager() {
		return LangCore.getBundleModelManager();
	}
	
	protected static DubBundleDescription getExistingDubBundleInfo(IProject project) {
		return assertNotNull(model.getBundleInfo(project)).getBundleDesc();
	}
	
	public static DependenciesContainer getDubContainer(IProject project) {
		BundleInfo bundleInfo = assertNotNull(model.getBundleInfo(project));
		return new DependenciesContainer(bundleInfo, project);
	}
	
	protected static LatchRunnable writeDubJsonWithModelLatch(IProject project, String contents) throws CoreException {
		LatchRunnable latchRunnable = new LatchRunnable();
		getModelAgent().submit(latchRunnable);
		writeDubJson(project, contents);
		return latchRunnable;
	}
	
	protected static void writeDubJson(IProject project, String contents) throws CoreException {
		writeStringToFile(project, "dub.json", contents);
	}
	
	public static Path[] srcFolders(String... elems) {
		return CommonDubTest.paths(elems);
	}
	
	public void writeDubJsonAndCheckDubModel(String dubJson, IProject project, DubBundleChecker expMainBundle)
			throws CoreException {
		LatchRunnable preUpdateLatch = writeDubJsonWithModelLatch(project, dubJson);
		DubBundleDescription unresolvedBundleDesc = getExistingDubBundleInfo(project);
		preUpdateLatch.releaseAll();
		
		checkDubModel(unresolvedBundleDesc, project, expMainBundle);
	}
	
	public void checkDubModel(DubBundleDescription unresolvedDubBundle, IProject project, 
			DubBundleChecker expMainBundle) throws CoreException {
		checkUnresolvedBundle(project, expMainBundle, unresolvedDubBundle);
		
		LangCore.getBundleModelManager().syncPendingUpdates();
		
		DubBundleDescription dubBundle = getExistingDubBundleInfo(project);
		if(unresolvedDubBundle.hasErrors()) {
			// Check that we did not attempt to call dub describe on a manifest with errors
			assertTrue(unresolvedDubBundle == dubBundle);
		}
		
		DubBundleException error = dubBundle.getError();
		if(error != null) {
			expMainBundle.checkBundleDescription(unresolvedDubBundle, false);
			testDubContainerUnresolved(project, expMainBundle, true);
			
			IMarker errorMarker = assertNotNull(getDubErrorMarker(project));
			assertTrue(errorMarker.getAttribute(IMarker.MESSAGE, "").startsWith(error.getMessage()));
			assertEquals(errorMarker.getAttribute(IMarker.SEVERITY), IMarker.SEVERITY_ERROR);
			
		} else {
			checkFullyResolvedCode(project, dubBundle, expMainBundle);
		}
	}
	
	/* ----------------- result checking code ----------------- */
	
	protected IMarker getDubErrorMarker(IProject project) throws CoreException {
		IMarker[] markers = DeeBundleModelManager.getDubErrorMarkers(project);
		if(markers.length == 0)
			return null;
		
		assertTrue(markers.length == 1);
		return markers[0];
	}
	
	protected void checkUnresolvedBundle(IProject project, DubBundleChecker expMainBundle,
			DubBundleDescription dubBundleDesc) {
		expMainBundle.checkBundleDescription(dubBundleDesc, false);
		testDubContainerUnresolved(project, expMainBundle, !expMainBundle.isResolvedOnlyError());
	}
	
	protected void checkFullyResolvedCode(IProject project, DubBundleDescription dubBundle, 
			DubBundleChecker expMainBundle) throws CoreException {
		expMainBundle.checkBundleDescription(dubBundle, true);
		testDubContainer(project, expMainBundle);

//		IScriptProject dubProject = DLTKCore.create(project);
//		checkRawBuildpath(dubProject.getRawBuildpath(), expMainBundle.sourceFolders);
		
//		checkResolvedBuildpath(dubProject.getResolvedBuildpath(false), expMainBundle.sourceFolders);
		
		IMarker[] dubErrorMarkers = DeeBundleModelManager.getDubErrorMarkers(project);
		assertTrue(dubErrorMarkers.length == 0);
	}
	
	protected void testDubContainerUnresolved(IProject project, DubBundleChecker expMainBundle, 
			boolean expectedError) {
		DependenciesContainer dubContainer = getDubContainer(project);
		LinkedList<IBundleModelElement> depChildren = CollectionUtil.createLinkedList(dubContainer.getChildren());
		for (String rawDep : expMainBundle.rawDeps) {
			checkAndRemoveRawDep(depChildren, rawDep);
		}
		if(expectedError) {
			removeErrorElement(expMainBundle, depChildren);
		}
		assertTrue(depChildren.isEmpty());
	}
	
	protected void checkAndRemoveRawDep(Collection<IBundleModelElement> children, String depName) {
		assertNotNull(removeChildDep(children, depName));
	}
	
	protected IBundleModelElement removeChildDep(Collection<IBundleModelElement> children, String depName) {
		for (IBundleModelElement dubElement : children) {
			if(dubElement instanceof IBundleModelElement) {
				IBundleModelElement depElement = (IBundleModelElement) dubElement;
				if(depElement.getElementName().equals(depName)) {
					assertTrue(children.remove(dubElement));
					return depElement;
				}
			}
		}
		return null;
	}
	
	protected void testDubContainer(IProject project, DubBundleChecker expMainBundle) {
		DependenciesContainer dubContainer = getDubContainer(project);
		assertTrue(dubContainer.getBundleInfo().getBundleDesc().isResolved());
		
		IBundleModelElement[] children = dubContainer.getChildren();
		LinkedList<IBundleModelElement> depChildren = CollectionUtil.createLinkedList(children);
		for (DubBundleChecker dep : expMainBundle.expectedDeps) {
			checkAndRemoveChildDep(depChildren, dep);
		}
		removeErrorElement(expMainBundle, depChildren);
		assertTrue(depChildren.isEmpty());
	}
	
	protected void removeErrorElement(DubBundleChecker expMainBundle, LinkedList<IBundleModelElement> depChildren) {
		if(expMainBundle.errorMsgStart != null) {
			assertTrue(depChildren.size() > 0);
			IBundleModelElement removed = depChildren.remove(0);
			BundleErrorElement dubErrorElement = assertCast(removed, BundleErrorElement.class);
			assertTrue(dubErrorElement.errorDescription.contains(expMainBundle.errorMsgStart));
		}
	}
	
	protected void checkAndRemoveChildDep(Collection<IBundleModelElement> children, DubBundleChecker dep) {
		String depName = dep.bundleName;
		IBundleModelElement depElement = removeChildDep(children, depName);
		assertNotNull(depElement);
		if(dep.sourceFolders == null) {
			return;
		}
		// TODO: check children
	}
	
	/* ----------------- buildpath checking ----------------- */
	
//	public static void checkRawBuildpath(IBuildpathEntry[] rawBuildpath, Path[] srcFolders) throws ModelException {
//		HashSet<Path> sourcePaths = hashSet(srcFolders);
//		
//		for (IBuildpathEntry bpEntry : rawBuildpath) {
//			IPath entryPath = bpEntry.getPath();
//			
//			if((bpEntry.getEntryKind() == IBuildpathEntry.BPE_CONTAINER)) {
//				assertFail();
//				continue;
//			}
//			
//			assertTrue(bpEntry.getEntryKind() == IBuildpathEntry.BPE_SOURCE);
//			assertTrue(bpEntry.isExternal() == false);
//			IPath folderPath = entryPath.removeFirstSegments(1); // Remove project segment
//			assertTrue(sourcePaths.remove(folderPath.toFile().toPath()));
//			continue;
//		}
//		
//		// Ensure we matched every entry
//		assertTrue(sourcePaths.isEmpty());
//	}
//	
//	public static void checkResolvedBuildpath(IBuildpathEntry[] buildpath, Path[] srcFolders) throws ModelException {
//		HashSet<Path> sourcePaths = hashSet(srcFolders);
//		
//		LinkedList<IBuildpathEntry> buildpathToVerify = CollectionUtil.createLinkedList(buildpath);
//		
//		for (ListIterator<IBuildpathEntry> iter = buildpathToVerify.listIterator(); iter.hasNext(); ) {
//			IBuildpathEntry bpEntry = iter.next();
//			
//			IPath entryPath = EnvironmentPathUtils.getLocalPath(bpEntry.getPath());
//			
//			if(bpEntry.getEntryKind() == IBuildpathEntry.BPE_LIBRARY) {
//				String entryPathStr = entryPath.toString();
//				assertTrue(
//						entryPathStr.endsWith("druntime/import") || 
//						entryPathStr.endsWith("phobos") ||
//						entryPathStr.startsWith("#special#builtin"));
//				iter.remove();
//				continue;
//			}
//			
//			assertTrue(bpEntry.getEntryKind() == IBuildpathEntry.BPE_SOURCE);
//			assertTrue(bpEntry.isExternal() == false);
//			IPath folderPath = entryPath.removeFirstSegments(1); // Remove project segment
//			assertTrue(sourcePaths.remove(folderPath.toFile().toPath()));
//			iter.remove();
//			continue;
//		}
//		
//		// Ensure we matched every entry
//		assertTrue(sourcePaths.isEmpty());
//		assertTrue(buildpathToVerify.isEmpty());
//	}
	
}