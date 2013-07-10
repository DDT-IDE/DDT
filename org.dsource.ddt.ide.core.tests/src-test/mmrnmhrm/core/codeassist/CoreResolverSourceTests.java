package mmrnmhrm.core.codeassist;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import melnorme.utilbox.misc.MiscUtil;
import mmrnmhrm.tests.BaseDeeTest;

import org.eclipse.core.runtime.CoreException;

import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.parser.DeeParserResult;
import dtool.resolver.ReferenceResolver.DirectDefUnitResolve;
import dtool.resolver.ResolverSourceTests;
import dtool.resolver.api.IModuleResolver;
import dtool.sourcegen.AnnotatedSource.MetadataEntry;

public class CoreResolverSourceTests extends ResolverSourceTests {
	
	static {
		MiscUtil.loadClass(BaseDeeTest.class);
	}
	
	public CoreResolverSourceTests(String testUIDescription, File file) {
		super(testUIDescription, file);
	}
	
	@Override
	public ITestsModuleResolver updateInstrumentModuleResolver(String projectFolderName, String moduleName,
		DeeParserResult parseResult, ITestsModuleResolver existingMR) {
		try {
			File projectDir = new File(file.getParent(), assertNotNull(projectFolderName));
			return TestsWorkspaceModuleResolver.updateTestsModuleResolver(projectDir, moduleName, parseResult, 
				(TestsWorkspaceModuleResolver) existingMR);
		} catch(CoreException | IOException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	// TODO: rest of custom tests
	
	@Override
	public void runRefSearchTest_________(DeeParserResult parseResult, IModuleResolver mr, int defaultOffset,
		MetadataEntry mde) {
		super.runRefSearchTest_________(parseResult, mr, defaultOffset, mde);
	}
	
	@Override
	protected HashSet<String> prepareResultProposals(Collection<DefUnit> results, boolean compareUsingName) {
		for (Iterator<DefUnit> iterator = results.iterator(); iterator.hasNext(); ) {
			DefUnit defUnit = iterator.next();
			if(defUnit.getArcheType() == EArcheType.Module) {
				String fqName = getDefUnitFullyTypedName(defUnit);
				if(fqName.equals("object") || fqName.equals("std.stdio")) {
					iterator.remove();
				}
			}
		}
		HashSet<String> trimedResults = super.prepareResultProposals(results, compareUsingName);
		return trimedResults;
	}
	
	@Override
	public DirectDefUnitResolve runFindTest_________(DeeParserResult parseResult, IModuleResolver mr, 
		MetadataEntry mde) {
		return super.runFindTest_________(parseResult, mr, mde);
	}
	
	@Override
	public void runFindMissingTest_________(DeeParserResult parseResult, IModuleResolver mr, MetadataEntry mde) {
		super.runFindMissingTest_________(parseResult, mr, mde);
	}
	
	@Override
	public void runFindFailTest_________(DeeParserResult parseResult, MetadataEntry mde) {
		super.runFindFailTest_________(parseResult, mde);
	}
	
}