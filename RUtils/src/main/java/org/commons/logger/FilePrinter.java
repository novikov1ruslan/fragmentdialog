package org.commons.logger;

import org.apache.commons.lang3.time.FastDateFormat;

import java.util.Calendar;

public class FilePrinter extends Print {
    private final FastDateFormat dateFormat;

    private final Calendar calendar;

    public FilePrinter(WarningListener warningListener) {
        super(warningListener);

        dateFormat = FastDateFormat.getInstance("HH:mm:ss.SSS");
        calendar = Calendar.getInstance();
    }

    protected String createFormattedMessage(int priority, String tag, String msg, String threadInfo) {
        calendar.setTimeInMillis(System.currentTimeMillis());
        String time = dateFormat.format(calendar);
        char level = LoggerImpl.logLevelToChar(priority);
        String pid = Integer.toString(android.os.Process.myPid());
        // 7 extra chars: level + ' ' + '\n' + '\t'. Note, that threadInfo includes trailing space
//			int capacity = time.length() + tag.length() + msg.length() + 7 + pid.length() + threadInfo.length();

        // format logs like logcat
        return time + ' ' + level + ' ' + pid + ' ' + threadInfo + tag + '\t' + msg + '\n';
    }
}
