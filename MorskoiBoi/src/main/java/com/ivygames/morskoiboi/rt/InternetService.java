package com.ivygames.morskoiboi.rt;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.R;

import org.acra.ACRA;
import org.commons.logger.Ln;

public class InternetService extends Service {
    private static final int NOTIFICATION_ID = -100;
    public static final String EXTRA_CONTENT_TITLE = "EXTRA_CONTENT_TITLE";
    public static final String EXTRA_CONTENT_TEXT = "EXTRA_CONTENT_TEXT";

    @Override
    public void onCreate() {
        super.onCreate();
        Ln.v("service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (intent == null) {
            Ln.i("service is recreating - stop");
            stopSelf();
            return START_NOT_STICKY;
        }

        Bundle extras = intent.getExtras();
        if (extras == null) {
            Ln.w("no extras - stop");
            ACRA.getErrorReporter().handleException(new RuntimeException("no extras"));
            stopSelf();
            return START_NOT_STICKY;
        }

        String contentTitle = extras.getString(EXTRA_CONTENT_TITLE);
        String contentText = extras.getString(EXTRA_CONTENT_TEXT);
        Notification notification = InternetService.buildNotification(this, contentTitle, contentText);
        // notification.defaults |= Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;

        startForeground(NOTIFICATION_ID, notification);
        // return START_REDELIVER_INTENT; // was used to prevent intent to be null after recreation
        return START_NOT_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Ln.v("service destroyed");
    }

    private static Notification buildNotification(Context packageContext, String contentTitle, String contentText) {
        PendingIntent contentIntent = InternetService.buildAppIntent(packageContext);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(packageContext);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentTitle(contentTitle);
        if (contentText != null) {
            builder.setContentText(contentText);
        }
        builder.setContentIntent(contentIntent);
        builder.setLocalOnly(true);
        builder.setShowWhen(false);
        builder.setOngoing(true);
        return builder.build();
    }

    private static PendingIntent buildAppIntent(Context packageContext) {
        Intent baseIntent = new Intent(packageContext, BattleshipActivity.class);
        baseIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getActivity(packageContext, 0, baseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
