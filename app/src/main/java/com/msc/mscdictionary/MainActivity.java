package com.msc.mscdictionary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;

import com.msc.mscdictionary.base.BaseActivity;
import com.msc.mscdictionary.fragment.TranslateFragment;
import com.msc.mscdictionary.media.MediaBuilder;
import com.msc.mscdictionary.model.Word;
import com.msc.mscdictionary.network.WordDAO;
import com.msc.mscdictionary.network.DictionaryCrawl;
import com.msc.mscdictionary.service.ClipBroadService;

public class MainActivity extends BaseActivity {
    private static final int DRAW_OVER_OTHER_APP_PERMISSION = 33;
    EditText edTextEn;
    TextView btnSearch;
    ProgressBar progress;
    ImageButton btnMenuDrawer;

    TextView tvMean;
    TextView tvVoice;
    ImageButton btnSpeaker;
    ProgressBar progressBar;

    Word currentWord;

    RelativeLayout llHeaderWord;

    private TranslateFragment translateFragment;

    DrawerLayout drawerLayout;
    @Override
    public int resLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void intView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        edTextEn = findViewById(R.id.edTextEn);
        btnSearch = findViewById(R.id.btnSearch);
        progress = findViewById(R.id.progress);
        btnMenuDrawer = findViewById(R.id.btnMenuDrawer);
        drawerLayout = findViewById(R.id.drawerLayout);

        tvMean = findViewById(R.id.tvMean);
        tvVoice = findViewById(R.id.tvVoice);
        btnSpeaker = findViewById(R.id.tvAudio);
        progressBar = findViewById(R.id.progressVoice);

        llHeaderWord = findViewById(R.id.llHeader);

        openTranslateFragment();
        onClick();
        askForSystemOverlayPermission();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent service = new Intent(MainActivity.this, ClipBroadService.class);
        startService(service);
    }

    private void onClick() {
        btnSpeaker.setOnClickListener(v -> {
            if(currentWord != null){
                playAudio(currentWord.getUrlSpeak());
            }
        });

        edTextEn.setOnClickListener(v -> {
            edTextEn.setCursorVisible(true);
        });
        btnSearch.setOnClickListener((v) -> {
            final String en = edTextEn.getText().toString();
            search(en);
        });

        btnMenuDrawer.setOnClickListener(v -> {
            drawerLayout.openDrawer(Gravity.LEFT);
            hideKeyboard(this);
        });

    }

    private void playAudio(String urlSpeak) {
        MediaBuilder.playLink(urlSpeak, new MediaBuilder.MediaCallback() {
            @Override
            public void start() {
                new Handler(Looper.getMainLooper()).post(() -> {
                    btnSpeaker.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                });
            }

            @Override
            public void end() {
                new Handler(Looper.getMainLooper()).post(() -> {
                    btnSpeaker.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                });
            }
        });
    }

    private void showLoad() {
        runOnUiThread(() -> {
            progress.setVisibility(View.VISIBLE);
            btnSearch.setVisibility(View.INVISIBLE);
        });
    }

    private void hideLoad(){
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            progress.setVisibility(View.INVISIBLE);
            btnSearch.setVisibility(View.VISIBLE);
            edTextEn.setText("");
            edTextEn.clearFocus();
            edTextEn.setCursorVisible(false);
        }, 1000);
    }

    private void setError(String error) {
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        edTextEn.setCursorVisible(false);
    }

    private void showKeyBroad() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    private void setResultSearch(Word word) {
        currentWord = word;
        new Handler(Looper.getMainLooper()).post(() -> {
            tvVoice.setText(word.getVoice());
            tvMean.setText(word.getEnWord().substring(0, 1).toUpperCase() + word.getEnWord().substring(1).toLowerCase());
            llHeaderWord.setVisibility(View.VISIBLE);
        });
        if(translateFragment != null && translateFragment.isVisible()){
            translateFragment.showResult(word);
        }
    }

    private void openTranslateFragment() {
        translateFragment = (TranslateFragment) findFragment(TranslateFragment.TAG);
        if(translateFragment == null){
            translateFragment = new TranslateFragment();
        }

        replaceFragment(translateFragment, R.id.container, TranslateFragment.TAG);
    }

    private void askForSystemOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, DRAW_OVER_OTHER_APP_PERMISSION);
        }
    }
    
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void search(String en) {
        showLoad();
        hideKeyboard(this);
        llHeaderWord.setVisibility(View.INVISIBLE);
        edTextEn.setText(en);
        WordDAO.checkHasWord(new Word(en, "", "", "", ""), new DictionaryCrawl.TranslateCallback() {
            @Override
            public void success(Word word) {
                setResultSearch(word);
                hideLoad();
            }

            @Override
            public void fail(String error) {
                DictionaryCrawl.instance(en, new DictionaryCrawl.TranslateCallback() {
                    @Override
                    public void success(Word word) {
                        setResultSearch(word);
                        hideLoad();
                    }

                    @Override
                    public void fail(final String error) {
                        setError(error);
                        hideLoad();
                    }
                }).translate();
            }
        });

    }
}
