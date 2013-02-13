package mmrnmhrm.core.search;


import org.dsource.ddt.ide.core.model.DeeModuleDeclaration;
import org.eclipse.dltk.core.ISourceModule;

import dtool.ast.ASTHomogenousVisitor;
import dtool.ast.ASTNeoNode;

public abstract class AbstractNodePatternMatcher {
	
	protected final DeeMatchLocator deeMatchLocator;
	
	protected final boolean matchDeclarations;
	protected final boolean matchReferences;
	
	public AbstractNodePatternMatcher(DeeMatchLocator deeMatchLocator, 
			boolean findDeclarations, boolean findReferences) {
		this.deeMatchLocator = deeMatchLocator;
		this.matchDeclarations = findDeclarations;
		this.matchReferences = findReferences;
	}
	
	public void doMatching(DeeModuleDeclaration deeUnit, final ISourceModule sourceModule) {
		deeUnit.neoModule.accept(new ASTHomogenousVisitor() {
			@Override
			public boolean preVisit(ASTNeoNode node) {
				return match(node, sourceModule);
			}
		});
	}
	
	public abstract boolean match(ASTNeoNode node, ISourceModule sourceModule);
	
	public void addMatch(ASTNeoNode node, int accLevel, ISourceModule sourceModule) {
		deeMatchLocator.addMatch(node, accLevel, sourceModule);
	}
	
}
