package com.msc.mscdictionary.activity;

import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.msc.mscdictionary.R;
import com.msc.mscdictionary.base.BaseActivity;
import com.msc.mscdictionary.database.OffFavouriteDAO;
import com.msc.mscdictionary.fragment.Game1Fragment;
import com.msc.mscdictionary.model.Word;

import java.util.ArrayList;
import java.util.List;

public class PracticeActivity extends BaseActivity {
    LottieAnimationView animationView;

    TextView tvStart;
    EditText edAmount;
    List<Word> listWord = new ArrayList<>();

    OffFavouriteDAO favouriteDAO;

    Game1Fragment game1Fragment;
    @Override
    public int resLayoutId() {
        return R.layout.activity_practice;
    }

    @Override
    public void initView() {
        animationView = findViewById(R.id.animate);
        enableAnimate();

        tvStart = findViewById(R.id.tvStart);
        edAmount = findViewById(R.id.edAmount);

        initValues();
        onClick();
    }

    private void initValues() {
        favouriteDAO = new OffFavouriteDAO(this);
        listWord = favouriteDAO.getAllWordFavourite();

        edAmount.setText(listWord.size() + "");
        edAmount.setSelection(edAmount.getText().length());
        edAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int i = Integer.parseInt(s.toString());
                    if( i > listWord.size()){
                        edAmount.setText(listWord.size() + "");
                        edAmount.setSelection(edAmount.getText().length());
                        new Handler().post(() -> {
                            Toast.makeText(PracticeActivity.this, getString(R.string.error_more_than_size) + listWord.size(), Toast.LENGTH_SHORT).show();
                        });
                    }else if( i <= 0){
                        edAmount.setText(1 + "");
                        edAmount.setSelection(edAmount.getText().length());
                        new Handler().post(() -> {
                            Toast.makeText(PracticeActivity.this, getString(R.string.error_min_size), Toast.LENGTH_SHORT).show();
                        });
                    }
                }catch (Exception e){

                }
            }
        });
    }

    private void onClick() {
        tvStart.setOnClickListener(v -> {
            if(listWord.size() == 0){
                Toast.makeText(this, getString(R.string.wanted_practice), Toast.LENGTH_SHORT).show();
            }else {
                disableAnimate();
                int from = 0;
                int to = Integer.parseInt(edAmount.getText().toString());
                startGame1(from, to);
            }
        });
    }

    private void startGame1(int from, int to) {

        List<Word> listPractice = new ArrayList<>();
        for (int i = from; i < to; i++) {
            listPractice.add(listWord.get(i));
        }

        game1Fragment = (Game1Fragment) findFragment(Game1Fragment.TAG);
        if(game1Fragment == null){
            game1Fragment = new Game1Fragment();
        }

        game1Fragment.setListWord(listPractice);
        replaceFragment(game1Fragment, R.id.container, Game1Fragment.TAG);
    }

    private List<Word> loadFavouriteWord(int from, int to) {
        return null;
    }

    private void disableAnimate() {
        animationView.setLayerType(View.LAYER_TYPE_NONE, null);
        animationView.pauseAnimation();
    }

    private void enableAnimate() {
        animationView.setAnimation("18525-isometric-typography.json");
        animationView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        animationView.playAnimation();
    }
}
