package com.ivygames.morskoiboi.screen.gameplay;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.screen.view.NotepadLinearLayout;

public class ChatDialogLayout extends NotepadLinearLayout {

    private EditText mChatText;
    private ListView mMessagesList;

    public ChatDialogLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mChatText = (EditText) findViewById(R.id.message_text);
        mMessagesList = (ListView) findViewById(R.id.messages_list);
    }

    public CharSequence getChatMessage() {
        return mChatText.getText();
    }

    public void setAdapter(ListAdapter adapter) {
        mMessagesList.setAdapter(adapter);
        mMessagesList.setSelection(adapter.getCount() - 1);
    }
}
