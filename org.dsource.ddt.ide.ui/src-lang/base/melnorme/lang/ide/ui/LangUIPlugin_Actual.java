package melnorme.lang.ide.ui;

import java.util.List;

import org.eclipse.jface.text.rules.IPartitionTokenScanner;

import melnorme.lang.ide.ui.editor.ILangEditorTextHover;
import melnorme.utilbox.misc.ArrayUtil;
import mmrnmhrm.ui.DeeUIPlugin;
import mmrnmhrm.ui.editor.hover.AnnotationHover_Adapter;
import mmrnmhrm.ui.editor.hover.DeeDocTextHover;
import mmrnmhrm.ui.text.DeePartitionScanner;
import mmrnmhrm.ui.text.DeePartitions;

/**
 * Actual/concrete IDE constants and other bindings, for Lang UI code. 
 */
public final class LangUIPlugin_Actual {
	
	public static final String PLUGIN_ID = DeeUIPlugin.PLUGIN_ID;
	
	// ID to start the debug plugin automatically, if present
	protected static final String DEBUG_PLUGIN_ID = "org.dsource.ddt.ide.debug";
	
	protected static void initTextHovers(List<Class<? extends ILangEditorTextHover<?>>> textHoverSpecifications) {
		textHoverSpecifications.add(AnnotationHover_Adapter.ProblemHover_Adapter.class);
		textHoverSpecifications.add(DeeDocTextHover.class);
		textHoverSpecifications.add(AnnotationHover_Adapter.class);
	}
	
	public static final String LANG_PARTITIONING = DeePartitions.PARTITIONING_ID;
	public static final String[] LEGAL_CONTENT_TYPES = 
			ArrayUtil.remove(DeePartitions.DEE_PARTITION_TYPES, DeePartitions.DEE_CODE);
	
	public static IPartitionTokenScanner createPartitionScanner() {
		return new DeePartitionScanner();
	}
	
}