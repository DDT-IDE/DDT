package dtool.ast.definitions;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.TemplateValueParameter;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.Reference;
import dtool.ast.references.ReferenceConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.descentadapter.ExpressionConverter;
import dtool.refmodel.IScopeNode;

public class TemplateParamValue extends TemplateParameter {

	public Reference type;
	public Resolvable specvalue;
	public Resolvable defaultvalue;

	public TemplateParamValue(TemplateValueParameter elem, ASTConversionContext convContext) {
		super(elem.ident);
		convertNode(elem);
		this.type = ReferenceConverter.convertType(elem.valType, convContext);
		this.specvalue = ExpressionConverter.convert(elem.specValue, convContext);
		this.defaultvalue = ExpressionConverter.convert(elem.defaultValue, convContext);
	}

	@Override
	public EArcheType getArcheType() {
		return EArcheType.Variable;
	}

	@Override
	public IScopeNode getMembersScope() {
		return type.getTargetScope();
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, specvalue);
			TreeVisitor.acceptChildren(visitor, defaultvalue);
		}
		visitor.endVisit(this);	
	}
	
}
