package mmrnmhrm.core.search;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;

import mmrnmhrm.core.DeeCore;

import org.dsource.ddt.ide.core.model.DeeModelUtil;
import org.dsource.ddt.ide.core.model.DeeModuleDeclaration;
import org.dsource.ddt.ide.core.model.engine.DeeModelEngine;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchMatch;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.core.search.matching.IMatchLocator;
import org.eclipse.dltk.core.search.matching.MatchLocator;
import org.eclipse.dltk.core.search.matching.MatchLocatorParser;
import org.eclipse.dltk.core.search.matching.PossibleMatch;

import dtool.ast.ASTNeoNode;
import dtool.ast.definitions.DefUnit;
import dtool.refmodel.NodeUtil;

public class DeeMatchLocator extends MatchLocator implements IMatchLocator {
	
	
	protected AbstractNodePatternMatcher patternMatcher;
	
	protected ArrayList<SearchMatch> matches;
	
	public DeeMatchLocator() {
	}
	
	@Override
	public void initialize(SearchPattern pattern, IDLTKSearchScope scope) {
		super.initialize(pattern, scope);
		this.patternMatcher = DeeNodePatternMatcherFactory.createPatternMatcher(this, this.pattern);
		assertNotNull(patternMatcher);
	}
	
	@Override
	public void initialize(IScriptProject project, int possibleMatchSize) throws ModelException {
		super.initialize(project, possibleMatchSize);
		assertTrue(parser instanceof DeeMatchLocatorParser);
	}
	
	public static class DeeMatchLocatorParser extends MatchLocatorParser {
		
		public DeeMatchLocatorParser(MatchLocator locator) {
			super(locator);
		}
		
		@Override
		public ModuleDeclaration parse(PossibleMatch possibleMatch) {
			ISourceModule sourceModule = (ISourceModule) possibleMatch.getModelElement();
			ModuleDeclaration module = super.parse(possibleMatch);
			return DeeModelUtil.parentizeDeeModuleDeclaration(module, sourceModule);
		}
		
		@Override
		public void parseBodies(ModuleDeclaration unit) {
			assertFail();
		}
		
	}
	
	@Override
	protected void locateMatches(IScriptProject scriptProject, PossibleMatch[] possibleMatches, int start, int length)
			throws CoreException {
		super.locateMatches(scriptProject, possibleMatches, start, length);
		
		// Warning: the rest of this class relies that the code for #locateMatches is like this:
		
//		initialize(scriptProject, length);
//		// create and resolve binding (equivalent to beginCompilation() in
//		// Compiler)
//		for (int i = start, maxUnits = start + length; i < maxUnits; i++) {
//			PossibleMatch possibleMatch = possibleMatches[i];
//			try {
//				if (!parse(possibleMatch))
//					continue;
//				worked();
//				process(possibleMatch);
//				if (this.numberOfMatches > 0
//						&& this.matchesToProcess[this.numberOfMatches - 1] == possibleMatch) {
//					// forget last possible match as it was processed
//					this.numberOfMatches--;
//				}
//			} finally {
//				possibleMatch.cleanUp();
//			}
//		}
	}
	
	@SuppressWarnings("restriction")
	@Override
	protected void getMethodBodies(ModuleDeclaration unit, 
			org.eclipse.dltk.internal.core.search.matching.MatchingNodeSet nodeSet) {
		// Do nothing. As a consequence this.parser.parseBodies() is not called. 
	}
	
	@Override
	protected void process(PossibleMatch possibleMatch) throws CoreException {
		
		DeeModuleDeclaration deeUnit = (DeeModuleDeclaration) possibleMatch.parsedUnit;
		ISourceModule sourceModule = (ISourceModule) possibleMatch.getModelElement();
		
		
		this.currentPossibleMatch = possibleMatch; // required by addMatch
		
		// Stage 1: collect matches 
		matches = new ArrayList<SearchMatch>();
		patternMatcher.doMatching(deeUnit, sourceModule);
		
		
		// Stage 2: report matches
		// BM: I don't quite like the way this code is structured, but we are following the DLTK way, literally
		
		// DLTK copied code, partially, 3.0
		// unit comes from ModuleDeclaration parsedUnit = this.parser.parse(possibleMatch);
		
		this.currentPossibleMatch = possibleMatch;
		
		ModuleDeclaration unit = possibleMatch.parsedUnit;
		try {
			if (unit == null || unit.isEmpty()) {
				return;
			}
			reportMatchingDo();
		} finally {
			this.matches = null;
			this.currentPossibleMatch = null;
		}
	}
	
	public void addMatch(ASTNeoNode node, int accLevel, ISourceModule sourceModule) {
		DefUnit defUnit = (node instanceof DefUnit) ? (DefUnit) node : NodeUtil.getOuterDefUnit(node);
		IModelElement enclosingType;
		try {
			enclosingType = DeeModelEngine.searchForModelElement(defUnit, sourceModule, true);
			assertNotNull(enclosingType);
		} catch (ModelException e) {
			enclosingType = sourceModule;
		}
		SearchMatch match = this.newDeclarationMatch(enclosingType, accLevel, node.matchStart(), node.matchLength());
		// TODO: create reference matches
		matches.add(match);
	}
	
	protected void reportMatchingDo() {
		for (SearchMatch match : matches) {
			try {
				report(match);
			} catch (CoreException e) {
				DeeCore.log(e);
			}
		}
	}
	
}