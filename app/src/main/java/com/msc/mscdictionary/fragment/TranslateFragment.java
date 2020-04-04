package com.msc.mscdictionary.fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.msc.mscdictionary.activity.MainActivity;
import com.msc.mscdictionary.R;
import com.msc.mscdictionary.ads.AdsHelper;
import com.msc.mscdictionary.base.BaseFragment;
import com.msc.mscdictionary.model.Word;
import com.msc.mscdictionary.util.AppUtil;
import com.msc.mscdictionary.util.Constant;


public class TranslateFragment extends BaseFragment {
    public static final String TAG = "TranslateFragment";
    WebView webViewMean;
    Word currentWord;
    TextView tvNoData, tvNoHistory;
    TextView tvError;
    private AdView mAdView;

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
        mAdView =(AdView)view.findViewById(R.id.adView);
        if(AppUtil.isNetworkConnected(getContext())){
            AdsHelper.setupAds(mAdView, getContext());
        }
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

            new Handler().postDelayed(() -> setPosition(), 200);
        });
    }

    private void setPosition() {
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
