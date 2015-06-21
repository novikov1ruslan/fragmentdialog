package org.commons.logger;

import android.util.Log;

import org.commons.logger.Ln.Config;
import org.commons.logger.Ln.Print;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.MemoryHandler;

public class BufferedFilePrint extends Print {
	/**
	 * name pattern and location of log files and their pattern
	 */
	private static final String PATTERN = "%u.%g.log";

	/**
	 * default size of each file in bytes
	 */
	private static final int LOG_FILE_SIZE = 2000000;

	//The size of a single record is approximately 100b
	private static final int LOG_RECORDS_TO_BUFFER = 10000;

	/**
	 * number of log files to use
	 */
	private static final int LOG_COUNT = 1;

	private final Logger logger;

	private MemoryHandler memoryHandler;

	private final Config config;

	public BufferedFilePrint(Config config) {
		super();
		this.config = config;
		logger = Logger.getLogger(getClass().getSimpleName());
		logger.setLevel(Level.ALL);
		Formatter formatter = new NoFormatter();
		if (config.filesPath == null) {
			return;
		}
		
		try {
			FileHandler fileHandler = new FileHandler(config.filesPath + '/' + config.filePrefix + PATTERN, LOG_FILE_SIZE, LOG_COUNT, true);
			fileHandler.setFormatter(formatter);
			memoryHandler = new MemoryHandler(fileHandler, LOG_RECORDS_TO_BUFFER, Level.OFF);
			memoryHandler.setFormatter(formatter);
			logger.addHandler(memoryHandler);
			// logger.setUseParentHandlers(false);
		} catch (IOException ioe) {
			Log.w(getClass().getSimpleName(), ioe);
		}
	}

	@Override
	public int println(int priority, String msg) {
		if (priority == Log.VERBOSE && priority < config.minimumLogLevel) {
			return 0;
		}
		
		String tag;
		if (priority == Log.VERBOSE) {
			tag = "";
		}
		else {
			tag = getScope(5);
		}
		msg = getThreadInfo() + msg;

		String threadInfo = getThreadInfo();
		// Level logLevel = priority >= Log.INFO ? Level.SEVERE : Level.FINE;
		// Log to private buffer no matter what minimum level is defined.
		// if (priority > Log.VERBOSE) {
		logger.log(Level.ALL, createFormattedMessage(priority, tag, msg, threadInfo));
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
		memoryHandler.push();
	}

	private static class NoFormatter extends Formatter {

		@Override
		public String format(LogRecord record) {
			return record.getMessage();
		}

	}
}
