package dtool.ast.definitions;

import java.util.Iterator;
import java.util.List;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.EnumDeclaration;
import descent.internal.compiler.parser.PROT;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.references.Reference;
import dtool.ast.references.ReferenceConverter;
import dtool.ast.statements.IStatement;
import dtool.descentadapter.DefinitionConverter;
import dtool.descentadapter.DescentASTConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

public class DefinitionEnum extends Definition implements IScopeNode, IStatement {

	public ArrayView<EnumMember> members;
	public Reference type;
	
	public DefinitionEnum(DefUnitDataTuple defunitInfo, PROT prot, EnumMember[] members, Reference reference,
			SourceRange sourceRange) {
		super(defunitInfo, prot);

		if (members != null) {
			this.members = new ArrayView<EnumMember>(members);
			for (EnumMember em : this.members) {
				em.setParent(this);
			}
		}
		
		this.type = reference;
		if (this.type != null)
			type.setParent(this);
		
		initSourceRange(sourceRange);
	}
	
	public static ASTNeoNode convertEnumDecl(EnumDeclaration elem, ASTConversionContext convContext) {
		if(elem.ident != null) {
			DefinitionEnum defEnum = new DefinitionEnum(
					DefinitionConverter.convertDsymbol(elem, convContext), elem.prot(),
					DescentASTConverter.convertManyToView(elem.members, EnumMember.class, convContext).getInternalArray(),
					ReferenceConverter.convertType(elem.memtype, convContext),
					DefinitionConverter.sourceRange(elem));
			return defEnum;
		} else {
			EnumContainer enumContainer = new EnumContainer(
					DescentASTConverter.convertManyToView(elem.members, EnumMember.class, convContext),
					ReferenceConverter.convertType(elem.memtype, convContext),
					DefinitionConverter.sourceRange(elem)
					);
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
