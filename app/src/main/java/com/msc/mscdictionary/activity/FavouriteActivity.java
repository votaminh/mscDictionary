package com.msc.mscdictionary.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.msc.mscdictionary.R;
import com.msc.mscdictionary.adaper.FavouriteAdapter;
import com.msc.mscdictionary.base.BaseActivity;
import com.msc.mscdictionary.database.OffFavouriteDAO;
import com.msc.mscdictionary.model.Word;

import java.util.ArrayList;
import java.util.List;

public class FavouriteActivity extends BaseActivity {
    RecyclerView reFavourite;
    FavouriteAdapter favouriteAdapter;
    List<Word> wordList = new ArrayList<>();

    OffFavouriteDAO favouriteDAO;

    @Override
    public int resLayoutId() {
        return R.layout.activiity_favourite;
    }

    @Override
    public void initView() {
        reFavourite = findViewById(R.id.reFavourite);
        favouriteDAO = new OffFavouriteDAO(this);
        buildReFavourite();
    }

    private void buildReFavourite() {
        wordList = favouriteDAO.getAllWordFavourite();
        favouriteAdapter = new FavouriteAdapter(wordList);
        favouriteAdapter.setCallback((i) -> {
            goToMainWithWord(wordList.get(i));
        });
        favouriteAdapter.setRemoveCallback((i) -> {
            checkData();
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, true);
        linearLayoutManager.setStackFromEnd(true);
        reFavourite.setLayoutManager(linearLayoutManager);
        reFavourite.setAdapter(favouriteAdapter);
        findViewById(R.id.progress).setVisibility(View.INVISIBLE);

        checkData();
    }

    private void checkData() {
        if(wordList.size() == 0){
            findViewById(R.id.tvNodata).setVisibility(View.VISIBLE);
            findViewById(R.id.progress).setVisibility(View.INVISIBLE);
        }else {
            findViewById(R.id.tvNodata).setVisibility(View.INVISIBLE);
            findViewById(R.id.progress).setVisibility(View.INVISIBLE);
        }
    }

    private void goToMainWithWord(Word word) {
        Intent intent = new Intent();
        intent.putExtra("en", word.getEnWord());
        setResult(RESULT_OK, intent);
        finish();
    }

}
