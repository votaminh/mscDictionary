package com.msc.mscdictionary.fragment;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
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
import com.msc.mscdictionary.custom.NestedWebView;
import com.msc.mscdictionary.model.Word;
import com.msc.mscdictionary.util.AppUtil;
import com.msc.mscdictionary.util.Constant;


public class TranslateFragment extends BaseFragment {
    public static final String TAG = "TranslateFragment";
    NestedWebView webViewMean, webviewTranslate;
    Word currentWord;
    TextView tvNoData, tvNoHistory;
    TextView tvError;
    private AdView mAdView;
    private MainActivity activity;
    private String error;

    @Override
    public int resLayoutId() {
        return R.layout.translate_fragment;
    }

    @Override
    public void initView(View view) {
        webViewMean = view.findViewById(R.id.webviewMean);
        setupWebviewMean();

        webviewTranslate = view.findViewById(R.id.webviewTranslate);
        setUpWebviewTranslate();

        tvNoData = view.findViewById(R.id.tvNodata);
        tvNoHistory = view.findViewById(R.id.tvNoHistory);
        tvError = view.findViewById(R.id.tvError);
        mAdView =(AdView)view.findViewById(R.id.adView);
        if(AppUtil.isNetworkConnected(getContext())){
            AdsHelper.setupAds(mAdView, getContext());
        }else {
            AdsHelper.goneAds(mAdView);
        }

        activity = (MainActivity) getActivity();
    }

    private void setUpWebviewTranslate() {
        WebSettings webSettingst = webviewTranslate.getSettings();
        webSettingst.setJavaScriptEnabled(true);
        webviewTranslate.setHorizontalScrollBarEnabled(false);
        webviewTranslate.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }
        });
        webviewTranslate.loadUrl("https://translate.google.com/#view=home&op=translate&sl=en&tl=vi&text=Click%20here%20to%20translate");
    }

    private void setupWebviewMean() {
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
    }


    public void enableTranslate(){
        webViewMean.setVisibility(View.INVISIBLE);
        webviewTranslate.setVisibility(View.VISIBLE);
        hideAllNote();
    }

    public void enableDictionary(){
        webViewMean.setVisibility(View.VISIBLE);
        webviewTranslate.setVisibility(View.INVISIBLE);
    }

    private void translateEn(String en) {
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
            new Handler().postDelayed(() -> setPosition(), 500);
        });
    }

    private void setPosition() {
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
            showWebview();
        }
    }

    private void showWebview() {
        webViewMean.setVisibility(View.VISIBLE);
        hideAllNote();
    }

    private void hideAllNote() {
        tvNoData.setVisibility(View.INVISIBLE);
        tvNoHistory.setVisibility(View.INVISIBLE);
        tvError.setVisibility(View.INVISIBLE);
    }

    public void setError(String error) {
        this.error = error;
        new Handler(Looper.getMainLooper()).post(() -> {
            if(AppUtil.isNetworkConnected(getContext())){
                showDialogAskTranslate();
            }else {
                tvNoHistory.setVisibility(View.INVISIBLE);
                tvError.setVisibility(View.VISIBLE);
                tvError.setText(error  + "  " + getString(R.string.error_no_found));
                webViewMean.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void showErrorDictionary(String error) {
        tvNoHistory.setVisibility(View.INVISIBLE);
        tvError.setVisibility(View.VISIBLE);
        tvError.setText(error  + "  " + getString(R.string.error_no));
        webViewMean.setVisibility(View.INVISIBLE);
    }

    private void showDialogAskTranslate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.title_dialog_ask_translate));
        builder.setMessage(getString(R.string.message_dialog_ask_translate));
        builder.setPositiveButton(getString(R.string.yes_lable), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(activity != null){
                    activity.tvTranslateClick();
                    webviewTranslate.loadUrl("https://translate.google.com/#view=home&op=translate&sl=en&tl=vi&text=" + error);
                }
            }
        });

        builder.setNeutralButton(getString(R.string.cancel_lable), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showErrorDictionary(error);
            }
        });
        builder.show();
    }


    public void enableVi_En() {
        webviewTranslate.loadUrl("https://translate.google.com/#view=home&op=translate&sl=vi&tl=en&text=Nh%E1%BA%A5n%20v%C3%A0o%20%C4%91%C3%A2y%20%C4%91%E1%BB%83%20d%E1%BB%8Bch");
    }

    public void enableEn_Vi() {
        webviewTranslate.loadUrl("https://translate.google.com/#view=home&op=translate&sl=en&tl=vi&text=Click%20here%20to%20translate");
    }

    public boolean checkBackTranslate() {
        if(webviewTranslate.canGoBack()){
            webviewTranslate.goBack();
            return true;
        }else return false;
    }
}
