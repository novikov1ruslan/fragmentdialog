package com.ivygames.morskoiboi.screen;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;

import com.ivygames.morskoiboi.R;
import com.ruslan.fragmentdialog.AlertDialogBuilder;
import com.ruslan.fragmentdialog.FragmentAlertDialog;

public class SimpleActionDialog {

    public static FragmentAlertDialog create(int stringRes, final Runnable command) {
        return SimpleActionDialog.create(stringRes, R.string.ok, command);
    }

    public static FragmentAlertDialog create(int messageStringRes, final int buttonStringRes, final Runnable command) {
        return new AlertDialogBuilder().setMessage(messageStringRes).setPositiveButton(buttonStringRes, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                command.run();
            }
        }).setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                command.run();
            }
        }).create();
    }

}
