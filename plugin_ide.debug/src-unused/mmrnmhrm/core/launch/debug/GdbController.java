package mmrnmhrm.core.launch.debug;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import melnorme.utilbox.misc.StringUtil;

import org.eclipse.cdt.dsf.mi.service.command.output.MIAsyncRecord;
import org.eclipse.cdt.dsf.mi.service.command.output.MIOOBRecord;
import org.eclipse.cdt.dsf.mi.service.command.output.MIParser;
import org.eclipse.cdt.dsf.mi.service.command.output.MIParser.RecordType;
import org.eclipse.cdt.dsf.mi.service.command.output.MIResultRecord;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.core.model.IStreamsProxy;

public class GdbController implements IDebuggerHandler {
	
	protected final IProcess gdbProcess;
	protected final Process sp;
	protected final IStreamsProxy streamsProxy;
	protected final ScheduledExecutorService dispatcher;
	protected final MIParser miParser;
	
	protected final DeeDebugTarget deeDebugTarget;
	
	public GdbController(IProcess gdbProcess, Process sp, DeeDebugTarget deeDebugTarget) {
		this.gdbProcess = gdbProcess;
		this.sp = sp;
		this.streamsProxy = gdbProcess.getStreamsProxy();
		
		this.deeDebugTarget = deeDebugTarget;
		
		this.dispatcher = new ScheduledThreadPoolExecutor(1);
		this.miParser = new MIParser();
		
		// TODO: race condition here because we don't read all output since process start, some initial might be skipped 
		streamsProxy.getOutputStreamMonitor().addListener(new IStreamListener() {
			
			@Override
			public void streamAppended(String text, IStreamMonitor monitor) {
				handleGdbOuput(text);
			
			}

		});
	}
	
	@Override
	public void dispose() {
		dispatcher.shutdown();
	}
	
	@Override
	public void commandStartSession() {
		dispatcher.submit(new Runnable() {
			@Override
			public void run() {
				doStart();
			}
		});
	}
	
	@Override
	public void commandSuspend() {
		dispatcher.submit(new Runnable() {
			@Override
			public void run() {
				doSuspend();
			}
		});
	}
	
	@Override
	public void commandResume() {
		dispatcher.submit(new Runnable() {
			@Override
			public void run() {
				doResume();
			}
		});
	}
	
	protected void doStart() {
		writeData("-exec-run --start\n");
	}
	
	protected void doResume() {
		writeData("-exec-continue\n");
	}
	
	protected void doSuspend() {
		writeData("-exec-interrupt\n");
	}
	
	protected void writeData(String string) {
		try {
			byte[] data = string.getBytes(StringUtil.ASCII); /*BUG here use UTF-8*/
			sp.getOutputStream().write(data); 
			sp.getOutputStream().flush();
		} catch (IOException ioe) {
			// TODO: handle error
		}

//		try {
//			streamsProxy.write("echo info source\n");
//		} catch (IOException e) {
//			DeeCore.log(e);
//		}
	}
	
	protected final StringBuilder debuggerOutputBuffer = new StringBuilder();
	
	protected void handleGdbOuput(String data) {
		// Split output by line.
		int eolIndex = data.indexOf('\n');
		if(eolIndex == -1) {
			debuggerOutputBuffer.append(data);
			return;
		}
		
		debuggerOutputBuffer.append(data, 0, eolIndex + 1);
		handleMIEvent(debuggerOutputBuffer.toString());
		debuggerOutputBuffer.setLength(0);
		
		data = data.substring(eolIndex+1);
		handleGdbOuput(data);
	}
	
	protected void handleMIEvent(String miEventLine) {
		RecordType recordType = miParser.getRecordType(miEventLine);
		if(recordType == RecordType.ResultRecord) {
			MIResultRecord miResultRecord = miParser.parseMIResultRecord(miEventLine);
		} else if (recordType == RecordType.OOBRecord) {
			MIOOBRecord miOOBRecord = miParser.parseMIOOBRecord(miEventLine);
			if(miOOBRecord instanceof MIAsyncRecord) {
				MIAsyncRecord miAsyncRecord = (MIAsyncRecord) miOOBRecord;
				miAsyncRecord.getMIResults();
				if(miAsyncRecord.getAsyncClass().equals("thread-created")) {
					handleMIThreadCreated(miAsyncRecord);
				} else if(miAsyncRecord.getAsyncClass().equals("thread-exited")) {
					handleMIThreadDeleted(miAsyncRecord);
				}
			}
		}
	}
	
	protected void handleMIThreadCreated(MIAsyncRecord miAsyncRecord) {
		deeDebugTarget.createThread("1");
	}
	
	protected void handleMIThreadDeleted(MIAsyncRecord miAsyncRecord) {
		deeDebugTarget.threads.remove("1");
	}
	
}