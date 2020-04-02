package com.msc.mscdictionary.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.msc.mscdictionary.R;
import com.msc.mscdictionary.base.BaseActivity;
import com.msc.mscdictionary.database.OffFavouriteDAO;
import com.msc.mscdictionary.database.OffHistoryDAO;
import com.msc.mscdictionary.database.OffWordDAO;
import com.msc.mscdictionary.fragment.TranslateFragment;
import com.msc.mscdictionary.media.MediaBuilder;
import com.msc.mscdictionary.model.Word;
import com.msc.mscdictionary.network.DictionaryCrawl;
import com.msc.mscdictionary.service.ClipBroadService;
import com.msc.mscdictionary.service.DownloadZipService;

public class MainActivity extends BaseActivity {
    private static final int DRAW_OVER_OTHER_APP_PERMISSION = 33;
    public static final int MAIN_REQUEST = 818;
    private static final int MAIN_TO_FAVOURITE = 34;
    EditText edTextEn;
    TextView btnSearch;
    ProgressBar progress;
    ImageButton btnMenuDrawer;

    TextView tvMean;
    TextView tvVoice;
    ImageButton btnSpeaker;
    ProgressBar progressBar;

    Word currentWord;
    RelativeLayout llHeaderWord;
    private TranslateFragment translateFragment;

    DrawerLayout drawerLayout;
    private OffWordDAO wordDAO;

    TextView tvHistory, tvFavourite, tvPractice;
    ImageButton btnFavourite;

    OffFavouriteDAO favouriteDAO;
    OffHistoryDAO historyDAO;

    @Override
    public int resLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void intView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        btnFavourite = findViewById(R.id.btnFavourite);
        edTextEn = findViewById(R.id.edTextEn);
        btnSearch = findViewById(R.id.btnSearch);
        progress = findViewById(R.id.progress);
        btnMenuDrawer = findViewById(R.id.btnMenuDrawer);
        drawerLayout = findViewById(R.id.drawerLayout);

        tvMean = findViewById(R.id.tvMean);
        tvVoice = findViewById(R.id.tvVoice);
        btnSpeaker = findViewById(R.id.tvAudio);
        progressBar = findViewById(R.id.progressVoice);

        llHeaderWord = findViewById(R.id.llHeader);

        tvHistory = findViewById(R.id.tvHistory);
        tvFavourite = findViewById(R.id.tvFavourite);
        tvPractice = findViewById(R.id.tvPractice);

        disableScrollAppbar();
        openTranslateFragment();
        onClick();
        askForSystemOverlayPermission();
//        showDialogDownloadData();

        wordDAO = new OffWordDAO(this);
        favouriteDAO = new OffFavouriteDAO(this);
        historyDAO = new OffHistoryDAO(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        Intent service = new Intent(MainActivity.this, ClipBroadService.class);
        startService(service);
    }

    private void onClick() {
        btnSpeaker.setOnClickListener(v -> {
            if(currentWord != null){
                playAudio(currentWord.getUrlSpeak());
            }
        });

        edTextEn.setOnClickListener(v -> {
            edTextEn.setCursorVisible(true);
        });
        btnSearch.setOnClickListener((v) -> {
            final String en = edTextEn.getText().toString();
            search(en);
        });

        btnMenuDrawer.setOnClickListener(v -> {
            drawerLayout.openDrawer(Gravity.LEFT);
            hideKeyboard(this);
        });

        btnFavourite.setOnClickListener(v -> {
            if(favouriteDAO.checkHas(currentWord)){
                removeFavourite(currentWord);
            }else {
                addFavourite(currentWord);
            }
        });

        tvHistory.setOnClickListener(v -> {
            showDialogAskLogin();
        });

        tvFavourite.setOnClickListener(v -> {
            goToFavourite();
        });

        tvPractice.setOnClickListener(v -> {
            showDialogAskLogin();
        });
    }

    private void goToFavourite() {
        Intent intent = new Intent(MainActivity.this, FavouriteActivity.class);
        startActivityForResult(intent, MAIN_TO_FAVOURITE);
    }

    private void addFavourite(Word currentWord) {
        animationLike();
        favouriteDAO.add(currentWord);
    }

    private void removeFavourite(Word currentWord) {
        animationUnLike();
        favouriteDAO.remove(currentWord);
    }

    private void animationLike() {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.5f, 1f, 0.5f, 1f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(500);
        scaleAnimation.setInterpolator(new AnticipateInterpolator());
        btnFavourite.startAnimation(scaleAnimation);

        new Handler().postDelayed(() -> btnFavourite.setImageResource(R.drawable.ic_favourite_select), 500);
    }

    private void animationUnLike() {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.5f, 1f, 0.5f, 1f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(500);
        scaleAnimation.setInterpolator(new AnticipateInterpolator());
        btnFavourite.startAnimation(scaleAnimation);

        new Handler().postDelayed(() -> btnFavourite.setImageResource(R.drawable.ic_favourite), 500);
    }

    private void playAudio(String urlSpeak) {
        MediaBuilder.playLink(urlSpeak, new MediaBuilder.MediaCallback() {
            @Override
            public void start() {
                new Handler(Looper.getMainLooper()).post(() -> {
                    btnSpeaker.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                });
            }

            @Override
            public void end() {
                new Handler(Looper.getMainLooper()).post(() -> {
                    btnSpeaker.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                });
            }
        });
    }

    private void showDialogAskLogin() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.title_dialog_ask_login));
        builder.setMessage(getString(R.string.message_dialog_ask_login));
        builder.setNegativeButton(getString(R.string.later_lable), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton(getString(R.string.login_now_lable), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openLogin();
            }
        });
        builder.show();
    }

    private void openLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(intent, MAIN_REQUEST);
    }

    private void showLoad() {
        runOnUiThread(() -> {
            progress.setVisibility(View.VISIBLE);
            btnSearch.setVisibility(View.INVISIBLE);
        });
    }

    private void hideLoad(){
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            progress.setVisibility(View.INVISIBLE);
            btnSearch.setVisibility(View.VISIBLE);
            edTextEn.setText("");
            edTextEn.clearFocus();
            edTextEn.setCursorVisible(false);
        }, 1000);
    }

    private void setError(String error) {
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        edTextEn.setCursorVisible(false);
    }

    private void showKeyBroad() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    private void setResultSearch(Word word) {
        currentWord = word;
        historyDAO.add(word);
        setFavourite();
        new Handler(Looper.getMainLooper()).post(() -> {
            tvVoice.setText(word.getVoice());
            tvMean.setText(word.getEnWord().substring(0, 1).toUpperCase() + word.getEnWord().substring(1).toLowerCase());
            llHeaderWord.setVisibility(View.VISIBLE);
        });
        if(translateFragment != null && translateFragment.isVisible()){
            translateFragment.showResult(word);
        }
    }

    private void setFavourite() {
        if(favouriteDAO.checkHas(currentWord)){
            btnFavourite.setImageResource(R.drawable.ic_favourite_select);
        }else {
            btnFavourite.setImageResource(R.drawable.ic_favourite);
        }
    }

    private void openTranslateFragment() {
        translateFragment = (TranslateFragment) findFragment(TranslateFragment.TAG);
        if(translateFragment == null){
            translateFragment = new TranslateFragment();
        }

        replaceFragment(translateFragment, R.id.container, TranslateFragment.TAG);
    }

    private void askForSystemOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, DRAW_OVER_OTHER_APP_PERMISSION);
        }
    }
    
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void search(String en) {
        showLoad();
        hideKeyboard(this);
        llHeaderWord.setVisibility(View.INVISIBLE);
        edTextEn.setText(en);
        wordDAO.getWordByEn(en, new DictionaryCrawl.TranslateCallback() {
            @Override
            public void success(Word word) {
                setResultSearch(word);
                hideLoad();
            }

            @Override
            public void fail(String error) {
                DictionaryCrawl.instance(en, new DictionaryCrawl.TranslateCallback() {
                    @Override
                    public void success(Word word) {
                        setResultSearch(word);
                        hideLoad();
                    }

                    @Override
                    public void fail(final String error) {
                        setError(error);
                        hideLoad();
                    }
                }).translate();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == MAIN_TO_FAVOURITE){
                String en = data.getExtras().getString("en");
                search(en);
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        }
    }

    public int[] getLocationHeader(){
        int[] location = new int[2];
        edTextEn.getLocationOnScreen(location);
        return location;
    }

    private void showDialogDownloadData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.title_dialog_ask_download));
        builder.setMessage(getString(R.string.message_dialog_ask_download));
        builder.setNegativeButton(getString(R.string.cancel_lable), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton(getString(R.string.download_lable), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadDataService();
            }
        });
        builder.show();
    }

    private void downloadDataService() {
        Intent intent = new Intent(MainActivity.this, DownloadZipService.class);
        startService(intent);
    }

    private void disableScrollAppbar() {
        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
        appBarLayout.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
                AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
                behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                    @Override
                    public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                        return false;
                    }
                });
            }
        });

    }

}
