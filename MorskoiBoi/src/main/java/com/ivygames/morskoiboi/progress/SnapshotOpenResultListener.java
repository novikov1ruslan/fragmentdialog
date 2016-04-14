package com.ivygames.morskoiboi.progress;

import com.google.android.gms.games.snapshot.Snapshot;

interface SnapshotOpenResultListener {
    void onConflict(String conflictId, Snapshot resolveSnapshot);

    void onUpdateServerWith(byte[] bytes);
}
