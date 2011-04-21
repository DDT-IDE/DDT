package dtool.ast.declarations;

import java.util.Arrays;
import java.util.Iterator;

import melnorme.utilbox.misc.ArrayUtil;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.Condition;
import descent.internal.compiler.parser.ConditionalDeclaration;
import descent.internal.compiler.parser.ConditionalStatement;
import descent.internal.compiler.parser.DVCondition;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.IsExp;
import descent.internal.compiler.parser.StaticIfCondition;
import dtool.ast.ASTNeoNode;
import dtool.ast.statements.IStatement;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.refmodel.INonScopedBlock;

public abstract class DeclarationConditional extends ASTNeoNode implements IStatement, INonScopedBlock {
	
	public NodeList thendecls;
	public NodeList elsedecls;

	public static DeclarationConditional create(ConditionalDeclaration elem, ASTConversionContext convContext) {
		doSetParent(elem, elem.decl);
		doSetParent(elem, elem.elsedecl);
		NodeList thendecls = NodeList.createNodeList(elem.decl, convContext); 
		NodeList elsedecls = NodeList.createNodeList(elem.elsedecl, convContext);
		
		//assertTrue(!(thendecls == null && elsedecls == null));
		Condition condition = elem.condition;
		return createConditional(elem, thendecls, elsedecls, condition, convContext);
	}
	
	public static void doSetParent(ASTDmdNode parent, Dsymbols children) {
		if(children != null) {
			for (Dsymbol dsymbol : children) {
				dsymbol.setParent(parent);
			}
		}
	}
	
	public static DeclarationConditional create(ConditionalStatement elem, ASTConversionContext convContext) {
		NodeList thendecls = NodeList.createNodeList(elem.ifbody, convContext); 
		NodeList elsedecls = NodeList.createNodeList(elem.elsebody, convContext);

		//assertTrue(!(thendecls == null && elsedecls == null));
		Condition condition = elem.condition;
		return createConditional(elem, thendecls, elsedecls, condition, convContext);
	}

	private static DeclarationConditional createConditional(ASTDmdNode elem, NodeList thendecls, NodeList elsedecls, 
			Condition condition, ASTConversionContext convContext) 
	{
		if(condition instanceof DVCondition) {
			return new DeclarationConditionalDV(elem, (DVCondition) condition, thendecls, elsedecls);
		}
		StaticIfCondition stIfCondition = (StaticIfCondition) condition;
		if(stIfCondition.exp instanceof IsExp && ((IsExp) stIfCondition.exp).id != null) {
			return new DeclarationStaticIfIsType(elem, (IsExp) stIfCondition.exp, thendecls, elsedecls, convContext);
		} else { 
			return new DeclarationStaticIf(elem, stIfCondition, thendecls, elsedecls, convContext);
		}
	}


	protected ASTNeoNode[] getMembers() {
		if(thendecls == null && elsedecls == null)
			return ASTNeoNode.NO_ELEMENTS;
		if(thendecls == null)
			return elsedecls.nodes;
		if(elsedecls == null)
			return thendecls.nodes;
		
		return ArrayUtil.concat(thendecls.nodes, elsedecls.nodes);
	}
	
	@Override
	public Iterator<ASTNeoNode> getMembersIterator() {
		return Arrays.asList(getMembers()).iterator();
	}

	
}
