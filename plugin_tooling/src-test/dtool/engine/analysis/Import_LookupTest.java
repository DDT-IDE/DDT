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
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.core.fntypes.Predicate;

import org.junit.Test;

import dtool.ast.definitions.Module;
import dtool.ast.references.NamedReference;
import dtool.ast.references.RefModule;
import dtool.engine.ResolvedModule;


public class Import_LookupTest extends CommonLookupTest {
	
	private static final String SRC_IMPORT_SELF = "import " + DEFAULT_ModuleName + "; ";
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
		
		assertTrue(result instanceof PackageNamespace); 
		
		IConcreteNamedElement concreteTarget = result.resolveConcreteElement(refToPackage.context);
		assertTrue(concreteTarget instanceof PackageNamespace);
		assertTrue(concreteTarget.getFullyQualifiedName().equals(fqn));
	}
	
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
	
	/* -----------------  ----------------- */

	@Test
	public void test_ImportContent() throws Exception { test_ImportContent$(); }
	public void test_ImportContent$() throws Exception {
		testLookup(parseModule_WithRef("import pack.foobar; ", "PackFoobar_member"),  
			checkSingleResult("int PackFoobar_member;")
		);
		
		testLookup(parseModule_WithRef("import pack.foobar; void PackFoobar_member;", "PackFoobar_member"),  
			checkSingleResult("void PackFoobar_member;")
		);
		
		// Special case: import self:
		testLookup(parseModule_WithRef(SRC_IMPORT_SELF, DEFAULT_ModuleName),  
			checkSingleResult("$"+DEFAULT_ModuleName+"/")
		);
		
		
		testLookup(parseModule_WithRef("import pack.foobar; ", "pack.foobar.pack"),  
			checkSingleResult("PackFoobar pack;")
		);
		// vs. Module name
		testLookup(parseModule_WithRef("import pack.foobar;", "pack"),  
			checkSingleResult("PNamespace[pack]")
		);
		testLookup(parseModule_WithRef("import pack.foobar; int pack;", "pack"),  
			checkNameConflict("PNamespace[pack]", "int pack;")
		);
		
	}
	
	/* -----------------  ----------------- */
	
	@Test
	public void test_importImplicitNamespace() throws Exception { test_importImplicitNamespace$(); }
	public void test_importImplicitNamespace$() throws Exception {
		
		testLookup(parseModule_WithRef("module xxx;", "xxx"),  
			checkSingleResult("module xxx;###")
		);

		
		// ------- Test name conflicts
		
		
		testLookup(parseModule_WithRef("module xxx; int xxx; ", "xxx"),
				
			checkSingleResult("int xxx;") 
		);
		
		testLookup(parseModule_WithRef("int xxx; import xxx; ", "xxx"),
				
			checkNameConflict("int xxx;", "module[xxx]") 
		);
		
		testLookup(parseModule_WithRef("int xxx; char xxx; import xxx; ", "xxx"),
				
			checkNameConflict("int xxx;", "char xxx;", "module[xxx]") 
		);
		
		// ------- Test namespace aggregation
		
		testLookup(parseModule_WithRef("import xxx.foo; import xxx.bar; import xxx.; ", "xxx"), 
				
			checkIsPackageNamespace(array("module[xxx.foo]", "module[xxx.bar]"))
		);
		
		testLookup(parseModule_WithRef(
			"import a.xxx.foo; import a.xxx.bar; import a.xxx.xpto.foo; import a.xxx.; "
			+ "import a.yyy.bar; import a.zzz;",
			"a"), 
			
			checkIsPackageNamespace(array(
				"PNamespace[a.xxx]", "PNamespace[a.yyy]","module[a.zzz]"
			))
		);
		// Aggregation on the second level
		testLookup(parseModule_WithRef(
			"import a.xxx.foo; import a.xxx.bar; import a.xxx.xpto.foo; import a.xxx.; "
			+ "import a.yyy.bar; import a.zzz;",
			"a.xxx"), 
			
			checkIsPackageNamespace(array(
				"module[a.xxx.foo]", "module[a.xxx.bar]", "PNamespace[a.xxx.xpto]" 
			))
		);
		// Test overload vs. a module import/ns
		testLookup(parseModule_WithRef("import xxx.foo; import xxx; ", "xxx"), 
			checkModuleProxy("module[xxx]")
		);
		testLookup(parseModule_WithRef("import xxx; import xxx.foo; ", "xxx"), 
			checkModuleProxy("module[xxx]")
		);
		
		
		// ------- Test duplicate import
		
		testLookup(parseModule_WithRef("module xxx; import xxx;", "xxx"), 
			
			checkModuleProxy("module[xxx]")
		);
		
		testLookup(parseModule_WithRef("import xxx; import xxx;", "xxx"),  
			
			checkModuleProxy("module[xxx]")
		);
		
		testLookup(
			parseModule_WithRef("import xxx.foo; import xxx.bar.z; import xxx.bar.z; import xxx.foo;", "xxx"),  
			
			checkIsPackageNamespace(array("module[xxx.foo]", "PNamespace[xxx.bar]"))
		);
		testLookup(
			parseModule_WithRef("import xxx.foo; import xxx.bar.z; import xxx.bar.z; import xxx.foo;", "xxx.bar"), 
			
			checkIsPackageNamespace(array("module[xxx.bar.z]"))
		);

		
		// ------- import vs. parent lexical scopes
		
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
	
	protected void doNamespaceLookupTest(ResolvedModule resolvedModule, String offsetMarker, 
			final String[] expectedResults) {
		testLookup(resolvedModule, offsetMarker, checkIsPackageNamespace(expectedResults));
	}
	
	protected Predicate<INamedElement> checkIsPackageNamespace(final String[] expectedResults) {
		return new Predicate<INamedElement>() {
			
			@Override
			public boolean evaluate(INamedElement matchedElement) {
				PackageNamespace packageNameSpace = assertInstance(matchedElement, PackageNamespace.class);
				assertEqualSet(
					hashSet(elementToStringArray(packageNameSpace.getNamespace().getElements())), 
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
	
	/* -----------------  ----------------- */
	
	@Test
	public void test_public_imports() throws Exception { test_public_imports$(); }
	public void test_public_imports$() throws Exception {
		
		// Check test sample file is correct for subsequent tests
		String FOO_PRIVATE_XXX = "foo_private__xxx";
		testLookup(parseModule_WithRef("import pack.foo_private; ", FOO_PRIVATE_XXX), 
			checkSingleResult("PackFooPrivate " + FOO_PRIVATE_XXX + ";"));
		
		/* -----------------  ----------------- */
		
		String PUBLIC_IMPORT = "import pack.public_import; import pack.zzz.non_existant";
		testLookup(parseModule_WithRef(PUBLIC_IMPORT, "xxx"), checkSingleResult("PackFoo xxx;"));
		testLookup(parseModule_WithRef(PUBLIC_IMPORT, FOO_PRIVATE_XXX), checkSingleResult(null));
		testLookup(parseModule_WithRef(PUBLIC_IMPORT, "pack"),  
			checkIsPackageNamespace(array(
				"module[pack.public_import]", "module[pack.foo]", 
				"PNamespace[pack.zzz]"
			))
		);
		testLookup(parseModule_WithRef(PUBLIC_IMPORT, "pack.public_import.xxx"), 
			checkSingleResult(null));
		testLookup(parseModule_WithRef(PUBLIC_IMPORT, "pack.public_import." + FOO_PRIVATE_XXX), 
			checkSingleResult(null));
		
		
		String PUBLIC_IMPORT2 = "import pack.public_import2; import pack.zzz.non_existant";
		testLookup(parseModule_WithRef(PUBLIC_IMPORT2, "xxx"), checkSingleResult("PackFoo xxx;"));
		testLookup(parseModule_WithRef(PUBLIC_IMPORT2, FOO_PRIVATE_XXX), checkSingleResult(null));
		testLookup(parseModule_WithRef(PUBLIC_IMPORT2, "pack"),  
			checkIsPackageNamespace(array(
				"module[pack.public_import2]", "module[pack.foo]", 
				"PNamespace[pack.zzz]"
			))
		);
		
		
		String PUBLIC_IMPORT_INDIRECT = "import pack.public_import_x; import pack.zzz.non_existant";
		testLookup(parseModule_WithRef(PUBLIC_IMPORT_INDIRECT, "xxx"), checkSingleResult("PackFoo xxx;"));
		testLookup(parseModule_WithRef(PUBLIC_IMPORT_INDIRECT, FOO_PRIVATE_XXX), checkSingleResult(null));
		testLookup(parseModule_WithRef(PUBLIC_IMPORT_INDIRECT, "pack"),  
			checkIsPackageNamespace(array(
				"module[pack.public_import_x]", 
				"module[pack.public_import]", "module[pack.foo]", 
				"PNamespace[pack.zzz]"
			))
		);
		
		// test visiting lexical module scope, after visiting imported scope.
		testLookup(parseModule_(
			" int xxx;"
			+ "class Xpto { " 
				+ SRC_IMPORT_SELF 
				+ " auto _ = xxx/*M*/; }"),
				
			checkSingleResult("int xxx;")
		);
		testLookup(parseModule_(
			" import foo;"
			+ "class Xpto { " 
				+ SRC_IMPORT_SELF 
				+ " auto _ = foo_member/*M*/; }"),
				
			checkSingleResult("int foo_member;")
		);
		
	}
	
}