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
	public void testResolveOfDirectRef() throws Exception { testResolveOfDirectRef$(); }
	public void testResolveOfDirectRef$() throws Exception {
		
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
	
	
	/* -----------------  ----------------- */
	
	@Test
	public void test_importImplicitNamespace() throws Exception { test_importImplicitNamespace$(); }
	public void test_importImplicitNamespace$() throws Exception {
		
		testLookup(parseModuleWithRef("module xxx;", "xxx"),  
			checkSingleResult("module xxx;###^")
		);

		
		// Test name conflicts
		
		testLookup(parseModuleWithRef("module xxx; int xxx; ", "xxx"),
				
			checkSingleResult("int xxx;") 
		);
		
		testLookup(parseModuleWithRef("int xxx; import xxx; ", "xxx"),
				
			checkNameConflict("int xxx;", "module[xxx]") 
		);
		
		testLookup(parseModuleWithRef("int xxx; char xxx; import xxx; ", "xxx"),
				
			checkNameConflict("int xxx;", "char xxx;", "module[xxx]") 
		);
		
		
		// Test duplicate import
		
		testLookup(parseModuleWithRef("module xxx; import xxx;", "xxx"), 
			
			checkModuleProxy("module[xxx]")
		);
		
		testLookup(parseModuleWithRef("import xxx; import xxx;", "xxx"),  
			
			checkModuleProxy("module[xxx]")
		);
		
		testLookup(
			parseModuleWithRef("import xxx.foo; import xxx.bar.z; import xxx.bar.z; import xxx.foo;", "xxx"),  
			
			checkNS(array("module[xxx.foo]", "PNamespace[xxx.bar]"))
		);
		testLookup(
			parseModuleWithRef("import xxx.foo; import xxx.bar.z; import xxx.bar.z; import xxx.foo;", "xxx.bar"), 
			
			checkNS(array("module[xxx.bar.z]"))
		);

		
		// Test namespace aggregation
		
		testLookup(parseModuleWithRef("import xxx.foo; import xxx.bar; import xxx.; ", "xxx"), 
				
			checkNS(array("module[xxx.foo]", "module[xxx.bar]"))
		);
		
//		doNamespaceLookupTest(parseModule_(
//			"import a.xxx.foo; import a.xxx.bar; import a.xxx.xpto.foo; import a.yyy.xpto;"
//			+ "import a.xxx.; import a.zzz.bar; "
//			+ "auto _ = a.xxx/*M*/"), "/*M*/",
//			
//			array(
//			"module[a.xxx.foo]", "module[a.xxx.bar]", "module[a.xxx.xpto.foo]" 
//		));
		
		testLookup(parseModuleWithRef(
			"import a.xxx.foo; import a.xxx.bar; import a.xxx.xpto.foo; import a.xxx.; "
			+ "import a.yyy.bar; "
			+ "import a.zzz;",
			"a"), 
			
			checkNS(array(
			"PNamespace[a.xxx]", "PNamespace[a.yyy]","module[a.zzz]"
			))
		);
		
		
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
	
	protected Predicate<INamedElement> checkModuleProxy(final String expectedToString) {
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