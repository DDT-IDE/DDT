package mmrnmhrm.ui.internal.text;

import org.eclipse.jface.preference.IPreferenceStore;

public class DeeAutoEditStrategy extends RubyAutoEditStrategy {
	
	public DeeAutoEditStrategy(String partitioning) {
		super(partitioning);
	}
	
	public DeeAutoEditStrategy(String partitioning, IPreferenceStore store) {
		super(partitioning, store);
	}
	
}
