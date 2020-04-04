package com.msc.mscdictionary.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.msc.mscdictionary.R;
import com.msc.mscdictionary.activity.MainActivity;
import com.msc.mscdictionary.custom.FloatWidgetBuilder;
import com.msc.mscdictionary.util.AppUtil;
import com.msc.mscdictionary.util.Constant;

public class ClipBroadService extends Service {
    private Notification status;
    private ClipboardManager clipboard;
    private boolean run = true;
    private ClipboardManager.OnPrimaryClipChangedListener listener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(clipboard == null){
            clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);

            listener = new ClipboardManager.OnPrimaryClipChangedListener() {
                @Override
                public void onPrimaryClipChanged() {
                    String a = clipboard.getText().toString();
                    if(!a.isEmpty()){
                        FloatWidgetBuilder builder = new FloatWidgetBuilder();
                        builder.prepare(getApplicationContext());
                        builder.showButtonFloat(a);
                    }
                }
            };
        }

        if(intent == null){
            return START_STICKY;
        }
        run = intent.getExtras().getBoolean(Constant.RUN_SERVICE, true);

        if(run){
            clipboard.addPrimaryClipChangedListener(listener);
            showNotification();
        }else {
            clipboard.removePrimaryClipChangedListener(listener);
        }
        return START_STICKY;
    }

    private void showNotification() {
        AppUtil.createNotificationChannel(this, "translateCopyText", NotificationManager.IMPORTANCE_LOW);

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        status = new NotificationCompat.Builder(getApplicationContext(), "translateCopyText")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getString(R.string.run_service))
                .setShowWhen(false)
                .setSound(null)
                .setAutoCancel( false )
                .setOngoing( true )
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(Constant.ID_NOTIFICATION_FLOAT, status);
    }
}
