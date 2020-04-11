package com.msc.mscdictionary.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;

import com.msc.mscdictionary.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

public class AppUtil {

    public static void createNotificationChannel(Context context, String id, int important) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = important;
            NotificationChannel channel = new NotificationChannel(id, id, importance);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public static String decodingHtml(String encoding) {
        String s = encoding;
        char c = '"';
        s = s.replace("%3C", "<");
        s = s.replace("%3E", ">");
        s = s.replace("%2F", "/");
        s = s.replace("%22", c +"");
        s = s.replace("%3D", "=");
        s = s.replace("%26", "&");
        s = s.replace("%3B", ";");
        s = s.replace("%3A", ":");
        s = s.replace("%29", ")");
        s = s.replace("%28", "(");
        s = s.replace("%5B", "[");
        s = s.replace("%5D", "]");
        s = s.replace("%2C", ",");
        s = s.replace("%21", "!");
        s = s.replace("%27", "'");
        s = s.replace("%C2%A31", "£1");
        s = s.replace("%BB", "");
        s = s.replace("%AB", "");
        s = s.replace("%0A", "");
        s = s.replace("+", " ");
        s = s.replace("%E1%A7", "ủ");
        s = s.replace("%C3%BA", "ú");
        s = s.replace("%E1%AD", "ử");
        s = s.replace("%C3%A1", "á");
        s = s.replace("%E1%BA%A1", "ạ");
        s = s.replace("%C3%A2", "â");
        s = s.replace("%E1%BA%A5", "ấ");
        s = s.replace("%E1%BA%A7", "ầ");
        s = s.replace("%E1%BA%B1", "ằ");
        s = s.replace("%E1%8D", "ã");
        s = s.replace("%E1%BA%AF", "ắ");
        s = s.replace("%C3%B4", "ô");
        s = s.replace("%E1%93", "ồ");
        s = s.replace("%E1%99", "ộ");
        s = s.replace("%E1%9D", "ờ");
        s = s.replace("%E1%91", "ố");
        s = s.replace("%E1%8F", "ỏ");
        s = s.replace("%E1%9F", "ở");
        s = s.replace("%E1%9B", "ớ");
        s = s.replace("%C3%B3", "ó");
        s = s.replace("%E1%83", "ể");
        s = s.replace("%E1%BA%BF", "ế");
        s = s.replace("%E1%81", "ề");
        s = s.replace("%E1%87", "ệ");
        s = s.replace("%C3%A0", "à");
        s = s.replace("%C3%A3", "ã");
        s = s.replace("%E1%BA%A9", "ẩ");
        s = s.replace("%E1%BA%B7", "ặ");
        s = s.replace("%C4%91", "đ");
        s = s.replace("%C4%90", "Đ");
        s = s.replace("%E1%BA%A3", "ả");
        s = s.replace("%C3%B2", "ầ");
        s = s.replace("%C3%AC", "ì");
        s = s.replace("%C3%AD", "í");
        s = s.replace("%C4%A9", "ĩ");
        s = s.replace("%E1%89", "ỉ");
        s = s.replace("%E1%8B", "ị");
        s = s.replace("%E1%B7", "ỷ");

        s = s.replace("%C4%83", "ă");
        s = s.replace("%C6%B0%C6%A1", "ươ");
        s = s.replace("%E1%A9", "ứ");
        s = s.replace("%E1%AF", "ữ");
        s = s.replace("%E1%A3", "ợ");
        s = s.replace("%E1", "ừ");
        s = s.replace("%C6%B0", "ư");
        s = s.replace("%C3%AA", "ê");
        s = s.replace("ừ%85", "ễ");
        s = s.replace("%C6%A1", "ơ");
        s = s.replace("%3F", "?");
        s = s.replace("ừ%95", "ổ");
        s = s.replace("ợ9", "ứ");
        s = s.replace("ừ%B1", "ự");
        s = s.replace("%C3%A9", "é");
        s = s.replace("%C3%B9", "ù");
        s = s.replace("ừ%BA%BD", "ẽ");
        s = s.replace("%C3%A9", "é");
        s = s.replace("ừ%B1", "ự");
        s = s.replace("%C3%BD", "ý");
        s = s.replace("ừ%BA%AD", "ậ");
        s = s.replace("%C3%A8", "è");
        s = s.replace("ừ%97", "ỗ");
        s = s.replace("%E2%80%93", "-");
        s = s.replace("%C5%A9", "ũ");
        s = s.replace("ẻ%B5", "ẵ");
        s = s.replace("ừ%BA", "ẫ");
        s = s.replace("%E2%80%A6", "...");
        s = s.replace("ừ%BA", "ẻ");
        s = s.replace("%2B", "+");

        return s;
    }

    public static void rateApp(Context context){
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        try {

            context.startActivity(goToMarket);
        } catch (Exception e) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public static void shareAppLink(Context context){
        String message = context.getString(R.string.share_contetn) + "\nhttp://play.google.com/store/apps/details?id=" + context.getPackageName();
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, message);

        context.startActivity(Intent.createChooser(share, "The system will open"));
    }

    public static String getLinkForWord(String enWord) {
        return "https://raw.githubusercontent.com/votaminh/DataStore/master/dictionaryApp/image/" + enWord + ".png";
    }

    public static String upperFirstChar(String enWord) {
        return  enWord.substring(0, 1).toUpperCase() + enWord.substring(1).toLowerCase();
    }
}
