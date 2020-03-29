package com.msc.mscdictionary.model;

import com.msc.mscdictionary.util.Constant;

public class Word {
    private int id;
    private String enWord;
    private String htmlFullMean;
    private String commonMean;
    private String voice;
    private String urlSpeak;

    public Word() {
    }

    public Word(int id,String enWord, String htmlFullMean, String commonMean, String voice, String urlSpeak) {
        this.id = id;
        this.enWord = enWord;
        this.htmlFullMean = Constant.header + htmlFullMean + Constant.endTag;
        this.commonMean = commonMean;
        this.voice = voice;
        this.urlSpeak = urlSpeak;
    }

    public Word(String enWord, String htmlFullMean, String commonMean, String voice, String urlSpeak) {
        this.htmlFullMean = Constant.header + htmlFullMean + Constant.endTag;
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
