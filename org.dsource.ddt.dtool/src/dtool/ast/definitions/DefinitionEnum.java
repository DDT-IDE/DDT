package dtool.ast.definitions;

import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.EnumDeclaration;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.references.Reference;
import dtool.ast.references.ReferenceConverter;
import dtool.ast.statements.IStatement;
import dtool.descentadapter.DescentASTConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

public class DefinitionEnum extends Definition implements IScopeNode, IStatement {

	public List<EnumMember> members;
	public Reference type;
	
	public DefinitionEnum(Dsymbol elem, ASTConversionContext convContext) {
		super(elem, convContext);
	}
	
	public static ASTNeoNode convertEnumDecl(EnumDeclaration elem, ASTConversionContext convContext) {
		if(elem.ident != null) {
			DefinitionEnum defEnum = new DefinitionEnum(elem, convContext);
			defEnum.members = DescentASTConverter.convertManyL(elem.members, defEnum.members, convContext) ;
			defEnum.type = ReferenceConverter.convertType(elem.memtype, convContext); 
			return defEnum;
		} else {
			EnumContainer enumContainer = new EnumContainer();
			enumContainer.setSourceRange(elem);
			enumContainer.members = DescentASTConverter.convertManyL(elem.members, enumContainer.members, convContext) ;
			enumContainer.type = ReferenceConverter.convertType(elem.memtype, convContext); 
			return enumContainer;
		}
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, members);
		}
		visitor.endVisit(this);

	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Enum;
	}

	@Override
	public IScopeNode getMembersScope() {
		return this;
	}
	
	@Override
	public List<IScope> getSuperScopes() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean hasSequentialLookup() {
		return true;
	}
	
	@Override
	public Iterator<EnumMember> getMembersIterator() {
		return members.iterator();
	}

	@Override
	public String toStringForCodeCompletion() {
		return getName() + " - " + getModuleScope().toStringAsElement();
	}

}
