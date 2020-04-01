package com.msc.mscdictionary.network;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

public class DownloadFile extends AsyncTask<String, Void, Void> {
    WeakReference<Activity> contextWeakReference;
    DownloadListener downloadListener;
    String out;

    public DownloadFile(Activity context, String out, DownloadListener downloadListener){
        contextWeakReference = new WeakReference<>(context);
        this.out = out;
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
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(contextWeakReference.get() != null){
            downloadListener.finish();
        }
    }

    private void publishProgress(int progress){
        if(contextWeakReference.get() != null){
            downloadListener.progress(progress);
        }
    }

    public interface DownloadListener{
        void progress(int progress);
        void fail(String error);
        void finish();
    }
}
