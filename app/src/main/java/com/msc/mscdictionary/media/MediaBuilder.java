package com.msc.mscdictionary.media;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

public class MediaBuilder {


    public static void playLink(String link, MediaCallback callback){
        if(link.isEmpty()){
            callback.start();
            callback.end();
        }else {
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
    }

    public interface MediaCallback{
        void start();
        void end();
        void fail(String error);
    }
}
