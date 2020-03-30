package com.msc.mscdictionary;

import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;

import com.google.gson.Gson;
import com.msc.mscdictionary.API.APIRetrofit;
import com.msc.mscdictionary.API.DataService;
import com.msc.mscdictionary.base.BaseActivity;
import com.msc.mscdictionary.fragment.TranslateFragment;
import com.msc.mscdictionary.model.Word;
import com.msc.mscdictionary.network.WordDAO;
import com.msc.mscdictionary.service.Dictionary;
import com.msc.mscdictionary.util.AppUtil;
import com.msc.mscdictionary.util.Constant;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends BaseActivity {
    EditText edTextEn;
    TextView btnSearch;
    ProgressBar progress;

    private TranslateFragment translateFragment;

    @Override
    public int resLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void intView() {
        edTextEn = findViewById(R.id.edTextEn);
        btnSearch = findViewById(R.id.btnSearch);
        progress = findViewById(R.id.progress);

        openTranslateFragment();
        onClick();
    }

    private void onClick() {
        btnSearch.setOnClickListener((v) -> {
            showLoad();
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
    }

    private void showLoad() {
        runOnUiThread(() -> {
            progress.setVisibility(View.VISIBLE);
            btnSearch.setVisibility(View.INVISIBLE);
        });
    }

    private void hideLoad(){
        runOnUiThread(() -> {
            progress.setVisibility(View.INVISIBLE);
            btnSearch.setVisibility(View.VISIBLE);
        });
    }

    private void setError(String error) {
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
}
