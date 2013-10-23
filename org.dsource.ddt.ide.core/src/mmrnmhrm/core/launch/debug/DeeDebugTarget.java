package mmrnmhrm.core.launch.debug;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;

public class DeeDebugTarget extends AbstractDebugElement implements IDebugTarget {
	
	protected final ILaunch launch;
	protected final IProcess process;
	protected final IDebuggerHandler debuggerHandler;
	
	protected DebugExecutionStatus status = DebugExecutionStatus.RUNNING;
	
	public DeeDebugTarget(ILaunch launch, IProcess process, IDebuggerHandler debuggerHandler) {
		this.launch = launch;
		this.process = process;
		this.debuggerHandler = debuggerHandler;
	}
	
	@Override
	public DeeDebugTarget getDebugTarget() {
		return this;
	}
	
	@Override
	public ILaunch getLaunch() {
		return launch;
	}
	
	@Override
	public IProcess getProcess() {
		return process;
	}
	
	@Override
	public String getName() throws DebugException {
		return process.getLabel() + "DEBUG TARGET";
	}
	
	// ---------------- Threads
	
	protected IThread[] threads = new IThread[] {
		new DeeDebugThread(this),
	};
	
	@Override
	public IThread[] getThreads() throws DebugException {
		return threads;
	}
	
	@Override
	public boolean hasThreads() throws DebugException {
		return true;
	}
	
	// ---------------- ITerminate , ISuspendResume ----------------
	
	@Override
	public boolean canTerminate() {
		return !isTerminated();
	}
	
	@Override
	public boolean isTerminated() {
		return status.isTerminated();
	}
	
	@Override
	public void terminate() throws DebugException {
		System.out.println("terminate"); // TODO
		getProcess().terminate(); // TODO: run async?
		status = DebugExecutionStatus.TERMINATED;
	}
	
	@Override
	public boolean canResume() {
		return status.canResume();
	}
	
	@Override
	public boolean canSuspend() {
		return status.canSuspend();
	}
	
	@Override
	public boolean isSuspended() {
		return status.isSuspended();
	}
	
	@Override
	public void resume() throws DebugException {
		System.out.println("resume"); // TODO
		status = DebugExecutionStatus.RUNNING;
	}
	
	@Override
	public void suspend() throws DebugException {
		debuggerHandler.commandSuspend();
		status = DebugExecutionStatus.SUSPENDED;
	}
	
	// ---------------- breakpoints
	
	@Override
	public boolean supportsBreakpoint(IBreakpoint breakpoint) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void breakpointAdded(IBreakpoint breakpoint) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
		// TODO Auto-generated method stub
		
	}
	
	// ---------------- IDisconnect
	
	@Override
	public boolean canDisconnect() {
		return false; // TODO: disconnect support
	}
	
	@Override
	public void disconnect() throws DebugException {
	}
	
	@Override
	public boolean isDisconnected() {
		return false;
	}
	
	// ---------------- IMemoryBlock ----------------
	
	@Override
	public boolean supportsStorageRetrieval() {
		return false; // TODO: support storage retrieval
	}
	
	@Override
	public IMemoryBlock getMemoryBlock(long startAddress, long length) throws DebugException {
		return null;
	}
	
}