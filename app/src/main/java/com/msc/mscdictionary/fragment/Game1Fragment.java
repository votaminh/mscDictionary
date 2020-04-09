package com.msc.mscdictionary.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.msc.mscdictionary.R;
import com.msc.mscdictionary.base.BaseFragment;
import com.msc.mscdictionary.model.Word;

import java.util.List;

public class Game1Fragment extends BaseFragment {
    public static final String TAG = "game1Fragment";
    private List<Word> listWord;

    ImageView imvMean, imvBlurBg, imvSpeak;
    TextView tvMean, tvPronounce, tvWord;

    int index = 0;

    @Override
    public int resLayoutId() {
        return R.layout.game_1_fragment;
    }

    @Override
    public void initView(View view) {
        imvMean = view.findViewById(R.id.imvMean);
        imvBlurBg = view.findViewById(R.id.imvBlurBg);
        imvSpeak = view.findViewById(R.id.imvSpeaker);

        tvMean = view.findViewById(R.id.tvMean);
        tvPronounce = view.findViewById(R.id.tvPronounce);
        tvWord = view.findViewById(R.id.tvWord);

        startGame(index);
    }

    private void startGame(int index) {
        Word word = listWord.get(index);

        tvWord.setText(word.getEnWord());
        tvPronounce.setText(word.getVoice());
        tvMean.setText(word.getCommonMean());


    }

    public void setListWord(List<Word> listPractice) {
        this.listWord = listPractice;
    }
}
