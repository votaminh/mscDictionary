package com.msc.mscdictionary.fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.msc.mscdictionary.activity.MainActivity;
import com.msc.mscdictionary.R;
import com.msc.mscdictionary.base.BaseFragment;
import com.msc.mscdictionary.model.Word;
import com.msc.mscdictionary.util.Constant;


public class TranslateFragment extends BaseFragment {
    public static final String TAG = "TranslateFragment";
    WebView webViewMean;
    Word currentWord;
    TextView tvNoData, tvNoHistory;
    TextView tvError;
    @Override
    public int resLayoutId() {
        return R.layout.translate_fragment;
    }

    @Override
    public void initView(View view) {
        webViewMean = view.findViewById(R.id.webviewMean);
        WebSettings webSettings = webViewMean.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webViewMean.setHorizontalScrollBarEnabled(false);
        webViewMean.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                String en = url.replace(Constant.BASELINK_SOHA, "");
                translateEn(en);
                return true;
            }
        });

        tvNoData = view.findViewById(R.id.tvNodata);
        tvNoHistory = view.findViewById(R.id.tvNoHistory);
        tvError = view.findViewById(R.id.tvError);
    }

    private void translateEn(String en) {
        MainActivity activity = (MainActivity) getActivity();
        if(activity != null){
            activity.search(en, true);
            webViewMean.setVisibility(View.INVISIBLE);
        }
    }

    public void showResult(Word word){
        new Handler(Looper.getMainLooper()).post(() -> {
            tvNoHistory.setVisibility(View.INVISIBLE);
            currentWord = word;
            String content = "";
            for (int i = 0; i < Constant.NUMBER_COUNT_BR; i++) {
                content += Constant.TAG_BR;
            }
            content += word.getHtmlFullMean();
            word.setHtmlFullMean(content);

            webViewMean.loadDataWithBaseURL(null, Constant.header + word.getHtmlFullMean() + Constant.endTag, "text/html", "utf-8", null);
            showWebview();

            setUpPosition();
        });
    }

    private void setUpPosition() {
        MainActivity activity = (MainActivity) getActivity();
        if(activity != null){
            int[] location = activity.getLocationHeader();
            float statusBarHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getContext().getResources().getDisplayMetrics());
            float headHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, getContext().getResources().getDisplayMetrics());
            float spacingTop = statusBarHeight + headHeight;

            float mid = (statusBarHeight + spacingTop)/2;

            if(location[1] > mid){
                webViewMean.scrollBy(0, 0);
            }else {
                webViewMean.scrollBy(0, (int) headHeight);
            }
        }
    }

    private void showWebview() {
        webViewMean.setVisibility(View.VISIBLE);

        tvNoData.setVisibility(View.INVISIBLE);
        tvNoHistory.setVisibility(View.INVISIBLE);
        tvError.setVisibility(View.INVISIBLE);
    }

    public void setError(String error) {
        new Handler(Looper.getMainLooper()).post(() -> {
            tvNoHistory.setVisibility(View.INVISIBLE);
            tvError.setVisibility(View.VISIBLE);
            tvError.setText(error);
            webViewMean.setVisibility(View.INVISIBLE);
        });
    }
}
