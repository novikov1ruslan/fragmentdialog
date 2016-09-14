package com.ivygames.common.settings;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import org.commons.logger.Ln;

import java.util.Set;

public class AutocommitEditor implements SharedPreferences.Editor {

    @NonNull
    private final SharedPreferences.Editor mEditor;

    public AutocommitEditor(@NonNull SharedPreferences.Editor editor) {
        mEditor = editor;
    }

    @Override
    public void apply() {
        mEditor.apply();
    }

    @Override
    public SharedPreferences.Editor clear() {
        mEditor.clear();
        mEditor.apply();
        return this;
    }

    @Override
    public boolean commit() {
        return mEditor.commit();
    }

    @Override
    public SharedPreferences.Editor putBoolean(String key, boolean value) {
        Ln.d("<-- (" + key + ',' + value + ')');
        mEditor.putBoolean(key, value);
        mEditor.apply();
        return this;
    }

    @Override
    public SharedPreferences.Editor putFloat(String key, float value) {
        Ln.d("<-- (" + key + ',' + value + ')');
        mEditor.putFloat(key, value);
        mEditor.apply();
        return this;
    }

    @Override
    public SharedPreferences.Editor putInt(String key, int value) {
        Ln.d("<-- (" + key + ',' + value + ')');
        mEditor.putInt(key, value);
        mEditor.apply();
        return this;
    }

    @Override
    public SharedPreferences.Editor putLong(String key, long value) {
        Ln.d("<-- (" + key + ',' + value + ')');
        mEditor.putLong(key, value);
        mEditor.apply();
        return this;
    }

    @Override
    public SharedPreferences.Editor putString(String key, String value) {
        Ln.d("<-- (" + key + ',' + value + ')');
        mEditor.putString(key, value);
        mEditor.apply();
        return this;
    }

    @Override
    public SharedPreferences.Editor putStringSet(String arg0, Set<String> arg1) {
        Ln.w("Not implemented");
        return this;
    }

    @Override
    public SharedPreferences.Editor remove(String key) {
        // Ln.d('(' + key + ')');
        // editor.remove(key);
        // editor.apply();
        Ln.w("Not implemented, and not needed");
        return this;
    }
}
