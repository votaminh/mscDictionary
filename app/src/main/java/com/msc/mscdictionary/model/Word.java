package com.msc.mscdictionary.model;

import com.google.gson.annotations.SerializedName;
import com.msc.mscdictionary.util.Constant;

public class Word {
    @SerializedName("id")
    private int id;

    @SerializedName("en")
    private String enWord;

    @SerializedName("html")
    private String htmlFullMean;

    @SerializedName("vi")
    private String commonMean;

    @SerializedName("voice")
    private String voice;

    @SerializedName("url")
    private String urlSpeak;

    public String date;

    public Word() {
    }

    public Word(int id,String enWord, String htmlFullMean, String commonMean, String voice, String urlSpeak) {
        if(enWord == null || enWord.isEmpty()){
            enWord = "";
        }

        if(htmlFullMean == null || htmlFullMean.isEmpty()){
            htmlFullMean = "";
        }

        if (commonMean == null || commonMean.isEmpty()){
            commonMean ="";
        }
        if(voice == null || voice.isEmpty()){
            voice = "";
        }
        if(urlSpeak == null || urlSpeak.isEmpty()){
            urlSpeak = "";
        }
        this.id = id;
        this.enWord = enWord;
        this.htmlFullMean = htmlFullMean;
        this.commonMean = commonMean;
        this.voice = voice;
        this.urlSpeak = urlSpeak;
    }

    public Word(String enWord, String htmlFullMean, String commonMean, String voice, String urlSpeak) {
        if(enWord == null || enWord.isEmpty()){
            enWord = "";
        }

        if(htmlFullMean == null || htmlFullMean.isEmpty()){
            htmlFullMean = "";
        }

        if (commonMean == null || commonMean.isEmpty()){
            commonMean ="";
        }
        if(voice == null || voice.isEmpty()){
            voice = "";
        }
        if(urlSpeak == null || urlSpeak.isEmpty()){
            urlSpeak = "";
        }
        this.htmlFullMean = htmlFullMean;
        this.enWord = enWord;
        this.commonMean = commonMean;
        this.voice = voice;
        this.urlSpeak = urlSpeak;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHtmlFullMean() {
        return htmlFullMean;
    }

    public void setHtmlFullMean(String htmlFullMean) {
        this.htmlFullMean = htmlFullMean;
    }

    public String getCommonMean() {
        return commonMean;
    }

    public void setCommonMean(String commonMean) {
        this.commonMean = commonMean;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public String getUrlSpeak() {
        return urlSpeak;
    }

    public void setUrlSpeak(String urlSpeak) {
        this.urlSpeak = urlSpeak;
    }

    public String getEnWord() {
        return enWord;
    }

    public void setEnWord(String enWord) {
        this.enWord = enWord;
    }
}
