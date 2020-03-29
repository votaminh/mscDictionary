package com.msc.mscdictionary.service;

import android.util.Log;
import android.webkit.WebView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;

public class Dictionary {
    String textVi = "";
    TranslateCallback translateCallback;
    private static Dictionary dictionary = null;

    private Dictionary(){

    }
    public static Dictionary instance(String textVi, TranslateCallback translateCallback){
        if(dictionary == null){
            dictionary = new Dictionary();
        }
        dictionary.setTextVi(textVi);
        dictionary.setTranslateCallback(translateCallback);
        return dictionary;
    }

    private void setTextVi(String textVi){
        this.textVi = textVi;
    }
    private void setTranslateCallback(TranslateCallback translateCallback){
        this.translateCallback = translateCallback;
    }

    public void translate(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                String url = "https://dict.laban.vn/find?type=1&query=" + textVi;
                Document doc = null;
                try {
                    doc = Jsoup.parse(new URL(url).openStream(), "utf-8", url);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(doc == null){
                    return;
                }

                doc.outputSettings().charset("utf-8");

                Elements sub = doc.select("div#content_selectable");

                Element content = sub.first();

                Elements meanSelection = content.select("div.content");
                translateCallback.success(meanSelection.toString());
            }
        }.start();
    }

    public interface TranslateCallback{
        void success(String mean);
        void fail(String error);
    }
}
