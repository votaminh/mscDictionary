package com.msc.mscdictionary.fragment;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.msc.mscdictionary.R;
import com.msc.mscdictionary.base.BaseFragment;
import com.msc.mscdictionary.media.MediaBuilder;
import com.msc.mscdictionary.model.Word;
import com.msc.mscdictionary.util.AppUtil;
import com.msc.mscdictionary.util.Constant;

import java.io.IOException;

public class TranslateFragment extends BaseFragment {
    public static final String TAG = "TranslateFragment";



    WebView webViewMean;


    Word currentWord;
    TextView tvNoData, tvNoHistory;
    @Override
    public int resLayoutId() {
        return R.layout.translate_fragment;
    }

    @Override
    public void initView(View view) {

        webViewMean = view.findViewById(R.id.webviewMean);
        webViewMean.setHorizontalScrollBarEnabled(false);

        tvNoData = view.findViewById(R.id.tvNodata);
        tvNoHistory = view.findViewById(R.id.tvNoHistory);
    }

    public void showResult(Word word){
        currentWord = word;
        new Handler(Looper.getMainLooper()).post(() -> {
            String content = "";
            for (int i = 0; i < 13; i++) {
                content += Constant.TAG_BR;
            }
            content += word.getHtmlFullMean();
            word.setHtmlFullMean(content);

            webViewMean.loadDataWithBaseURL(null, Constant.header + word.getHtmlFullMean() + Constant.endTag, "text/html", "utf-8", null);
            showWebview();
        });
    }

    private void showWebview() {
        webViewMean.setVisibility(View.VISIBLE);

        tvNoData.setVisibility(View.INVISIBLE);
        tvNoHistory.setVisibility(View.INVISIBLE);
    }
}
