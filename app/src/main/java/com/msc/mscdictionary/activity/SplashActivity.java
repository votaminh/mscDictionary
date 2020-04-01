package com.msc.mscdictionary.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.msc.mscdictionary.R;
import com.msc.mscdictionary.base.BaseActivity;
import com.msc.mscdictionary.database.DataHelper;
import com.msc.mscdictionary.database.WriteFile;
import com.msc.mscdictionary.network.DownloadFile;
import com.msc.mscdictionary.util.AppUtil;
import com.msc.mscdictionary.util.Constant;
import com.msc.mscdictionary.util.SharePreferenceUtil;

import java.io.IOException;

public class SplashActivity extends BaseActivity {
    private NotificationCompat.Builder builder;
    private NotificationManagerCompat notificationManager;
    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public int resLayoutId() {
        return R.layout.splash_activity;
    }

    @Override
    public void intView() {
        boolean hadRan = SharePreferenceUtil.getBooleanPerferences(this, Constant.HAS_RAN_APP, false);
        if(hadRan){
            textView = findViewById(R.id.tv);
            textView.setPadding(0 , 0, 0, getHeightNavi());
            new Handler().postDelayed(() -> goToMain(), 500);
        }else {

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
                    new Thread(() -> {
                        WriteFile.unZipFile(fileDes, fileDesZip, true);
                        notificationManager.cancel(2);

                        new Handler(Looper.getMainLooper()).postDelayed(() -> goToMain(), 0);
                    }).start();
                }
            });

            downloadFile.execute(link);

            TextView textView = findViewById(R.id.tv);
            textView.setPadding(0 , 0, 0, getHeightNavi());
            SharePreferenceUtil.saveBooleanPereferences(this, Constant.HAS_RAN_APP, true);
            new Handler().postDelayed(() -> textView.setText("Wait a moment while we preparing setup for your device"), 2000);
            new Handler().postDelayed(() -> textView.setText("Downloading data ..."), 4000);
        }
    }

    private void showNotificationDownload() {
        AppUtil.createNotificationChannel(this, "download");

        builder = new NotificationCompat.Builder(getApplicationContext(), "download")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Download Data")
                .setContentText(getString(R.string.title_download_notifi))
                .setShowWhen(true)
                .setSound(null)
                .setAutoCancel( false )
                .setOngoing( true )
                .setProgress(100, 0, false)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(2, builder.build());
    }

    private void goToMain() {

        DataHelper dataHelper = new DataHelper(this);
        try {
            dataHelper.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public int getHeightNavi(){
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
