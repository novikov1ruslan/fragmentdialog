package com.ivygames.common.backup;

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;

import org.commons.logger.Ln;

public class BackupAgent extends BackupAgentHelper {
    // A key to uniquely identify the set of backup data
    private static final String PREFS_BACKUP_KEY = "prefs";

    // Allocate a helper and add it to the backup agent
    @Override
    public void onCreate() {

        String preferencesName = getApplicationContext().getPackageName() + "_preferences";
        addHelper(PREFS_BACKUP_KEY, new SharedPreferencesBackupHelper(this, preferencesName));
        Ln.i("backup agent created");
    }

}
