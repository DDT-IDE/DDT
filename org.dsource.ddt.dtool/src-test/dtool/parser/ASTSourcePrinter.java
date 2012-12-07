package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import dtool.ast.ASTNeoAbstractVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.declarations.DeclarationConditional;
import dtool.ast.declarations.DeclarationImport;
import dtool.ast.declarations.DeclarationInvariant;
import dtool.ast.declarations.DeclarationUnitTest;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.DefinitionAlias;
import dtool.ast.definitions.DefinitionClass;
import dtool.ast.definitions.DefinitionCtor;
import dtool.ast.definitions.DefinitionEnum;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.DefinitionInterface;
import dtool.ast.definitions.DefinitionStruct;
import dtool.ast.definitions.DefinitionTemplate;
import dtool.ast.definitions.DefinitionTypedef;
import dtool.ast.definitions.DefinitionUnion;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.Symbol;
import dtool.ast.expressions.ExpLiteralFunc;
import dtool.ast.expressions.ExpLiteralNewAnonClass;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.CommonRefNative;
import dtool.ast.references.CommonRefQualified;
import dtool.ast.references.NamedReference;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.RefTemplateInstance;
import dtool.ast.references.Reference;

public class ASTSourcePrinter extends ASTNeoAbstractVisitor {
	
	public static String printSource(ASTNeoNode node) {
		ASTSourcePrinter astSourcePrinter = new ASTSourcePrinter();
		node.accept(astSourcePrinter);
		return astSourcePrinter.sb.toString();
	}
	
	protected final StringBuilder sb = new StringBuilder();
	

	@Override
	public boolean preVisit(ASTNeoNode node) {
		return true;
	}

	@Override
	public void postVisit(ASTNeoNode node) {
	}

	@Override
	public boolean visit(ASTNeoNode node) {
		throw assertFail();
	}

	@Override
	public boolean visit(Symbol node) {
		throw assertFail();
	}

	@Override
	public boolean visit(DefUnit node) {
		throw assertFail();
	}

	@Override
	public boolean visit(Module module) {
		if(module.md != null) {
			sb.append("module ");
			for (String pack : module.md.packages) {
				sb.append(pack + ".");
			}
			sb.append(module.getName());
			sb.append(";");
		}
		
		for (ASTNeoNode decl : module.members) {
			decl.accept(this);
		}
		return false;
	}

	@Override
	public boolean visit(DefinitionStruct node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean visit(DefinitionUnion node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean visit(DefinitionClass node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean visit(DefinitionInterface node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean visit(DefinitionTemplate node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean visit(DefinitionVariable node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean visit(DefinitionEnum node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean visit(DefinitionTypedef node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean visit(DefinitionAlias node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean visit(DefinitionFunction node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean visit(DefinitionCtor node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean visit(Resolvable node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean visit(Reference node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean visit(CommonRefNative node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean visit(NamedReference node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean visit(CommonRefQualified node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean visit(RefIdentifier node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean visit(RefTemplateInstance node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean visit(DeclarationImport node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean visit(DeclarationInvariant node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean visit(DeclarationUnitTest node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean visit(DeclarationConditional node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean visit(ExpLiteralFunc node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean visit(ExpLiteralNewAnonClass node) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
