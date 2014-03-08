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


import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.misc.CollectionUtil.createCollection;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.ListIterator;

import melnorme.lang.ide.core.utils.ResourceUtils;
import melnorme.utilbox.concurrency.ITaskAgent;
import melnorme.utilbox.concurrency.LatchRunnable;
import melnorme.utilbox.misc.CollectionUtil;
import melnorme.utilbox.misc.FileUtil;
import melnorme.utilbox.misc.StringUtil;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.projectmodel.DubDependenciesContainer.DubErrorElement;
import mmrnmhrm.core.projectmodel.DubDependenciesContainer.ICommonDepElement;
import mmrnmhrm.tests.BaseDeeTest;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

import dtool.dub.CommonDubTest;
import dtool.dub.CommonDubTest.DubBundleChecker;
import dtool.dub.DubBundle.DubBundleException;
import dtool.dub.DubBundleDescription;
import dtool.dub.DubDescribeParserTest;

/**
 * Utilities for manipulation of Dub projects
 */
public abstract class BaseDubModelManagerTest extends BaseDeeTest {
	
	protected static final Path ECLIPSE_WORKSPACE_PATH = DeeCore.getWorkspaceRoot().getLocation().toFile().toPath();
	
	static {
		initDubRepositoriesPath();
	}
	
	private static void initDubRepositoriesPath() {
		DubDescribeParserTest.initDubRepositoriesPath();
		DubDescribeParserTest.dubAddPath(ECLIPSE_WORKSPACE_PATH);
		DubModelManager.startDefault();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
	    		System.out.println("BaseDubModelManagerTest shutdown hook.");
	    		DubDescribeParserTest.dubRemovePath(ECLIPSE_WORKSPACE_PATH);
				DubDescribeParserTest.cleanupDubRepositoriesPath();
			}
		});
	}
	
	public static String readFileContents(Path path) throws IOException {
		assertTrue(path.isAbsolute());
		return FileUtil.readStringFromFile(path.toFile(), StringUtil.UTF8);
	}
	
	public static void writeStringToFile(IProject project, String name, String contents) 
			throws CoreException {
		IFile file = project.getFile(name);
		ResourceUtils.writeToFile(file, new ByteArrayInputStream(contents.getBytes(StringUtil.UTF8)));
	}
	
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
	
	/* -----------------  ----------------- */
	
	protected Path loc(IProject project) {
		return project.getLocation().toFile().toPath();
	}
	
	protected static ITaskAgent getModelAgent() {
		return DubModelManager.getDefault().internal_getModelAgent();
	}
	
	protected void _awaitModelUpdates_() {
		DubModelManager.getDefault().syncPendingUpdates();
	}
	
	protected static DubModelManager getProjectModel() {
		return DubModelManager.getDefault();
	}
	
	protected static DubBundleDescription getExistingDubBundleInfo(String projectName) {
		return assertNotNull(DubModel.getBundleInfo(projectName));
	}
	
	protected DubDependenciesContainer getDubContainer(IProject project) {
		return DubModelManager.getDubContainer(project);
	}
	
	protected static LatchRunnable writeDubJsonWithModelLatch(IProject project, String contents) 
			throws CoreException {
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
		DubBundleDescription unresolvedBundleDesc = getExistingDubBundleInfo(project.getName());
		preUpdateLatch.releaseAll();
		
		checkDubModel(unresolvedBundleDesc, project, expMainBundle);
	}
	
	public void checkDubModel(DubBundleDescription unresolvedDubBundle, IProject project, 
			DubBundleChecker expMainBundle) throws CoreException {
		checkUnresolvedBundle(project, expMainBundle, unresolvedDubBundle);
		
		DubModelManager.getDefault().syncPendingUpdates();
		
		DubBundleDescription dubBundle = getExistingDubBundleInfo(project.getName());
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
		IMarker[] markers = DubModelManager.getDubErrorMarkers(project);
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
			DubBundleChecker expMainBundle) throws ModelException, CoreException {
		expMainBundle.checkBundleDescription(dubBundle, true);
		testDubContainer(project, expMainBundle);

		DubBundleChecker[] deps = expMainBundle.deps;
		IScriptProject dubProject = DLTKCore.create(project);
		checkRawBuildpath(dubProject.getRawBuildpath(), expMainBundle.sourceFolders);
		
		checkResolvedBuildpath(dubProject.getResolvedBuildpath(false), expMainBundle.sourceFolders, deps);
		
		IMarker[] dubErrorMarkers = DubModelManager.getDubErrorMarkers(project);
		assertTrue(dubErrorMarkers.length == 0);
	}
	
	protected void testDubContainerUnresolved(IProject project, DubBundleChecker expMainBundle, 
			boolean expectedError) {
		DubDependenciesContainer dubContainer = getDubContainer(project);
		assertNotNull(dubContainer);
		LinkedList<IDubElement> depChildren = CollectionUtil.createLinkedList(dubContainer.getChildren());
		for (String rawDep : expMainBundle.rawDeps) {
			checkAndRemoveRawDep(depChildren, rawDep);
		}
		if(expectedError) {
			removeErrorElement(expMainBundle, depChildren);
		}
		assertTrue(depChildren.isEmpty());
	}
	
	protected void checkAndRemoveRawDep(Collection<IDubElement> children, String depName) {
		assertNotNull(removeChildDep(children, depName));
	}
	
	protected ICommonDepElement removeChildDep(Collection<IDubElement> children, String depName) {
		for (IDubElement dubElement : children) {
			if(dubElement instanceof ICommonDepElement) {
				ICommonDepElement depElement = (ICommonDepElement) dubElement;
				if(depElement.getBundleName().equals(depName)) {
					assertTrue(children.remove(dubElement));
					return depElement;
				}
			}
		}
		return null;
	}
	
	protected void testDubContainer(IProject project, DubBundleChecker expMainBundle) {
		DubDependenciesContainer dubContainer = getDubContainer(project);
		assertNotNull(dubContainer);
		assertTrue(dubContainer.getBundleInfo().isResolved());
		
		IDubElement[] children = dubContainer.getChildren();
		LinkedList<IDubElement> depChildren = CollectionUtil.createLinkedList(children);
		for (DubBundleChecker dep : expMainBundle.deps) {
			checkAndRemoveChildDep(depChildren, dep);
		}
		removeErrorElement(expMainBundle, depChildren);
		assertTrue(depChildren.isEmpty());
	}
	
	protected void removeErrorElement(DubBundleChecker expMainBundle, LinkedList<IDubElement> depChildren) {
		if(expMainBundle.errorMsgStart != null) {
			assertTrue(depChildren.size() > 0);
			IDubElement removed = depChildren.remove(0);
			DubErrorElement dubErrorElement = assertCast(removed, DubErrorElement.class);
			assertTrue(dubErrorElement.errorDescription.startsWith(expMainBundle.errorMsgStart));
		}
	}
	
	protected void checkAndRemoveChildDep(Collection<IDubElement> children, DubBundleChecker dep) {
		String depName = dep.bundleName;
		ICommonDepElement depElement = removeChildDep(children, depName);
		assertNotNull(depElement);
		if(dep.sourceFolders == null) {
			return;
		}
		// TODO: check children
	}
	
	/* ----------------- buildpath checking ----------------- */
	
	public static void checkRawBuildpath(IBuildpathEntry[] rawBuildpath, Path[] srcFolders) throws ModelException {
		HashSet<Path> sourcePaths = hashSet(srcFolders);
		
		for (IBuildpathEntry bpEntry : rawBuildpath) {
			IPath entryPath = bpEntry.getPath();
			if(entryPath.segment(0).equals(ScriptRuntime.INTERPRETER_CONTAINER)) {
				continue;
			}
			
			if((bpEntry.getEntryKind() == IBuildpathEntry.BPE_CONTAINER)) {
				assertTrue(entryPath.toString() .equals(DubBuildpathContainerInitializer.ID));
				continue;
			}
			
			assertTrue(bpEntry.getEntryKind() == IBuildpathEntry.BPE_SOURCE);
			assertTrue(bpEntry.isExternal() == false);
			IPath folderPath = entryPath.removeFirstSegments(1); // Remove project segment
			assertTrue(sourcePaths.remove(folderPath.toFile().toPath()));
			continue;
		}
		
		// Ensure we matched every entry
		assertTrue(sourcePaths.isEmpty());
	}
	
	public static void checkResolvedBuildpath(IBuildpathEntry[] buildpath, Path[] srcFolders, 
			DubBundleChecker[] deps) throws ModelException {
		HashSet<Path> sourcePaths = hashSet(srcFolders);
		
		LinkedList<IBuildpathEntry> buildpathToVerify = CollectionUtil.createLinkedList(buildpath);
		
		for (DubBundleChecker bundleDep : deps) {
			verifyAndRemoveDepBuildpathEntries(bundleDep, buildpathToVerify);
		}
		
		for (ListIterator<IBuildpathEntry> iter = buildpathToVerify.listIterator(); iter.hasNext(); ) {
			IBuildpathEntry bpEntry = iter.next();
			
			IPath entryPath = EnvironmentPathUtils.getLocalPath(bpEntry.getPath());
			
			if(bpEntry.getEntryKind() == IBuildpathEntry.BPE_LIBRARY && !DubContainer.isDubBuildpathEntry(bpEntry)) {
				String entryPathStr = entryPath.toString();
				assertTrue(
						entryPathStr.endsWith("druntime/import") || 
						entryPathStr.endsWith("phobos") ||
						entryPathStr.startsWith("#special#builtin"));
				iter.remove();
				continue;
			}
			
			assertTrue(bpEntry.getEntryKind() == IBuildpathEntry.BPE_SOURCE);
			assertTrue(bpEntry.isExternal() == false);
			IPath folderPath = entryPath.removeFirstSegments(1); // Remove project segment
			assertTrue(sourcePaths.remove(folderPath.toFile().toPath()));
			iter.remove();
			continue;
		}
		
		// Ensure we matched every entry
		assertTrue(sourcePaths.isEmpty());
		assertTrue(buildpathToVerify.isEmpty());
	}
	
	protected static void verifyAndRemoveDepBuildpathEntries(DubBundleChecker bundleDep,
			LinkedList<IBuildpathEntry> buildpathToVerify) {
		if(bundleDep instanceof ProjDepChecker) {
			ProjDepChecker projDepChecker = (ProjDepChecker) bundleDep;
			removeDepProjBPEntry(projDepChecker.project, buildpathToVerify);
		} else {
			for (Path srcFolderPath : createCollection(bundleDep.sourceFolders)) {
				Path srcFolderAbsolutePath = bundleDep.location.resolve(srcFolderPath);
				removeDepBuildpathEntry(buildpathToVerify, srcFolderAbsolutePath);
			}
		}
	}
	
	protected static void removeDepProjBPEntry(IProject project, LinkedList<IBuildpathEntry> buildpathToVerify) {
		for (ListIterator<IBuildpathEntry> iter = buildpathToVerify.listIterator(); iter.hasNext(); ) {
			IBuildpathEntry bpEntry = iter.next();
			IPath bpEntryPath = EnvironmentPathUtils.getLocalPath(bpEntry.getPath());
			
			if(bpEntryPath.equals(project.getFullPath())) {
				assertTrue(bpEntry.getEntryKind() == IBuildpathEntry.BPE_PROJECT);
				iter.remove();
				return;
			}
		}
		assertFail(); // Must find match
	}
	
	protected static void removeDepBuildpathEntry(LinkedList<IBuildpathEntry> buildpathToVerify,
			Path srcFolderAbsolutePath) {
		for (ListIterator<IBuildpathEntry> iter = buildpathToVerify.listIterator(); iter.hasNext(); ) {
			IBuildpathEntry bpEntry = iter.next();
			
			IPath bpEntryPath = EnvironmentPathUtils.getLocalPath(bpEntry.getPath());
			if(bpEntryPath.toFile().toPath().equals(srcFolderAbsolutePath)) {
				assertTrue(bpEntry.getEntryKind() == IBuildpathEntry.BPE_LIBRARY);
				iter.remove();
				return;
			}
		}
		assertFail(); // Must find match
	}
	
	public static class ProjDepChecker extends DubBundleChecker {
		
		protected IProject project;
		
		public ProjDepChecker(IProject project, String bundleName) {
			super(project.getLocation().toFile().toPath(), bundleName);
			this.project = project;
		}
		
	}
	
}