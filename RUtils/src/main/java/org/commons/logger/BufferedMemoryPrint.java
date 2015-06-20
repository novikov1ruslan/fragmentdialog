package org.commons.logger;

import org.apache.commons.collections4.collection.SynchronizedCollection;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.commons.logger.Ln.Config;
import org.commons.logger.Ln.Print;

import android.util.Log;

public class BufferedMemoryPrint extends Print {

	// The size of a single record is approximately 100b
	private static final int LOG_RECORDS_TO_BUFFER = 10000;

	private Config config;

	private SynchronizedCollection<String> queue = SynchronizedCollection.synchronizedCollection(new CircularFifoQueue<String>(LOG_RECORDS_TO_BUFFER));

	public BufferedMemoryPrint(Config config) {
		super();
		this.config = config;
		if (config.filesPath == null) {
			return;
		}
	}

	@Override
	public int println(int priority, String msg) {
		if (priority == Log.VERBOSE && priority < config.minimumLogLevel) {
			return 0;
		}

		String tag = null;
		if (priority == Log.VERBOSE) {
			tag = "";
		} else {
			tag = getScope(5);
		}
		msg = getThreadInfo() + msg;

		String threadInfo = getThreadInfo();
		// Level logLevel = priority >= Log.INFO ? Level.SEVERE : Level.FINE;
		// Log to private buffer no matter what minimum level is defined.
		// if (priority > Log.VERBOSE) {
		queue.add(createFormattedMessage(priority, tag, msg, threadInfo));
		// }

		// // if priority >= Log.INFO publish the buffer to the file (Level.SEVERE) and clear the buffer
		// if (priority >= Log.INFO) {
		// memoryHandler.push();
		// }

		// log into logcat only if minimum level is INFO and above (production level)
		return priority >= config.minimumLogLevel ? Log.println(priority, tag, threadInfo + msg) : 0;
	}

	@Override
	public void push() {
	}

}
