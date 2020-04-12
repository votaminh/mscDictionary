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
import java.net.URLConnection;

public class MediaBuilder {
    public static MediaPlayer mediaPlayer = null;
    public static void playLink(String link, MediaCallback callback) {
        new Thread(() -> {
            callback.start();
            try {
                String link1 = link;
                if(link1.contains("github")){
                    URL url = new URL(link1);
                    URLConnection conexion = url.openConnection();
                    conexion.connect();
                }

                mediaPlayer = new MediaPlayer();
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
            } catch (Exception e) {
                e.printStackTrace();
                callback.fail(e.toString());
            }
        }).start();
    }

    public static void stop() {
        if(mediaPlayer != null){
            mediaPlayer.stop();
        }
    }

    public interface MediaCallback{
        void start();
        void end();
        void fail(String error);
    }

}
