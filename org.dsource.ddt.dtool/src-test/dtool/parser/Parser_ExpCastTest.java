package dtool.parser;

import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

import dtool.ast.ASTNeoNode;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.Module;
import dtool.ast.expressions.ExpCast;
import dtool.ast.expressions.InfixExpression;
import dtool.ast.expressions.InitializerExp;

/**
 * Parser/AST import tests.
 * TODO test aliasing, more tests for selection imports. Test more invalid cases
 */
public class Parser_ExpCastTest extends Parser__CommonTest {
	
	@Test
	public void testImport() throws Exception { testImport$(); }
	public void testImport$() throws CoreException {
		Module module = testDtoolParse(
				"int foo = cast(Foo) blah;" +
				"int foo = cast(pack.Foo) (1+blah);"+
				"int foo = cast(Foo) 1+blah;"
		);
		
		ExpCast expCast0 = getExpCastFromInit(module.getChildren()[0]);
		assertEquals(expCast0.type.toStringAsElement(), "Foo");
		assertEquals(expCast0.exp.toStringAsElement(), "blah");
		
		ExpCast expCast1 = getExpCastFromInit(module.getChildren()[1]);
		assertEquals(expCast1.type.toStringAsElement(), "pack.Foo");
		assertCast(expCast1.exp, InfixExpression.class);
		
		DefinitionVariable child2 = downCast(module.getChildren()[2]);
		ExpCast expCast2 = downCast(downCast(downCast(child2.init, InitializerExp.class).exp, 
				InfixExpression.class).leftExp, ExpCast.class); 
			
		assertEquals(expCast2.type.toStringAsElement(), "Foo");
		assertEquals(expCast2.exp.toStringAsElement(), "1");
		
	}
	
	protected ExpCast getExpCastFromInit(ASTNeoNode defVariableNode) {
		DefinitionVariable defVariable = downCast(defVariableNode);
		return downCast(downCast(defVariable.init, InitializerExp.class).exp, ExpCast.class);
	}
	
	
	@Test
	public void testInvalid() throws Exception { testInvalid$(); }
	public void testInvalid$() throws Exception {
		testParseInvalidSyntax(
				"int foo = cast(Foo blah;");
	}
	
}
