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
import com.msc.mscdictionary.util.Constant;

import java.io.IOException;

public class TranslateFragment extends BaseFragment {
    public static final String TAG = "TranslateFragment";

    TextView tvMean;
    TextView tvVoice;
    ImageButton btnSpeaker;
    WebView webViewMean;

    ProgressBar progressBar;
    Word currentWord;

    RelativeLayout llHeader;
    TextView tvNoData, tvNoHistory;
    @Override
    public int resLayoutId() {
        return R.layout.translate_fragment;
    }

    @Override
    public void initView(View view) {
        tvMean = view.findViewById(R.id.tvMean);
        tvVoice = view.findViewById(R.id.tvVoice);
        btnSpeaker = view.findViewById(R.id.tvAudio);

        webViewMean = view.findViewById(R.id.webviewMean);
        webViewMean.setHorizontalScrollBarEnabled(false);

        progressBar = view.findViewById(R.id.progress);
        tvNoData = view.findViewById(R.id.tvNodata);
        tvNoHistory = view.findViewById(R.id.tvNoHistory);
        llHeader = view.findViewById(R.id.llHeader);

        onClick();
    }

    private void onClick() {
        btnSpeaker.setOnClickListener(v -> {
            if(currentWord != null){
                playAudio(currentWord.getUrlSpeak());
            }
        });
    }

    private void playAudio(String urlSpeak) {
        MediaBuilder.playLink(urlSpeak, new MediaBuilder.MediaCallback() {
            @Override
            public void start() {
                new Handler(Looper.getMainLooper()).post(() -> {
                    btnSpeaker.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                });
            }

            @Override
            public void end() {
                new Handler(Looper.getMainLooper()).post(() -> {
                    btnSpeaker.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                });
            }
        });
    }

    public void showResult(Word word){
        currentWord = word;
        new Handler(Looper.getMainLooper()).post(() -> {
            tvVoice.setText(word.getVoice());
            tvMean.setText(word.getEnWord().substring(0, 1).toUpperCase() + word.getEnWord().substring(1).toLowerCase());
            webViewMean.loadDataWithBaseURL(null, Constant.header + word.getHtmlFullMean() + Constant.endTag, "text/html", "utf-8", null);
            showWebview();
        });
    }

    private void showWebview() {
        webViewMean.setVisibility(View.VISIBLE);
        llHeader.setVisibility(View.VISIBLE);
        tvNoData.setVisibility(View.INVISIBLE);
        tvNoHistory.setVisibility(View.INVISIBLE);
    }
}
