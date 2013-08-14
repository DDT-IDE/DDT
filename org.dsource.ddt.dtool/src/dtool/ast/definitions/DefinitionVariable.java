package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.declarations.IDeclaration;
import dtool.ast.expressions.IInitializer;
import dtool.ast.references.Reference;
import dtool.ast.statements.IStatement;
import dtool.parser.Token;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.IDefUnitReference;
import dtool.util.ArrayView;

/**
 * A variable definition. 
 * Optionally has multiple variables defined with the multi-identifier syntax.
 * TODO fragments semantic visibility
 */
public class DefinitionVariable extends CommonDefinition implements IDeclaration, IStatement { 
	
	public static final ArrayView<DefVarFragment> NO_FRAGMENTS = ArrayView.create(new DefVarFragment[0]);
	
	public final Reference type; // Can be null
	public final Reference cstyleSuffix;
	public final IInitializer init;
	protected final ArrayView<DefVarFragment> fragments;
	
	public DefinitionVariable(Token[] comments, ProtoDefSymbol defId, Reference type, Reference cstyleSuffix,
		IInitializer init, ArrayView<DefVarFragment> fragments)
	{
		super(comments, defId);
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
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, type);
		acceptVisitor(visitor, defname);
		acceptVisitor(visitor, cstyleSuffix);
		acceptVisitor(visitor, init);
		
		acceptVisitor(visitor, fragments);
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
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Variable;
	}
	
	public IDefUnitReference determineType() {
		if(type != null)
			return type;
		return NativeDefUnit.nullReference; 
	}
	
	public ArrayView<DefVarFragment> getFragments() {
		return fragments == null ? NO_FRAGMENTS : fragments;
	}
	
	@Override
	public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
		if(type == null)
			return; // TODO: auto references
		Reference.resolveSearchInReferedMembersScope(search, type);
	}
	
	public static class DefinitionAutoVariable extends DefinitionVariable {
		
		public DefinitionAutoVariable(Token[] comments, ProtoDefSymbol defId, IInitializer init,
			ArrayView<DefVarFragment> fragments) {
			super(comments, defId, null, null, init, fragments);
		}
		
		@Override
		public ASTNodeTypes getNodeType() {
			return ASTNodeTypes.DEFINITION_AUTO_VARIABLE;
		}
		
	}
	
}