package dtool.ast.expressions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.parser.DeeTokens;

public class PrefixExpression extends Expression {
	
	public static enum PrefixOpType {
		ADDRESS(DeeTokens.AND),
		PRE_INCREMENT(DeeTokens.INCREMENT),
		PRE_DECREMENT(DeeTokens.DECREMENT),
		POINTER(DeeTokens.STAR),
		NEGATIVE(DeeTokens.MINUS),
		POSITIVE(DeeTokens.PLUS),
		NOT(DeeTokens.NOT),
		COMPLEMENT(DeeTokens.CONCAT),
		;
		
		public final DeeTokens token;
		
		private PrefixOpType(DeeTokens token) {
			this.token = token;
			assertTrue(token.getSourceValue() != null);
		}
		
		private static final PrefixOpType[] mapping = initMapping(PrefixOpType.values());
		
		private static PrefixOpType[] initMapping(PrefixOpType[] tokenEnum) {
			PrefixOpType[] mappingArray = new PrefixOpType[DeeTokens.values().length];
			for (PrefixOpType prefixOpType : tokenEnum) {
				mappingArray[prefixOpType.token.ordinal()] = prefixOpType;
			}
			return mappingArray;
		}
		
		public static PrefixOpType tokenToPrefixOpType(DeeTokens token) {
			return mapping[token.ordinal()];
		}
		
	}
	
	public final PrefixOpType kind;
	public final Resolvable exp;
	
	public PrefixExpression(PrefixOpType kind, Resolvable exp, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.exp = parentize(exp);
		this.kind = kind;
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(kind.token.getSourceValue(), " ");
		cp.append(exp);
	}
	
}