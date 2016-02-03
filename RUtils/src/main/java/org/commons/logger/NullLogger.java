package org.commons.logger;

final class NullLogger implements Logger {

    @Override
    public int v(Throwable t) {
        return 0;
    }

    @Override
    public int v(Object s1, Object... args) {
        return 0;
    }

    @Override
    public int v(Throwable throwable, Object s1, Object... args) {
        return 0;
    }

    @Override
    public int d(Throwable t) {
        return 0;
    }

    @Override
    public int d(Object s1, Object... args) {
        return 0;
    }

    @Override
    public int d(Throwable throwable, Object s1, Object... args) {
        return 0;
    }

    @Override
    public int i(Throwable t) {
        return 0;
    }

    @Override
    public int i(Object s1, Object... args) {
        return 0;
    }

    @Override
    public int i(Throwable throwable, Object s1, Object... args) {
        return 0;
    }

    @Override
    public int w(Throwable t) {
        return 0;
    }

    @Override
    public int w(Object s1, Object... args) {
        return 0;
    }

    @Override
    public int w(Throwable throwable, Object s1, Object... args) {
        return 0;
    }

    @Override
    public int e(Throwable t) {
        return 0;
    }

    @Override
    public int e(Object s1, Object... args) {
        return 0;
    }

    @Override
    public int e(Throwable throwable, Object s1, Object... args) {
        return 0;
    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public boolean isVerboseEnabled() {
        return false;
    }

    @Override
    public int getLogLevel() {
        return 0;
    }

    @Override
    public void push() {

    }
}
