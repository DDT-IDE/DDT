package dtool.ast.definitions;

import static dtool.util.NewUtils.assertNotNull_;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Collection;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.DefUnitDescriptor;
import dtool.ast.IASTVisitor;
import dtool.ast.declarations.IDeclaration;
import dtool.ast.expressions.IInitializer;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;
import dtool.refmodel.IDefUnitReference;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;
import dtool.util.ArrayView;

/**
 * A variable definition. 
 * Optionally has multiple variables defined with the multi-identifier syntax.
 * TODO fragments semantic visibility
 */
public class DefinitionVariable extends Definition implements IDeclaration, IStatement { 
	
	public static final ArrayView<DefVarFragment> NO_FRAGMENTS = ArrayView.create(new DefVarFragment[0]);
	
	public final Reference type;
	public final Reference cstyleSuffix;
	public final IInitializer init;
	public final ArrayView<DefVarFragment> fragments;
	
	public DefinitionVariable(ProtoDefSymbol defId, Reference type, Reference cstyleSuffix, IInitializer init,
		ArrayView<DefVarFragment> fragments) {
		this(defId, assertNotNull_(type), cstyleSuffix, init, fragments, false);
	}
	
	protected DefinitionVariable(ProtoDefSymbol defId, Reference type, Reference cstyleSuffix, IInitializer init,
		ArrayView<DefVarFragment> fragments, @SuppressWarnings("unused") boolean dummy)
	{
		super(defId);
		this.type = parentize(type);
		this.cstyleSuffix = parentize(cstyleSuffix);
		this.init = parentize(init);
		this.fragments = parentize(fragments);
		assertTrue(fragments == null || fragments.size() > 0);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DEFINITION_VARIABLE;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, cstyleSuffix);
			TreeVisitor.acceptChildren(visitor, init);
			
			TreeVisitor.acceptChildren(visitor, fragments);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(type, " ");
		cp.append(defname);
		cp.append(cstyleSuffix);
		cp.append(" = ", init);
		cp.appendList(", ", fragments, ", ", "");
		cp.append(";");
	}
	
	// TODO refactor this into own class?
	public static class DefinitionAutoVariable extends DefinitionVariable {
		
		public DefinitionAutoVariable(ProtoDefSymbol defId, IInitializer init, 
			ArrayView<DefVarFragment> fragments) {
			super(defId, null, null, init, fragments, false);
		}
		
		@Override
		public ASTNodeTypes getNodeType() {
			return ASTNodeTypes.DEFINITION_AUTO_VARIABLE;
		}
		
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Variable;
	}
	
	public IDefUnitReference getTypeReference() {
		return determineType();
	}
	
	private IDefUnitReference determineType() {
		if(type != null)
			return type;
		return NativeDefUnit.nullReference; // TODO: auto references
	}
	
	@Override
	public IScopeNode getMembersScope(IModuleResolver moduleResolver) {
		Collection<DefUnit> defunits = determineType().findTargetDefUnits(moduleResolver, true);
		if(defunits == null || defunits.isEmpty())
			return null;
		return defunits.iterator().next().getMembersScope(moduleResolver);
		//return defunit.getMembersScope();
	}
	
	@Deprecated
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
		return defname.toStringAsCode() + "   " + getTypeString() + " - " + getModuleScope().toStringAsElement();
	}
	
	
	public static class CStyleRootRef extends Reference {
		
		public CStyleRootRef() { }
		
		@Override
		public ASTNodeTypes getNodeType() {
			return ASTNodeTypes.CSTYLE_ROOT_REF;
		}
		
		@Override
		public void toStringAsCode(ASTCodePrinter cp) {
		}
		
		@Override
		public boolean canMatch(DefUnitDescriptor defunit) {
			return false;
		}

		@Override
		public Collection<DefUnit> findTargetDefUnits(IModuleResolver moduleResolver, boolean findFirstOnly) {
			return null;
		}
		
		@Override
		public void accept0(IASTVisitor visitor) {
			visitor.visit(this);
			visitor.endVisit(this);
		}
		
	}
}