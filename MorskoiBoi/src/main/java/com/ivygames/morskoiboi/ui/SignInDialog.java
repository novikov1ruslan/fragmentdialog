package com.ivygames.morskoiboi.ui;

import com.ivygames.morskoiboi.R;
import com.ruslan.fragmentdialog.AlertDialogBuilder;
import com.ruslan.fragmentdialog.FragmentAlertDialog;

public class SignInDialog extends FragmentAlertDialog {

	@Override
	protected int getLayout() {
		return R.layout.sign_in_dialog;
	}

	public static class Builder extends AlertDialogBuilder {

		@Override
		protected FragmentAlertDialog createInternal() {
			return new SignInDialog();
		}
	}
}
