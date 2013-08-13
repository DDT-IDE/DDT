package dtool.resolver;


/**
 */
public interface IResolveParticipant extends IBaseScope {

	void provideResultsForSearch(CommonDefUnitSearch search, boolean importsOnly);

}