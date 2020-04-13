package com.msc.mscdictionary.media;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.msc.mscdictionary.R;
import com.msc.mscdictionary.database.OffWordDAO;
import com.msc.mscdictionary.firebase.MyFirebase;
import com.msc.mscdictionary.model.Word;
import com.msc.mscdictionary.network.DictionaryCrawl;
import com.msc.mscdictionary.network.DownloadFile;
import com.msc.mscdictionary.util.AppUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class MediaBuilder {
    public static MediaPlayer mediaPlayer = null;
    public static void playLink(Context context, String link, MediaCallback callback) {
        new Thread(() -> {
            callback.start();
            try {
                String link1 = link;
                if(link1.contains("github")){
                    if(AppUtil.isNetworkConnected(context)){
                        URL url = new URL(link1);
                        URLConnection conexion = url.openConnection();
                        conexion.connect();
                    }else {
                        callback.fail(context.getString(R.string.no_connect));
                    }
                }else if(link1.contains("http")){
                    if(!AppUtil.isNetworkConnected(context)){
                        callback.fail(context.getString(R.string.no_connect));
                        return;
                    }
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
                callback.fail(context.getString(R.string.no_audio));
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
