package com.msc.mscdictionary;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.msc.mscdictionary.base.BaseActivity;
import com.msc.mscdictionary.util.Constant;
import com.msc.mscdictionary.util.SharePreferenceUtil;

public class SplashActivity extends BaseActivity {

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
            TextView textView = findViewById(R.id.tv);
            textView.setPadding(0 , 0, 0, getHeightNavi());
            new Handler().postDelayed(() -> goToMain(), 500);
        }else {
            TextView textView = findViewById(R.id.tv);
            textView.setPadding(0 , 0, 0, getHeightNavi());
            SharePreferenceUtil.saveBooleanPereferences(this, Constant.HAS_RAN_APP, true);
            new Handler().postDelayed(() -> textView.setText("Wait a moment while we preparing setup for your device"), 2000);
            new Handler().postDelayed(() -> textView.setText("Downloading data ..."), 4000);
            new Handler().postDelayed(() -> textView.setText("Finish"), 8000);
            new Handler().postDelayed(() -> goToMain(), 9000);
        }
    }

    private void goToMain() {
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
}
