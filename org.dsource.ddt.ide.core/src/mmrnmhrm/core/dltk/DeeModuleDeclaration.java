package mmrnmhrm.core.dltk;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.parser.IModuleDeclaration;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import descent.internal.compiler.parser.ast.IASTNode;
import dtool.ast.definitions.Module;

public class DeeModuleDeclaration extends ModuleDeclaration implements IModuleDeclaration {

	public static interface EModelStatus {
		int OK = 0;
		int PARSER_INTERNAL_ERROR = 1;
		int PARSER_SYNTAX_ERRORS = 2;
	}
	
		public int status;
	
	public Module neoModule;
	public descent.internal.compiler.parser.Module dmdModule;
	
	
	public DeeModuleDeclaration(descent.internal.compiler.parser.Module dmdModule) {
		super(dmdModule.getLength());
		assertNotNull(dmdModule);
		this.dmdModule = dmdModule;
	}
	
	public String toStringParseStatus() {
		switch(getParseStatus()) {
		case EModelStatus.PARSER_INTERNAL_ERROR: return "Internal Error";
		case EModelStatus.PARSER_SYNTAX_ERRORS: return "Syntax Errors";
		case EModelStatus.OK: return "OK";
		default: assertFail(); return null;
		}
	}

	public int getParseStatus() {
		return status;
	}

	@SuppressWarnings("unchecked")
	public void setNeoModule(Module neoModule) {
		this.neoModule = neoModule;
		getStatements().add(neoModule);
		assertTrue(neoModule.hasNoSourceRangeInfo() 
				|| (neoModule.getStartPos() == 0 && neoModule.getEndPos() == dmdModule.getEndPos()));
	}
	
	public IASTNode getEffectiveModuleNode() {
		return neoModule == null ? dmdModule : neoModule;
	}
}
