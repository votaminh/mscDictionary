package com.msc.mscdictionary.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.msc.mscdictionary.R;
import com.msc.mscdictionary.database.WriteFile;
import com.msc.mscdictionary.network.DownloadFile;
import com.msc.mscdictionary.util.AppUtil;
import com.msc.mscdictionary.util.Constant;

public class DownloadZipService extends Service {
    private NotificationCompat.Builder builder;
    private NotificationManagerCompat notificationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String link = Constant.LINK_ZIP_DATABASE;
        String fileDes = getFilesDir().toString() + Constant.ZIP_NAME;
        String fileDesZip = getFilesDir().toString() + "/";

        showNotificationDownload();

        DownloadFile downloadFile = new DownloadFile(getApplicationContext(), fileDes);
        downloadFile.setDownloadListener(new DownloadFile.DownloadListener() {
            @Override
            public void progress(int progress) {
                builder.setProgress(100, progress, false);
                notificationManager.notify(2, builder.build());
            }

            @Override
            public void fail(String error) {
                builder.setContentText(getString(R.string.error_download));
                builder.setAutoCancel( true )
                        .setOngoing( false );
                notificationManager.notify(2, builder.build());
            }

            @Override
            public void finish() {
                builder.setContentText(getString(R.string.unzipping));
                notificationManager.notify(2, builder.build());
                WriteFile.unZipFile(fileDes, fileDesZip, true);
                notificationManager.cancel(2);
                stopService(new Intent(getApplicationContext(), DownloadZipService.class));
            }
        });

        downloadFile.execute(link);
        return START_STICKY;
    }

    private void showNotificationDownload() {
        AppUtil.createNotificationChannel(this, "download", NotificationManager.IMPORTANCE_LOW);

        builder = new NotificationCompat.Builder(getApplicationContext(), "download")
                .setSmallIcon(R.drawable.ic_app)
                .setContentTitle("Data offline")
                .setContentText(getString(R.string.title_download_notifi))
                .setShowWhen(true)
                .setSound(null)
                .setAutoCancel( false )
                .setOngoing( true )
                .setProgress(100, 0, false)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(Constant.ID_NOTIFICATION_DOWNLOAD_ZIP, builder.build());
    }
}
