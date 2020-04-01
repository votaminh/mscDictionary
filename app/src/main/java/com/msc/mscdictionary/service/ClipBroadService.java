package com.msc.mscdictionary.service;

import android.app.Notification;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.msc.mscdictionary.R;
import com.msc.mscdictionary.custom.FloatWidgetBuilder;
import com.msc.mscdictionary.util.AppUtil;

public class ClipBroadService extends Service {
    private Notification status;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showNotification();
        final ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.addPrimaryClipChangedListener( new ClipboardManager.OnPrimaryClipChangedListener() {
            public void onPrimaryClipChanged() {
                String a = clipboard.getText().toString();
                if(!a.isEmpty()){
                    FloatWidgetBuilder builder = new FloatWidgetBuilder();
                    builder.prepare(getApplicationContext());
                    builder.showButtonFloat(a);
                }
            }
        });
        return START_STICKY;
    }

    private void showNotification() {
        AppUtil.createNotificationChannel(this, "translateCopyText");

        status = new NotificationCompat.Builder(getApplicationContext(), "translateCopyText")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getString(R.string.run_service))
                .setShowWhen(false)
                .setSound(null)
                .setAutoCancel( false )
                .setOngoing( true )
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(1, status);
    }
}
