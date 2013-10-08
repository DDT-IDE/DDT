package mmrnmhrm.core.launch.debug;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;

//TODO: IDebugTarget implement
public class DeeDebugTarget extends AbstractDebugElement implements IDebugTarget {
	
	protected final ILaunch launch;
	protected final IProcess process;
	
	public DeeDebugTarget(ILaunch launch, IProcess process) {
		this.launch = launch;
		this.process = process;
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
		return process.getLabel();
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
		return true;
	}
	
	@Override
	public boolean isTerminated() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void terminate() throws DebugException {
		System.out.println("terminate");
		// TODO Auto-generated method stub
	}
	
	@Override
	public boolean canResume() {
		return true;
	}
	
	@Override
	public boolean canSuspend() {
		return true;
	}
	
	@Override
	public boolean isSuspended() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void resume() throws DebugException {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void suspend() throws DebugException {
		// TODO Auto-generated method stub
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