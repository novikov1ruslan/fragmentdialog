package org.commons.logger;

import android.util.Log;
import android.util.SparseArray;

import org.apache.commons.lang3.time.FastDateFormat;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A more natural android logging facility.
 * <p/>
 * WARNING: CHECK OUT COMMON PITFALLS BELOW
 * <p/>
 * Unlike {@link android.util.Log}, Log provides sensible defaults. Debug and Verbose logging is enabled for applications that have "android:debuggable=true" in
 * their AndroidManifest.xml. For apps built using SDK Tools r8 or later, this means any debug build. Release builds built with r8 or later will have verbose
 * and debug log messages turned off.
 * <p/>
 * The default tag is automatically set to your app's package name, and the current context (eg. activity, service, application, etc) is appended as well. You
 * can add an additional parameter to the tag using {@link #Log(String)}.
 * <p/>
 * Log-levels can be programmatically overridden for specific instances using {@link #Log(String, boolean, boolean)}.
 * <p/>
 * Log messages may optionally use {@link String#format(String, Object...)} formatting, which will not be evaluated unless the log statement is output.
 * Additional parameters to the logging statement are treated as varrgs parameters to {@link String#format(String, Object...)}
 * <p/>
 * Also, the current file and line is automatically appended to the tag (this is only done if debug is enabled for performance reasons).
 * <p/>
 * COMMON PITFALLS: * Make sure you put the exception FIRST in the call. A common mistake is to place it last as is the android.util.Log convention, but then it
 * will get treated as varargs parameter. * vararg parameters are not appended to the log message! You must insert them into the log message using %s or another
 * similar format parameter
 * <p/>
 * Usage Examples:
 * <p/>
 * Ln.v("hello there"); Ln.d("%s %s", "hello", "there"); Ln.e( exception, "Error during some operation"); Ln.w( exception, "Error during %s operation",
 * "some other");
 */
public class Ln {
    /**
     * Caches file/line scopes for fast logging
     */
    private static final Map<String, SparseArray<String>> SCOPES = new ConcurrentHashMap<String, SparseArray<String>>();

    private static final ThreadLocal<String> threadName = new ThreadLocal<String>();

    /**
     * config is initially set to BaseConfig() with sensible defaults, then replaced by BaseConfig(ContextSingleton) during guice static injection pass.
     */
    private static Config config;

    /**
     * print is initially set to Print(), then replaced by guice during static injection pass. This allows overriding where the log message is delivered to.
     */
    private static final Print print = new Print();

    private static volatile WarningListener sWarningListener;

    public static void setConfiguration(Config config) {
        // print = new CircularFifoPrinter(config);
        Ln.config = config;
        // print = new BufferedFilePrint(config);
    }

    private Ln() {
    }

    public static void setWarningListener(WarningListener listener) {
        sWarningListener = listener;
    }

    public static int v(Throwable t) {
        return print.println(Log.VERBOSE, Log.getStackTraceString(t));
    }

    public static int v(Object s1, Object... args) {
        final String s = Strings.toString(s1);
        final String message = args.length > 0 ? String.format(s, args) : s;
        return print.println(Log.VERBOSE, message);
    }

    public static int v(Throwable throwable, Object s1, Object... args) {
        final String s = Strings.toString(s1);
        final String message = (args.length > 0 ? String.format(s, args) : s) + '\n' + Log.getStackTraceString(throwable);
        return print.println(Log.VERBOSE, message);
    }

    public static int d(Throwable t) {
        return print.println(Log.DEBUG, Log.getStackTraceString(t));
    }

    public static int d(Object s1, Object... args) {
        final String s = Strings.toString(s1);
        final String message = args.length > 0 ? String.format(s, args) : s;
        return print.println(Log.DEBUG, message);
    }

    public static int d(Throwable throwable, Object s1, Object... args) {
        final String s = Strings.toString(s1);
        final String message = (args.length > 0 ? String.format(s, args) : s) + '\n' + Log.getStackTraceString(throwable);
        return print.println(Log.DEBUG, message);
    }

    public static int i(Throwable t) {
        return print.println(Log.INFO, Log.getStackTraceString(t));
    }

    public static int i(Object s1, Object... args) {
        final String s = Strings.toString(s1);
        final String message = args.length > 0 ? String.format(s, args) : s;
        return print.println(Log.INFO, message);
    }

    public static int i(Throwable throwable, Object s1, Object... args) {
        final String s = Strings.toString(s1);
        final String message = (args.length > 0 ? String.format(s, args) : s) + '\n' + Log.getStackTraceString(throwable);
        return print.println(Log.INFO, message);
    }

    public static int w(Throwable t) {
        return print.println(Log.WARN, Log.getStackTraceString(t));
    }

    public static int w(Object s1, Object... args) {
        final String s = Strings.toString(s1);
        final String message = args.length > 0 ? String.format(s, args) : s;
        return print.println(Log.WARN, message);
    }

    public static int w(Throwable throwable, Object s1, Object... args) {
        final String s = Strings.toString(s1);
        final String message = (args.length > 0 ? String.format(s, args) : s) + '\n' + Log.getStackTraceString(throwable);
        return print.println(Log.WARN, message);
    }

    public static int e(Throwable t) {
        return print.println(Log.ERROR, Log.getStackTraceString(t));
    }

    public static int e(Object s1, Object... args) {
        final String s = Strings.toString(s1);
        final String message = args.length > 0 ? String.format(s, args) : s;
        return print.println(Log.ERROR, message);
    }

    public static int e(Throwable throwable, Object s1, Object... args) {
        final String s = Strings.toString(s1);
        final String message = (args.length > 0 ? String.format(s, args) : s) + '\n' + Log.getStackTraceString(throwable);
        return print.println(Log.ERROR, message);
    }

    public static boolean isDebugEnabled() {
        return config.minimumLogLevel <= Log.DEBUG;
    }

    public static boolean isVerboseEnabled() {
        return config.minimumLogLevel <= Log.VERBOSE;
    }

    public static class Config {
        protected final int minimumLogLevel;
        protected final String filesPath;
        public final String filePrefix;

        public Config(int minLogLevel, String filesPath, String filePrefix) {
            minimumLogLevel = minLogLevel;
            this.filesPath = filesPath;
            this.filePrefix = filePrefix;
        }
    }

    public static int getLogLevel() {
        return config.minimumLogLevel;
    }

    public static String logLevelToString(int logLevel) {
        switch (logLevel) {
            case Log.VERBOSE:
                return "VERBOSE";
            case Log.DEBUG:
                return "DEBUG";
            case Log.INFO:
                return "INFO";
            case Log.WARN:
                return "WARN";
            case Log.ERROR:
                return "ERROR";
            case Log.ASSERT:
                return "ASSERT";
        }

        return "UNKNOWN";
    }

    public static char logLevelToChar(int loglevel) {
        switch (loglevel) {
            case Log.VERBOSE:
                return 'V';
            case Log.DEBUG:
                return 'D';
            case Log.INFO:
                return 'I';
            case Log.WARN:
                return 'W';
            case Log.ERROR:
                return 'E';
            case Log.ASSERT:
                return 'A';
        }

        return 'U';
    }

    public static void push() {
        print.push();
    }

    /**
     * Default implementation logs to android.util.Log
     */
    public static class Print {

        private final FastDateFormat dateFormat;

        private final Calendar calendar;

        public Print() {
            dateFormat = FastDateFormat.getInstance("HH:mm:ss.SSS");
            calendar = Calendar.getInstance();
        }

        public int println(int priority, String msg) {
            int ret = Log.println(priority, getScope(5), getThreadInfo() + msg);
            if (sWarningListener != null && priority >= Log.WARN) {
                sWarningListener.onWaring(msg, priority);
            }
            return ret;
        }

        public void push() {

        }

        protected String createFormattedMessage(int priority, String tag, String msg, String threadInfo) {
            calendar.setTimeInMillis(System.currentTimeMillis());
            String time = dateFormat.format(calendar);
            char level = Ln.logLevelToChar(priority);
            String pid = Integer.toString(android.os.Process.myPid());
            // 7 extra chars: level + ' ' + '\n' + '\t'. Note, that threadInfo includes trailing space
//			int capacity = time.length() + tag.length() + msg.length() + 7 + pid.length() + threadInfo.length();

            // format logs like logcat
            return time + ' ' + level + ' ' + pid + ' ' + threadInfo + tag + '\t' + msg + '\n';
        }

        protected String getThreadInfo() {
            // msg = String.format("[%s(%s)] %s", Thread.currentThread().getName(), Thread.currentThread().getId(),
            // msg);
            // msg = "[" + Thread.currentThread().getName() + "(" + Thread.currentThread().getId() + ")" +
            // Process.getThreadPriority(Process.myTid()) + "] " + msg;

            // StringBuilder stringBuilder = sb.get();
            // if (stringBuilder == null) {
            // stringBuilder = new StringBuilder(100);
            // sb.set(stringBuilder);
            // }

            String name = threadName.get();
            if (name == null) {
                StringBuilder stringBuilder = new StringBuilder(32);
                // stringBuilder.setLength(0);
                Thread curThread = Thread.currentThread();
                stringBuilder.append('[').append(curThread.getName()).append('(').append(curThread.getId()).append(")] ");
                name = stringBuilder.toString();
                threadName.set(name);
            }

            return name;
        }

        protected static String getScope(int skipDepth) {
            final StackTraceElement trace = Thread.currentThread().getStackTrace()[skipDepth];

            String fileName = trace.getFileName();
            SparseArray<String> fileScope = SCOPES.get(fileName);
            if (fileScope == null) {
                fileScope = new SparseArray<String>();
                SCOPES.put(fileName, fileScope);
            }

            int lineNumber = trace.getLineNumber();
            String scope = fileScope.get(lineNumber);
            if (scope == null) {
                scope = fileName.substring(0, fileName.indexOf('.')) + ":" + lineNumber + ":" + trace.getMethodName();
                fileScope.put(lineNumber, scope);
            }

            return scope;
        }
    }

}
