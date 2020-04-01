package com.msc.mscdictionary.network;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;



import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Created by medchiheb at octadev on 4/07/2017.
 */

public class DowloadUnzip {


    private DownloadListener mTheListener;
    private String zipFile;

    public DowloadUnzip(String path){
        zipFile = path;
    }

    public String getZipFile() {
        return zipFile;
    }

    public void setZipFile(String zipFile) {
        this.zipFile = zipFile;
    }

    public void dowload(final String zipurl) {
        new AsyncTask<String, String, String>()

        {

            String result = "";

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mTheListener.DownloadProgress(1, 0);


            }

            @Override
            protected String doInBackground(String... params) {

                int count;

                try {
                    URL url = new URL(zipurl);
                    URLConnection conexion = url.openConnection();
                    conexion.connect();
                    int lenghtOfFile = conexion.getContentLength();
                    InputStream input = new BufferedInputStream(url.openStream());

                    OutputStream output = new FileOutputStream(zipFile);

                    byte data[] = new byte[1024];
                    long total = 0;

                    while ((count = input.read(data)) != -1) {
                        total += count;
                        publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                        output.write(data, 0, count);
                    }
                    output.close();
                    input.close();
                    result = "true";

                } catch (Exception e) {

                    result = "false";
                }
                return result;
            }

            protected void onProgressUpdate(String... progress) {
                Log.d("ANDRO_ASYNC", progress[0]);
                mTheListener.DownloadProgress(1, Integer.parseInt(progress[0]));

            }

            @Override
            protected void onPostExecute(String mmmm) {
                super.onPostExecute(result);
//                if (result.equalsIgnoreCase("true")) {
//                    try {
//                        unzip();
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                } else {
//                    mTheListener.DownloadProgress(-1, 0);
//
//                }
            }
        }.execute(zipurl);


    }



//
//    public void unzip() throws IOException {
//
//
//        new AsyncTask<String, Void, Boolean>() {
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//                mTheListener.DownloadProgress(2, 0);
//
//
//            }
//
//            @Override
//            protected Boolean doInBackground(String... params) {
//                String filePath = params[0];
//                String destinationPath = params[1];
//
//                File archive = new File(filePath);
//                try {
//
//
//                    ZipFile zipfile = new ZipFile(archive);
//                    for (Enumeration e = zipfile.entries(); e.hasMoreElements(); ) {
//                        ZipEntry entry = (ZipEntry) e.nextElement();
//                        unzipEntry(zipfile, entry, destinationPath);
//                    }
//
//
//                    UnzipUtil d = new UnzipUtil(zipFile, directory);
//                    d.unzip();
//
//                } catch (Exception e) {
//
//                    return false;
//                }
//
//                return true;
//            }
//
//            @Override
//            protected void onPostExecute(Boolean result) {
//                mTheListener.DownloadProgress(3, 0);
//
//                super.onPostExecute(result);
//            }
//        }.execute(zipFile);
//
//    }
//
//
//    private void unzipEntry(ZipFile zipfile, ZipEntry entry,
//                            String outputDir) throws IOException {
//        if (!entry.isDirectory() && !entry.getName().contains("_")) {
//            if (entry.isDirectory()) {
//                createDir(new File(outputDir,  entry.getName()));
//                return;
//            }
//
//            File outputFile = new File(outputDir,  entry.getName());
//            if (!outputFile.getParentFile().exists()) {
//                createDir(outputFile.getParentFile());
//            }
//
//            // Log.v("", "Extracting: " + entry);
//            BufferedInputStream inputStream = new BufferedInputStream(zipfile.getInputStream(entry));
//            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
//
//            try {
//
//            } finally {
//                outputStream.flush();
//                outputStream.close();
//                inputStream.close();
//
//
//            }
//        }
//    }

    private void createDir(File dir) {
        if (dir.exists()) {
            return;
        }
        if (!dir.mkdirs()) {
            throw new RuntimeException("Can not create dir " + dir);
        }
    }

    public void setDownloadListener(DownloadListener listen) {
        mTheListener = listen;
    }

    public interface DownloadListener {
         void DownloadProgress(int event, int position);
    }

    public class UnzipUtil {
        private String _zipFile;
        private String _location;

        public UnzipUtil(String zipFile, String location) {
            _zipFile = zipFile;
            _location = location;

        }

        public void unzip() {
            try {
                FileInputStream fin = new FileInputStream(_zipFile);
                ZipInputStream zin = new ZipInputStream(fin);
                ZipEntry ze = null;
                while ((ze = zin.getNextEntry()) != null) {
                    Log.v("Decompress", "Unzipping " + ze.getName());
                    if (!ze.getName().contains("_")) {

                        if (ze.isDirectory()) {
                            // _dirChecker("a"+ ze.getName());
                        } else {
                            if (!ze.getName().contains("M")) {
                                FileOutputStream fout = new FileOutputStream(_location + ze.getName());
                                //   for (int c = zin.read(); c != -1; c = zin.read()) {
                                //   fout.write(c);


                                byte[] buffer = new byte[8192];
                                int len;
                                while ((len = zin.read(buffer)) != -1) {
                                    fout.write(buffer, 0, len);
                                }
                                fout.close();

                                //  }

                                zin.closeEntry();
                                fout.close();
                            }
                        }
                    }
                }
                zin.close();
            } catch (Exception e) {
                Log.e("Decompress", "unzip", e);
            }

        }


    }

}
