package com.msc.mscdictionary.network;

import android.util.Log;

import com.msc.mscdictionary.API.APIRetrofit;
import com.msc.mscdictionary.API.DataService;
import com.msc.mscdictionary.model.Word;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WordDAO {

    private static DataService dataService = APIRetrofit.getAPI().create(DataService.class);
    public static void insertWord(Word word){
        Call<ResponseBody> response = dataService.insertWord(word.getEnWord(), word.getHtmlFullMean(), word.getCommonMean(), word.getVoice(), word.getUrlSpeak());
        response.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    String s = null;
                    try {
                        s = new String(response.body().bytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("lala", "onFailure: ");
            }
        });
    }

    public static boolean checkHasWord(Word word, DictionaryCrawl.TranslateCallback callback){
        word.setEnWord(word.getEnWord().toLowerCase());
        Call<List<Word>> response = dataService.checkHasWord(word.getEnWord());
        response.enqueue(new Callback<List<Word>>() {
            @Override
            public void onResponse(Call<List<Word>> call, Response<List<Word>> response) {
                List<Word> list = response.body();
                if(list.size() > 0){
                    callback.success(list.get(0));
                }else {
                    callback.fail("");
                }
            }

            @Override
            public void onFailure(Call<List<Word>> call, Throwable t) {
                callback.fail(t.toString());
            }
        });
        return true;
    }
}
