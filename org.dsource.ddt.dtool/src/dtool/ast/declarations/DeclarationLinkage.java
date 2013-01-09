package dtool.ast.declarations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertUnreachable;
import static melnorme.utilbox.core.CoreUtil.areEqual;
import descent.internal.compiler.parser.LINK;
import dtool.ast.ASTCodePrinter;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.NodeList;
import dtool.ast.SourceRange;
import dtool.ast.statements.IStatement;

public class DeclarationLinkage extends DeclarationAttrib implements IStatement {
	
	public enum Linkage {
	    DEFAULT(null),
	    D("D"),
	    C("C"),
	    CPP("C++"),
	    WINDOWS("Windows"),
	    PASCAL("Pascal"),
	    SYSTEM("System");
	    
	    protected String name;
	    
	    private Linkage(String name) {
			this.name = name;
	    }
	    
	    public static Linkage fromLINK(LINK linkage) {
	    	switch (linkage) {
	    	case LINKdefault: return DEFAULT;
	    	case LINKd: return D;
	    	case LINKc: return C;
	    	case LINKcpp: return CPP;
	    	case LINKwindows: return WINDOWS;
	    	case LINKpascal: return PASCAL;
	    	case LINKsystem: return SYSTEM;
			}
	    	throw assertUnreachable();
	    }
	    
	    public static Linkage fromString(String str) {
	    	Linkage[] values = Linkage.values();
	    	for (Linkage linkage : values) {
				if(areEqual(linkage.name, str)) {
					return linkage;
				}
			}
	    	return null;
	    }
	    
	}
	
	public final Linkage linkage;
	
	public DeclarationLinkage(Linkage linkage, NodeList decls, SourceRange sourceRange) {
		super(decls, sourceRange);
		this.linkage = linkage;
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptBodyChildren(visitor);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("extern ");
		if(linkage != Linkage.DEFAULT) {
			cp.append("(", linkage.name, ")");
		}
		// TODO:
	}
	
}