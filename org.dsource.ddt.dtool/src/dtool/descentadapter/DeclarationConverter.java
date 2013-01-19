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
import descent.internal.compiler.parser.IsExp;
import descent.internal.compiler.parser.Statement;
import descent.internal.compiler.parser.StaticIfCondition;
import descent.internal.compiler.parser.VersionCondition;
import dtool.ast.ASTNeoNode;
import dtool.ast.NodeList;
import dtool.ast.NodeList2;
import dtool.ast.SourceRange;
import dtool.ast.declarations.DeclarationConditionalDV;
import dtool.ast.declarations.DeclarationStaticIf;
import dtool.ast.declarations.DeclarationStaticIfIsType;
import dtool.ast.definitions.Symbol;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.util.ArrayView;

public class DeclarationConverter extends BaseDmdConverter {
	
	public static ASTNeoNode convert(ConditionalDeclaration elem, ASTConversionContext convContext) {
		DeclarationConverter.doSetParent(elem, elem.decl);
		DeclarationConverter.doSetParent(elem, elem.elsedecl);
		NodeList thendecls = DeclarationConverter.createNodeList(elem.decl, convContext); 
		NodeList elsedecls = DeclarationConverter.createNodeList(elem.elsedecl, convContext);
		
		//assertTrue(!(thendecls == null && elsedecls == null));
		Condition condition = elem.condition;
		return createConditional(elem, thendecls, elsedecls, condition, convContext);
	}
	
	public static ASTNeoNode convert(ConditionalStatement elem, ASTConversionContext convContext) {
		NodeList thendecls = DeclarationConverter.createNodeList(elem.ifbody, convContext); 
		NodeList elsedecls = DeclarationConverter.createNodeList(elem.elsebody, convContext);

		//assertTrue(!(thendecls == null && elsedecls == null));
		Condition condition = elem.condition;
		return createConditional(elem, thendecls, elsedecls, condition, convContext);
	}
	
	public static ASTNeoNode createConditional(ASTDmdNode elem, NodeList thendecls, NodeList elsedecls, 
			Condition condition, ASTConversionContext convContext) 
	{
		if(condition instanceof DVCondition) {
			DVCondition dvCondition = (DVCondition) condition;
			Symbol ident = null;
			if(dvCondition.ident != null) {
				ident = new Symbol(new String(dvCondition.ident), DefinitionConverter.sourceRange(dvCondition));
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
	
	public static NodeList createNodeList(Statement body, ASTConversionContext convContext) {
		if(body == null)
			return null;
		if(body instanceof CompoundStatement) {
			CompoundStatement cst = (CompoundStatement) body;
			return new NodeList(DescentASTConverter.convertMany(cst.sourceStatements, convContext), true);
		} else {
			return new NodeList(DescentASTConverter.convertMany(Collections.singleton(body), convContext), false);
		}
	}
	
	public static NodeList2 createNodeList2(Statement body, ASTConversionContext convContext) {
		if(body == null)
			return null;
		SourceRange sr = null;
		if(body instanceof CompoundStatement) {
			CompoundStatement cst = (CompoundStatement) body;
			return new NodeList2(DescentASTConverter.convertMany(cst.sourceStatements, convContext), sr);
		} else {
			return new NodeList2(DescentASTConverter.convertMany(Collections.singleton(body), convContext), sr);
		}
	}
	
	public static NodeList createNodeList(Collection<Dsymbol> decl, ASTConversionContext convContext) {
		if(decl == null)
			return null;
		return new NodeList(DescentASTConverter.convertMany(decl, convContext), false);
	}
	
	public static NodeList2 createNodeList2(Collection<Dsymbol> decl, ASTConversionContext convContext) {
		if(decl == null)
			return null;
		ArrayView<ASTNeoNode> decls = DescentASTConverter.convertMany(decl, convContext);
		SourceRange sr = null;
		if(!decls.isEmpty()) {
			sr = sourceRangeStrict(decls.get(0).getStartPos(), decls.get(decls.size()-1).getEndPos());
		}
		return new NodeList2(decls, sr);
	}
	
}