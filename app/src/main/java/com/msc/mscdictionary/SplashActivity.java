package com.msc.mscdictionary;

import android.content.Intent;
import android.os.Handler;

import com.msc.mscdictionary.base.BaseActivity;

public class SplashActivity extends BaseActivity {
    @Override
    public int resLayoutId() {
        return R.layout.splash_activity;
    }

    @Override
    public void intView() {
        new Handler().postDelayed(() -> goToMain(), 2000);
    }

    private void goToMain() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
