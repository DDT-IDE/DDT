package dtool.ast.definitions;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.PROT;
import descent.internal.compiler.parser.TypedefDeclaration;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.expressions.Initializer;
import dtool.ast.references.Reference;
import dtool.ast.references.ReferenceConverter;
import dtool.ast.statements.IStatement;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.refmodel.IScopeNode;

public class DefinitionTypedef extends Definition implements IStatement {

	Reference type;
	Initializer initializer;
	
	public DefinitionTypedef(TypedefDeclaration elem, ASTConversionContext convContext) {
		super(elem, convContext);
		this.type = ReferenceConverter.convertType(elem.sourceBasetype, convContext);
		this.initializer = Initializer.convert(elem.init, convContext);
	}
	
	public DefinitionTypedef(DefUnitDataTuple dudt, PROT prot, Reference type, Initializer initializer) {
		super(dudt, prot);
		this.type = type;
		if (this.type != null)
			this.type.setParent(this);
		this.initializer = initializer;
		if (this.initializer != null)
			this.initializer.setParent(this);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, initializer);
		}
		visitor.endVisit(this);
	}

	@Override
	public EArcheType getArcheType() {
		return EArcheType.Typedef;
	}
	
	@Override
	public IScopeNode getMembersScope() {
		return type.getTargetScope();
	}
	
	@Override
	public String toStringForCodeCompletion() {
		return getName() +" -> "+ type.toStringAsElement() 
		+" - "+ getModuleScope().toStringAsElement();
	}


}
