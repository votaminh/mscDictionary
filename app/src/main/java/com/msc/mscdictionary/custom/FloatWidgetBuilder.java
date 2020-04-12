package com.msc.mscdictionary.custom;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnticipateInterpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.msc.mscdictionary.R;
import com.msc.mscdictionary.activity.MainActivity;
import com.msc.mscdictionary.database.OffFavouriteDAO;
import com.msc.mscdictionary.database.OffWordDAO;
import com.msc.mscdictionary.firebase.MyFirebase;
import com.msc.mscdictionary.model.Word;
import com.msc.mscdictionary.network.DictionaryCrawl;
import com.msc.mscdictionary.network.WordDAO;
import com.msc.mscdictionary.util.AppUtil;
import com.msc.mscdictionary.util.Constant;
import com.msc.mscdictionary.util.SharePreferenceUtil;

import static android.content.Context.WINDOW_SERVICE;

public class FloatWidgetBuilder {
    private static View floatWidgetView = null;
    private static View quickTranslate = null;
    private static WindowManager windowManager = null;
    private static String en = "";
    private static WindowManager.LayoutParams paramQuick;
    private static WindowManager.LayoutParams paramFloat;

    Context context;
    private long oldTime = 0;
    private long currentTime = 0;
    private TextView edEn;
    private View tvClose;
    private View progressBar;
    private TextView tvResult;
    private TextView tvSearch;
    private TextView openTranslate;
    private View llDictionary;
    private NestedWebView webviewTranslate;

    public void prepare(Context context){
        this.context = context;
        if(floatWidgetView == null){
            initView(context);
        }

        if(quickTranslate == null){
            initQuickView(context);
        }
        buildWindowManager(context);
    }

    private void initQuickView(Context context) {
        quickTranslate = LayoutInflater.from(context).inflate(R.layout.quickly_translate_layout, null);
    }

    private void initView(Context context) {
        floatWidgetView = LayoutInflater.from(context).inflate(R.layout.translate_float, null);
        floatWidgetView.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            int mWidth = context.getResources().getDisplayMetrics().widthPixels;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = paramFloat.x;
                        initialY = paramFloat.y;

                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        oldTime = System.currentTimeMillis();
                        return true;
                    case MotionEvent.ACTION_UP:
                        currentTime = System.currentTimeMillis();
                        int middle = mWidth / 2;

                        if(currentTime - oldTime < 300){
                            removeFloat();
                            showQuickly(en);
                        }else {
                            if(paramFloat.x >= middle){
                                ani(paramFloat.x, mWidth);
                            }else {
                                ani(paramFloat.x, 0);
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        int xDiff = Math.round(event.getRawX() - initialTouchX);
                        int yDiff = Math.round(event.getRawY() - initialTouchY);

                        paramFloat.x = initialX + xDiff;
                        paramFloat.y = initialY + yDiff;

                        updateView();
                        return true;
                }
                return false;
            }
        });
    }

    private void ani(float from, float to) {
        ValueAnimator va = ValueAnimator.ofFloat(from, to);
        int mDuration = 200;
        va.setInterpolator(new AnticipateInterpolator());
        va.setDuration(mDuration);
        va.addUpdateListener((a) -> {
            float x = (float) a.getAnimatedValue();
            paramFloat.x = (int) x;
            updateView();
        });
        va.start();
    }

    private void updateView() {
        if(windowManager != null){
            windowManager.updateViewLayout(floatWidgetView, paramFloat);
            SharePreferenceUtil.saveIntPereferences(context, Constant.X_FLOAT, paramFloat.x);
            SharePreferenceUtil.saveIntPereferences(context, Constant.Y_FLOAT, paramFloat.y);
        }
    }

    private static void buildWindowManager(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            paramQuick = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);

            paramFloat = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            paramQuick = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);

            paramFloat = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }

        //Specify the view position
        paramQuick.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        paramQuick.x = 0;
        paramQuick.y = 0;

        paramFloat.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        paramFloat.x = 0;
        paramFloat.y = 100;

        windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
    }

    public void showButtonFloat(String en){
        this.en = en;
        try{
            windowManager.removeView(quickTranslate);
        }catch (Exception e){

        }
        try{
            windowManager.removeView(floatWidgetView);
        }catch (Exception e){

        }
        paramFloat.x = SharePreferenceUtil.getIntPereferences(context, Constant.X_FLOAT, 0);
        paramFloat.y = SharePreferenceUtil.getIntPereferences(context, Constant.Y_FLOAT, 100);
        windowManager.addView(floatWidgetView, paramFloat);
    }

    public void showQuickly(String s){
        try{
            windowManager.removeView(quickTranslate);
        }catch (Exception e){

        }
        try{
            windowManager.removeView(floatWidgetView);
        }catch (Exception e){

        }
        windowManager.addView(quickTranslate, paramQuick);
        setUpQuickly(s);
    }

    private void setUpQuickly(String s) {
        if(edEn == null){
            edEn = quickTranslate.findViewById(R.id.edTextEn);
            tvClose = quickTranslate.findViewById(R.id.tvClose);
            progressBar = quickTranslate.findViewById(R.id.progress);
            tvResult = quickTranslate.findViewById(R.id.tvResult);
            tvSearch = quickTranslate.findViewById(R.id.btnSearch);

            openTranslate = quickTranslate.findViewById(R.id.tvOpenTranslate);

            llDictionary = quickTranslate.findViewById(R.id.llDictionary);
            webviewTranslate = quickTranslate.findViewById(R.id.webviewTranslate);

            WebSettings webSettingst = webviewTranslate.getSettings();
            webSettingst.setJavaScriptEnabled(true);
            webviewTranslate.setHorizontalScrollBarEnabled(false);
            webviewTranslate.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return true;
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    view.scrollTo(0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56 * 2, context.getResources().getDisplayMetrics()));
                }
            });
            webviewTranslate.loadUrl("https://translate.google.com/#view=home&op=translate&sl=en&tl=vi&text=Click%20here%20to%20translate");
        }

        edEn.setOnClickListener(v -> openApp(s));
        tvResult.setOnClickListener(v -> openApp(s));
        tvSearch.setOnClickListener(v -> openApp(s));
        tvClose.setOnClickListener(v -> removeTranslate());

        openTranslate.setOnClickListener(v -> openApp(s));

        tvResult.setText("");
        progressBar.setVisibility(View.VISIBLE);
        webviewTranslate.getLayoutParams().height = context.getResources().getDisplayMetrics().heightPixels/2;
        webviewTranslate.setVisibility(View.GONE);
        llDictionary.setVisibility(View.VISIBLE);
        openTranslate.setVisibility(View.GONE);



        edEn.setText(s);
        OffWordDAO offWordDAO = new OffWordDAO(context);
        offWordDAO.getWordByEn(s, new DictionaryCrawl.TranslateCallback() {
            @Override
            public void success(Word word) {
                boolean saved = SharePreferenceUtil.getBooleanPerferences(context, Constant.ENABLE_AUTO_FAVOURITE, false);
                if(saved){
                    OffFavouriteDAO favouriteDAO = new OffFavouriteDAO(context);
                    if(!favouriteDAO.checkHas(word)){
                        favouriteDAO.add(word);
                    }
                }
                String encoding = word.getHtmlFullMean();
                word.setHtmlFullMean(AppUtil.decodingHtml(encoding));
                new Handler(Looper.getMainLooper()).post(() -> {
                    tvResult.setText(word.getCommonMean());
                    progressBar.setVisibility(View.INVISIBLE);
                });
            }

            @Override
            public void fail(String error) {
                DictionaryCrawl.instance(s, new DictionaryCrawl.TranslateCallback() {
                    @Override
                    public void success(Word word) {
                        boolean saved = SharePreferenceUtil.getBooleanPerferences(context, Constant.ENABLE_AUTO_FAVOURITE, false);
                        if(saved){
                            saveWordOffline(word);
                            OffFavouriteDAO favouriteDAO = new OffFavouriteDAO(context);
                            if(!favouriteDAO.checkHas(word)){
                                favouriteDAO.add(word);
                            }
                        }

                        new Handler(Looper.getMainLooper()).post(() -> {
                            tvResult.setText(word.getCommonMean());
                            progressBar.setVisibility(View.INVISIBLE);
                        });
                    }

                    @Override
                    public void fail(final String error) {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            progressBar.setVisibility(View.INVISIBLE);
                            webviewTranslate.loadUrl("https://translate.google.com/#view=home&op=translate&sl=en&tl=vi&text=" + s);
                            webviewTranslate.setVisibility(View.VISIBLE);
                            llDictionary.setVisibility(View.GONE);
                            openTranslate.setVisibility(View.VISIBLE);
                        });
                    }
                }).translate();
            }
        });
    }

    private void saveWordOffline(Word word) {
        int biggestId = SharePreferenceUtil.getIntPereferences(context, Constant.CURRENT_ID_WORD, 0);
        biggestId++;
        word.setId(biggestId);
        SharePreferenceUtil.saveIntPereferences(context, Constant.CURRENT_ID_WORD, biggestId);
        OffWordDAO wordDAO = new OffWordDAO(context);
        wordDAO.addWord(word);

        // uptoFirebase
        MyFirebase.uploadWord(word, context);
    }

    private void openApp(String s) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constant.EN_NAME_PUT, s);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        removeTranslate();
        context.startActivity(intent);
    }

    public void removeTranslate(){
        windowManager.removeView(quickTranslate);
    }

    public void removeFloat(){
        windowManager.removeView(floatWidgetView);
    }
}
