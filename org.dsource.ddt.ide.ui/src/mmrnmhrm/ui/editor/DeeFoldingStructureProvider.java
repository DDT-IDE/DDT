package mmrnmhrm.ui.editor;

import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.text.DeePartitions;

import org.dsource.ddt.ide.core.DeeNature;
import org.eclipse.core.runtime.ILog;
import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ui.text.folding.AbstractASTFoldingStructureProvider;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;

import dtool.ast.definitions.DefinitionAggregate;
import dtool.ast.definitions.DefinitionFunction;

// TODO DLTK use the new API
public class DeeFoldingStructureProvider extends AbstractASTFoldingStructureProvider {

	@Override
	protected ILog getLog() {
		return DeePlugin.getInstance().getLog();
	}


	@Override
	protected String getNatureId() {
		return DeeNature.NATURE_ID;
	}

	@Override
	protected String getPartition() {
		return DeePartitions.DEE_PARTITIONING;
	}
	
	@Override
	protected String[] getPartitionTypes() {
		return DeePartitions.DEE_PARTITION_TYPES;
	}
	
	@Override
	protected String getCommentPartition() {
		// XXX: DLTK limitation: Hum, seems DLTK supports only one comment partition?
		return DeePartitions.DEE_MULTI_DOCCOMMENT;
	}


	@Override
	protected IPartitionTokenScanner getPartitionScanner() {
		return DeePlugin.getInstance().getTextTools().getPartitionScanner();
	}

	@Override
	protected boolean initiallyCollapse(ASTNode s, FoldingStructureComputationContext ctx) {
		return false;
	}

	@Override
	protected boolean initiallyCollapseComments(IRegion commentRegion,
			FoldingStructureComputationContext ctx) {
		return false;
	}
	
	@Override
	protected FoldingASTVisitor getFoldingVisitor(int offset) {
		return new FoldingASTVisitor(offset) {
			@Override
			public boolean visit(ASTNode node) throws Exception {
				if (node instanceof DefinitionAggregate) {
					add(node);
				} else if (node instanceof DefinitionFunction) {
					add(node);
				}
				return super.visit(node);
			}

		};
	}

	@Override
	protected boolean mayCollapse(ASTNode node, FoldingStructureComputationContext ctx) {
		if (node instanceof DefinitionAggregate) {
			return true;
		} else if (node instanceof DefinitionFunction) {
			return true;
		}
		return false;
	}


}
