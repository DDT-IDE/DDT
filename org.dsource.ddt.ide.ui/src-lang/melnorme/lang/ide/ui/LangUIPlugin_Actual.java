package melnorme.lang.ide.ui;

import java.util.List;

import melnorme.lang.ide.ui.editors.ILangEditorTextHover;
import mmrnmhrm.ui.editor.hover.AnnotationHover_Adapter;
import mmrnmhrm.ui.editor.hover.DeeDocTextHover;

import org.dsource.ddt.ui.DeeUIPlugin;

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
	
}