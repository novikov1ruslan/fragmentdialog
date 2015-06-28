package com.ivygames.morskoiboi.model;

public class ChatMessage {

    private boolean mMe;
    private final String mText;

    public static ChatMessage newMyMessage(String text) {
        ChatMessage message = new ChatMessage(text);
        message.mMe = true;
        return message;
    }

    public static ChatMessage newEnemyMessage(String text) {
        return new ChatMessage(text);
    }

    private ChatMessage(String text) {
        mText = text.replaceAll("\n", "");
    }

    public String getText() {
        return mText;
    }

    public boolean isMe() {
        return mMe;
    }

    @Override
    public String toString() {
        return mText;
    }
}
