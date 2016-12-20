package com.ivygames.common.backup;

import android.app.backup.BackupManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import org.commons.logger.Ln;

import java.util.Set;

class BackupEditor implements SharedPreferences.Editor {

    @NonNull
    private final BackupManager mBackupManager;
    @NonNull
    private final SharedPreferences.Editor mEditor;

    BackupEditor(@NonNull Context context, @NonNull SharedPreferences.Editor editor) {
        mBackupManager = new BackupManager(context);
        mEditor = editor;
    }

    @Override
    public SharedPreferences.Editor putBoolean(String key, boolean value) {
        mEditor.putBoolean(key, value);
        mBackupManager.dataChanged();
        return this;
    }

    @Override
    public SharedPreferences.Editor putFloat(String key, float value) {
        mEditor.putFloat(key, value);
        mBackupManager.dataChanged();
        return this;
    }

    @Override
    public SharedPreferences.Editor putInt(String key, int value) {
        mEditor.putInt(key, value);
        mBackupManager.dataChanged();
        return this;
    }

    @Override
    public SharedPreferences.Editor putLong(String key, long value) {
        mEditor.putLong(key, value);
        mBackupManager.dataChanged();
        return this;
    }

    @Override
    public SharedPreferences.Editor putString(String key, String value) {
        mEditor.putString(key, value);
        mBackupManager.dataChanged();
        return this;
    }

    @Override
    public SharedPreferences.Editor putStringSet(String key, Set<String> values) {
        Ln.e("not implemented");
        return this;
    }

    @Override
    public SharedPreferences.Editor remove(String key) {
        Ln.e("not implemented");
        return this;
    }

    @Override
    public SharedPreferences.Editor clear() {
        Ln.i("debug - clearing settings");
        mEditor.clear();
        return this;
    }

    @Override
    public boolean commit() {
        boolean commit = mEditor.commit();
        mBackupManager.dataChanged();
        return commit;
    }

    @Override
    public void apply() {
        mEditor.apply();
        mBackupManager.dataChanged();
    }
}
