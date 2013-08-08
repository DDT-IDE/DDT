package mmrnmhrm.core.codeassist;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import mmrnmhrm.core.parser.DeeModuleParsingUtil;

import org.eclipse.dltk.codeassist.ScriptCompletionEngine;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.CompletionContext;
import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.core.CompletionRequestor;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;

import dtool.DeeNamingRules;
import dtool.ast.definitions.DefUnit;
import dtool.parser.DeeParser;
import dtool.parser.DeeParserResult;
import dtool.resolver.PrefixDefUnitSearch;
import dtool.resolver.api.IDefUnitMatchAccepter;
import dtool.resolver.api.IModuleResolver;
import dtool.resolver.api.NullModuleResolver;
import dtool.resolver.api.PrefixSearchOptions;

public class DeeCompletionEngine extends ScriptCompletionEngine {
	
	protected CompletionRequestor getRequestor() {
		return requestor;
	}
	
	@Override
	public void complete(IModuleSource moduleSource, final int position, int i) {
		assertNotNull(requestor);
		requestor.beginReporting();
		try {
			CompletionContext context = new CompletionContext();
			requestor.acceptContext(context);
			
			IDefUnitMatchAccepter collectorAdapter = new IDefUnitMatchAccepter() {
				@Override
				public void accept(DefUnit defUnit, PrefixSearchOptions searchOptions) {
					CompletionProposal proposal = createProposal(defUnit, position, searchOptions);
					requestor.accept(proposal);
				}
			};
			
			DeeParserResult parseResult;
			IModuleResolver mr;
			
			if(moduleSource instanceof ISourceModule) {
				ISourceModule sourceModule = (ISourceModule) moduleSource;
				parseResult = DeeModuleParsingUtil.getParsedDeeModuleDecl(sourceModule).deeParserResult;
				mr = new DeeProjectModuleResolver(sourceModule.getScriptProject());
			} else {
				String defaultModuleName = getDefaultModuleName(moduleSource);
				parseResult = DeeParser.parseSource(moduleSource.getSourceContents(), defaultModuleName);
				
				IModelElement modelElement = moduleSource.getModelElement();
				if(modelElement != null) {
					mr = new DeeProjectModuleResolver(modelElement.getScriptProject());
				} else {
					mr = new NullModuleResolver();
				}
			}
			
			PrefixDefUnitSearch.doCompletionSearch(parseResult, position, mr, collectorAdapter);
		} finally {
			requestor.endReporting();
		}
	}
	
	public String getDefaultModuleName(IModuleSource moduleSource) {
		String fileName = moduleSource.getFileName();
		return fileName == null ? "" : DeeNamingRules.getModuleNameFromFileName(fileName);
	}
	
	protected CompletionProposal createProposal(DefUnit defUnit, int ccOffset, PrefixSearchOptions searchOptions) {
		String rplStr = defUnit.getName().substring(searchOptions.namePrefixLen);
		
		CompletionProposal proposal = createProposal(CompletionProposal.TYPE_REF, ccOffset);
		proposal.setName(defUnit.getExtendedName());
		proposal.setCompletion(rplStr);
		proposal.setReplaceRange(ccOffset, ccOffset + searchOptions.rplLen);
		proposal.setExtraInfo(defUnit);
		
		return proposal;
	}
	
}