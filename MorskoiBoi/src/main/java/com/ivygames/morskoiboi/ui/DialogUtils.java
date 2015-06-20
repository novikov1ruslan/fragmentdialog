package com.ivygames.morskoiboi.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.ivygames.morskoiboi.R;
import com.ruslan.fragmentdialog.AlertDialogBuilder;
import com.ruslan.fragmentdialog.FragmentAlertDialog;

class DialogUtils {

	public static FragmentAlertDialog newOkCancelDialog(int message, final Runnable command) {
		return new AlertDialogBuilder().setMessage(message).setPositiveButton(R.string.ok, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				command.run();
			}
		}).setNegativeButton(R.string.cancel).create();
	}

	public static FragmentAlertDialog newOkCancelDialog(String message, final Runnable command) {
		return new AlertDialogBuilder().setMessage(message).setPositiveButton(R.string.ok, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				command.run();
			}
		}).setNegativeButton(R.string.cancel).create();
	}

	public static FragmentAlertDialog newOkDialog(String message, final Runnable command) {
		return new AlertDialogBuilder().setMessage(message).setPositiveButton(R.string.ok, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				command.run();
			}
		}).create();
	}

	public static FragmentAlertDialog newOkDialog(int message, final Runnable command) {
		return new AlertDialogBuilder().setMessage(message).setPositiveButton(R.string.ok, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				command.run();
			}
		}).create();
	}
}
