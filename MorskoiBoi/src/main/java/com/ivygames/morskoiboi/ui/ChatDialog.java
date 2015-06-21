package com.ivygames.morskoiboi.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.ui.view.ChatAdapter;
import com.ivygames.morskoiboi.ui.view.ChatDialogLayout;
import com.ruslan.fragmentdialog.AlertDialogBuilder;
import com.ruslan.fragmentdialog.FragmentAlertDialog;

import org.apache.commons.lang3.Validate;
import org.commons.logger.Ln;

public class ChatDialog extends FragmentAlertDialog {

	private final ChatAdapter mChatAdapter;

	public ChatDialog() {
		this(null);
	}

	public ChatDialog(ChatAdapter adapter) {
		super();
		mChatAdapter = Validate.notNull(adapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			Ln.i("dialog recreating");
			return null;
		}

		ChatDialogLayout layout = (ChatDialogLayout) super.onCreateView(inflater, container, null);
		layout.setAdapter(mChatAdapter);
		return layout;
	}

	@Override
	protected int getLayout() {
		return R.layout.chat_dialog;
	}

	public CharSequence getChatMessage() {
		return ((ChatDialogLayout) getContentView()).getChatMessage();
	}

	public static class Builder extends AlertDialogBuilder {

		private final ChatAdapter mChatAdapter;

		public Builder(ChatAdapter adapter) {
			mChatAdapter = adapter;
		}

		public Builder setName(CharSequence name) {
			setMessage(name + ":");
			return this;
		}

		@Override
		protected FragmentAlertDialog createInternal() {
			return new ChatDialog(mChatAdapter);
		}
	}
}
