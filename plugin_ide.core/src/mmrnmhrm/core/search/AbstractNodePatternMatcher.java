package mmrnmhrm.core.search;


import java.nio.file.Path;

import org.eclipse.dltk.core.ISourceModule;

import dtool.ast.ASTNode;
import dtool.ast.ASTVisitor;
import dtool.parser.DeeParserResult.ParsedModule;

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
	
	public void doMatching(ParsedModule parsedModule, final ISourceModule sourceModule, final Path filePath) {
		parsedModule.getModuleNode().accept(new ASTVisitor() {
			@Override
			public boolean preVisit(ASTNode node) {
				return match(node, sourceModule, filePath);
			}
		});
	}
	
	public abstract boolean match(ASTNode node, ISourceModule sourceModule, Path filePath);
	
	public void addMatch(ASTNode node, int accLevel, ISourceModule sourceModule) {
		deeMatchLocator.addMatch(node, accLevel, sourceModule);
	}
	
}
