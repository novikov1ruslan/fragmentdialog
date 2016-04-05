package org.commons.logger;

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
 * can add an additional parameter to the tag using {@link #Ln(String)}.
 * <p/>
 * Log-levels can be programmatically overridden for specific instances using {@link #Ln(String, boolean, boolean)}.
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

    private Ln() {
    }

    private static Logger sLogger = new NullLogger();

    public static void injectLogger(Logger logger) {
        sLogger = logger;
    }

    public static int v(Throwable t) {
        return sLogger.v(t);
    }

    public static int v(Object s1, Object... args) {
        return sLogger.v(s1, args);
    }

    public static int v(Throwable throwable, Object s1, Object... args) {
        return sLogger.v(throwable, s1, args);
    }

    public static int d(Throwable t) {
        return sLogger.d(t);
    }

    public static int d(Object s1, Object... args) {
        return sLogger.d(s1, args);
    }

    public static int d(Throwable throwable, Object s1, Object... args) {
        return sLogger.d(throwable, s1, args);
    }

    public static int i(Throwable t) {
        return sLogger.i(t);
    }

    public static int i(Object s1, Object... args) {
        return sLogger.i(s1, args);
    }

    public static int i(Throwable throwable, Object s1, Object... args) {
        return sLogger.i(throwable, s1, args);

    }

    public static int w(Throwable t) {
        return sLogger.w(t);
    }

    public static int w(Object s1, Object... args) {
        return sLogger.w(s1, args);
    }

    public static int w(Throwable throwable, Object s1, Object... args) {
        return sLogger.w(throwable, s1, args);

    }

    public static int e(Throwable t) {
        return sLogger.e(t);
    }

    public static int e(Object s1, Object... args) {
        return sLogger.e(s1, args);
    }

    public static int e(Throwable throwable, Object s1, Object... args) {
        return sLogger.e(throwable, s1, args);

    }

    public static boolean isDebugEnabled() {
        return sLogger.isDebugEnabled();
    }

    public static boolean isVerboseEnabled() {
        return sLogger.isVerboseEnabled();
    }

    public static int getLogLevel() {
        return sLogger.getLogLevel();
    }

    public static void push() {
        sLogger.push();
    }

}
