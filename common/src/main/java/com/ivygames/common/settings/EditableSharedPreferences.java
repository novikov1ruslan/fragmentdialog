package com.ivygames.common.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import org.commons.logger.Ln;

import java.util.Map;
import java.util.Set;

public class EditableSharedPreferences implements SharedPreferences, SharedPreferences.Editor {

    private final SharedPreferences sp;
    private final Editor editor;

    @SuppressLint("CommitPrefEdits")
    public EditableSharedPreferences(@NonNull Context context) {
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sp.edit();
    }

    @Override
    public boolean contains(String key) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Editor edit() {
        return this;
    }

    @Override
    public Map<String, ?> getAll() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return sp.getBoolean(key, defValue);
    }

    @Override
    public float getFloat(String key, float defValue) {
        return sp.getFloat(key, defValue);
    }

    @Override
    public int getInt(String key, int defValue) {
        return sp.getInt(key, defValue);
    }

    @Override
    public long getLong(String key, long defValue) {
        return sp.getLong(key, defValue);
    }

    @Override
    public String getString(String key, String defValue) {
        return sp.getString(key, defValue);
    }

    @Override
    public Set<String> getStringSet(String arg0, Set<String> arg1) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void apply() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Editor clear() {
        editor.clear();
        editor.apply();
        return editor;
    }

    @Override
    public boolean commit() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Editor putBoolean(String key, boolean value) {
        Ln.d("<-- (" + key + ',' + value + ')');
        editor.putBoolean(key, value);
        editor.apply();
        return editor;
    }

    @Override
    public Editor putFloat(String key, float value) {
        Ln.d("<-- (" + key + ',' + value + ')');
        editor.putFloat(key, value);
        editor.apply();
        return editor;
    }

    @Override
    public Editor putInt(String key, int value) {
        Ln.d("<-- (" + key + ',' + value + ')');
        editor.putInt(key, value);
        editor.apply();
        return editor;
    }

    @Override
    public Editor putLong(String key, long value) {
        Ln.d("<-- (" + key + ',' + value + ')');
        editor.putLong(key, value);
        editor.apply();
        return editor;
    }

    @Override
    public Editor putString(String key, String value) {
        Ln.d("<-- (" + key + ',' + value + ')');
        editor.putString(key, value);
        editor.apply();
        return editor;
    }

    @Override
    public Editor putStringSet(String arg0, Set<String> arg1) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Editor remove(String key) {
        // Ln.d('(' + key + ')');
        // editor.remove(key);
        // editor.apply();
        Ln.e("remove is not needed in this game");
        return editor;
    }
}
