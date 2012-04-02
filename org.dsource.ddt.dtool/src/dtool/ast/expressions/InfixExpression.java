package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;

public class InfixExpression extends Expression {
	
	// XXX: AST: link this with token ?
	public interface Type {
		
		int MUL = 11;
		int DIV = 12;
		int MOD = 13;
		int ADD = 14;
		int MIN = 15;
		int CAT = 16;
		int SHIFT_LEFT = 17;
		int SHIFT_RIGHT = 18;
		int UNSIGNED_SHIFT_RIGHT = 19;
		int CMP = 20;
		int IN = 21;
		int EQUAL = 22;
		int IDENTITY = 23;
		int AND = 24;
		int XOR = 25;
		int OR = 26;
		int AND_AND = 27;
		int OR_OR = 28;
		int ASSIGN = 30;
		int ADD_ASSIGN = 31;
		int MIN_ASSIGN = 32;
		int MUL_ASSIGN = 33;
		int DIV_ASSIGN = 34;
		int MOD_ASSIGN = 35;
		int AND_ASSIGN = 36;
		int OR_ASSIGN = 37;
		int XOR_ASSIGN = 38;
		int SHIFT_LEFT_ASSIGN = 39;
		int SHIFT_RIGHT_ASSIGN = 40;
		int UNSIGNED_SHIFT_RIGHT_ASSIGN = 41;
		int CAT_ASSIGN = 42;
		int COMMA = 44;
		int NOT_IDENTITY = 45;
		int POW_ASSIGN = 46;
		int POW = 47;
	}

	public final Resolvable leftExp;
	public final Resolvable rightExp;
	
	public final int kind;
	
	public InfixExpression(Resolvable left, int kind, Resolvable right, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.leftExp = left; parentize(this.leftExp);
		this.kind = kind; 
		this.rightExp = right; parentize(this.rightExp);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, leftExp);
			TreeVisitor.acceptChildren(visitor, rightExp);
		}
		visitor.endVisit(this);
	}

}
