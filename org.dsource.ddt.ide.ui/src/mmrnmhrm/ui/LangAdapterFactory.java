package mmrnmhrm.ui;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.IContributorResourceAdapter;

// not used anymore
public class LangAdapterFactory implements IAdapterFactory, IContributorResourceAdapter {
	
	
	private static final Class<?>[] ADAPTER_LIST= new Class[] {
		IResource.class,
		IContributorResourceAdapter.class,
	};
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		/*if (IResource.class.equals(adapterType)) {
			return getResource((ILangElement) adaptableObject);
		} if (IFolder.class.equals(adapterType)) {
			return getResource((ILangElement) adaptableObject);
		}*/if (IContributorResourceAdapter.class.equals(adapterType)) {
			return this;
		}
		return null;
	}
	
	@Override
	public Class<?>[] getAdapterList() {
		return ADAPTER_LIST;
	}
	
	@Override
	public IResource getAdaptedResource(IAdaptable adaptable) {
//		if(adaptable instanceof ILangElement)
//			return ((ILangElement) adaptable).getUnderlyingResource();
//		
		return null;
	}
	
}
