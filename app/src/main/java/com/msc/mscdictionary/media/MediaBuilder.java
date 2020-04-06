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


    private static String link;

    public static void playLink(String en, MediaCallback callback){
        new Thread(() -> {
            String url = "https://dict.laban.vn/ajax/getsound?accent=uk&word=" + en;
            Document doc = null;
            try {
                doc = Jsoup.parse(new URL(url).openStream(), "utf-8", url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(doc == null){
                callback.fail("can't get audio");
                return;
            }

            try {
                JSONObject jsonObj = new JSONObject(doc.text());
                link = jsonObj.optString("data", "");

            } catch (JSONException e) {
                e.printStackTrace();
                callback.fail("error");
            }

            if(link.isEmpty()){
                callback.start();
                callback.end();
            }else {
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
            }
        }).start();
    }

    public interface MediaCallback{
        void start();
        void end();
        void fail(String error);
    }

}
