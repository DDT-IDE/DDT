package dtool.ast.declarations;

import static melnorme.utilbox.core.CoreUtil.areEqual;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNeoNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.statements.IStatement;

public class DeclarationLinkage extends DeclarationAttrib implements IStatement {
	
	public enum Linkage {
	    D("D"),
	    C("C"),
	    CPP("C++"),
	    WINDOWS("Windows"),
	    PASCAL("Pascal"),
	    SYSTEM("System");
	    
	    public String name;
	    
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
	
	public final String linkageName;
	public final Linkage linkage;
	
	public DeclarationLinkage(String linkageName, AttribBodySyntax bodySyntax, ASTNeoNode bodyDecls) {
		super(bodySyntax, bodyDecls);
		this.linkageName = linkageName;
		this.linkage = Linkage.fromString(linkageName);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECLARATION_LINKAGE;
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
		if(linkageName != null) {
			cp.appendStrings("(", linkageName, ")");
		}
		cp.append(" ");
		toStringAsCode_body(cp);
	}
	
}