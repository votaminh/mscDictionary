package com.msc.mscdictionary.activity;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.msc.mscdictionary.R;
import com.msc.mscdictionary.base.BaseActivity;
import com.msc.mscdictionary.database.DataHelper;
import com.msc.mscdictionary.database.OffFavouriteDAO;
import com.msc.mscdictionary.database.OffHistoryDAO;
import com.msc.mscdictionary.database.OffWordDAO;
import com.msc.mscdictionary.database.WriteFile;
import com.msc.mscdictionary.network.DownloadFile;
import com.msc.mscdictionary.util.AppUtil;
import com.msc.mscdictionary.util.Constant;
import com.msc.mscdictionary.util.SharePreferenceUtil;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class SplashActivity extends BaseActivity {
    private NotificationCompat.Builder builder;
    private NotificationManagerCompat notificationManager;
    private TextView textView;
    private boolean hadRan = false;

    TextView tvProgress;
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
    public void initView() {
        tvProgress = findViewById(R.id.tvProgress);
        hadRan = SharePreferenceUtil.getBooleanPerferences(this, Constant.HAS_RAN_APP, false);
        if(hadRan){
            goToMain();
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
                    new Handler(Looper.getMainLooper()).post(() -> tvProgress.setText(progress + " %"));
                }

                @Override
                public void fail(String error) {
                    builder.setContentText(getString(R.string.error_download));
                    builder.setAutoCancel( true )
                            .setOngoing( false );
                    notificationManager.notify(2, builder.build());
                    new Handler(Looper.getMainLooper()).post(() -> showDialogError());
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
            new Handler().postDelayed(() -> textView.setText("Wait a moment while we preparing setup for your device"), 2000);
            new Handler().postDelayed(() -> textView.setText("Downloading data ..."), 4000);
        }
    }

    private void showDialogError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.title_dialog_error_download));
        builder.setMessage(getString(R.string.message_dialog_error_download));
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    private void showNotificationDownload() {
        AppUtil.createNotificationChannel(this, "download", NotificationManager.IMPORTANCE_LOW);

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
        if(!hadRan){
           createDataBase();
           SharePreferenceUtil.saveBooleanPereferences(this, Constant.HAS_RAN_APP, true);

           DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
           Date date = new Date();
           String dateS = dateFormat.format(date);
           SharePreferenceUtil.saveStringPereferences(this, Constant.TIME_FIRST_RUN, dateS);
        }
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();


    }

    private void createDataBase() {
        tvProgress.setText(getString(R.string.extrac_zip_lable));
        DataHelper dataHelper = new DataHelper(this);
        dataHelper.createDatabase();
        OffWordDAO wordDAO = new OffWordDAO(this);
        int biggestId = wordDAO.getBiggestId();
        SharePreferenceUtil.saveIntPereferences(this, Constant.CURRENT_ID_WORD, biggestId);
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
