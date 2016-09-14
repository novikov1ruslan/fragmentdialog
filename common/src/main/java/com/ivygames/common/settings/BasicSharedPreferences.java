package com.ivygames.common.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import java.util.Map;
import java.util.Set;

public abstract class BasicSharedPreferences implements SharedPreferences {

    private final SharedPreferences sp;

    public BasicSharedPreferences(@NonNull Context context) {
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public Editor edit() {
        return sp.edit();
    }

    @Override
    public boolean contains(String key) {
        throw new RuntimeException("Not implemented");
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

}
