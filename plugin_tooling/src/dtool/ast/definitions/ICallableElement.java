package dtool.ast.definitions;

import melnorme.utilbox.collections.ArrayView;

public interface ICallableElement {
	
	ArrayView<IFunctionParameter> getParameters();
	
}