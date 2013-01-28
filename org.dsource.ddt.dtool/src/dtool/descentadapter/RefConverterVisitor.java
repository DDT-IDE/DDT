package dtool.descentadapter;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.CoreUtil.upCast;
import descent.internal.compiler.parser.DotIdExp;
import descent.internal.compiler.parser.DotTemplateInstanceExp;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.FuncLiteralDeclaration;
import descent.internal.compiler.parser.TemplateInstanceWrapper;
import descent.internal.compiler.parser.TypeExp;
import descent.internal.compiler.parser.TypeInstance;
import dtool.ast.ASTNeoNode;
import dtool.ast.SourceRange;
import dtool.ast.definitions.IFunctionParameter;
import dtool.ast.references.RefReturn;
import dtool.ast.references.RefTypeSlice;
import dtool.ast.references.Reference;
import dtool.ast.references.TypeDelegate;
import dtool.ast.references.RefTypeDynArray;
import dtool.ast.references.TypeFunction;
import dtool.ast.references.TypeMapArray;
import dtool.ast.references.RefTypePointer;
import dtool.ast.references.TypeStaticArray;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

/**
 * This class is a mixin of sorts (using inheritance for code reuse).
 * Do not use it, instead use it's subclass: {@link DeclarationConverterVisitor}
 */
abstract class RefConverterVisitor extends CoreConverterVisitor {
	

	@Override
	public boolean visit(FuncLiteralDeclaration elem) {
		return visit(upCast(elem, FuncDeclaration.class));
	}
	
	@Override
	public boolean visit(TypeExp node) {
		return endAdapt(ReferenceConverter.createExpReference(node, convContext));
	}
	
	
	@Override
	public boolean visit(DotIdExp node) {
		return endAdapt(ReferenceConverter.createExpReference(node, convContext));
	}
		
	@Override
	public boolean visit(DotTemplateInstanceExp node) {
		return endAdapt(ReferenceConverter.createExpReference(node, convContext));
	}

	/* ---- References & co. --- */
	
	@Override
	public boolean visit(TemplateInstanceWrapper node) {
		throw assertFail(); // This should not occur as a top/direct/first node to convert
//		return endAdapt(new ExpReference(node, convContext));
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.IdentifierExp elem) {
		return endAdapt(ReferenceConverter.createExpReference(elem));
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TypeBasic elem) {
		return endAdapt(ReferenceConverter.convert(elem));
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TemplateInstance elem) {
		return endAdapt(ReferenceConverter.convertTemplateInstance(elem, convContext));
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TypeIdentifier elem) {
		return endAdapt(ReferenceConverter.convertTypeIdentifier(elem, convContext));
	}

	@Override
	public boolean visit(TypeInstance elem) {
		return endAdapt(ReferenceConverter.convertTypeInstance(elem, convContext));
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TypeTypeof elem) {
		return endAdapt(ReferenceConverter.convertTypeTypeOf(elem, convContext));
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TypeReturn elem) {
		return endAdapt(new RefReturn(DefinitionConverter.sourceRange(elem)));
	}
	
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TypeAArray elem) {
		return endAdapt(
			new TypeMapArray(
				ReferenceConverter.convertType(elem.index, convContext),
				ReferenceConverter.convertType(elem.next, convContext),
				DefinitionConverter.sourceRange(elem)
			)
		);
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TypeDArray elem) {
		return endAdapt(
			new RefTypeDynArray(
				ReferenceConverter.convertType(elem.next, convContext),
				DefinitionConverter.sourceRange(elem)
			)
		);
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TypeSArray elem) {
		return endAdapt(
			new TypeStaticArray(
				ReferenceConverter.convertType(elem.next, convContext),
				ExpressionConverter.convert(elem.dim, convContext),
				DefinitionConverter.sourceRange(elem)
			)
		);
	}
	
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TypeDelegate elem) {
		descent.internal.compiler.parser.TypeFunction typeFunction = ((descent.internal.compiler.parser.TypeFunction) elem.next);
		return endAdapt(
			new TypeDelegate(
				DescentASTConverter.convertElem(elem.rto, Reference.class, convContext),
				DescentASTConverter.convertMany(typeFunction.parameters, IFunctionParameter.class, convContext),
				DefinitionConverter.convertVarArgs(typeFunction.varargs),
				DefinitionConverter.sourceRange(elem)
			)
		);
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TypeFunction elem) {
		return endAdapt(
			convertFunction(elem, DefinitionConverter.sourceRange(elem), convContext)
		);
	}
	
	public static TypeFunction convertFunction(descent.internal.compiler.parser.TypeFunction elem,
			SourceRange sourceRange, ASTConversionContext convContext) {
		return new TypeFunction(
			DescentASTConverter.convertElem(elem.next, Reference.class, convContext), 
			DescentASTConverter.convertMany(elem.parameters, IFunctionParameter.class, convContext), 
			DefinitionConverter.convertVarArgs(elem.varargs), 
			elem.linkage,
			sourceRange
		);
	}
	
	public static ASTNeoNode convertTypePointer(descent.internal.compiler.parser.TypePointer elem, ASTConversionContext convContext) {
		if(elem.next instanceof descent.internal.compiler.parser.TypeFunction) {
			descent.internal.compiler.parser.TypeFunction tf = (descent.internal.compiler.parser.TypeFunction) elem.next; 
			return convertFunction(tf, DefinitionConverter.sourceRange(elem), convContext);
		} else {
			return new RefTypePointer(
				ReferenceConverter.convertType(elem.next, convContext),
				DefinitionConverter.sourceRange(elem)
			);
		}
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TypePointer elem) {
		return endAdapt(convertTypePointer(elem, convContext));
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TypeSlice elem) {
		return endAdapt(new RefTypeSlice(
				ReferenceConverter.convertType(elem.next, convContext),
				ExpressionConverter.convert(elem.lwr, convContext),
				ExpressionConverter.convert(elem.upr, convContext),
				DefinitionConverter.sourceRange(elem))
		);
	}
	
}