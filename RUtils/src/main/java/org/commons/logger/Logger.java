package org.commons.logger;

public interface Logger {

    int v(Throwable t);

    int v(Object s1, Object... args);

    int v(Throwable throwable, Object s1, Object... args);

    int d(Throwable t);

    int d(Object s1, Object... args);

    int d(Throwable throwable, Object s1, Object... args);

    int i(Throwable t);

    int i(Object s1, Object... args);

    int i(Throwable throwable, Object s1, Object... args);

    int w(Throwable t);

    int w(Object s1, Object... args);

    int w(Throwable throwable, Object s1, Object... args);

    int e(Throwable t);

    int e(Object s1, Object... args);

    int e(Throwable throwable, Object s1, Object... args);

    boolean isDebugEnabled();

    boolean isVerboseEnabled();

    int getLogLevel();

    void push();

}
