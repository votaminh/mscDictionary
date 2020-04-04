package com.msc.mscdictionary.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdView;
import com.msc.mscdictionary.R;
import com.msc.mscdictionary.adaper.FavouriteAdapter;
import com.msc.mscdictionary.adaper.HistoryAdapter;
import com.msc.mscdictionary.ads.AdsHelper;
import com.msc.mscdictionary.base.BaseActivity;
import com.msc.mscdictionary.database.OffHistoryDAO;
import com.msc.mscdictionary.model.Word;
import com.msc.mscdictionary.util.AppUtil;
import com.msc.mscdictionary.util.Constant;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends BaseActivity {
    RecyclerView reHistory;
    HistoryAdapter historyAdapter;
    List<Word> wordList = new ArrayList<>();

    OffHistoryDAO historyDAO;
    private AdView mAdView;

    @Override
    public int resLayoutId() {
        return R.layout.activity_history;
    }

    @Override
    public void initView() {
        new Handler().postDelayed(() -> {
            reHistory = findViewById(R.id.reHistory);
            historyDAO = new OffHistoryDAO(this);
            buildReFavourite();
            continueBuild();
            mAdView =(AdView)findViewById(R.id.adView);
            if(AppUtil.isNetworkConnected(this)){
                AdsHelper.setupAds(mAdView, this);
            }else {
                AdsHelper.goneAds(mAdView);
            }
        }, 500);
    }

    private void buildReFavourite() {
        historyAdapter = new HistoryAdapter(wordList);
        historyAdapter.setCallback((i) -> {
            goToMainWithWord(wordList.get(i));
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        reHistory.setLayoutManager(linearLayoutManager);

        reHistory.setAdapter(historyAdapter);

        new Thread(() -> {
            wordList = historyDAO.getAllHistory();
            wordList = assignWordByDate(wordList);
            new Handler(Looper.getMainLooper()).post(() -> {
                historyAdapter.setData(wordList);
                findViewById(R.id.progress).setVisibility(View.INVISIBLE);
                if(wordList.size() == 0){
                    findViewById(R.id.tvNodata).setVisibility(View.VISIBLE);
                }
            }
            );
        }).start();
    }

    /**
     * Hàm này dùng để thêm các phân tử ảo vào những nơi phân chia ngày . nó sẽ tạo 1 word ảo với en = %dd ,
     * @param wordList
     * @return
     */
    private List<Word> assignWordByDate(List<Word> wordList) {
        wordList = invertPositoinList(wordList);
        List<Word> list = new ArrayList<>();
        String currentTime = "0";
        for (int i = 0; i < wordList.size(); i++) {
            Word word = wordList.get(i);
            if(!currentTime.equals(word.date)){
                Word ghostWord = new Word(Constant.GHOST_EN, "", "", "", "");
                ghostWord.date = word.date;
                list.add(ghostWord);
                currentTime = word.date;
            }
            list.add(word);
        }
        return list;
    }

    private List<Word> invertPositoinList(List<Word> list) {
        List<Word> invertList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            invertList.add(0, list.get(i));
        }
        return invertList;
    }

    private void goToMainWithWord(Word word) {
        Intent intent = new Intent();
        intent.putExtra("en", word.getEnWord());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void continueBuild() {

    }


}