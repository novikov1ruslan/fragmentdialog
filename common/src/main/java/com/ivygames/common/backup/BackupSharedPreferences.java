package com.ivygames.common.backup;

import android.app.backup.BackupManager;
import android.content.Context;
import android.support.annotation.NonNull;

import com.ivygames.common.settings.EditableSharedPreferences;

public class BackupSharedPreferences extends EditableSharedPreferences {

    @NonNull
    private final BackupManager mBackupManager;

    public BackupSharedPreferences(@NonNull Context context) {
        super(context);
        mBackupManager = new BackupManager(context);
    }

    @Override
    public Editor putBoolean(String key, boolean value) {
        Editor editor = super.putBoolean(key, value);
        mBackupManager.dataChanged();
        return editor;
    }

    @Override
    public Editor putFloat(String key, float value) {
        Editor editor = super.putFloat(key, value);
        mBackupManager.dataChanged();
        return editor;
    }

    @Override
    public Editor putInt(String key, int value) {
        Editor editor = super.putInt(key, value);
        mBackupManager.dataChanged();
        return editor;
    }

    @Override
    public Editor putLong(String key, long value) {
        Editor editor = super.putLong(key, value);
        mBackupManager.dataChanged();
        return editor;
    }

    @Override
    public Editor putString(String key, String value) {
        Editor editor = super.putString(key, value);
        mBackupManager.dataChanged();
        return editor;
    }
}


