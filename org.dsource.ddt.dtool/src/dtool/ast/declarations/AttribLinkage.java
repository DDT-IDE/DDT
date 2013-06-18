package dtool.ast.declarations;

import static melnorme.utilbox.core.CoreUtil.areEqual;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;

public class AttribLinkage extends Attribute {
	
	public final String linkageName;
	public final Linkage linkage;
	
	public AttribLinkage(String linkageName) {
		this.linkageName = linkageName;
		this.linkage = Linkage.fromString(linkageName);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.ATTRIB_LINKAGE;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		if (visitor.visit(this)) {
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("extern");
		if(linkageName != null) {
			cp.appendStrings("(", linkageName, ")");
		}
	}
	
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
	
}