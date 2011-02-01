package mmrnmhrm.core.dltk;

import mmrnmhrm.core.model.DeeNature;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.AbstractSourceElementParser;
import org.eclipse.dltk.core.DLTKCore;

public class DeeSourceElementParser extends AbstractSourceElementParser {
	
	public DeeSourceElementParser() {
	}
	
	@Override
	protected String getNatureId() {
		return DeeNature.NATURE_ID;
	}
	
	@Override
	public void parseSourceModule(IModuleSource module) {
		final ModuleDeclaration moduleDeclaration = parse(module);
		if (moduleDeclaration != null) {
			DeeModuleDeclaration deeModuleDecl = (DeeModuleDeclaration) moduleDeclaration;
			DeeSourceElementProvider provider = new DeeSourceElementProvider(getRequestor());
//			final SourceElementRequestVisitor requestor = createVisitor();
			
			try {
				provider.provide(deeModuleDecl);
//				moduleDeclaration.traverse(requestor);
			} catch (Exception e) {
				if (DLTKCore.DEBUG) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
