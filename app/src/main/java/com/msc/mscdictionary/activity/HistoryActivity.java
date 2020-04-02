package com.msc.mscdictionary.activity;

import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.msc.mscdictionary.R;
import com.msc.mscdictionary.adaper.FavouriteAdapter;
import com.msc.mscdictionary.adaper.HistoryAdapter;
import com.msc.mscdictionary.base.BaseActivity;
import com.msc.mscdictionary.database.OffHistoryDAO;
import com.msc.mscdictionary.model.Word;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends BaseActivity {
    RecyclerView reHistory;
    HistoryAdapter historyAdapter;
    List<Word> wordList = new ArrayList<>();

    OffHistoryDAO historyDAO;

    @Override
    public int resLayoutId() {
        return R.layout.activity_history;
    }

    @Override
    public void intView() {
        reHistory = findViewById(R.id.reHistory);
        historyDAO = new OffHistoryDAO(this);
        buildReFavourite();
        continueBuild();
    }

    private void buildReFavourite() {
        wordList = historyDAO.getAllHistory();
        historyAdapter = new HistoryAdapter(wordList);
        historyAdapter.setCallback((i) -> {
            goToMainWithWord(wordList.get(i));
        });
        reHistory.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        reHistory.setAdapter(historyAdapter);
        findViewById(R.id.progress).setVisibility(View.INVISIBLE);
        if(wordList.size() == 0){
            findViewById(R.id.tvNodata).setVisibility(View.VISIBLE);
        }
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