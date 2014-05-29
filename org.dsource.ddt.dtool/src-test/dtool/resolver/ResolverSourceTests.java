package dtool.resolver;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.misc.MiscUtil.nullToOther;

import java.io.File;

import dtool.ast.references.NamedReference;
import dtool.parser.DeeParser;
import dtool.parser.DeeParserResult;
import dtool.project.NullModuleResolver;
import dtool.resolver.ReferenceResolver.DirectDefUnitResolve;
import dtool.sourcegen.AnnotatedSource;
import dtool.sourcegen.AnnotatedSource.MetadataEntry;
import dtool.util.NewUtils;

public class ResolverSourceTests extends BaseResolverSourceTests {
	
	public ResolverSourceTests(String testUIDescription, File file) {
		super(testUIDescription, file);
	}
	
	public static final class TestsNullModuleResolver extends NullModuleResolver 
		implements ITestsModuleResolver {
		@Override
		public void cleanupChanges() {
		}
	}
	
	protected DeeParserResult parseResult;
	
	@Override
	public void setupTestProject(String moduleName, String projectFolderName, AnnotatedSource testCase) {
		moduleName = nullToOther(moduleName, DEFAULT_MODULE_NAME);
		parseResult = DeeParser.parseSource(testCase.source, moduleName);
		
		if(projectFolderName == null || projectFolderName.isEmpty()) {
			mr = new TestsNullModuleResolver();
			mrTestCleanup = new TestsNullModuleResolver();
			return;
		}
		TestsSimpleModuleResolver existingMR = moduleResolvers.get(projectFolderName);
		if(existingMR == null) {
			File projectFolder = getProjectDirectory(projectFolderName);
			existingMR = new TestsSimpleModuleResolver(projectFolder);
			moduleResolvers.put(projectFolderName, existingMR); // Cache the MR data
		}
		if(moduleName != null) {
			existingMR.setExtraModule(moduleName, parseResult);
		}
		mr = existingMR;
		mrTestCleanup = existingMR;
	}
	
	@Override
	public void runRefSearchTest_________(RefSearchOptions options) {
		
		PrefixDefUnitSearch search = 
			PrefixDefUnitSearch.doCompletionSearch(parseResult, options.offset, mr);
		
		assertEquals(search.getResultCode(), options.expectedStatusCode);
		assertEquals(search.searchOptions.rplLen, options.rplLen);
		
		checkResults(search.getResults(), options.expectedResults);
	}
	
	@Override
	protected void runFindFailTest_________(MetadataEntry mde) {
		DirectDefUnitResolve resolveResult = resolveAtOffset(mde.offset);
		assertTrue(resolveResult.pickedRef == null || resolveResult.invalidPickRef);
	}
	
	public DirectDefUnitResolve resolveAtOffset(int offset) {
		return ReferenceResolver.resolveAtOffset(parseResult, offset, mr);
	}
	
	@Override
	protected void runFindTest_________(MetadataEntry mde) {
		doFindTest(mde);
	}
	
	public DirectDefUnitResolve doFindTest(MetadataEntry mde) {
		String[] expectedResults = splitValues(mde.sourceValue);
		return doRunFindTest(mde.offset, expectedResults);
	}
	
	@Override
	public void runFindMissingTest_________(MetadataEntry mde) {
		assertTrue(mde.sourceValue == null);
		DirectDefUnitResolve result = doRunFindTest(mde.offset, NewUtils.EMPTY_STRING_ARRAY);
		assertTrue(result.resolvedDefUnits == null);
		assertTrue(result.pickedRef instanceof NamedReference);
		NamedReference pickedRef_named = (NamedReference) result.pickedRef;
		assertTrue(pickedRef_named.isMissingCoreReference());
	}
	
	public DirectDefUnitResolve doRunFindTest(int offset, String[] expectedResults) {
		DirectDefUnitResolve resolveResult = resolveAtOffset(offset);
		checkResults(resolveResult.getResolvedDefUnits(), expectedResults, false, false);
		return resolveResult;
	}
	
}