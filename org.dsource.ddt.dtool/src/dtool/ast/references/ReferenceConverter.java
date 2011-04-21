package dtool.ast.references;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.blindCast;
import static melnorme.utilbox.core.CoreUtil.downCast;

import java.util.ArrayList;
import java.util.List;

import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.DotIdExp;
import descent.internal.compiler.parser.DotTemplateInstanceExp;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Identifiers;
import descent.internal.compiler.parser.TemplateInstance;
import descent.internal.compiler.parser.TemplateInstanceWrapper;
import descent.internal.compiler.parser.TemplateMixin;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeIdentifier;
import descent.internal.compiler.parser.TypeInstance;
import descent.internal.compiler.parser.TypeQualified;
import dtool.DToolBundle;
import dtool.ast.ASTNeoNode;
import dtool.ast.NeoSourceRange;
import dtool.ast.declarations.InvalidSyntaxDeclaration;
import dtool.ast.definitions.MixinContainer;
import dtool.ast.definitions.NamedMixin;
import dtool.ast.expressions.Expression;
import dtool.descentadapter.BaseDmdConverter;
import dtool.descentadapter.DescentASTConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.refmodel.IDefUnitReferenceNode;


/**
 * Helper class for converting references from the DMD AST
 */
public abstract class ReferenceConverter extends BaseDmdConverter {
	
	public static Reference convertType(Type type, ASTConversionContext convContext) {
		Reference entity = (Reference) DescentASTConverter.convertElem(type, convContext);
		return entity;
	}
	
	
	private static RefIdentifier convertToRefIdentifierValid(descent.internal.compiler.parser.IdentifierExp elem) {
		return convertToRefIdentifier(elem, sourceRangeValid(elem));
	}
	
	public static RefIdentifier convertToRefIdentifier(descent.internal.compiler.parser.IdentifierExp elem) {
		return convertToRefIdentifier(elem, elem.hasNoSourceRangeInfo() ? null : sourceRangeValid(elem));
	}
	
	private static RefIdentifier convertToRefIdentifier(descent.internal.compiler.parser.IdentifierExp elem,
			NeoSourceRange sourceRange) {
		assertTrue(elem.getClass() == IdentifierExp.class && elem.ident.length > 0);
		RefIdentifier refIdentifier = new RefIdentifier(new String(elem.ident));
		if (sourceRange != null) {
			refIdentifier.setSourceRange(sourceRange);
		}
		return refIdentifier;
	}
	
	@Deprecated 
	public static RefIdentifier convertToRefIdentifierXXX(descent.internal.compiler.parser.IdentifierExp elem) {
		RefIdentifier refIdentifier = convertToRefIdentifierOrNull(elem);
		assertNotNull(refIdentifier);
		return refIdentifier;
	}
	
	public static RefIdentifier convertToRefIdentifierOrNull(descent.internal.compiler.parser.IdentifierExp elem) {
		if (elem instanceof TemplateInstanceWrapper) {
			TemplateInstanceWrapper wrapper = (TemplateInstanceWrapper) elem;
			assertTrue(elem.ident == null);
			return convertToRefIdentifierValid(wrapper.tempinst.name);
		} else if(elem.ident.length == 0) {
			return null;
		} else {
			return convertToRefIdentifier(elem);
		}
	}

	
	public static RefIdentifier convert(descent.internal.compiler.parser.TypeBasic elem) {
		return new RefIdentifier(elem.ty.name, sourceRange(elem));
	}
	
	/* --- Conversion of qualified containers. --- */

	public static ASTNeoNode convertMixinInstance(TemplateMixin elem, ASTConversionContext convContext) {
		int startPosRef = elem.typeStart;
		int endPosRef = elem.typeStart + elem.typeLength;
		Identifiers idents = elem.idents;
		
		if(DToolBundle.BUGS_MODE) {
			idents.size();
		}
		if(idents == null) {
			assertTrue(convContext.hasSyntaxErrors());
			assertTrue(elem.ident == null);
			return new InvalidSyntaxDeclaration(elem);
		}
		NeoSourceRange sourceRange = sourceRangeValid(elem);
		
		Reference tplReference = convertFromIdents(startPosRef, null, null, true, idents, idents.size(), convContext);
		NeoSourceRange sourceRangeTplInst = sourceRangeValid(startPosRef, endPosRef);
		RefTemplateInstance refTplInstance = createRefTemplateInstance(tplReference, elem.tiargs, sourceRangeTplInst,
				convContext);
		if (elem.ident != null) {
			return new NamedMixin(elem, refTplInstance, convContext, sourceRange);
		} else {
			return new MixinContainer(refTplInstance, sourceRange);
		}
	}
	
	public static Reference convertTypeIdentifier(TypeIdentifier elem, ASTConversionContext convContext) {
		Reference rootent;
		if (elem.ident.ident.length == 0) {
			rootent = null;
		} else {
			rootent = convertToRefIdentifier(elem.ident);
		}
		Reference rootRef = rootent;
		return createReferenceFromIdents(elem, rootRef, convContext);
	}
	
	public static Reference convertTypeTypeOf(descent.internal.compiler.parser.TypeTypeof elem
			, ASTConversionContext convContext) {
		Reference rootRef = new TypeTypeof(Expression.convert(elem.exp, convContext), sourceRangeValid(elem));
		return createReferenceFromIdents(elem, rootRef, convContext);
	}
	
	public static Reference convertTypeInstance(TypeInstance elem, ASTConversionContext convContext) {
		Identifiers idents = elem.idents;
		assertNotNull(idents);
		
		int startPos = elem.getStartPos();
		
		TemplateInstance tempinst = elem.tempinst; // This contains leftmost instance
		
		RefIdentifier leftRefIdent = convertToRefIdentifierValid(tempinst.name);
		Integer endPos = null;
		if (idents.size() == 0) {
			endPos = elem.getEndPos();
		}
		RefTemplateInstance leftRefTplInst = createRefTemplateInstance(startPos, endPos, tempinst, leftRefIdent,
				convContext);
		
		Reference topRef = convertFromIdents(startPos, null, leftRefTplInst, false, idents, idents.size(), convContext);
		return topRef;
	}
	
	public static RefTemplateInstance convertTemplateInstance(TemplateInstance elem, ASTConversionContext convContext) {
		RefIdentifier refRawTemplate = convertToRefIdentifierValid(elem.name);
		NeoSourceRange sourceRange = sourceRangeValid(elem);
		return createRefTemplateInstance(refRawTemplate, elem.tiargs, sourceRange, convContext);
	}
	
	private static RefTemplateInstance createRefTemplateInstance(int startPos, final Integer endPos,
			TemplateInstance templInstance,
			Reference tplReference, ASTConversionContext convContext) {
		int endPosFixed;
		ArrayList<ASTDmdNode> tiargs = templInstance.tiargs;
		if (endPos == null) {
			// Let's estimate a source range
			if (tiargs.size() > 0) {
				ASTDmdNode lastArg = tiargs.get(tiargs.size() - 1);
				endPosFixed = lastArg.hasNoSourceRangeInfo() && !DToolBundle.BUGS_MODE ? -1 : lastArg.getEndPos();
				// we could add one to endPosFixed, but it's possible there are no parenthesis
			} else {
				endPosFixed = "!()".length();
			}
			
			if (!templInstance.hasNoSourceRangeInfo()) {
				assertTrue(templInstance.getEndPos() >= endPosFixed);
				endPosFixed = templInstance.getEndPos();
			}
			assertTrue(endPosFixed != -1);
		} else {
			endPosFixed = endPos;
		}
		NeoSourceRange sourceRange = new NeoSourceRange(startPos, endPosFixed);
		return createRefTemplateInstance(tplReference, tiargs, sourceRange, convContext);
	}
	
	public static RefTemplateInstance createRefTemplateInstance(Reference tplReference,
			List<ASTDmdNode> tiargs, NeoSourceRange sourceRange, ASTConversionContext convContext) {
		ASTNeoNode[] tiargsNew = DescentASTConverter.convertMany(tiargs, convContext);
		return new RefTemplateInstance(tplReference, tiargsNew, sourceRange);
	}
	
	
	/* --- Conversion of qualified stuff. --- */

	
	private static Reference createReferenceFromIdents(TypeQualified elem, Reference rootRef
			, ASTConversionContext convContext) {
		Identifiers idents = elem.idents;
		if(idents.size() == 0){
			return rootRef;
		}
		assertTrue(elem.hasNoSourceRangeInfo() == false);
		int endIx = idents.size();
		return convertFromIdents(elem.getStartPos(), elem.getEndPos(), rootRef, false, idents, endIx, convContext);
	}
	
	private static Reference convertFromIdents(int startPos, Integer endPos, Reference rootRef,
			boolean noRootRef, List<IdentifierExp> idents, int endIx, ASTConversionContext convContext) {
		assertTrue(endIx >= 0);
		
		if (endIx == 0) {
			assertTrue(noRootRef == false);
			return rootRef;
		}
		
		IdentifierExp lastIdentExp = idents.get(endIx - 1);
		
		if (lastIdentExp instanceof TemplateInstanceWrapper) {
			TemplateInstanceWrapper wrapper = (TemplateInstanceWrapper) lastIdentExp;
			assertTrue(wrapper.hasNoSourceRangeInfo());
			Reference tplReference = createReferenceFromIdentsNoTpl(startPos, rootRef, idents, endIx - 1, convContext,
					noRootRef);
			// nodes in tplReference will need to find out their endPos themselves
			return createRefTemplateInstance(startPos, endPos, wrapper.tempinst, tplReference, convContext);
		}
		
		return createReferenceFromIdentsNoTpl(startPos, rootRef, idents, endIx - 1, convContext, noRootRef);
	}
	
	private static Reference createReferenceFromIdentsNoTpl(int startPos, Reference rootRef,
			List<IdentifierExp> idents,
			int lastIx, ASTConversionContext convContext, boolean noRootRef) {
		IdentifierExp lastIdentExp = idents.get(lastIx);
		RefIdentifier singleRef = convertToRefIdentifierOrNull(lastIdentExp);
		if (lastIx == 0 && noRootRef) {
			return singleRef;
		}
		CommonRefQualified refQualified;
		Reference rootReference = null;
		if (lastIx == 0 && noRootRef == false && rootRef == null) {
		} else {
			rootReference = convertFromIdents(startPos, null, rootRef, noRootRef, idents, lastIx, convContext);
		}
		if (rootReference == null) {
			refQualified = new RefModuleQualified(singleRef, null);
		} else {
			refQualified = new RefQualified(rootReference, singleRef);
			assertTrue(rootReference.hasNoSourceRangeInfo() == false);
			assertTrue(rootReference.getStartPos() == startPos);
		}
		
		refQualified.setStart(startPos);
		refQualified.setEndPos(singleRef.getEndPos());
		return refQualified;
	}
	
	
	
	/* --- Conversion from Expressions . --- */
	
	public static Reference convertDotIdexp(DotIdExp elem, ASTConversionContext convContext) {
		descent.internal.compiler.parser.Expression rootExpression = elem.e1;
		IdentifierExp subIdentifierExp = elem.ident;
		
		if(elem.hasNoSourceRangeInfo()) {
			assertTrue(convContext.module.hasSyntaxErrors());
			assertTrue(subIdentifierExp.hasNoSourceRangeInfo() == false);
		}
		
		if(subIdentifierExp.hasNoSourceRangeInfo()) {
			assertTrue(convContext.module.hasSyntaxErrors());
			assertTrue(rootExpression.hasNoSourceRangeInfo() == false);
			// Ignore subExp
			IDefUnitReferenceNode rootRef = Expression.convert2(rootExpression, convContext);
			return (Reference) rootRef;
		} 
		
		if(DToolBundle.UNSUPPORTED_DMD_CONTRACTS) {
			assertTrue(elem.hasNoSourceRangeInfo() || (elem.getEndPos() == subIdentifierExp.getEndPos()));
			// convertToRawReference will correct the range if created reference.
		}
		
		NeoSourceRange topSourceRange = elem.hasNoSourceRangeInfo() ? null : sourceRangeValid(elem); 
		return convertToRawReference(topSourceRange, convContext, rootExpression, subIdentifierExp);
	}
	
	public static Reference convertDotTemplateIdExp(DotTemplateInstanceExp elem, ASTConversionContext convContext) {
		Reference rawTplRef = convertToRawReference(elem, convContext, elem.e1, elem.ti.name);
		
		NeoSourceRange tplInstSourceRange = sourceRangeValid(rawTplRef.getStartPos(), elem.ti.getEndPos());
		if(!elem.hasNoSourceRangeInfo()) {
			if(DToolBundle.UNSUPPORTED_DMD_CONTRACTS)
				assertTrue(elem.getStartPos() == tplInstSourceRange.getStartPos());
			assertTrue(elem.getEndPos() == tplInstSourceRange.getEndPos());
		}
		
		RefTemplateInstance refTplInstance = createRefTemplateInstance(rawTplRef, elem.ti.tiargs, tplInstSourceRange, convContext); 
		return refTplInstance;
	}

	private static Reference convertToRawReference(ASTDmdNode elem, ASTConversionContext convContext,
			descent.internal.compiler.parser.Expression rootIdentifierExp, IdentifierExp subIdentifierExp) {
		NeoSourceRange topSourceRange = elem.hasNoSourceRangeInfo() ? null : sourceRangeValid(elem); 
		return convertToRawReference(topSourceRange, convContext, rootIdentifierExp, subIdentifierExp);
	}
		
	private static Reference convertToRawReference(NeoSourceRange topSourceRange, ASTConversionContext convContext,
			descent.internal.compiler.parser.Expression rootIdentifierExp, IdentifierExp subIdentifierExp) {
		RefIdentifier subName = convertToRefIdentifierValid(subIdentifierExp);
		assertTrue(subName.getEndPos() == subIdentifierExp.getEndPos());
		
		int newEndPos = subIdentifierExp.getEndPos();
		
		if(rootIdentifierExp instanceof IdentifierExp && downCast(rootIdentifierExp, IdentifierExp.class).length == 0) {
			assertTrue((topSourceRange == null && rootIdentifierExp.hasNoSourceRangeInfo()) == false);
			RefModuleQualified refModuleQual = new RefModuleQualified(subName, null);
			
			int newStartPos = (topSourceRange == null) ? rootIdentifierExp.getStartPos() : topSourceRange.getStartPos();
			refModuleQual.setSourceRange(sourceRangeValid(newStartPos, newEndPos));
			return refModuleQual;
		} else {
			IDefUnitReferenceNode rootRef = Expression.convert2(rootIdentifierExp, convContext);
			
			if(rootRef.hasNoSourceRangeInfo() && !DToolBundle.BUGS_MODE){
				assertTrue(topSourceRange != null);
				// Estimate a range
				ASTNeoNode rootRefAsNode = blindCast(rootRef);
				rootRefAsNode.setStart(topSourceRange.getStartPos());
				rootRefAsNode.setEndPos(subName.getStartPos()-1);
			}
			
			if(topSourceRange != null) {
				assertTrue(topSourceRange.getStartPos() <= rootRef.getStartPos());
				if(DToolBundle.BUGS_MODE) 
					assertTrue(topSourceRange.getStartPos() == rootRef.getStartPos());
			}
			
			RefQualified refQualified = new RefQualified(rootRef, subName, topSourceRange);
			int newStartPos = refQualified.getQualifier().getStartPos();
			// Fix some DMD missing ranges 
			refQualified.setSourceRange(sourceRangeValid(newStartPos, newEndPos));
			return refQualified;
		}
	}
	
}
