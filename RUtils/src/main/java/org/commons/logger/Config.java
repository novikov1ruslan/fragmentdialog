package org.commons.logger;

public class Config {
    protected final int minimumLogLevel;
    protected final String filesPath;
    public final String filePrefix;

    public Config(int minLogLevel, String filesPath, String filePrefix) {
        minimumLogLevel = minLogLevel;
        this.filesPath = filesPath;
        this.filePrefix = filePrefix;
    }
}
