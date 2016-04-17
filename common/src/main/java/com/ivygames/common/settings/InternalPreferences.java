package com.ivygames.common.settings;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.commons.logger.Ln;

import java.util.Map;
import java.util.Set;

public class InternalPreferences implements SharedPreferences, SharedPreferences.Editor {

    private final SharedPreferences mSharedPreferences;
    private final SharedPreferences.Editor mEditor;

    private static volatile Application sApplication;

    public static void setContext(Application application) {
        sApplication = application;
    }

    private static final InternalPreferences INSTANCE = new InternalPreferences();

    public static InternalPreferences getInstance() {
        return INSTANCE;
    }

    private InternalPreferences() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(sApplication);
        mEditor = mSharedPreferences.edit();
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
        return mSharedPreferences.getBoolean(key, defValue);
    }

    @Override
    public float getFloat(String key, float defValue) {
        return mSharedPreferences.getFloat(key, defValue);
    }

    @Override
    public int getInt(String key, int defValue) {
        return mSharedPreferences.getInt(key, defValue);
    }

    @Override
    public long getLong(String key, long defValue) {
        return mSharedPreferences.getLong(key, defValue);
    }

    @Override
    public String getString(String key, String defValue) {
        return mSharedPreferences.getString(key, defValue);
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
        mEditor.clear();
        mEditor.commit();
        return mEditor;
    }

    @Override
    public boolean commit() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Editor putBoolean(String key, boolean value) {
        Ln.d("(%s, %b)", key, value);
        mEditor.putBoolean(key, value);
        mEditor.commit();
        return mEditor;
    }

    @Override
    public Editor putFloat(String key, float value) {
        Ln.d("(%s, %f)", key, value);
        mEditor.putFloat(key, value);
        mEditor.commit();
        return mEditor;
    }

    @Override
    public Editor putInt(String key, int value) {
        Ln.d("putInt: (%s, %d)", key, value);
        mEditor.putInt(key, value);
        mEditor.commit();
        return mEditor;
    }

    @Override
    public Editor putLong(String key, long value) {
        Ln.d("putLong: (%s, %d)", key, value);
        mEditor.putLong(key, value);
        mEditor.commit();
        return mEditor;
    }

    @Override
    public Editor putString(String key, String value) {
        Ln.d("putString: (%s, %s)", key, value);
        mEditor.putString(key, value);
        mEditor.commit();
        return mEditor;
    }

    @Override
    public Editor putStringSet(String arg0, Set<String> arg1) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Editor remove(String key) {
        Ln.v("remove: (%s)", key);
        mEditor.remove(key);
        mEditor.commit();
        return mEditor;
    }
}
