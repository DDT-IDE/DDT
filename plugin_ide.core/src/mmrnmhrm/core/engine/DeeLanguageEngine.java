/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.engine;

import dtool.engine.DToolServer;
import dtool.engine.ModuleParseCache;
import dtool.engine.SemanticManager;
import dtool.engine.operations.DeeSymbolCompletionResult;
import dtool.engine.operations.FindDefinitionResult;
import melnorme.lang.ide.core.LangCore;
import melnorme.lang.tooling.common.ops.IOperationMonitor;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.Location;

/**
 * Handle communication with DToolServer.
 */
public class DeeLanguageEngine {
	
	public static DeeLanguageEngine getDefault() {
		return LangCore.deeLanguageEngine();
	}
	
	protected final DToolServer dtoolServer;
	
	public DeeLanguageEngine() {
		super();
		
		dtoolServer = new DToolServer() {
			@Override
			public void logError(String message, Throwable throwable) {
				super.logError(message, throwable);
				// Note: the error logging is important not just logging in normal usage, 
				// but also for tests detecting errors. It's not the best way, but works for now.
				LangCore.logError(message, throwable);
			}
		};
	}
	
	public SemanticManager getServerSemanticManager() {
		return dtoolServer.getSemanticManager();
	}
	
	protected ModuleParseCache getParseCache() {
		return getServerSemanticManager().getParseCache();
	}
	
	/* ----------------- operations ----------------- */
	
	// Only tests may modify this variable:
	public static volatile Location compilerPathOverride = null;
	
	public class CodeCompletionOperation extends DeeEngineOperation<DeeSymbolCompletionResult> {
		
		protected final String effectiveDubPath;
		
		public CodeCompletionOperation(Location location, int timeoutMillis, int offset, String effectiveDubPath) {
			super(DeeLanguageEngine.this, location, offset, timeoutMillis, "Code Completion");
			this.effectiveDubPath = effectiveDubPath;
		}
		
		@Override
		protected DeeSymbolCompletionResult doRunOperationWithWorkingCopy(IOperationMonitor om) 
				throws CommonException, OperationCancellation {
			return dtoolServer.doCodeCompletion(location.toPath(), offset, DeeLanguageEngine.compilerPathOverride, 
				effectiveDubPath);
		}
		
	}
	
	public class FindDefinitionOperation extends DeeEngineOperation<FindDefinitionResult> {
		
		protected final String effectiveDubPath;
		
		public FindDefinitionOperation(Location location, int offset, int timeoutMillis, String effectiveDubPath) {
			super(DeeLanguageEngine.this, location, offset, timeoutMillis, "Find Definition");
			this.effectiveDubPath = effectiveDubPath;
		}
		
		@Override
		protected FindDefinitionResult doRunOperationWithWorkingCopy(IOperationMonitor om) 
				throws CommonException, OperationCancellation {
			return dtoolServer.doFindDefinition(location.toPath(), offset, effectiveDubPath);
		}
	}
	
	public class FindDDocViewOperation extends DeeEngineOperation<String> {
		
		protected final String effectiveDubPath;
		
		public FindDDocViewOperation(Location location, int offset, int timeoutMillis, String effectiveDubPath) {
			super(DeeLanguageEngine.this, location, offset, timeoutMillis, "Resolve DDoc");
			this.effectiveDubPath = effectiveDubPath;
		}
		
		@Override
		protected String doRunOperationWithWorkingCopy(IOperationMonitor om) 
				throws CommonException, OperationCancellation {
			return dtoolServer.getDDocHTMLView(location.toPath(), offset, effectiveDubPath);
		}
	}
	
}