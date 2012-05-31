package dtool.ast.definitions;

import java.util.Collection;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.PROT;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.declarations.InvalidSyntaxDeclaration;
import dtool.ast.expressions.Initializer;
import dtool.ast.references.Reference;
import dtool.ast.references.ReferenceConverter;
import dtool.ast.statements.IStatement;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.refmodel.IDefUnitReference;
import dtool.refmodel.IScopeNode;

/**
 * A definition of a variable
 */
public class DefinitionVariable extends Definition implements IStatement {
	
	public static ASTNeoNode convert(descent.internal.compiler.parser.VarDeclaration elem, ASTConversionContext convContext) {
		if(elem.ident == null) {
			return new InvalidSyntaxDeclaration(elem, 
					ReferenceConverter.convertType(elem.type, convContext), Initializer.convert(elem.init, convContext));
		}  else {
			return new DefinitionVariable(elem, convContext);
		}
	}
	
	public final Reference type;
	public final Initializer init;

	public DefinitionVariable(descent.internal.compiler.parser.VarDeclaration elem, ASTConversionContext convContext) {
		super(elem, convContext);
		this.type = ReferenceConverter.convertType(elem.type, convContext);
		this.init = Initializer.convert(elem.init, convContext);
	}
	
	public DefinitionVariable(DefUnitDataTuple dudt, PROT prot, Reference type, Initializer init) {
		super(dudt, prot);
		this.type = type;
		this.init = init;
		
		if (this.type != null)
			this.type.setParent(this);
		if (this.init != null)
			this.init.setParent(this);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, init);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Variable;
	}
	
	public Initializer getInitializer() {
		return init;
	}
	
	public Reference getTypeReference() {
		return type;
	}
	
	private IDefUnitReference determineType() {
		if(type != null)
			return type;
		return NativeDefUnit.nullReference;
	}

	@Override
	public IScopeNode getMembersScope() {
		Collection<DefUnit> defunits = determineType().findTargetDefUnits(true);
		if(defunits == null || defunits.isEmpty())
			return null;
		return defunits.iterator().next().getMembersScope();
		//return defunit.getMembersScope();
	}
	
	private String getTypeString() {
		if(type != null)
			return type.toStringAsElement();
		return "auto";
	}
	
	@Override
	public String toStringForHoverSignature() {
		String str = getTypeString() + " " + getName();
		return str;
	}
	
	@Override
	public String toStringForCodeCompletion() {
		return defname.toStringAsElement() + "   " + getTypeString() + " - "
				+ getModuleScope().toStringAsElement();
	}

}
