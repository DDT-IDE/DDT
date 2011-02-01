package dtool.ast.expressions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Collection;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.DotIdExp;
import descent.internal.compiler.parser.DotTemplateInstanceExp;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.ScopeExp;
import descent.internal.compiler.parser.TypeExp;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.references.Reference;
import dtool.ast.references.ReferenceConverter;
import dtool.descentadapter.DescentASTConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

/**
 * An Expression wrapping a Reference
 */
public class ExpReference extends Expression {
	
	public Reference ref;
	
	public ExpReference(Reference ref) {
		assertNotNull(ref);
		this.ref = ref;
	}
	
	public ExpReference(IdentifierExp elem) {
		this(ReferenceConverter.convertToRefIdentifier(elem));
		convertNode(elem);
	}
	
	public ExpReference(TypeExp elem, ASTConversionContext convContext) {
		this(ReferenceConverter.convertType(elem.type, convContext));
		convertNode(elem);
	}
	
	public ExpReference(DotIdExp elem, ASTConversionContext convContext) {
		this(ReferenceConverter.convertDotIdexp(elem, convContext));
		convertNode(elem);
	}
	
	public ExpReference(DotTemplateInstanceExp elem, ASTConversionContext convContext) {
		this(ReferenceConverter.convertDotTemplateIdExp(elem, convContext));
		convertNode(elem);
	}
	
	public ExpReference(ScopeExp elem, ASTConversionContext convContext) {
		this((Reference) DescentASTConverter.convertElem(elem.sds, convContext));
		convertNode(elem);
	}
	
	
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ref);
		}
		visitor.endVisit(this);	 
	}
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly) {
		return ref.findTargetDefUnits(findFirstOnly);
	}
	
	@Override
	public String toStringAsElement() {
		return ref.toStringAsElement();
	}
	
}
