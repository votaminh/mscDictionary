package com.msc.mscdictionary.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceUtil {
    public static void saveStringPereferences(Context context, String name, String value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.SHAREPREFERENCES, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(name, value).commit();
    }

    public static void saveIntPereferences(Context context, String name, int value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.SHAREPREFERENCES, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(name, value).commit();
    }

    public static void saveFloatPereferences(Context context, String name, Float value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.SHAREPREFERENCES, Context.MODE_PRIVATE);
        sharedPreferences.edit().putFloat(name, value).commit();
    }

    public static void saveBooleanPereferences(Context context, String name, boolean value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.SHAREPREFERENCES, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(name, value).commit();
    }

    public static boolean getBooleanPerferences(Context context, String name, boolean defaultValule){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.SHAREPREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(name, defaultValule);
    }

    public static int getIntPereferences(Context context, String name, int defaultValule) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.SHAREPREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(name, defaultValule);
    }

    public static String getStringPereferences(Context context, String name, String defaultValule) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.SHAREPREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(name, defaultValule);
    }

}
