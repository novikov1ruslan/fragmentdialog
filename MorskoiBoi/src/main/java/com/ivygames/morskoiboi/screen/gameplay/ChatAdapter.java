package com.ivygames.morskoiboi.screen.gameplay;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ivygames.morskoiboi.R;
import com.ivygames.battleship.ChatMessage;

import java.util.ArrayList;
import java.util.List;

class ChatAdapter extends BaseAdapter {
    private final List<ChatMessage> mMessages = new ArrayList<>();
    private final LayoutInflater mInflater;

    ChatAdapter(LayoutInflater inflater) {
        mInflater = inflater;
    }

    public void add(ChatMessage message) {
        mMessages.add(message);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mMessages.size();
    }

    @Override
    public ChatMessage getItem(int position) {
        return mMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage message = getItem(position);
        if (convertView == null) {
            int resource = message.isMe ? R.layout.my_message_entry : R.layout.enemy_message_entry;
            convertView = mInflater.inflate(resource, parent, false);
        }

        TextView messageView = (TextView) convertView;
        messageView.setText(message.text);

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).isMe ? 0 : 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

}
