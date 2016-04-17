package org.commons.logger;

import android.util.Log;


public class LoggerImpl implements Logger {

    private static Config config;

    private final Print print;

    public LoggerImpl(Config config, WarningListener warningListener) {
        LoggerImpl.config = config;
        print = new Print(warningListener);
    }

    @Override
    public int v(Throwable t) {
        return print.println(Log.VERBOSE, Log.getStackTraceString(t));
    }

    @Override
    public int v(Object s1, Object... args) {
        final String s = Strings.toString(s1);
        final String message = args.length > 0 ? String.format(s, args) : s;
        return print.println(Log.VERBOSE, message);
    }

    @Override
    public int v(Throwable throwable, Object s1, Object... args) {
        final String s = Strings.toString(s1);
        final String message = (args.length > 0 ? String.format(s, args) : s) + '\n' + Log.getStackTraceString(throwable);
        return print.println(Log.VERBOSE, message);
    }

    @Override
    public int d(Throwable t) {
        return print.println(Log.DEBUG, Log.getStackTraceString(t));
    }

    @Override
    public int d(Object s1, Object... args) {
        final String s = Strings.toString(s1);
        final String message = args.length > 0 ? String.format(s, args) : s;
        return print.println(Log.DEBUG, message);
    }

    @Override
    public int d(Throwable throwable, Object s1, Object... args) {
        final String s = Strings.toString(s1);
        final String message = (args.length > 0 ? String.format(s, args) : s) + '\n' + Log.getStackTraceString(throwable);
        return print.println(Log.DEBUG, message);
    }

    @Override
    public int i(Throwable t) {
        return print.println(Log.INFO, Log.getStackTraceString(t));
    }

    @Override
    public int i(Object s1, Object... args) {
        final String s = Strings.toString(s1);
        final String message = args.length > 0 ? String.format(s, args) : s;
        return print.println(Log.INFO, message);
    }

    @Override
    public int i(Throwable throwable, Object s1, Object... args) {
        final String s = Strings.toString(s1);
        final String message = (args.length > 0 ? String.format(s, args) : s) + '\n' + Log.getStackTraceString(throwable);
        return print.println(Log.INFO, message);
    }

    @Override
    public int w(Throwable t) {
        return print.println(Log.WARN, Log.getStackTraceString(t));
    }

    @Override
    public int w(Object s1, Object... args) {
        final String s = Strings.toString(s1);
        final String message = args.length > 0 ? String.format(s, args) : s;
        return print.println(Log.WARN, message);
    }

    @Override
    public int w(Throwable throwable, Object s1, Object... args) {
        final String s = Strings.toString(s1);
        final String message = (args.length > 0 ? String.format(s, args) : s) + '\n' + Log.getStackTraceString(throwable);
        return print.println(Log.WARN, message);
    }

    @Override
    public int e(Throwable t) {
        return print.println(Log.ERROR, Log.getStackTraceString(t));
    }

    @Override
    public int e(Object s1, Object... args) {
        final String s = Strings.toString(s1);
        final String message = args.length > 0 ? String.format(s, args) : s;
        return print.println(Log.ERROR, message);
    }

    @Override
    public int e(Throwable throwable, Object s1, Object... args) {
        final String s = Strings.toString(s1);
        final String message = (args.length > 0 ? String.format(s, args) : s) + '\n' + Log.getStackTraceString(throwable);
        return print.println(Log.ERROR, message);
    }

    @Override
    public boolean isDebugEnabled() {
        return config.minimumLogLevel <= Log.DEBUG;
    }

    @Override
    public boolean isVerboseEnabled() {
        return config.minimumLogLevel <= Log.VERBOSE;
    }

    @Override
    public int getLogLevel() {
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

    public static char logLevelToChar(int logLevel) {
        switch (logLevel) {
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

    @Override
    public void push() {
        print.push();
    }

}
