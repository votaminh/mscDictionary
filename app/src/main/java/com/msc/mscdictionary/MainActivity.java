package com.msc.mscdictionary;

import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;

import com.msc.mscdictionary.base.BaseActivity;
import com.msc.mscdictionary.fragment.TranslateFragment;
import com.msc.mscdictionary.service.Dictionary;
import com.msc.mscdictionary.util.Constant;

public class MainActivity extends BaseActivity {
    EditText edTextEn;
    TextView btnSearch;
    private TranslateFragment translateFragment;

    @Override
    public int resLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void intView() {
        edTextEn = findViewById(R.id.edTextEn);
        btnSearch = findViewById(R.id.btnSearch);

        openTranslateFragment();
        onClick();
    }

    private void onClick() {
        btnSearch.setOnClickListener((v) -> {
            final String en = edTextEn.getText().toString();
            Dictionary.instance(en, new Dictionary.TranslateCallback() {
                @Override
                public void success(String mean) {
                    setResultSearch(mean);
                }

                @Override
                public void fail(final String error) {
                    setError(error);
                }
            }).translate();
        });
    }

    private void setError(String error) {
    }

    private void setResultSearch(String mean) {
        if(translateFragment != null && translateFragment.isVisible()){
            translateFragment.showResult("test mean", Constant.header + mean + Constant.endTag);
        }
    }

    private void openTranslateFragment() {
        translateFragment = (TranslateFragment) findFragment(TranslateFragment.TAG);
        if(translateFragment == null){
            translateFragment = new TranslateFragment();
        }

        replaceFragment(translateFragment, R.id.container, TranslateFragment.TAG);
    }
}
