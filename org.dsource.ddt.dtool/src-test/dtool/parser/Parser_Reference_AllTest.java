package dtool.parser;

import org.junit.Test;

//Could use some range/index tests here
public final class Parser_Reference_AllTest extends Parser_Reference_CommonTest {
	
	// TODO: Combine samples in one
	
	protected static String[] sampleStaticRefs = array(
			"foo", 
			
			"pack.foo", "org.pack.foo",
			
			".foo", 
			".pack.foo", ".org.pack.foo",
			
			"foo!(int)", "foo!(int, T)",  "foo!(int, T, 3, \"str\")",
			"pack.foo!(int, T)", ".foo!(.blah, 3)", "org.pack.foo!(.pack.foo!(int, T))",
			"foo!(typeof(bar))", ".pack.foo!(typeof(.org.pack.foo))",
			
			"typeof(foo)", "typeof(123)", "typeof(\"123\")",
			"typeof(.pack)", "typeof(.pack.foo!(int, T))", "typeof(foo!(.pack.foo!(int, T)))"
	
	);
	
	protected static String[] sampleStaticRefs_nonCanonical = array(
			"foo ! foo.bar", "pack.foo!int"
	); 
	
	protected static String[] sampleNonStaticRef = array(
			"(blah).foo", "(1+blah).foo",
			"[1, 2].foo", "[].foo", "([1, 2]).foo", "func(blah).foo",
			
			"__LINE__.stringof", "__FILE__.foo"
	); 
	
	@Test
	public void test_ReferencesAll() throws Exception { test_ReferencesAll$(); }
	public void test_ReferencesAll$() throws Exception {
		for (String refString : sampleStaticRefs) {
			runCommonTest(refString);
		}
		for (String refString : sampleNonStaticRef) {
			checkToStringAsElement = false;
			runCommonTest(refString, RefFragmentDesc.EXP);
		}
	}
	
}
