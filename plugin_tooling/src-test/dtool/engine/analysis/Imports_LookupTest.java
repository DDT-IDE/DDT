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
package dtool.engine.analysis;

import static dtool.engine.analysis.Resolvables_SemanticsTest.testResolveElement;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.concurrent.ExecutionException;

import melnorme.lang.tooling.engine.ErrorElement;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.completion.CompletionScopeLookup;
import melnorme.lang.tooling.engine.resolver.ResolvableResult;
import melnorme.lang.tooling.engine.scoping.ResolutionLookup;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.core.fntypes.Predicate;

import org.junit.Test;

import dtool.ast.definitions.Module;
import dtool.ast.references.NamedReference;
import dtool.ast.references.RefModule;
import dtool.engine.ResolvedModule;


public class Imports_LookupTest extends CommonLookupTest {
	
	@Test
	public void testImports() throws Exception { testImports$(); }
	public void testImports$() throws Exception {
		
		testRefModule(parseElement("import target;", "target", RefModule.class), "target");
		testRefModule(parseElement("import pack.target;", "pack.target", RefModule.class), "pack.target");
		
		testRefModule(parseElement("import not_found;", "not_found", RefModule.class), 
			Resolvables_SemanticsTest.NOT_FOUND_SpecialMarker);
		
		// Test package refs.
		testPackageRef(parseRef("import pack.target; auto x = pack;", "pack;"), "pack");
		testPackageRef(parseRef("import pack.subpack.target; auto x = pack;", "pack;"), "pack");
		
		testPackageRef(parseRef("import pack.subpack.target; auto x = pack.subpack/**/;", "subpack/**/"), 
			"pack.subpack");
		
	}
	
	protected PickedElement<NamedReference> parseRef(String source, String marker) throws ExecutionException {
		return parseElement(source, marker, NamedReference.class);
	}
	
	protected void testRefModule(PickedElement<RefModule> refModuleElement, String fqn) {
		ResolvableResult resolution = testResolveElement(refModuleElement);
		
		INamedElement result = resolution.result;
		assertTrue(result.getFullyQualifiedName().equals(fqn));
		
		// Test that it resolves to an alias of the actual module unit. 
		// This is an optimization to not parse the module until really necessary. 
		assertTrue(result instanceof ModuleProxy); 
		
		IConcreteNamedElement moduleTarget = result.resolveConcreteElement(refModuleElement.context);
		
		if(fqn.equals(Resolvables_SemanticsTest.NOT_FOUND_SpecialMarker)) {
			assertTrue(moduleTarget instanceof ErrorElement);
			assertTrue(moduleTarget.getFullyQualifiedName().equals(ErrorElement.NOT_FOUND__Name));
		} else {
			assertTrue(moduleTarget instanceof Module);
			assertTrue(moduleTarget.getFullyQualifiedName().equals(fqn));
		}
	}
	
	protected void testPackageRef(PickedElement<? extends NamedReference> refToPackage, String fqn) {
		ResolvableResult resolution = testResolveElement(refToPackage);
		
		INamedElement result = resolution.result;
		assertTrue(result.getFullyQualifiedName().equals(fqn));
		
		if(fqn == ErrorElement.NOT_FOUND__Name) {
			return;
		}
		
		assertTrue(result instanceof PackageNamespaceFragment); 
		
		IConcreteNamedElement concreteTarget = result.resolveConcreteElement(refToPackage.context);
		assertTrue(concreteTarget instanceof PackageNamespaceFragment);
		assertTrue(concreteTarget.getFullyQualifiedName().equals(fqn));
	}
	
	
	@Test
	public void testShadowing() throws Exception { testShadowing$(); }
	public void testShadowing$() throws Exception {
		PickedElement<Module> modulePick = parseElement("import foo; class foo_member; ", "", Module.class);
		
		Module node = modulePick.element;
		CompletionScopeLookup lookup = new CompletionScopeLookup(node, node.getEndPos(), modulePick.context);
		node.performNameLookup(lookup);
		
		resultsChecker(lookup).checkResults(array(
			"_tests/", "foo/", "everywhere/",
			
			"_tests/foo_member",
			"foo/foo_member2",
			
			"everywhere/everywhere_member"
		));
		
	}
	
	/* -----------------  ----------------- */
	
	@Test
	public void test_PackageNamespace() throws Exception { test_PackageNamespace$(); }
	public void test_PackageNamespace$() throws Exception {
		
		// Test name conflicts
		
		testLookup(parseModule_("module xxx; int xxx; "
				+ "auto _ = xxx/*M*/;"),
				
			checkSingleResult("int xxx;") 
		);
		
		testLookup(parseModule_("int xxx; import xxx; "
				+ "auto _ = xxx/*M*/;"),
				
			checkNameConflict("int xxx;", "module[xxx]") 
		);
		
		testLookup(parseModule_("int xxx; char xxx; import xxx; "
				+ "auto _ = xxx/*M*/;"),
				
			checkNameConflict("int xxx;", "char xxx;", "module[xxx]") 
		);
		
		
		// Test namespace aggregation
		testLookup(parseModule_("import xxx.foo; import xxx.bar; import xxx.; "
				+ "auto _ = xxx/*M*/;"), 
				
			checkNS(array("module[xxx.foo]", "module[xxx.bar]"))
		);
		
		// Test duplicate import
		
//		testLookup(parseModule_(
//			"import xxx; import xxx;" 
//			+ " auto + = xxx/*M*/"),  
//			
//			checkModule("module[xxx]")
//		);
//		
//		testLookup(parseModule_(
//			"import xxx.foo; import xxx.bar.z; import xxx.bar.z; import xxx.foo;" 
//			+ " auto + = xxx/*M*/"),  
//			
//			checkNS(array("module[xxx.foo]", "module[xxx.bar.z]"))
//		);
		
		
//		doNamespaceLookupTest(parseModule_(
//			"import a.xxx.foo; import a.xxx.bar; import a.xxx.xpto.foo; import a.yyy.xpto;"
//			+ "import a.xxx.; import a.zzz.bar; "
//			+ "auto _ = a.xxx/*M*/"), "/*M*/",
//			
//			array(
//			"module[a.xxx.foo]", "module[a.xxx.bar]", "module[a.xxx.xpto.foo]" 
//		));
		
		doNamespaceLookupTest(parseModule_(
			"import a.xxx.foo; import a.xxx.bar; import a.xxx.xpto.foo; import a.xxx.; "
			+ "import a.yyy.bar; "
			+ "import a.zzz;"
			+ "auto _ = a/*M*/"), 
			"/*M*/",
			
			array(
			"PNamespace[a.xxx]", "PNamespace[a.yyy]","module[a.zzz]" 
		));
		
		
		String scopeOverload_vsImport = 
			"void xxx;" +
			"void func() {" +
			"	import xxx; auto _ = xxx/*M*/" +
			"}";	
		
		testLookup(parseModule_(scopeOverload_vsImport), 
			checkSingleResult(
			"module[xxx]"
		));
		
	}
	
	protected ResolutionLookup doNamespaceLookupTest(ResolvedModule resolvedModule, String offsetMarker, 
			final String[] expectedResults) {
		return testLookup(resolvedModule, offsetMarker, checkNS(expectedResults));
	}
	
	protected Predicate<INamedElement> checkNS(final String[] expectedResults) {
		return new Predicate<INamedElement>() {
			
			@Override
			public boolean evaluate(INamedElement matchedElement) {
				PackageNamespace packageNameSpace = assertInstance(matchedElement, PackageNamespace.class);
				assertEqualSet(
					hashSet(elementToStringArray(packageNameSpace.getNamedElements().values())), 
					hashSet(expectedResults)
				);
				
				return true;
			}
			
		};
	}
	
	protected Predicate<INamedElement> checkModule(final String expectedToString) {
		return new Predicate<INamedElement>() {
			@Override
			public boolean evaluate(INamedElement matchedElement) {
				ModuleProxy moduleProxy = assertInstance(matchedElement, ModuleProxy.class);
				assertTrue(namedElementToString(moduleProxy).equals(expectedToString));
				return true;
			}
		};
	}
	
}