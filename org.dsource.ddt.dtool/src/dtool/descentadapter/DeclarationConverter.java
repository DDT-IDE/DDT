package dtool.descentadapter;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Collection;
import java.util.Collections;

import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.CompoundStatement;
import descent.internal.compiler.parser.Condition;
import descent.internal.compiler.parser.ConditionalDeclaration;
import descent.internal.compiler.parser.ConditionalStatement;
import descent.internal.compiler.parser.DVCondition;
import descent.internal.compiler.parser.DebugCondition;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.Statement;
import descent.internal.compiler.parser.StaticIfCondition;
import descent.internal.compiler.parser.VersionCondition;
import dtool.ast.ASTNode;
import dtool.ast.NodeList_OLD;
import dtool.ast.SourceRange;
import dtool.ast.declarations.AbstractConditionalDeclaration;
import dtool.ast.declarations.AbstractConditionalDeclaration.VersionSymbol;
import dtool.ast.declarations.DeclarationAttrib.AttribBodySyntax;
import dtool.ast.declarations.DeclarationDebugVersion;
import dtool.ast.declarations.DeclarationStaticIf;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.util.ArrayView;

public class DeclarationConverter extends BaseDmdConverter {
	
	public static ASTNode convert(ConditionalDeclaration elem, ASTConversionContext convContext) {
		DeclarationConverter.doSetParent(elem, elem.decl);
		DeclarationConverter.doSetParent(elem, elem.elsedecl);
		NodeList_OLD thendecls = DeclarationConverter.createNodeList2(elem.decl, convContext); 
		NodeList_OLD elsedecls = DeclarationConverter.createNodeList2(elem.elsedecl, convContext);
		
		//assertTrue(!(thendecls == null && elsedecls == null));
		Condition condition = elem.condition;
		return createConditional(elem, thendecls, elsedecls, condition, convContext);
	}
	
	public static ASTNode convert(ConditionalStatement elem, ASTConversionContext convContext) {
		NodeList_OLD thendecls = DeclarationConverter.createNodeList2(elem.ifbody, convContext); 
		NodeList_OLD elsedecls = DeclarationConverter.createNodeList2(elem.elsebody, convContext);
		
		//assertTrue(!(thendecls == null && elsedecls == null));
		Condition condition = elem.condition;
		return createConditional(elem, thendecls, elsedecls, condition, convContext);
	}
	
	public static ASTNode createConditional(ASTDmdNode elem, NodeList_OLD thendecls, NodeList_OLD elsedecls, 
			Condition condition, ASTConversionContext convContext) 
	{
		if(condition instanceof DVCondition) {
			DVCondition dvCondition = (DVCondition) condition;
			VersionSymbol ident = null;
			if(dvCondition.ident != null) {
				ident = connect(DefinitionConverter.sourceRange(dvCondition), 
					new AbstractConditionalDeclaration.VersionSymbol(new String(dvCondition.ident)));
			}
			boolean isDebug = condition instanceof DebugCondition;
			assertTrue(isDebug || dvCondition instanceof VersionCondition);
			
			return connect(DefinitionConverter.sourceRange(elem), new DeclarationDebugVersion(
				isDebug,
				ident,
				AttribBodySyntax.BRACE_BLOCK,
				thendecls, 
				elsedecls
			));
		}
		StaticIfCondition stIfCondition = (StaticIfCondition) condition;
		// Disable DeclarationStaticIfIsType creation
//		if(false && stIfCondition.exp instanceof IsExp && ((IsExp) stIfCondition.exp).id != null) {
//			IsExp isExp = ((IsExp) stIfCondition.exp);
//			return connect(DefinitionConverter.sourceRange(elem),
//				new DeclarationStaticIfIsType(
//				ReferenceConverter.convertType(isExp.targ, convContext),
//				DefinitionConverter.convertIdToken(isExp.id).value, DefinitionConverter.sourceRange(isExp.id),
//				isExp.tok,
//				ReferenceConverter.convertType(isExp.tspec, convContext),
//				thendecls, elsedecls,
//				new SourceRange(isExp.getStartPos(), elem.getEndPos() - isExp.getStartPos())
//			));
//		} else {
			return connect(DefinitionConverter.sourceRange(elem),
				new DeclarationStaticIf(
				ExpressionConverter.convert(stIfCondition.exp, convContext),
				AttribBodySyntax.BRACE_BLOCK,
				thendecls, 
				elsedecls
			));
//		}
	}
	
	public static void doSetParent(ASTDmdNode parent, Collection<Dsymbol> children) {
		if(children != null) {
			for (Dsymbol dsymbol : children) {
				dsymbol.setParent(parent);
			}
		}
	}
	
	public static NodeList_OLD createNodeList2(Statement body, ASTConversionContext convContext) {
		if(body == null)
			return null;
		SourceRange sr = null;
		if(body instanceof CompoundStatement) {
			CompoundStatement cst = (CompoundStatement) body;
			return connect(sr, new NodeList_OLD(DescentASTConverter.convertMany(cst.sourceStatements, convContext)));
		} else {
			return connect(sr, new NodeList_OLD(DescentASTConverter.convertMany(Collections.singleton(body), convContext)));
		}
	}
	
	public static NodeList_OLD createNodeList2(Collection<Dsymbol> decl, ASTConversionContext convContext) {
		if(decl == null)
			return null;
		ArrayView<ASTNode> decls = DescentASTConverter.convertMany(decl, convContext);
		SourceRange sr = null;
		if(!decls.isEmpty()) {
			sr = sourceRangeStrict(decls.get(0).getStartPos(), decls.get(decls.size()-1).getEndPos());
		}
		return connect(sr, new NodeList_OLD(decls));
	}
	
}