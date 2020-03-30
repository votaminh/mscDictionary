package com.msc.mscdictionary.API;

import com.msc.mscdictionary.model.Word;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface DataService {
    @GET("insertWord.php")
    Call<ResponseBody> insertWord(@Query("en") String en, @Query("html") String html, @Query("vi") String vi, @Query("voice") String voice, @Query("url") String urlAudio);

    @GET("checkHasWord.php")
    Call<List<Word>> checkHasWord(@Query("en") String en);

}
