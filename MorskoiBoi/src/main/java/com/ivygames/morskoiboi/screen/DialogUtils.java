package com.ivygames.morskoiboi.screen;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.ivygames.morskoiboi.R;
import com.ruslan.fragmentdialog.AlertDialogBuilder;
import com.ruslan.fragmentdialog.FragmentAlertDialog;

public final class DialogUtils {

    private DialogUtils() {
        // utils
    }

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

    public static FragmentAlertDialog newOkDialog(int message, final Runnable command) {
        return new AlertDialogBuilder().setMessage(message).setPositiveButton(R.string.ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                command.run();
            }
        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                command.run();
            }
        }).create();
    }

    public static void showNote(FragmentManager fm, int msgId) {
        FragmentAlertDialog.showNote(fm, FragmentAlertDialog.TAG, msgId);
    }
}