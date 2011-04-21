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
import dtool.ast.expressions.ExpReference;
import dtool.ast.references.RefReturn;
import dtool.ast.references.RefTypeSlice;
import dtool.ast.references.ReferenceConverter;
import dtool.ast.references.TypeDelegate;
import dtool.ast.references.TypeDynArray;
import dtool.ast.references.TypeFunction;
import dtool.ast.references.TypeMapArray;
import dtool.ast.references.TypePointer;
import dtool.ast.references.TypeStaticArray;

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
		return endAdapt(new ExpReference(node, convContext));
	}
	
	
	@Override
	public boolean visit(DotIdExp node) {
		return endAdapt(new ExpReference(node, convContext));
	}
		
	@Override
	public boolean visit(DotTemplateInstanceExp node) {
		return endAdapt(new ExpReference(node, convContext));
	}

	/* ---- References & co. --- */
	
	@Override
	public boolean visit(TemplateInstanceWrapper node) {
		throw assertFail(); // This should not occur as a top/direct/first node to convert
//		return endAdapt(new ExpReference(node, convContext));
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.IdentifierExp elem) {
		return endAdapt(new ExpReference(elem));
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
		return endAdapt(new RefReturn(elem));
	}
	
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TypeAArray elem) {
		return endAdapt(new TypeMapArray(elem, convContext));
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TypeDArray elem) {
		return endAdapt(new TypeDynArray(elem, convContext));
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TypeSArray elem) {
		return endAdapt(new TypeStaticArray(elem, convContext));
	}
	
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TypeDelegate elem) {
		return endAdapt(new TypeDelegate(elem, convContext));
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TypeFunction elem) {
		return endAdapt(new TypeFunction(elem, convContext));
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TypePointer elem) {
		return endAdapt(TypePointer.convertTypePointer(elem, convContext));
	}
	
	@Override
	public boolean visit(descent.internal.compiler.parser.TypeSlice elem) {
		return endAdapt(new RefTypeSlice(elem, convContext));
	}


}