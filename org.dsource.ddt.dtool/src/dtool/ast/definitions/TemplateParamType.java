package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.TemplateThisParameter;
import descent.internal.compiler.parser.TemplateTypeParameter;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.references.Reference;
import dtool.ast.references.ReferenceConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.refmodel.IScopeNode;

public class TemplateParamType extends TemplateParameter {

	public Reference specType;
	public Reference defaultType;

	public TemplateParamType(TemplateTypeParameter elem, ASTConversionContext convContext) {
		super(elem.ident);
		convertNode(elem);
		this.specType = ReferenceConverter.convertType(elem.specType, convContext);
		this.defaultType = ReferenceConverter.convertType(elem.defaultType, convContext);
	}
	
	public TemplateParamType(TemplateThisParameter elem, ASTConversionContext convContext) {
		this((TemplateTypeParameter) elem, convContext); // TODO: TODO
	}
	

	@Override
	public EArcheType getArcheType() {
		return EArcheType.TypeParameter;
	}
	
	/*
	 * Can be null
	 */
	@Override
	public IScopeNode getMembersScope() {
		if(specType == null)
			return null;
		return specType.getTargetScope();
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, specType);
			TreeVisitor.acceptChildren(visitor, defaultType);
		}
		visitor.endVisit(this);
	}


}
