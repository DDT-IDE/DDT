package dtool.descentadapter;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Collection;

import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.Condition;
import descent.internal.compiler.parser.ConditionalDeclaration;
import descent.internal.compiler.parser.ConditionalStatement;
import descent.internal.compiler.parser.DVCondition;
import descent.internal.compiler.parser.DebugCondition;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.IsExp;
import descent.internal.compiler.parser.StaticIfCondition;
import descent.internal.compiler.parser.VersionCondition;
import dtool.ast.SourceRange;
import dtool.ast.declarations.DeclarationConditional;
import dtool.ast.declarations.DeclarationConditionalDV;
import dtool.ast.declarations.DeclarationStaticIf;
import dtool.ast.declarations.DeclarationStaticIfIsType;
import dtool.ast.declarations.NodeList;
import dtool.ast.definitions.Symbol;
import dtool.ast.references.ReferenceConverter;
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
			DVCondition dvCondition = (DVCondition) condition;
			Symbol ident;
			if(dvCondition.ident != null) {
				ident = new Symbol(new String(dvCondition.ident));
				SourceRange identSourceRange = DefinitionConverter.sourceRange(dvCondition);
				if(identSourceRange != null) {
					ident.setSourceRange(identSourceRange);
				}
			} else {
				ident = null;
			}
			boolean isDebug = condition instanceof DebugCondition;
			assertTrue(isDebug || dvCondition instanceof VersionCondition);
			
			return new DeclarationConditionalDV(
				isDebug,
				ident,
				thendecls, elsedecls,
				DefinitionConverter.sourceRange(elem)
			);
		}
		StaticIfCondition stIfCondition = (StaticIfCondition) condition;
		if(stIfCondition.exp instanceof IsExp && ((IsExp) stIfCondition.exp).id != null) {
			IsExp isExp = ((IsExp) stIfCondition.exp);
			return new DeclarationStaticIfIsType(
				ReferenceConverter.convertType(isExp.targ, convContext),
				DefinitionConverter.convertIdToken(isExp.id).value, DefinitionConverter.sourceRange(isExp.id),
				isExp.tok,
				ReferenceConverter.convertType(isExp.tspec, convContext),
				thendecls, elsedecls,
				new SourceRange(isExp.getStartPos(), elem.getEndPos() - isExp.getStartPos()),
				DefinitionConverter.sourceRange(elem)
			);
		} else {
			return new DeclarationStaticIf(
				ExpressionConverter.convert(stIfCondition.exp, convContext),
				thendecls, elsedecls,
				DefinitionConverter.sourceRange(elem)
			);
		}
	}
	
	public static void doSetParent(ASTDmdNode parent, Collection<Dsymbol> children) {
		if(children != null) {
			for (Dsymbol dsymbol : children) {
				dsymbol.setParent(parent);
			}
		}
	}
	
}
