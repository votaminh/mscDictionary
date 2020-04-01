package com.msc.mscdictionary.network;

import com.msc.mscdictionary.model.Word;
import com.msc.mscdictionary.util.Constant;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;

public class DictionaryCrawl {
    String textEn = "";
    TranslateCallback translateCallback;
    private static DictionaryCrawl dictionaryCrawl = null;

    private DictionaryCrawl(){

    }
    public static DictionaryCrawl instance(String textVi, TranslateCallback translateCallback){
        if(dictionaryCrawl == null){
            dictionaryCrawl = new DictionaryCrawl();
        }
        dictionaryCrawl.setTextEn(textVi);
        dictionaryCrawl.setTranslateCallback(translateCallback);
        return dictionaryCrawl;
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
                textEn = textEn.replace(",", "");
                textEn = textEn.replace(".", "");
                textEn = textEn.toLowerCase();
                String url = Constant.BASELINK_SOHA + textEn;
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

                String voice = "";
                if( elementFirstWord.select("span.color-black").first() != null){
                    voice = elementFirstWord.select("span.color-black").first().text();
                }

                String urlVoide = "";

                Element content = sub.first();

                try{
                    Elements meanSelection = content.select("div.content");
                    String commonMean = meanSelection.first().select("div.margin25").first().text();

                    Word w = new Word(textEn, meanSelection.toString(), commonMean, voice, urlVoide);
                    getLinkAudio(w, new TranslateCallback() {
                        @Override
                        public void success(Word word) {
                            WordDAO.insertWord(word);
                            translateCallback.success(word);
                        }

                        @Override
                        public void fail(String error) {
                            WordDAO.insertWord(w);
                            translateCallback.success(w);
                        }
                    });
                }catch (Exception e){
                    translateCallback.fail("no word");
                }


            }
        }.start();
    }

    public static void getLinkAudio(Word word, TranslateCallback callback){
        String url = "https://dict.laban.vn/ajax/getsound?accent=uk&word=" + word.getEnWord();
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
            String link = jsonObj.optString("data", "");
            word.setUrlSpeak(link);
            callback.success(word);
        } catch (JSONException e) {
            e.printStackTrace();
            callback.fail("error");
        }

    }

    public interface TranslateCallback{
        void success(Word word);
        void fail(String error);
    }
}
