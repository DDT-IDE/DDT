package org.dsource.ddt.lang.core.launch;

import java.util.ArrayList;
import java.util.List;

import melnorme.utilbox.misc.ArrayUtil;

import org.eclipse.dltk.launching.AbstractInterpreterRunner;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.InterpreterConfig;

/**
 * Extension to AbstractInterpreterRunner
 * 
 * TODO: allow running with requiring an IInterpreterInstall
 * 
 * @author bruno
 */
public abstract class AbstractInterpreterRunner_LangExtension extends AbstractInterpreterRunner {
	
	protected AbstractInterpreterRunner_LangExtension(IInterpreterInstall install) {
		super(install);
	}
	
	public String[] renderCommandLineForCompiledExecutable(InterpreterConfig config) {
		List<String> items = new ArrayList<String>();
		
		items.add(config.getScriptFilePath().toString());
		
		// native executable arguments
		List<String> scriptArgs = config.getScriptArgs();
		items.addAll(scriptArgs);
		
		return ArrayUtil.createFrom(items, String.class);
	}
	
}