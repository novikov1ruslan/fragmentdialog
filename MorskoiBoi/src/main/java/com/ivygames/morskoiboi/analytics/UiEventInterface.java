package com.ivygames.morskoiboi.analytics;

public interface UiEventInterface {

    void screenView(String screenName);

    void send(String action);

    void send(String action, String label);

    void send(String action, int value);
}