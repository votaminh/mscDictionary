package com.msc.mscdictionary.network;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.util.Log;

import com.msc.mscdictionary.util.AppUtil;
import com.msc.mscdictionary.util.Constant;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

public class DownloadFile extends AsyncTask<String, Void, Void> {
    private static final int MAX_SIZE_BITMAP = 500;
    WeakReference<Context> contextWeakReference;
    DownloadListener downloadListener;
    String out;
    boolean isError = false;

    public DownloadFile(Context context, String out){
        contextWeakReference = new WeakReference<>(context);
        this.out = out;
    }

    public void setDownloadListener(DownloadListener downloadListener){
        this.downloadListener = downloadListener;
    }

    @Override
    protected Void doInBackground(String... urlString) {
        int count;
        try {
            URL url = new URL(urlString[0]);
            URLConnection conexion = url.openConnection();
            conexion.connect();
            int lenghtOfFile = conexion.getContentLength();
            InputStream input = new BufferedInputStream(url.openStream());

            OutputStream output = new FileOutputStream(out);

            byte data[] = new byte[1024];
            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress((int) ((total * 100) / lenghtOfFile));
                output.write(data, 0, count);
            }
            output.close();
            input.close();

        } catch (Exception e) {
            if(contextWeakReference.get() != null){
                downloadListener.fail(e.toString());
                isError = true;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(contextWeakReference.get() != null && !isError){
            downloadListener.finish();
        }
    }

    private void publishProgress(int progress){
        if(contextWeakReference.get() != null){
            downloadListener.progress(progress);
        }
    }

    public static void downloadBitmap(String link, DownloadBitmapListener downloadListener){
        new Thread(() -> {
            try {
                URL url = new URL(link);
                URLConnection conexion = url.openConnection();
                conexion.connect();
                InputStream input = new BufferedInputStream(url.openStream());
                Bitmap b = BitmapFactory.decodeStream(input);
                input.close();

                if(b.getWidth() > MAX_SIZE_BITMAP){
                    float ratio = b.getWidth()/(float)b.getHeight();
                    b = Bitmap.createScaledBitmap(b, MAX_SIZE_BITMAP, (int) (MAX_SIZE_BITMAP/ratio), false);
                }else if(b.getHeight() > MAX_SIZE_BITMAP){
                    float ratio = b.getWidth()/(float)b.getHeight();
                    b = Bitmap.createScaledBitmap(b, (int) (MAX_SIZE_BITMAP * ratio), MAX_SIZE_BITMAP, false);
                }

                downloadListener.success(b);
            }catch (Exception e){
                downloadListener.fail();
            }
        }).start();
    }

    public static void downloadAudio(String link,String en, DownloadListener listener){
        new Thread(() -> {
            try {
                URL url = new URL(link);
                URLConnection conexion = url.openConnection();
                conexion.connect();
                InputStream input = new BufferedInputStream(url.openStream());

                String pathParrentOffline = Constant.PATH_PARRENT_AUDIO;
                File parent = new File(pathParrentOffline);
                if(!parent.exists()){
                    parent.mkdir();
                }

                File audio = new File(parent, en + ".mp3");
                FileOutputStream out = new FileOutputStream(audio);

                byte[] b = new byte[8];
                while (input.read(b) != -1){
                    out.write(b);
                }

                listener.finish();

                out.flush();
                out.close();
                input.close();
            }catch (Exception e){
                listener.fail(e.toString());
            }
        }).start();
    }

    public interface DownloadListener{
        void progress(int progress);
        void fail(String error);
        void finish();
    }

    public interface DownloadBitmapListener{
        void success(Bitmap b);
        void fail();
    }
}
