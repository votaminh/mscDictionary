package com.msc.mscdictionary.fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.msc.mscdictionary.R;
import com.msc.mscdictionary.base.BaseFragment;

public class TranslateFragment extends BaseFragment {
    public static final String TAG = "TranslateFragment";

    TextView tvMean;
    WebView webViewMean;

    @Override
    public int resLayoutId() {
        return R.layout.translate_fragment;
    }

    @Override
    public void initView(View view) {
        tvMean = view.findViewById(R.id.tvMean);
        webViewMean = view.findViewById(R.id.webviewMean);
        webViewMean.setHorizontalScrollBarEnabled(false);
    }

    public void showResult(String commonMean, String fullMean){
        new Handler(Looper.getMainLooper()).post(() -> {
            webViewMean.loadDataWithBaseURL(null, fullMean, "text/html", "utf-8", null);
        });
    }
}
