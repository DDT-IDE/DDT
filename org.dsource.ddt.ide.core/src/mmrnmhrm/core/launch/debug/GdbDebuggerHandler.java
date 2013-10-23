package mmrnmhrm.core.launch.debug;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import melnorme.utilbox.misc.StringUtil;
import mmrnmhrm.core.DeeCore;

import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;

public class GdbDebuggerHandler implements IDebuggerHandler {
	
	protected final IProcess gdbProcess;
	protected final Process sp;
	protected final IStreamsProxy streamsProxy;
	protected final ScheduledExecutorService dispatcher;
	
	public GdbDebuggerHandler(IProcess gdbProcess, Process sp) {
		this.gdbProcess = gdbProcess;
		this.sp = sp;
		this.streamsProxy = gdbProcess.getStreamsProxy();
		
		this.dispatcher = new ScheduledThreadPoolExecutor(1);
	}

	@Override
	public void commandSuspend() {
		System.out.println("suspend"); // TODO
		
		dispatcher.submit(new Runnable() {
			@Override
			public void run() {
				doSuspend();
			}
		});
		doSuspend();
	}
	
	protected void doSuspend() {
		try {
			sp.getOutputStream().write("echo info\n".getBytes(StringUtil.ASCII));
			// BUG here: use UTF-8
			sp.getOutputStream().flush();
		} catch (IOException ioe) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(ioe);
		}
		
		try {
			streamsProxy.write("echo info source\n");
		} catch (IOException e) {
			DeeCore.log(e);
		}
	}
	
	protected void dispose() {
		dispatcher.shutdown();
	}
	
}