package com.ivygames.morskoiboi.screen.gameplay;

interface TimerViewInterface {

    /**
     * @param millis time in milliseconds
     */
    void setTotalTime(int millis);

    void setCurrentTime(int millis);

    void setAlarmThreshold(int millis);
}
