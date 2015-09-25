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
package dtool.engine.analysis;

import static dtool.engine.analysis.Resolvables_SemanticsTest.testResolveElement;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.concurrent.ExecutionException;

import melnorme.lang.tooling.engine.ErrorElement;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.tooling.engine.ErrorElement.NotFoundErrorElement;
import melnorme.lang.tooling.engine.resolver.ReferenceResult;
import melnorme.lang.tooling.symbols.IConcreteNamedElement;
import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.lang.tooling.symbols.PackageNamespace;
import java.util.function.Predicate;

import org.junit.Test;

import dtool.ast.definitions.Module;
import dtool.ast.references.NamedReference;
import dtool.ast.references.RefModule;
import dtool.engine.ResolvedModule;
import dtool.engine.util.NamedElementUtil;


public class Import_LookupTest extends CommonLookupTest {
	
	protected PickedElement<NamedReference> parseRef(String source, String marker) throws ExecutionException {
		return parseElement(source, marker, NamedReference.class);
	}
	
	protected void testRefModule(PickedElement<RefModule> refModuleElement, String fqn, boolean isNotFound) {
		ReferenceResult resolution = testResolveElement(refModuleElement);
		
		INamedElement result = resolution.result;
		assertTrue(result.getFullyQualifiedName().equals(fqn));
		
		// Test that it resolves to an alias of the actual module unit. 
		// This is an optimization to not parse the module until really necessary. 
		assertTrue(result instanceof ModuleProxy); 
		
		IConcreteNamedElement moduleTarget = result.resolveConcreteElement(refModuleElement.context);
		
		if(isNotFound) {
			assertTrue(moduleTarget instanceof ErrorElement);
			assertTrue(moduleTarget.getFullyQualifiedName().equals(NotFoundErrorElement.NOT_FOUND__Name));
		} else {
			assertTrue(moduleTarget instanceof Module);
			assertTrue(moduleTarget.getFullyQualifiedName().equals(fqn));
		}
	}
	
	protected void testPackageRef(PickedElement<? extends NamedReference> refToPackage, String fqn) {
		ReferenceResult resolution = testResolveElement(refToPackage);
		
		INamedElement result = resolution.result;
		assertTrue(result.getFullyQualifiedName().equals(fqn));
		
		if(fqn == NotFoundErrorElement.NOT_FOUND__Name) {
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
		
		testRefModule(parseElement("import target;", "target", RefModule.class), "target", false);
		testRefModule(parseElement("import pack.target;", "pack.target", RefModule.class), "pack.target", false);
		
		testRefModule(parseElement("import not_found;", "not_found", RefModule.class), "not_found", true);
		
		// Test package refs.
		testPackageRef(parseRef("import pack.target; auto x = pack;", "pack;"), "pack");
		testPackageRef(parseRef("import pack.subpack.target; auto x = pack;", "pack;"), "pack");
		
		testPackageRef(parseRef("import pack.subpack.target; auto x = pack.subpack/**/;", "subpack/**/"), 
			"pack.subpack");
		
	}
	
	/* -----------------  ----------------- */
	
	protected static final String SRC_IMPORT_SELF = "import " + DEFAULT_ModuleName + "; ";
	
	@Test
	public void test_ImportContent() throws Exception { test_ImportContent$(); }
	public void test_ImportContent$() throws Exception {
		
		// ---------- Basic test - module statement
		
		testLookup(parseModule_WithRef("module xxx;", "xxx"),  
			namedElementChecker("$xxx/")
		);
		testLookup(parseModule_WithRef("", DEFAULT_ModuleName),  
			namedElementChecker("$"+DEFAULT_ModuleName+"/")
		);
		
		
		testLookup(parseModule_WithRef("module xxx; int foo;", "xxx.foo"),  
			namedElementChecker("int foo;")
		);
		
		testLookup(parseModule_WithRef("module xxx; int foo;", "xxx.foo"),  
			namedElementChecker("int foo;")
		);
		testLookup(parseModule_WithRef("module pack.xxx; int foo;", "pack.xxx.foo"),  
			namedElementChecker("int foo;")
		);
		
		
		// ---------- Basic test 
		
		testLookup(parseModule_WithRef("import pack.foobar; ", "PackFoobar_member"),  
			namedElementChecker("int PackFoobar_member;")
		);
		
		testLookup(parseModule_WithRef("import pack.foobar; void PackFoobar_member;", "PackFoobar_member"),  
			namedElementChecker("void PackFoobar_member;")
		);
		
		// vs. package name
		testLookup(parseModule_WithRef("import pack.foobar;", "pack"),  
			namedElementChecker("PNamespace[pack]")
		);
		testLookup(parseModule_WithRef("import pack.foobar; int pack;", "pack"),  
			checkNameConflict("PNamespace[pack]", "int pack;")
		);
		
		// Special case: import self:
		testLookup(parseModule_WithRef(SRC_IMPORT_SELF, DEFAULT_ModuleName),  
			namedElementChecker("$"+DEFAULT_ModuleName+"/")
		);
		testLookup(parseModule_WithRef(SRC_IMPORT_SELF + "int xxx;", "xxx"),  
			namedElementChecker("int xxx;")
		);
		
		testLookup(parseModule_WithRef("module "+DEFAULT_ModuleName+";"+ SRC_IMPORT_SELF, DEFAULT_ModuleName),  
			namedElementChecker("$"+DEFAULT_ModuleName+"/")
		);
		testLookup(parseModule_WithRef("module "+DEFAULT_ModuleName+";"+ SRC_IMPORT_SELF + "int xxx;", "xxx"),  
			namedElementChecker("int xxx;")
		);
		
		testLookup(getUpdatedModule(DEFAULT_TestsBundle_Source.resolve_fromValid("pack/import_self.d")),  
			namedElementChecker("$pack.import_self/")
		);
		testLookup(getUpdatedModule(DEFAULT_TestsBundle_Source.resolve_fromValid("pack/import_self.d")), 
			"/*M2*/", namedElementChecker("int xpto;")
		);
		testLookup(getUpdatedModule(DEFAULT_TestsBundle_Source.resolve_fromValid("pack/import_self_indirect.d")),  
			namedElementChecker("$pack.import_self_indirect/")
		);
		testLookup(getUpdatedModule(DEFAULT_TestsBundle_Source.resolve_fromValid("pack/import_self_indirect.d")),  
			"/*M2*/", namedElementChecker("int xpto;")
		);
		
		testLookup(getUpdatedModule(DEFAULT_TestsBundle_Source.resolve_fromValid("pack/import_self.d")), 
			"/*M3*/", namedElementChecker("int xpto;")
		);
		
		
		// ------- Test namespace aggregation
		testLookup(parseModule_WithRef("import pack.foo; import pack.foobar; import pack.non_existant;", "pack"),  
			checkIsPackageNamespace(array(
				"module[pack.foo]", "module[pack.foobar]", "module[pack.non_existant]"
			))
		);
		// Test namespace aggregation - across scopes
		testLookup(parseModule_("import pack.foo; class Xpto { import pack.foobar; " + mref("pack") + "}"),  
			checkIsPackageNamespace(array(
				"module[pack.foo]", "module[pack.foobar]" 
			))
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
		
		// Test overload: packagevs. a module import/ns
		testLookup(parseModule_WithRef("import xxx.foo; import xxx; ", "xxx"), 
			checkModuleProxy("module[xxx]")
		);
		testLookup(parseModule_WithRef("import xxx; import xxx.foo; ", "xxx"), 
			checkModuleProxy("module[xxx]")
		);
		
		
		// ------- Test duplicate import
		testLookup(parseModule_WithRef("import xxx; import xxx;", "xxx"),  
			checkModuleProxy("module[xxx]")
		);
		
		testLookup(parseModule_WithRef("module xxx; import xxx;", "xxx"), 
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
		
		// ------- Test name conflicts
		
		testLookup(parseModule_WithRef("module xxx; int xxx; ", "xxx"),
			namedElementChecker("int xxx;") 
		);
		
		testLookup(parseModule_WithRef("int xxx; import xxx; ", "xxx"),
			checkNameConflict("int xxx;", "module[xxx]") 
		);
		
		testLookup(parseModule_WithRef("int xxx; char xxx; import xxx; ", "xxx"),
			checkNameConflict("int xxx;", "char xxx;", "module[xxx]") 
		);

		
		// ----------------------
		
		
		// Test contents of fully-qualified namespace
		testLookup(parseModule_WithRef("import pack.foobar; ", "pack.foobar.PackFoobar_member"),  
			namedElementChecker("int PackFoobar_member;")
		);
		testLookup(parseModule_WithRef("import pack.foobar; ", "pack.foobar.pack"),  
			namedElementChecker(expectNotFound("pack"))
		);
		


		// ------- import vs. parent lexical scopes
		
		String scopeOverload_vsImport = 
			"void xxx;" +
			"void func() {" +
			"	import xxx; auto _ = xxx/*M*/" +
			"}";	
		testLookup(parseModule_(scopeOverload_vsImport), 
			namedElementChecker(
			"module[xxx]"
		));
		

		test_public_imports$();
	}
	
	protected void doNamespaceLookupTest(ResolvedModule resolvedModule, String offsetMarker, 
			final String[] expectedResults) {
		testLookup(resolvedModule, offsetMarker, checkIsPackageNamespace(expectedResults));
	}
	
	protected Predicate<INamedElement> checkModuleProxy(final String expectedToString) {
		return new Predicate<INamedElement>() {
			@Override
			public boolean test(INamedElement matchedElement) {
				ModuleProxy moduleProxy = assertInstance(matchedElement, ModuleProxy.class);
				assertTrue(NamedElementUtil.namedElementToString(moduleProxy).equals(expectedToString));
				return true;
			}
		};
	}
	
	/* -----------------  ----------------- */
	
	public void test_public_imports$() throws Exception {
		
		// Check test sample file is correct for subsequent tests
		String FOO_PRIVATE_XXX = "foo_private__xxx";
		testLookup(parseModule_WithRef("import pack.foo_private; ", FOO_PRIVATE_XXX), 
			namedElementChecker("PackFooPrivate " + FOO_PRIVATE_XXX + ";"));
		
		/* -----------------  ----------------- */
		
		String PUBLIC_IMPORT = "import pack.public_import; import pack.zzz.non_existant";
		testLookup(parseModule_WithRef(PUBLIC_IMPORT, "xxx"), namedElementChecker("PackFoo xxx;"));
		testLookup(parseModule_WithRef(PUBLIC_IMPORT, FOO_PRIVATE_XXX), notfoundChecker(FOO_PRIVATE_XXX));
		testLookup(parseModule_WithRef(PUBLIC_IMPORT, "pack"),  
			checkIsPackageNamespace(array(
				"module[pack.public_import]", "module[pack.foo]", 
				"PNamespace[pack.zzz]"
			))
		);
		
		// Test as members scope 
		// -> note this behavior is not according to DMD, but is it according to spec? 
		// Should be, if not, ugly spec behavior
		testLookup(parseModule_WithRef(PUBLIC_IMPORT, "pack.public_import.xxx"), 
			notfoundChecker("xxx"));
		testLookup(parseModule_WithRef(PUBLIC_IMPORT, "pack.public_import." + FOO_PRIVATE_XXX), 
			notfoundChecker(FOO_PRIVATE_XXX));
		testLookup(parseModule_WithRef(PUBLIC_IMPORT, "pack.public_import.pack"), 
			notfoundChecker("pack"));
		
		testLookup(parseModule_WithRef("class Xpto { import pack.foo; }", "Xpto.pack"), 
			notfoundChecker("pack"));
		
		
		String PUBLIC_IMPORT2 = "import pack.public_import2; import pack.zzz.non_existant";
		testLookup(parseModule_WithRef(PUBLIC_IMPORT2, "xxx"), namedElementChecker("PackFoo xxx;"));
		testLookup(parseModule_WithRef(PUBLIC_IMPORT2, FOO_PRIVATE_XXX), notfoundChecker(FOO_PRIVATE_XXX));
		testLookup(parseModule_WithRef(PUBLIC_IMPORT2, "pack"),  
			checkIsPackageNamespace(array(
				"module[pack.public_import2]", "module[pack.foo]", 
				"PNamespace[pack.zzz]"
			))
		);
		
		
		String PUBLIC_IMPORT_INDIRECT = "import pack.public_import_x; import pack.zzz.non_existant";
		testLookup(parseModule_WithRef(PUBLIC_IMPORT_INDIRECT, "xxx"), namedElementChecker("PackFoo xxx;"));
		testLookup(parseModule_WithRef(PUBLIC_IMPORT_INDIRECT, FOO_PRIVATE_XXX), notfoundChecker(FOO_PRIVATE_XXX));
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
				
			namedElementChecker("int xxx;")
		);
		testLookup(parseModule_(
			" import foo;"
			+ "class Xpto { " 
				+ SRC_IMPORT_SELF 
				+ " auto _ = foo_member/*M*/; }"),
				
			namedElementChecker("int foo_member;")
		);
		
	}
	
	public static Predicate<INamedElement> checkIsPackageNamespace(final String[] expectedResults) {
		return new Predicate<INamedElement>() {
			
			@Override
			public boolean test(INamedElement matchedElement) {
				PackageNamespace packageNameSpace = assertInstance(matchedElement, PackageNamespace.class);
				assertEqualSet(
					hashSet(elementToStringArray(packageNameSpace.getNamespaceElements())), 
					hashSet(expectedResults)
				);
				
				return true;
			}
			
		};
	}
	
}