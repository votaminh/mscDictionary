package com.msc.mscdictionary.media;

import android.media.AudioManager;
import android.media.MediaPlayer;

import com.msc.mscdictionary.model.Word;
import com.msc.mscdictionary.network.DictionaryCrawl;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;

public class MediaBuilder {

    public static void playLink(String link, MediaCallback callback) {
        new Thread(() -> {
            callback.start();
            try {
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(link);
                mediaPlayer.prepare();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        callback.end();
                    }

                });
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public interface MediaCallback{
        void start();
        void end();
        void fail(String error);
    }

}
