package mmrnmhrm.core.launch.debug;

import java.util.HashMap;

import melnorme.utilbox.misc.ArrayUtil;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.debug.core.DebugEvent;
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
	protected final IDebuggerHandler debuggerController;
	
	protected DebugExecutionState state = DebugExecutionState.RUNNING;
	
	public DeeDebugTarget(ILaunch launch, IProcess process, Process sp) {
		super(null);
		this.launch = launch;
		this.process = process;
		this.debuggerController = new GdbController(process, sp, this);

		debuggerController.commandStartSession();
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
	
	protected void shutdown() {
		debuggerController.dispose();
	}
	
	// ---------------- Threads
	
	protected HashMap<String, DeeDebugThread> threads = new HashMap<>();
	
	@Override
	public synchronized IThread[] getThreads() throws DebugException {
		return ArrayUtil.createFrom(threads.values(), IThread.class);
	}
	
	@Override
	public synchronized boolean hasThreads() throws DebugException {
		return !threads.isEmpty();
	}
	
	public synchronized void createThread(String id) {
		DeeDebugThread debugThread = new DeeDebugThread(this);
		threads.put(id, debugThread);
		debugThread.fireCreationEvent();
	}
	
	public synchronized void removeThread(String id) {
		DeeDebugThread debugThread = threads.get(id);
		if(debugThread != null) {
			debugThread.fireTerminateEvent();
			threads.remove(id);
		}
	}
	
	// ---------------- ITerminate , ISuspendResume ----------------
	
	@Override
	public boolean canTerminate() {
		return !isTerminated();
	}
	
	@Override
	public boolean isTerminated() {
		return state.isTerminated();
	}
	
	@Override
	public boolean canResume() {
		return state.canResume();
	}
	
	@Override
	public boolean canSuspend() {
		return state.canSuspend();
	}
	
	@Override
	public boolean isSuspended() {
		return state.isSuspended();
	}
	
	@Override
	public void terminate() throws DebugException {
		getProcess().terminate();
		setTerminated();
	}
	
	@Override
	public void resume() throws DebugException {
		debuggerController.commandResume();
	}
	
	@Override
	public void suspend() throws DebugException {
		debuggerController.commandSuspend();
	}
	
	public synchronized void setTerminated() {
		state = DebugExecutionState.TERMINATED;
		fireTerminateEvent();
		shutdown();
	}

	public synchronized void setResumed(boolean clientRequest) {
		state = DebugExecutionState.RUNNING;
		fireResumeEvent(clientRequest ? DebugEvent.CLIENT_REQUEST : DebugEvent.UNSPECIFIED );
	}
	
	
	protected synchronized void setSuspended(boolean clientRequest) {
		state = DebugExecutionState.SUSPENDED;
		fireResumeEvent(clientRequest ? DebugEvent.CLIENT_REQUEST : DebugEvent.UNSPECIFIED );
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