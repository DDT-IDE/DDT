package mmrnmhrm.core.search;


import org.dsource.ddt.ide.core.model.DeeModuleDeclaration;
import org.eclipse.dltk.core.ISourceModule;

import dtool.ast.ASTVisitor;
import dtool.ast.ASTNode;

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
		deeUnit.module.accept(new ASTVisitor() {
			@Override
			public boolean preVisit(ASTNode node) {
				return match(node, sourceModule);
			}
		});
	}
	
	public abstract boolean match(ASTNode node, ISourceModule sourceModule);
	
	public void addMatch(ASTNode node, int accLevel, ISourceModule sourceModule) {
		deeMatchLocator.addMatch(node, accLevel, sourceModule);
	}
	
}
