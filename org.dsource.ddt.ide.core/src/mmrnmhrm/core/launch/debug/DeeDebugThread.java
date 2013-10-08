package mmrnmhrm.core.launch.debug;

import mmrnmhrm.core.launch.debug.AbstractDebugElement.ChildDebugElement;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;

public class DeeDebugThread extends ChildDebugElement implements IThread {
	
	public DeeDebugThread(DeeDebugTarget debugTarget) {
		super(debugTarget);
	}
	
	@Override
	public String getName() throws DebugException {
		// TODO Auto-generated method stub
		return "Thread";
	}
	
	@Override
	public int getPriority() throws DebugException {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public IBreakpoint[] getBreakpoints() {
		// TODO Auto-generated method stub
		return null;
	}
	
	// ---------------- stack frames
	
	protected IStackFrame[] stackFrames = new IStackFrame[]{
		new DeeStackFrame(this)
	};
	
	@Override
	public IStackFrame[] getStackFrames() throws DebugException {
		return stackFrames; // TODO
	}
	
	@Override
	public boolean hasStackFrames() throws DebugException {
		return true;
	}
	
	@Override
	public IStackFrame getTopStackFrame() throws DebugException {
		return stackFrames[0];
	}
	
	
	// ---------------- suspend/resume
	
	@Override
	public boolean canResume() {
		return false;
	}
	
	@Override
	public boolean canSuspend() {
		return false;
	}
	
	@Override
	public boolean isSuspended() {
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
	
	// ---------------- Stepping
	
	@Override
	public boolean canStepInto() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean canStepOver() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean canStepReturn() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean isStepping() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void stepInto() throws DebugException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void stepOver() throws DebugException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void stepReturn() throws DebugException {
		// TODO Auto-generated method stub
		
	}
	
	// ---------------- ITerminate
	
	@Override
	public boolean canTerminate() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean isTerminated() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void terminate() throws DebugException {
		// TODO Auto-generated method stub
	}
	
}