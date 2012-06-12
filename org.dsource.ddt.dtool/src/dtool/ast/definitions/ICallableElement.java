package dtool.ast.definitions;

import dtool.util.ArrayView;

public interface ICallableElement {
	
	ArrayView<IFunctionParameter> getParameters();
	
}