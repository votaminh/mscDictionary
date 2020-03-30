package com.msc.mscdictionary.fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.msc.mscdictionary.R;
import com.msc.mscdictionary.base.BaseFragment;
import com.msc.mscdictionary.model.Word;
import com.msc.mscdictionary.util.Constant;

public class TranslateFragment extends BaseFragment {
    public static final String TAG = "TranslateFragment";

    TextView tvMean;
    TextView tvVoice;
    WebView webViewMean;

    @Override
    public int resLayoutId() {
        return R.layout.translate_fragment;
    }

    @Override
    public void initView(View view) {
        tvMean = view.findViewById(R.id.tvMean);
        tvVoice = view.findViewById(R.id.tvVoice);

        webViewMean = view.findViewById(R.id.webviewMean);
        webViewMean.setHorizontalScrollBarEnabled(false);
    }

    public void showResult(Word word){
        new Handler(Looper.getMainLooper()).post(() -> {
            tvVoice.setText(word.getVoice());
            tvMean.setText(word.getEnWord().substring(0, 1).toUpperCase() + word.getEnWord().substring(1).toLowerCase());
            webViewMean.loadDataWithBaseURL(null, Constant.header + word.getHtmlFullMean() + Constant.endTag, "text/html", "utf-8", null);
        });
    }
}
