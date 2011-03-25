package mmrnmhrm.core.codeassist;

import java.util.Iterator;

import org.eclipse.dltk.codeassist.ScriptCompletionEngine;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.core.CompletionRequestor;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementVisitor;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;

import dtool.ast.definitions.DefUnit;
import dtool.contentassist.CompletionSession;
import dtool.refmodel.PrefixDefUnitSearch;
import dtool.refmodel.PrefixDefUnitSearch.IDefUnitMatchAccepter;
import dtool.refmodel.PrefixSearchOptions;

public class ExamplePythonCompletionEngine extends ScriptCompletionEngine {
	
	private int actualCompletionPosition;
	private int offset;
	
	@Override
	public void complete(IModuleSource module, int position, int pos) {
		this.actualCompletionPosition = position;
		this.offset = pos;
		String[] keywords = new String[] { "and", "del", "for", "is", "raise",
				"assert", "elif", "from", "lambda", "break", "else", "global",
				"not", "try", "class", "except", "if", "or", "while",
				"continue", "exec", "import", "pass", "yield", "def",
				"finally", "in", "print", "self", "return" };
		for(int j = 0; j < keywords.length; j++) {
			createProposal(keywords[j], null);
		}
		
		// Completion for model elements.
		IModelElement modelElement = module.getModelElement();
		try {
			modelElement.accept(new IModelElementVisitor() {
				@Override
				public boolean visit(IModelElement element) {
					if(element.getElementType() > IModelElement.SOURCE_MODULE) {
						createProposal(element.getElementName(), element);
					}
					return true;
				}
			});
		} catch(ModelException e) {
			if(DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		}
		
		if(!(modelElement instanceof ISourceModule)) {
			return;
		}
		ISourceModule sourceModule = (ISourceModule) modelElement;
		
		doCompletionSearch(position, sourceModule, module.getSourceContents(), null, null, requestor);
	}


	public void doCompletionSearch(final int offset, ISourceModule moduleUnit, String source, CompletionSession session,
			IDefUnitMatchAccepter defUnitAccepter, final CompletionRequestor collector) {
		
		if(defUnitAccepter != null) {
			PrefixDefUnitSearch.doCompletionSearch(offset, moduleUnit, source, session, defUnitAccepter);
		}
		
		IDefUnitMatchAccepter collectorAdapter = new IDefUnitMatchAccepter() {
			@Override
			public void accept(DefUnit defUnit, PrefixSearchOptions searchOptions) {
				String rplStr = defUnit.getName().substring(searchOptions.prefixLen);

				CompletionProposal proposal = createProposal(CompletionProposal.TYPE_REF, offset);
				proposal.setName(defUnit.toStringForCodeCompletion());
				proposal.setCompletion(rplStr);
				proposal.setReplaceRange(offset, offset + rplStr.length());
//				proposal.setModelElement(name);
				proposal.setExtraInfo(defUnit);
				
				collector.accept(proposal);
			}
			
		};
		
		PrefixDefUnitSearch.doCompletionSearch(offset, moduleUnit, source, session, collectorAdapter);
	}
	
	
	private void createProposal(String name, IModelElement element) {
		CompletionProposal proposal = null;
		try {
			if(element == null) {
				proposal = this.createProposal(CompletionProposal.KEYWORD, this.actualCompletionPosition);
			} else {
				switch(element.getElementType()) {
//				case IModelElement.METHOD:
//					proposal = this.createProposal(
//							CompletionProposal.METHOD_DECLARATION,
//							this.actualCompletionPosition);
//					proposal.setFlags(((IMethod) element).getFlags());
//					break;
				case IModelElement.FIELD:
					proposal = this.createProposal(
							CompletionProposal.FIELD_REF,
							this.actualCompletionPosition);
					proposal.setFlags(((IField) element).getFlags());
					break;
				case IModelElement.TYPE:
					proposal = this.createProposal(CompletionProposal.TYPE_REF,
							this.actualCompletionPosition);
					proposal.setFlags(((IType) element).getFlags());
					break;
				default:
					proposal = this.createProposal(CompletionProposal.KEYWORD,
							this.actualCompletionPosition);
					break;
				}
			}
			proposal.setName(name);
			proposal.setCompletion(name);
			proposal.setReplaceRange(actualCompletionPosition - offset,
					actualCompletionPosition - offset);
			proposal.setRelevance(20);
			proposal.setModelElement(element);
			this.requestor.accept(proposal);
		} catch(Exception e) {
		}
	}
	
}