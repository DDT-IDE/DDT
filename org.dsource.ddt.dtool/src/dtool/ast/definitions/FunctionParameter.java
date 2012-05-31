package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Collection;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeBasic;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.Reference;
import dtool.ast.references.ReferenceConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.descentadapter.ExpressionConverter;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.NodeUtil;

// TODO: Need to test this a lot more, especially with many other kinds of parameters
public class FunctionParameter extends DefUnit implements IFunctionParameter {
	
	public Reference type;
	public int storageClass;
	public Resolvable defaultValue;
	
	public FunctionParameter(descent.internal.compiler.parser.Argument elem, ASTConversionContext convContext) {
		super(elem.ident);
		setSourceRange(elem);
		assertNotNull(elem.type);
		
		if(elem.type instanceof TypeBasic && ((TypeBasic)elem.type).ty.name == null) {
			assertFail();
			this.type = null;
		} else {
			this.type = ReferenceConverter.convertType(elem.type, convContext);
		}
		assertNotNull(this.type);
		this.storageClass = elem.storageClass;
		this.defaultValue = ExpressionConverter.convert(elem.defaultArg, convContext);
	}
	
	public FunctionParameter(Type type, IdentifierExp id, ASTConversionContext convContext) {
		super(id);
		setSourceRange(type.getStartPos(), id.getEndPos() - type.getStartPos());
		
		this.type = ReferenceConverter.convertType(type, convContext);
	}
	
	public FunctionParameter(DefUnitDataTuple dudt, int storageClass, Reference type, Resolvable defaultValue) {
		super(dudt);
		this.storageClass = storageClass;
		
		this.type = type;
		if (this.type != null)
			this.type.setParent(this);
		
		this.defaultValue = defaultValue;
		if (this.defaultValue != null)
			this.defaultValue.setParent(this);
	}

	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Variable;
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
			//TreeVisitor.acceptChildren(visitor, inout);
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, defaultValue);
		}
		visitor.endVisit(this);	
	}

	@Override
	public IScopeNode getMembersScope() {
		Collection<DefUnit> defunits = type.findTargetDefUnits(true);
		if(defunits == null || defunits.isEmpty())
			return null;
		return defunits.iterator().next().getMembersScope();
		//return defunit.getMembersScope();
	}
	
	@Override
	public String toStringForHoverSignature() {
		return type.toStringAsElement() + " " + getName();
	}
	
	@Override
	public String toStringForCodeCompletion() {
		return getName() + "   " + type.toStringAsElement() + " - "
				+ NodeUtil.getOuterDefUnit(this).toStringAsElement();
	}

	@Override
	public String toStringAsFunctionSignaturePart() {
		return type.toStringAsElement() + " " + getName();
	}
	
	@Override
	public String toStringAsFunctionSimpleSignaturePart() {
		return type.toStringAsElement();
	}

	@Override
	public String toStringInitializer() {
		if(defaultValue == null)
			return null;
		return defaultValue.toStringAsElement();
	}

}
