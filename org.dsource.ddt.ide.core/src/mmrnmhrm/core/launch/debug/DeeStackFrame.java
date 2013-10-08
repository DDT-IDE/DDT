package mmrnmhrm.core.launch.debug;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import mmrnmhrm.core.launch.debug.AbstractDebugElement.ChildDebugElement;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;

public class DeeStackFrame extends ChildDebugElement implements IStackFrame {
	
	protected final DeeDebugThread debugThread;
	
	public DeeStackFrame(DeeDebugThread debugThread) {
		super(debugThread.getDebugTarget());
		this.debugThread = debugThread;
	}
	
	@Override
	public String getName() throws DebugException {
		return "DeeStackFrame";
	}
	
	@Override
	public IThread getThread() {
		return debugThread;
	}
	
	@Override
	public boolean hasVariables() throws DebugException {
		return false;
	}
	
	@Override
	public IVariable[] getVariables() throws DebugException {
		throw assertFail();
		// TODO Auto-generated method stub
	}
	
	
	@Override
	public int getLineNumber() throws DebugException {
		// TODO Auto-generated method stub
		return -1;
	}
	
	@Override
	public int getCharStart() throws DebugException {
		// TODO Auto-generated method stub
		return -1;
	}
	
	@Override
	public int getCharEnd() throws DebugException {
		// TODO Auto-generated method stub
		return -1;
	}
	
	@Override
	public boolean hasRegisterGroups() throws DebugException {
		return false; // TODO 
	}
	
	@Override
	public IRegisterGroup[] getRegisterGroups() throws DebugException {
		// TODO Auto-generated method stub
		return null;
	}
	
	// ---------------- suspend/resume/terminate
	
	@Override
	public boolean canResume() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean canSuspend() {
		// TODO Auto-generated method stub
		return false;
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
	
	// ---------------- stepping
	
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
	
}