package com.msc.mscdictionary.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.msc.mscdictionary.R;
import com.msc.mscdictionary.base.BaseFragment;
import com.msc.mscdictionary.firebase.MyFirebase;
import com.msc.mscdictionary.media.MediaBuilder;
import com.msc.mscdictionary.model.Word;
import com.msc.mscdictionary.network.DownloadFile;
import com.msc.mscdictionary.util.AppUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import jp.wasabeef.blurry.Blurry;

public class Game1Fragment extends BaseFragment {
    public static final String TAG = "game1Fragment";
    private List<Word> listWord = new ArrayList<>();

    ImageView imvMean, imvBlurBg, imvSpeak;
    TextView tvMean, tvPronounce, tvWord;
    TextView tvA, tvB, tvC, tvD;
    ProgressBar pA, pB, pC, pD;
    ProgressBar progressBar;
    TextView tvTime, tvCount;
    int index = 0;
    private Word currentWord;

    int correctAnswer;
    int userAnswer;
    private boolean isClicked = false;
    float alphaText = 1;
    int timeAni = 0;
    RelativeLayout llRoot;

    Bitmap nextBitmap, noImageBitmap;
    private boolean runGame = false;
    int timeSecond = 0;
    @Override
    public int resLayoutId() {
        return R.layout.game_1_fragment;
    }

    @Override
    public void initView(View view) {
        llRoot = view.findViewById(R.id.root);
        imvMean = view.findViewById(R.id.imvMean);
        imvBlurBg = view.findViewById(R.id.imvBlurBg);
        imvSpeak = view.findViewById(R.id.imvSpeaker);

        progressBar = view.findViewById(R.id.progress);
        tvMean = view.findViewById(R.id.tvMean);
        tvPronounce = view.findViewById(R.id.tvPronounce);
        tvWord = view.findViewById(R.id.tvWord);

        tvA = view.findViewById(R.id.tvA);
        tvB = view.findViewById(R.id.tvB);
        tvC = view.findViewById(R.id.tvC);
        tvD = view.findViewById(R.id.tvD);

        pA = view.findViewById(R.id.progressA);
        pB = view.findViewById(R.id.progressB);
        pC = view.findViewById(R.id.progressC);
        pD = view.findViewById(R.id.progressD);
        hideAllProgress();

        tvTime = view.findViewById(R.id.tvTime);
        tvCount = view.findViewById(R.id.tvCount);

        noImageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_gallery);

        runGame = true;
        loadFirstResource();
        runTime();
    }

    private void runTime() {
        new Thread(() -> {
            while (runGame){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                timeSecond ++;

                long minutes = TimeUnit.SECONDS.toMinutes(timeSecond);
                long restTimeSecond = timeSecond - TimeUnit.MINUTES.toSeconds(minutes);

                long seconds = TimeUnit.SECONDS.toSeconds(restTimeSecond);

                new Handler(Looper.getMainLooper()).post(() -> {
                    if(minutes == 0){
                        tvTime.setText("" +seconds);
                    }else {
                        tvTime.setText(minutes + " : " + seconds);
                    }
                });
            }
        }).start();
    }

    private void loadFirstResource() {
        DownloadFile.downloadBitmap(AppUtil.getLinkForWord(listWord.get(0).getEnWord()), new DownloadFile.DownloadBitmapListener() {
            @Override
            public void success(Bitmap b) {
                nextBitmap = b;
                finishLoadFirst();
            }

            @Override
            public void fail() {
                nextBitmap = Bitmap.createBitmap(noImageBitmap);
                finishLoadFirst();
                MyFirebase.addWordNoImage(listWord.get(0).getEnWord());
            }
        });
    }

    private void finishLoadFirst() {
        new Handler(Looper.getMainLooper()).post(() -> startGame(index));
    }

    private void hideAllProgress() {
        pA.setVisibility(View.INVISIBLE);
        pB.setVisibility(View.INVISIBLE);
        pC.setVisibility(View.INVISIBLE);
        pD.setVisibility(View.INVISIBLE);
    }

    private void onClick() {
        isClicked = false;
        imvSpeak.setOnClickListener(v -> {
            playAudio(currentWord.getUrlSpeak());
        });

        tvA.setOnClickListener(v -> {
            if(!isClicked){
                userAnswer = 1;
                isClicked = true;
                tvA.setBackgroundResource(R.drawable.bg_text_click);
                tvA.setTextColor(getResources().getColor(R.color.white));

                pA.setVisibility(View.VISIBLE);

                checkAnswer();
            }
        });

        tvB.setOnClickListener(v -> {
            if(!isClicked){
                userAnswer = 2;
                isClicked = true;
                tvB.setBackgroundResource(R.drawable.bg_text_click);
                tvB.setTextColor(getResources().getColor(R.color.white));

                pB.setVisibility(View.VISIBLE);
                checkAnswer();
            }

        });

        tvC.setOnClickListener(v -> {
            if(!isClicked){
                userAnswer = 3;
                isClicked = true;
                tvC.setBackgroundResource(R.drawable.bg_text_click);
                tvC.setTextColor(getResources().getColor(R.color.white));

                pC.setVisibility(View.VISIBLE);
                checkAnswer();
            }
        });

        tvD.setOnClickListener(v -> {
            if(!isClicked){
                userAnswer = 4;
                isClicked = true;
                tvD.setBackgroundResource(R.drawable.bg_text_click);
                tvD.setTextColor(getResources().getColor(R.color.white));

                pD.setVisibility(View.VISIBLE);
                checkAnswer();
            }
        });
    }

    private void checkAnswer() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            hideAllProgress();

            tvWord.setText(currentWord.getEnWord());

            // Đầu tiên hiện ô đúng lên animation
            switch (correctAnswer){
                case 1 :
                    animation(tvA, R.drawable.bg_correct);
                    break;
                case 2 :
                    animation(tvB, R.drawable.bg_correct);
                    break;
                case 3 :
                    animation(tvC, R.drawable.bg_correct);
                    break;
                case 4 :
                    animation(tvD, R.drawable.bg_correct);
                    break;
            }

            // nếu câu trả lời sai thì mới hiện
            if(correctAnswer != userAnswer){
                switch (userAnswer){
                    case 1 :
                        setWrong(tvA);
                        break;
                    case 2 :
                        setWrong(tvB);
                        break;
                    case 3 :
                        setWrong(tvC);
                        break;
                    case 4 :
                        setWrong(tvD);
                        break;
                }
            }
        }, 1000);
    }

    private void setWrong(TextView tv) {
        tv.setBackgroundResource(R.drawable.bg_wrong);
        tv.setTextColor(getResources().getColor(R.color.white));
    }

    private void animation(TextView tv, int bg_correct) {
        tv.setBackgroundResource(bg_correct);
        tv.setTextColor(getResources().getColor(R.color.white));
        setupAnimation(tv);
        timeAni = 0;
    }

    private void setupAnimation(TextView tv) {
        if(alphaText == 1){
            startAnimation(1, 0, tv);
        }else {
            startAnimation(0, 1, tv);
        }
    }

    private void startAnimation(int i, int i1, View view) {
        timeAni ++;
        ValueAnimator va = ValueAnimator.ofFloat(i, i1);
        int mDuration = 500;
        va.setDuration(mDuration);
        va.setInterpolator(new AccelerateInterpolator());
        va.addUpdateListener((animation -> {
            alphaText = (float) animation.getAnimatedValue();
            view.setAlpha(alphaText);
        }));
        va.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(timeAni < 3){
                    setupAnimation((TextView) view);
                }else {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if(index == listWord.size() - 1){
                            finishGame();
                        }else {
                            index ++;
                            startGame(index);
                        }
                    });
                }
            }
        });

        va.start();
    }

    private void finishGame() {
        runGame = false;
    }

    private void startGame(int index) {
        resetBackground();
        currentWord = listWord.get(index);
        tvCount.setText((index + 1) + "/" + listWord.size());

        tvWord.setText("?");
        tvPronounce.setText(currentWord.getVoice());
        tvMean.setText(currentWord.getCommonMean());
        imvMean.setImageBitmap(nextBitmap);
        Blurry.with(getContext()).radius(100).from(nextBitmap).into(imvBlurBg);

        if(!currentWord.getUrlSpeak().isEmpty()){
            playAudio(currentWord.getUrlSpeak());
        }else {
            imvSpeak.setVisibility(View.INVISIBLE);
        }

        correctAnswer = random(1, 4);
        setCorrectAnswer();

        String[] randomAnswer = getRandomAnswer();
        setRandomAnswer(randomAnswer);

        onClick();
        loadNextResource(index);
    }

    private void resetBackground() {
        tvA.setBackgroundResource(R.drawable.bg_transparent);
        tvA.setTextColor(getResources().getColor(R.color.black));
        tvB.setBackgroundResource(R.drawable.bg_transparent);
        tvB.setTextColor(getResources().getColor(R.color.black));
        tvC.setBackgroundResource(R.drawable.bg_transparent);
        tvC.setTextColor(getResources().getColor(R.color.black));
        tvD.setBackgroundResource(R.drawable.bg_transparent);
        tvD.setTextColor(getResources().getColor(R.color.black));
    }

    private void loadNextResource(int index) {
        if(index == listWord.size() - 1){
            return;
        }
        int i = index + 1;
        Word nextWord = listWord.get(i);
        String link = AppUtil.getLinkForWord(nextWord.getEnWord());
        DownloadFile.downloadBitmap(link, new DownloadFile.DownloadBitmapListener() {
            @Override
            public void success(Bitmap b) {
                nextBitmap = b;
            }

            @Override
            public void fail() {
                MyFirebase.addWordNoImage(nextWord.getEnWord());
                nextBitmap = Bitmap.createBitmap(noImageBitmap);
            }
        });
    }

    private void setRandomAnswer(String[] randomAnswer) {
        switch (correctAnswer){
            case 1 :
                tvB.setText(randomAnswer[0]);
                tvC.setText(randomAnswer[1]);
                tvD.setText(randomAnswer[2]);
                break;
            case 2 :
                tvA.setText(randomAnswer[0]);
                tvC.setText(randomAnswer[1]);
                tvD.setText(randomAnswer[2]);
                break;
            case 3 :
                tvB.setText(randomAnswer[0]);
                tvA.setText(randomAnswer[1]);
                tvD.setText(randomAnswer[2]);
                break;
            case 4 :
                tvB.setText(randomAnswer[0]);
                tvC.setText(randomAnswer[1]);
                tvA.setText(randomAnswer[2]);
                break;
        }
    }

    private String[] getRandomAnswer() {
        String[] strings = new String[3];
        strings[0] = getRandomWord(0, listWord.size() -1);
        strings[1] = getRandomWord(0, listWord.size() - 1);
        strings[2] = getRandomWord(0, listWord.size() - 1);

        return strings;
    }

    private String getRandomWord(int i, int size) {
        String en = listWord.get(random(i, size)).getEnWord();
        if(en.equals(currentWord.getEnWord())){
            return getRandomWord(i, size);
        }else {
            return en;
        }
    }

    private void setCorrectAnswer() {
        switch (correctAnswer){
            case 1 :
                tvA.setText(currentWord.getEnWord());
                break;
            case 2 :
                tvB.setText(currentWord.getEnWord());
                break;
            case 3 :
                tvC.setText(currentWord.getEnWord());
                break;
            case 4 :
                tvD.setText(currentWord.getEnWord());
                break;
        }
    }

    private void playAudio(String link){
        MediaBuilder.playLink(link, new MediaBuilder.MediaCallback() {
            @Override
            public void start() {
                new Handler(Looper.getMainLooper()).post(() -> {
                   progressBar.setVisibility(View.VISIBLE);
                   imvSpeak.setVisibility(View.INVISIBLE);
                });
            }

            @Override
            public void end() {
                new Handler(Looper.getMainLooper()).post(() -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    imvSpeak.setVisibility(View.VISIBLE);
                });
            }

            @Override
            public void fail(String error) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    imvSpeak.setVisibility(View.VISIBLE);
                });
            }
        });
    }

    public int random(int min, int max){
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }


    public void setListWord(List<Word> listPractice) {
        randomElementWord(listPractice);
    }

    private void randomElementWord(List<Word> listPractice) {
        int r = random(0, listPractice.size() - 1);
        listWord.add(listPractice.get(r));
        listPractice.remove(r);
        if(listPractice.size() > 0){
            randomElementWord(listPractice);
        }
    }
}
