package mmrnmhrm.core.search;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.core.search.index.EntryResult;
import org.eclipse.dltk.core.search.index.Index;
import org.eclipse.dltk.core.search.indexing.IIndexConstants;
import org.eclipse.dltk.core.search.indexing.IndexManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import melnorme.utilbox.misc.StringUtil;
import mmrnmhrm.tests.BaseDeeTest;

public abstract class BaseDeeSearchEngineTest extends BaseDeeTest {
	
	protected static IProjectFragment getSrcFolder(IScriptProject scriptProject, String folderName) {
		return scriptProject.getProjectFragment(scriptProject.getProject().getFolder(folderName));
	}

	protected static ISourceModule getModule(IScriptProject project, String srcFolder, String pkg, String module) {
		IScriptFolder scriptFolder = getSrcFolder(project, srcFolder).getScriptFolder(pkg);
		return scriptFolder.getSourceModule(module + ".d");
	}

	protected static IType getElement(IScriptProject scriptProject, String srcFolder, String pkg, String srcModule) {
		ISourceModule sourceModule = getModule(scriptProject, srcFolder, pkg, srcModule);
		return sourceModule.getType(srcModule);
		//return sourceModule;
	}
	
	
	@BeforeClass
	public static void setup() {
		enableDLTKIndexer();
	}
	
	@AfterClass
	public static void teardown() {
		disableDLTKIndexer();
	}
	
	/* ---------- Some debug helper ---------- */ 
	
	@SuppressWarnings("restriction")
	public static void printIndexDebugInfo(IProject prj) throws Exception {
		
		System.out.println("========= Index DEBUG INFO ========");
		
		IndexManager im = org.eclipse.dltk.internal.core.ModelManager.getModelManager().getIndexManager();
		Index idx = im.getIndex(prj.getFullPath(), true, true); // This is index file for project root
		
		assertNotNull(im.indexLocations.keyTable);
		System.out.println("===== Index Locations ====\n" + im.indexLocations + "\n");
		
		im.waitUntilReady();
		
		// And then check using
		String[] docNames = idx.queryDocumentNames(null); // To check all documents in this index
		assertNotNull(docNames);
		System.out.println("===== Index docs ====\n" + StringUtil.collToString(docNames, "\n") );
		
		System.out.println("===== Query: Type Decl, * ====");
		debugPrintCategory(idx, IIndexConstants.TYPE_DECL);
		System.out.println("===== Query: Field Decl, * ====");
		debugPrintCategory(idx, IIndexConstants.FIELD_DECL);
		System.out.println("===== Query: Method Decl, * ====");
		debugPrintCategory(idx, IIndexConstants.METHOD_DECL);
		System.out.println("===== Query: Ref, * ====");
		debugPrintCategory(idx, IIndexConstants.REF);
		System.out.println("===== Query: Method Ref, * ====");
		debugPrintCategory(idx, IIndexConstants.METHOD_REF);
	}
	
	protected static void debugPrintCategory(Index idx, char[] category) throws IOException {
		char[][] categoryArray = {category};
		EntryResult[] query = idx.query(categoryArray, new char[]{'*'}, SearchPattern.R_PATTERN_MATCH);
		if(query == null) {
			System.out.println("__ null __");
			return;
		}
		for (EntryResult entryResult : query) {
			System.out.println(entryResult.getWord());
		}
	}
	
}