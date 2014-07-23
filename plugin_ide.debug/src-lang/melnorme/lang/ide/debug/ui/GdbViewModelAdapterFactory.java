package melnorme.lang.ide.debug.ui;

import melnorme.lang.ide.debug.ui.viewmodel.LangGdbViewModelAdapter;

import org.dsource.ddt.debug.core.DeeGdbLaunch;
import org.eclipse.cdt.dsf.debug.ui.viewmodel.SteppingController;
import org.eclipse.cdt.dsf.gdb.ui.viewmodel.GdbViewModelAdapter;
import org.eclipse.cdt.dsf.gdb.ui.viewmodel.IGdbViewModelServicesFactory;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.IAdapterFactory;

public class GdbViewModelAdapterFactory implements IAdapterFactory {
	
	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (IGdbViewModelServicesFactory.class.equals(adapterType) && adaptableObject instanceof DeeGdbLaunch) {
			return new IGdbViewModelServicesFactory() {
				
				@Override
				public GdbViewModelAdapter createGdbViewModelAdapter(DsfSession session, 
						SteppingController steppingController) {
					return new LangGdbViewModelAdapter(session, steppingController);
				}
				
			};
		}
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Class[] getAdapterList() {
		return new Class[] { IGdbViewModelServicesFactory.class };
	}
	
}

