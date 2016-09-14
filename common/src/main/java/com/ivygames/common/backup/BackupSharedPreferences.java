package com.ivygames.common.backup;

import android.content.Context;
import android.support.annotation.NonNull;

import com.ivygames.common.settings.AutocommitEditor;
import com.ivygames.common.settings.BasicSharedPreferences;

public class BackupSharedPreferences extends BasicSharedPreferences {

    @NonNull
    private final Context mContext;

    public BackupSharedPreferences(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public Editor edit() {
        return new BackupEditor(mContext, new AutocommitEditor(super.edit()));
    }

}
