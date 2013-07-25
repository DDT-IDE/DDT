package mmrnmhrm.core.codeassist;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model_elements.DeeModelEngine;
import mmrnmhrm.core.parser.DeeModuleParsingUtil;

import org.eclipse.dltk.codeassist.ScriptCompletionEngine;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.CompletionContext;
import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.core.CompletionRequestor;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;

import dtool.DeeNamingRules;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
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
			
			final ISourceModule sourceModule;
			DeeParserResult parseResult;
			IModuleResolver mr;
			
			if(moduleSource instanceof ISourceModule) {
				sourceModule = (ISourceModule) moduleSource;
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
				sourceModule = null;
			}
			
			IDefUnitMatchAccepter collectorAdapter = new IDefUnitMatchAccepter() {
				@Override
				public void accept(DefUnit defUnit, PrefixSearchOptions searchOptions) {
					CompletionProposal proposal = createProposal(defUnit, position, sourceModule, searchOptions);
					requestor.accept(proposal);
				}
			};
			PrefixDefUnitSearch.doCompletionSearch(parseResult, position, mr, collectorAdapter);
		} finally {
			requestor.endReporting();
		}
	}
	
	public String getDefaultModuleName(IModuleSource moduleSource) {
		String fileName = moduleSource.getFileName();
		return fileName == null ? "" : DeeNamingRules.getModuleNameFromFileName(fileName);
	}
	
	protected CompletionProposal createProposal(DefUnit defUnit, int ccOffset, ISourceModule sourceModule,
			PrefixSearchOptions searchOptions) {
		String rplStr = defUnit.getName().substring(searchOptions.namePrefixLen);
		
		CompletionProposal proposal = createProposal(CompletionProposal.TYPE_REF, ccOffset);
		proposal.setName(defUnit.toStringForCodeCompletion());
		proposal.setCompletion(rplStr);
		proposal.setReplaceRange(ccOffset, ccOffset + searchOptions.rplLen);
		proposal.setExtraInfo(defUnit);
		
		// TODO: remove this code, it's not necessary to setModelElement
		Module moduleNode = defUnit.getModuleNode();
		if(moduleNode != null && sourceModule != null) {
			// We need the check above because of synthetic defUnits TODO address this in a different way

			DeeProjectModuleResolver moduleResolver = new DeeProjectModuleResolver(sourceModule);
			try {
				ISourceModule defUnitSourceModule = moduleResolver.findModuleUnit(moduleNode, sourceModule);
				if(defUnitSourceModule != null) {
					IMember me = DeeModelEngine.findCorrespondingModelElement(defUnit, defUnitSourceModule);
					proposal.setModelElement(me);
				}
			} catch(ModelException e) {
				// Just log, don't set model element
				DeeCore.log(e);
			}
		}
		
		return proposal;
	}
	
}