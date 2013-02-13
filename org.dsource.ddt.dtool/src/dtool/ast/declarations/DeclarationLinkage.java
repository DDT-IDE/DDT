package dtool.ast.declarations;

import static melnorme.utilbox.core.CoreUtil.areEqual;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.IASTVisitor;
import dtool.ast.NodeList2;
import dtool.ast.SourceRange;
import dtool.ast.statements.IStatement;

public class DeclarationLinkage extends DeclarationAttrib implements IStatement {
	
	public enum Linkage {
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
	
	public DeclarationLinkage(Linkage linkage, AttribBodySyntax bodySyntax, NodeList2 decls, SourceRange sr) {
		super(bodySyntax, decls, sr);
		this.linkage = linkage;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("extern");
		if(linkage != null) {
			cp.append("(", linkage.name, ")");
		}
		cp.append(" ");
		toStringAsCode_body(cp);
	}
	
}