package dtool.resolver;

import java.util.Iterator;
import java.util.List;

import dtool.ast.IASTNode;
import dtool.resolver.api.IModuleResolver;

/**
 */
public interface IResolveParticipant {

	void provideResultsForSearch(CommonDefUnitSearch search, boolean importsOnly);

}