package com.ivygames.common.dialog;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.ivygames.common.R;
import com.ruslan.fragmentdialog.AlertDialogBuilder;
import com.ruslan.fragmentdialog.FragmentAlertDialog;

public final class DialogUtils {

    private DialogUtils() {
        // utils
    }

    public static FragmentAlertDialog newOkCancelDialog(@StringRes int message, @NonNull final Runnable command) {
        return new AlertDialogBuilder().setMessage(message).setPositiveButton(R.string.ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                command.run();
            }
        }).setNegativeButton(R.string.cancel).create();
    }

    public static FragmentAlertDialog newOkCancelDialog(@NonNull String message, @NonNull final Runnable command) {
        return new AlertDialogBuilder().setMessage(message).setPositiveButton(R.string.ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                command.run();
            }
        }).setNegativeButton(R.string.cancel).create();
    }

    public static FragmentAlertDialog newOkDialog(@StringRes int message, @NonNull final Runnable command) {
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

    public static void showNote(@NonNull FragmentManager fm, int msgId) {
        FragmentAlertDialog.showNote(fm, FragmentAlertDialog.TAG, msgId);
    }

    public static FragmentAlertDialog twoOptions(@NonNull String message,
                                                 @NonNull String option1, @NonNull final Runnable command1,
                                                 @NonNull String option2, @NonNull final Runnable command2) {
        OnClickListener listener1 = new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                command1.run();
            }
        };
        OnClickListener listener2 = new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                command2.run();
            }
        };
        return new AlertDialogBuilder().setMessage(message)
                .setPositiveButton(option1, listener1)
                .setNegativeButton(option2, listener2).create();
    }
}