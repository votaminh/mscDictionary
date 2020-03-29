package com.msc.mscdictionary.service;

import com.msc.mscdictionary.model.Word;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;

public class Dictionary {
    String textEn = "";
    TranslateCallback translateCallback;
    private static Dictionary dictionary = null;

    private Dictionary(){

    }
    public static Dictionary instance(String textVi, TranslateCallback translateCallback){
        if(dictionary == null){
            dictionary = new Dictionary();
        }
        dictionary.setTextEn(textVi);
        dictionary.setTranslateCallback(translateCallback);
        return dictionary;
    }

    private void setTextEn(String textEn){
        this.textEn = textEn;
    }
    private void setTranslateCallback(TranslateCallback translateCallback){
        this.translateCallback = translateCallback;
    }

    public void translate(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                String url = "https://dict.laban.vn/find?type=1&query=" + textEn;
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

                Elements word = doc.select("div.world");
                Element elementFirstWord = word.first();

                String voice = elementFirstWord.select("span.color-black").first().text();

                String urlVoide = "";

                Element content = sub.first();

                Elements meanSelection = content.select("div.content");

                String commonMean = meanSelection.first().select("div.margin25").first().text();
                translateCallback.success(new Word(textEn, meanSelection.toString(), commonMean, voice, urlVoide));
            }
        }.start();
    }

    public interface TranslateCallback{
        void success(Word word);
        void fail(String error);
    }
}
