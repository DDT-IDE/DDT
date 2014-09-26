package mmrnmhrm.core.search;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;

import melnorme.utilbox.misc.MiscUtil;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.engine_client.DToolClient;
import mmrnmhrm.core.engine_client.DToolClient_Bad;
import mmrnmhrm.core.model_elements.DeeModelEngine;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.declarations.ModuleDeclarationWrapper;
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
import org.eclipse.dltk.core.search.matching.PatternLocator;
import org.eclipse.dltk.core.search.matching.PossibleMatch;

import dtool.ast.ASTNode;
import dtool.ast.definitions.DefUnit;
import dtool.ast.util.NodeUtil;
import dtool.parser.DeeParserResult.ParsedModule;

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
			Path filePath = getFilePath(possibleMatch);
			if(filePath == null)
				return null;
			
			ParsedModule parsedModule = DToolClient.getDefaultModuleCache().getParsedModuleOrNull(filePath);
			if(parsedModule == null) {
				return null;
			}
			return new ModuleDeclarationWrapper(new DeeModuleDeclaration(parsedModule));
			//return super.parse(possibleMatch);
		}
		
		@Override
		public void parseBodies(ModuleDeclaration unit) {
			assertFail();
		}
		
	}
	
	public static Path getFilePath(PossibleMatch possibleMatch) {
		Path filePath = null;
		
		// Try alternative path location
		ISourceModule sourceModule = (ISourceModule) possibleMatch.getModelElement();
		filePath = DToolClient_Bad.getFilePathOrNull(sourceModule);
		
		if(filePath == null && possibleMatch.resource != null) {
			IPath location = possibleMatch.resource.getLocation();
			if(location != null) {
				filePath = MiscUtil.createPathOrNull(location.toOSString());
			}
		}
		
		return filePath;
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
		if(possibleMatch.parsedUnit == null) {
			return;
		}
		
		DeeModuleDeclaration deeUnit = getDeeModuleDeclaration(possibleMatch.parsedUnit);
		ISourceModule sourceModule = (ISourceModule) possibleMatch.getModelElement();
		Path filePath = getFilePath(possibleMatch);
		
		this.currentPossibleMatch = possibleMatch; // required by addMatch
		
		// Stage 1: collect matches 
		matches = new ArrayList<SearchMatch>();
		patternMatcher.doMatching(deeUnit.deeParserResult, sourceModule, filePath);
		
		
		// Stage 2: report matches
		// BM: I don't quite like the way this code is structured, but we are following the DLTK way, literally
		
		// DLTK copied code, partially, 3.0
		// unit comes from ModuleDeclaration parsedUnit = this.parser.parse(possibleMatch);
		this.parser.parse(possibleMatch);
		
		this.currentPossibleMatch = possibleMatch;
		
		ModuleDeclaration unit = possibleMatch.parsedUnit;
		try {
			if (unit == null || getDeeModuleDeclaration(unit) == null /*Modified code*/) {
				return;
			}
			reportMatchingDo();
		} finally {
			this.matches = null;
			this.currentPossibleMatch = null;
		}
	}
	
	protected static DeeModuleDeclaration getDeeModuleDeclaration(ModuleDeclaration parsedUnit) {
		if(parsedUnit instanceof ModuleDeclarationWrapper) {
			ModuleDeclarationWrapper wrapper = (ModuleDeclarationWrapper) parsedUnit;
			if(wrapper.getTarget() instanceof DeeModuleDeclaration) {
				return (DeeModuleDeclaration) wrapper.getTarget();
			}
		}
		throw assertFail();
	}
	
	public void addMatch(ASTNode node, int accLevel, ISourceModule sourceModule) {
		DefUnit defUnit = (node instanceof DefUnit) ? (DefUnit) node : NodeUtil.getOuterDefUnit(node);
		IModelElement enclosingType;
		try {
			enclosingType = DeeModelEngine.searchForModelElement(defUnit, sourceModule, true);
			assertNotNull(enclosingType);
		} catch (ModelException e) {
			enclosingType = sourceModule;
		}
		int level = SearchMatch.A_INACCURATE;
		if(accLevel == PatternLocator.ACCURATE_MATCH || accLevel == PatternLocator.ERASURE_MATCH) {
			level = SearchMatch.A_ACCURATE;
		}
		
		SearchMatch match = this.newDeclarationMatch(enclosingType, level, node.getOffset(), node.getLength());
		// TODO: create reference matches
		matches.add(match);
	}
	
	protected void reportMatchingDo() {
		for (SearchMatch match : matches) {
			try {
				report(match);
			} catch (CoreException e) {
				DeeCore.logStatus(e);
			}
		}
	}
	
}