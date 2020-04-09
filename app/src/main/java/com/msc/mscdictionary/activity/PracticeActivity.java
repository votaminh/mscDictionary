package com.msc.mscdictionary.activity;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
    EditText edFrom, edTo;
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
        edFrom = findViewById(R.id.edFrom);
        edTo = findViewById(R.id.edTo);

        initValues();
        onClick();
    }

    private void initValues() {
        favouriteDAO = new OffFavouriteDAO(this);
        listWord = favouriteDAO.getAllWordFavourite();

        edFrom.setText("0");
        edTo.setText(listWord.size() + "");
    }

    private void onClick() {
        tvStart.setOnClickListener(v -> {
            disableAnimate();
            int from = Integer.parseInt(edFrom.getText().toString());
            int to = Integer.parseInt(edTo.getText().toString());

            startGame1(from, to);
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
