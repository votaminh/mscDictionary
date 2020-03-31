package com.msc.mscdictionary;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;

import com.msc.mscdictionary.base.BaseActivity;
import com.msc.mscdictionary.fragment.TranslateFragment;
import com.msc.mscdictionary.model.Word;
import com.msc.mscdictionary.network.WordDAO;
import com.msc.mscdictionary.service.Dictionary;
import com.msc.mscdictionary.util.AppUtil;

public class MainActivity extends BaseActivity {
    EditText edTextEn;
    TextView btnSearch;
    ProgressBar progress;
    ImageButton btnMenuDrawer;

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

        openTranslateFragment();
        onClick();
    }


    private void onClick() {
        btnSearch.setOnClickListener((v) -> {
            showLoad();
            hideKeyboard(this);
            final String en = edTextEn.getText().toString();
            WordDAO.checkHasWord(new Word(en, "", "", "", ""), new Dictionary.TranslateCallback() {
                @Override
                public void success(Word word) {
                    String encoding = word.getHtmlFullMean();
                    word.setHtmlFullMean(AppUtil.decodingHtml(encoding));
                    setResultSearch(word);
                    hideLoad();
                }

                @Override
                public void fail(String error) {
                    Dictionary.instance(en, new Dictionary.TranslateCallback() {
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


        });

        btnMenuDrawer.setOnClickListener(v -> {
            drawerLayout.openDrawer(Gravity.LEFT);
            hideKeyboard(this);
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
    }

    private void showKeyBroad() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    private void setResultSearch(Word word) {
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

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
