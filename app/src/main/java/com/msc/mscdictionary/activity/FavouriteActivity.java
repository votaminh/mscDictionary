package com.msc.mscdictionary.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.msc.mscdictionary.R;
import com.msc.mscdictionary.adaper.FavouriteAdapter;
import com.msc.mscdictionary.ads.AdsHelper;
import com.msc.mscdictionary.base.BaseActivity;
import com.msc.mscdictionary.database.OffFavouriteDAO;
import com.msc.mscdictionary.model.Word;
import com.msc.mscdictionary.util.AppUtil;

import java.util.ArrayList;
import java.util.List;

public class FavouriteActivity extends BaseActivity {
    RecyclerView reFavourite;
    FavouriteAdapter favouriteAdapter;
    List<Word> wordList = new ArrayList<>();

    OffFavouriteDAO favouriteDAO;
    private AdView mAdView;

    @Override
    public int resLayoutId() {
        return R.layout.activiity_favourite;
    }

    @Override
    public void initView() {
        reFavourite = findViewById(R.id.reFavourite);
        favouriteDAO = new OffFavouriteDAO(this);
        buildReFavourite();
        mAdView =(AdView)findViewById(R.id.adView);
        if(AppUtil.isNetworkConnected(this)){
            AdsHelper.setupAds(mAdView, this);
        }else {
            AdsHelper.goneAds(mAdView);
        }
    }

    private void buildReFavourite() {
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
        new Thread(() -> {
            wordList = favouriteDAO.getAllWordFavourite();
            new Handler(Looper.getMainLooper()).post(() -> {
                favouriteAdapter.setData(wordList);
                findViewById(R.id.progress).setVisibility(View.INVISIBLE);
                checkData();
            });
        }).start();

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
