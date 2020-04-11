package com.msc.mscdictionary.network;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.msc.mscdictionary.util.Constant;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

public class DownloadFile extends AsyncTask<String, Void, Void> {
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

                downloadListener.success(b);
            }catch (Exception e){
                downloadListener.fail();
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
