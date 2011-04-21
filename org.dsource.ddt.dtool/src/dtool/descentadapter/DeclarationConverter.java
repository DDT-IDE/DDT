package dtool.descentadapter;

import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.Condition;
import descent.internal.compiler.parser.ConditionalDeclaration;
import descent.internal.compiler.parser.ConditionalStatement;
import descent.internal.compiler.parser.DVCondition;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.IsExp;
import descent.internal.compiler.parser.StaticIfCondition;
import dtool.ast.declarations.DeclarationConditional;
import dtool.ast.declarations.DeclarationConditionalDV;
import dtool.ast.declarations.DeclarationStaticIf;
import dtool.ast.declarations.DeclarationStaticIfIsType;
import dtool.ast.declarations.NodeList;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

public class DeclarationConverter extends BaseDmdConverter {
	
	public static DeclarationConditional convert(ConditionalDeclaration elem, ASTConversionContext convContext) {
		DeclarationConverter.doSetParent(elem, elem.decl);
		DeclarationConverter.doSetParent(elem, elem.elsedecl);
		NodeList thendecls = NodeList.createNodeList(elem.decl, convContext); 
		NodeList elsedecls = NodeList.createNodeList(elem.elsedecl, convContext);
		
		//assertTrue(!(thendecls == null && elsedecls == null));
		Condition condition = elem.condition;
		return createConditional(elem, thendecls, elsedecls, condition, convContext);
	}
	
	public static DeclarationConditional convert(ConditionalStatement elem, ASTConversionContext convContext) {
		NodeList thendecls = NodeList.createNodeList(elem.ifbody, convContext); 
		NodeList elsedecls = NodeList.createNodeList(elem.elsebody, convContext);

		//assertTrue(!(thendecls == null && elsedecls == null));
		Condition condition = elem.condition;
		return createConditional(elem, thendecls, elsedecls, condition, convContext);
	}
	
	public static DeclarationConditional createConditional(ASTDmdNode elem, NodeList thendecls, NodeList elsedecls, 
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
	
	protected static void doSetParent(ASTDmdNode parent, Dsymbols children) {
		if(children != null) {
			for (Dsymbol dsymbol : children) {
				dsymbol.setParent(parent);
			}
		}
	}
	
}
