package com.ivygames.morskoiboi.ui.view;

public interface TimerViewInterface {

    /**
     * @param millis time in milliseconds
     */
    void setTotalTime(int millis);

    void setCurrentTime(int millis);

    void setAlarmThreshold(int millis);
}
