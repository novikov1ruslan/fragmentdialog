package org.commons.settings;

import java.util.Map;
import java.util.Set;

import org.commons.logger.Ln;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class InternalPreferences implements SharedPreferences, SharedPreferences.Editor {

	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	
	private static Application mApplication;
	
	private static final String LOCK = new String("InternalPreferences_LOCK");
	
	public static void setContext(Application application) {
		synchronized (LOCK) {
			mApplication = application;
		}
	}

	private static final InternalPreferences INSTANCE = new InternalPreferences();

	public static InternalPreferences getInstance() {
		return INSTANCE;
	}

	private InternalPreferences() {
		sp = PreferenceManager.getDefaultSharedPreferences(mApplication);
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
		editor.commit();
		return editor;
	}

	@Override
	public boolean commit() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Editor putBoolean(String key, boolean value) {
		Ln.d("(%s, %b)", key, value);
		editor.putBoolean(key, value);
		editor.commit();
		return editor;
	}

	@Override
	public Editor putFloat(String key, float value) {
		Ln.d("(%s, %f)", key, value);
		editor.putFloat(key, value);
		editor.commit();
		return editor;
	}

	@Override
	public Editor putInt(String key, int value) {
		Ln.d("putInt: (%s, %d)", key, value);
		editor.putInt(key, value);
		editor.commit();
		return editor;
	}

	@Override
	public Editor putLong(String key, long value) {
		Ln.d("putLong: (%s, %d)", key, value);
		editor.putLong(key, value);
		editor.commit();
		return editor;
	}

	@Override
	public Editor putString(String key, String value) {
		Ln.d("putString: (%s, %s)", key, value);
		editor.putString(key, value);
		editor.commit();
		return editor;
	}

	@Override
	public Editor putStringSet(String arg0, Set<String> arg1) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Editor remove(String key) {
		Ln.v("remove: (%s)", key);
		editor.remove(key);
		editor.commit();
		return editor;
	}
}
