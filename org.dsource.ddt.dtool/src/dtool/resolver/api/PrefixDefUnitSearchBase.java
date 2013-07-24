package dtool.resolver.api;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.IScopeNode;

// TODO: cleanup this hierarchy
public abstract class PrefixDefUnitSearchBase extends CommonDefUnitSearch {
	
	public static enum ECompletionResultStatus {
		RESULT_OK("ok"),
		INVALID_TOKEN_LOCATION("invalid_token"),
		INVALID_REFQUAL_LOCATION("invalid_qualified"),
		OTHER_REFERENCE("other_ref")
		;
		
		protected final String id;
		
		ECompletionResultStatus(String id) {
			this.id = assertNotNull(id);
		}
		
		public static ECompletionResultStatus fromId(String statusId) {
			for (ECompletionResultStatus status : values()) {
				if(status.id.equals(statusId)) {
					return status;
				}
			}
			return null;
		}
	}

	public final PrefixSearchOptions searchOptions;
	
	public PrefixDefUnitSearchBase(IScopeNode refScope, int refOffset, IModuleResolver moduleResolver,
		PrefixSearchOptions searchOptions) {
		super(refScope, refOffset, moduleResolver);
		this.searchOptions = searchOptions;
	}
	
}