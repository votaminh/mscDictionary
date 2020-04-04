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
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.msc.mscdictionary.R;
import com.msc.mscdictionary.base.BaseActivity;
import com.msc.mscdictionary.database.OffFavouriteDAO;
import com.msc.mscdictionary.database.OffHistoryDAO;
import com.msc.mscdictionary.database.OffWordDAO;
import com.msc.mscdictionary.firebase.MyFirebase;
import com.msc.mscdictionary.fragment.TranslateFragment;
import com.msc.mscdictionary.media.MediaBuilder;
import com.msc.mscdictionary.model.Word;
import com.msc.mscdictionary.network.DictionaryCrawl;
import com.msc.mscdictionary.service.ClipBroadService;
import com.msc.mscdictionary.service.DownloadZipService;
import com.msc.mscdictionary.util.AppUtil;
import com.msc.mscdictionary.util.Constant;
import com.msc.mscdictionary.util.SharePreferenceUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends BaseActivity {
    private static final int DRAW_OVER_OTHER_APP_PERMISSION = 33;
    public static final int MAIN_REQUEST = 818;
    private static final int MAIN_TO_FAVOURITE = 34;
    private static final int MAIN_TO_HISTORY = 35;
    EditText edTextEn;
    TextView btnSearch;
    ProgressBar progress;
    ImageButton btnMenuDrawer;

    TextView tvMean;
    TextView tvVoice;
    ImageButton btnSpeaker;
    ProgressBar progressBar;

    Switch swFloat;
    RelativeLayout llFloat;
    
    Word currentWord;
    RelativeLayout llHeaderWord;
    private TranslateFragment translateFragment;

    DrawerLayout drawerLayout;
    private OffWordDAO wordDAO;

    TextView tvHistory, tvFavourite, tvPractice, tvRate, tvShare;
    ImageButton btnFavourite;

    OffFavouriteDAO favouriteDAO;
    OffHistoryDAO historyDAO;
    private String enInput = "";
    private AlertDialog dialogTutorial;
    private boolean outOfApp = false;

    @Override
    public int resLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
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

        tvRate = findViewById(R.id.tvRate);
        tvShare = findViewById(R.id.tvShare);

        swFloat = findViewById(R.id.swFloat);
        llFloat = findViewById(R.id.llTurnFloat);
        onClick();
        
        disableScrollAppbar();
        openTranslateFragment();
//        showDialogDownloadData();

        wordDAO = new OffWordDAO(this);
        favouriteDAO = new OffFavouriteDAO(this);
        historyDAO = new OffHistoryDAO(this);

        openDefault();
    }

    private void openDefault() {
        if(getIntent().getExtras() != null){
            String en = getIntent().getExtras().getString(Constant.EN_NAME_PUT, "");
            search(en, true);
            new Handler().postDelayed(() -> hideKeyboard(this), 500);
        }else {
            new Handler().postDelayed(() -> {
                showKeyBroad(edTextEn);
                edTextEn.setCursorVisible(true);
            }, 500);
        }

        boolean enableFloat = SharePreferenceUtil.getBooleanPerferences(this, Constant.ENABLE_FLOAT, false);
        if(enableFloat){
            enableServiceFloat();
            swFloat.setChecked(true);
        }else {
            swFloat.setChecked(false);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharePreferenceUtil.saveBooleanPereferences(this, Constant.SHOW_TOAST_BACK, true);
    }

    @Override
    public void onBackPressed() {
        if(currentWord == null){
            moveTaskToBack();
            return;
        }

        if(outOfApp){
            moveTaskToBack();
        }else {
            outOfApp = true;
            boolean showToast = SharePreferenceUtil.getBooleanPerferences(this, Constant.SHOW_TOAST_BACK, true);
            if(showToast){
                Toast.makeText(this, getString(R.string.tap_again_to_out), Toast.LENGTH_SHORT).show();
                SharePreferenceUtil.saveBooleanPereferences(this, Constant.SHOW_TOAST_BACK, false);
            }
            new Handler().postDelayed(() -> {
                outOfApp = false;
            }, 400);

            backHistory();
        }

    }

    private void moveTaskToBack() {
        if(checkShowRate()){
            moveTaskToBack(true);
        }
    }

    /**
     * Néu user đã sữ dụng 3 ngày mà chưa rate thì sẽ hiện dialog rate
     */
    private boolean checkShowRate() {
        boolean rated = SharePreferenceUtil.getBooleanPerferences(this, Constant.RATE, false);
        if(!rated){
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date();
            String d1 = dateFormat.format(date);
            String d2 = SharePreferenceUtil.getStringPereferences(this, Constant.TIME_FIRST_RUN, "");
            try {
                Date date1 = dateFormat.parse(d1);
                Date date2 = dateFormat.parse(d2);
                long diff = date2.getTime() - date1.getTime();
                int day = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                if(day > 3){
                    showDialogRate();
                }else {
                    rated = true;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                rated = true;
            }
        }
        return rated;
    }

    private void showDialogRate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.title_dialog_rate));
        builder.setMessage(getString(R.string.message_dialog_rate));
        builder.setPositiveButton(getString(R.string.rate_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AppUtil.rateApp(getApplicationContext());
                SharePreferenceUtil.saveBooleanPereferences(getApplicationContext(), Constant.RATE, true);
            }
        });
        builder.setNeutralButton(getString(R.string.rate_later), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharePreferenceUtil.saveBooleanPereferences(getApplicationContext(), Constant.RATE, false);
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date date = new Date();
                String dateS = dateFormat.format(date);
                SharePreferenceUtil.saveStringPereferences(getApplicationContext(), Constant.TIME_FIRST_RUN, dateS);
                moveTaskToBack(true);
            }
        });
        builder.show();
    }

    private void backHistory() {
        int currentIdHistory = historyDAO.getIdWord(currentWord.getId());
        if(currentIdHistory == 0){
            moveTaskToBack();
        }
        int idPreviousWord = historyDAO.getEnById(currentIdHistory - 1);
        currentWord = wordDAO.getWordById(idPreviousWord);
        if(currentWord == null){
            moveTaskToBack(true);
        }else {
            search(currentWord.getEnWord(), false);
        }
    }

    private void onClick() {
        edTextEn.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event != null &&
                                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (event == null || !event.isShiftPressed()) {
                                enterInput();
                                return true; // consume.
                            }
                        }
                        return false; // pass on to other listeners.
                    }
                }
        );
        tvRate.setOnClickListener(v -> {
            AppUtil.rateApp(this);
            SharePreferenceUtil.saveBooleanPereferences(getApplicationContext(), Constant.RATE, true);
        });
        tvShare.setOnClickListener(v -> AppUtil.shareAppLink(this));
        llFloat.setOnClickListener(v -> {
            swFloat.toggle();
            if(swFloat.isChecked()){
                boolean showTutorial = SharePreferenceUtil.getBooleanPerferences(getApplicationContext(), Constant.TUTORIAL, true);
                if(showTutorial){
                    showDialogTutorial();
                    SharePreferenceUtil.saveBooleanPereferences(getApplicationContext(), Constant.TUTORIAL, false);
                }else{
                    askForSystemOverlayPermission();
                }
                enableServiceFloat();
                SharePreferenceUtil.saveBooleanPereferences(this,Constant.ENABLE_FLOAT, true);
            }else {
                disableServiceFloat();
                SharePreferenceUtil.saveBooleanPereferences(this,Constant.ENABLE_FLOAT, false);
            }
        });
        
        btnSpeaker.setOnClickListener(v -> {
            if(currentWord != null){
                playAudio(currentWord.getUrlSpeak());
            }
        });

        btnSearch.setOnClickListener((v) -> {
            enterInput();
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
            goToHistory();
            drawerLayout.closeDrawer(Gravity.LEFT);
        });

        tvFavourite.setOnClickListener(v -> {
            goToFavourite();
            drawerLayout.closeDrawer(Gravity.LEFT);
        });

        tvPractice.setOnClickListener(v -> {
            showDialogPractive();
            drawerLayout.closeDrawer(Gravity.LEFT);
        });
    }

    private void enterInput() {
        final String en = edTextEn.getText().toString();
        if(en.isEmpty()){
            Toast.makeText(this, getString(R.string.can_not_empty), Toast.LENGTH_SHORT).show();
        }else {
            search(en, true);
        }
    }

    private void showDialogPractive() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.title_dialog_practice));
        builder.setMessage(getString(R.string.message_dialog_practice));
        builder.setPositiveButton(getString(R.string.understand), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }


    private void enableServiceFloat() {
        Intent service = new Intent(MainActivity.this, ClipBroadService.class);
        service.putExtra(Constant.RUN_SERVICE, true);
        startService(service);
    }

    private void disableServiceFloat() {
        Intent service = new Intent(MainActivity.this, ClipBroadService.class);
        service.putExtra(Constant.RUN_SERVICE, false);
        startService(service);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.cancel(Constant.ID_NOTIFICATION_FLOAT);
    }

    private void goToHistory() {
        Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
        startActivityForResult(intent, MAIN_TO_HISTORY);
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
        scaleAnimation.setDuration(Constant.DURATION_SCALE_FAVOURITE);
        scaleAnimation.setInterpolator(new AnticipateInterpolator());
        btnFavourite.startAnimation(scaleAnimation);

        new Handler().postDelayed(() -> btnFavourite.setImageResource(R.drawable.ic_favourite_select), Constant.DURATION_SCALE_FAVOURITE);
    }

    private void animationUnLike() {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.5f, 1f, 0.5f, 1f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(Constant.DURATION_SCALE_FAVOURITE);
        scaleAnimation.setInterpolator(new AnticipateInterpolator());
        btnFavourite.startAnimation(scaleAnimation);

        new Handler().postDelayed(() -> btnFavourite.setImageResource(R.drawable.ic_favourite), Constant.DURATION_SCALE_FAVOURITE);
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

            @Override
            public void fail(String error) {
                Toast.makeText(MainActivity.this, "No Speak", Toast.LENGTH_SHORT).show();
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
        }, 1000);
    }

    private void setError(String s) {
        runOnUiThread(() -> {
            llHeaderWord.setVisibility(View.INVISIBLE);
            if(translateFragment != null && translateFragment.isVisible()){
                String error = enInput + "  " + getString(R.string.error_no_found);
                translateFragment.setError(error);
            }
        });
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

    private void showKeyBroad(EditText textEdit) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(textEdit, InputMethodManager.SHOW_IMPLICIT);
    }

    private void setResultSearch(Word word, boolean addHistory) {
        currentWord = word;
        if(addHistory){
            historyDAO.add(word);
        }
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
        new Handler(Looper.getMainLooper()).post(() -> {
            if(favouriteDAO.checkHas(currentWord)){
                btnFavourite.setImageResource(R.drawable.ic_favourite_select);
            }else {
                btnFavourite.setImageResource(R.drawable.ic_favourite);
            }
        });
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
            goToSettingPermission();
        }
    }

    private void showDialogTutorial() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_tutorial, null);
        builder.setView(view);

        dialogTutorial = builder.create();
        dialogTutorial.show();
        dialogTutorial.setCancelable(false);

        TextView tvUnderstand = view.findViewById(R.id.btnUnderstand);
        tvUnderstand.setOnClickListener(v -> {
            askForSystemOverlayPermission();
            dialogTutorial.dismiss();
        });

    }

    private void goToSettingPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, DRAW_OVER_OTHER_APP_PERMISSION);
    }

    public void search(String en, boolean addHistory) {
        this.enInput = validateInput(en);
        showLoad();
        hideKeyboard(this);
        edTextEn.setText(en);
        tvMean.setText(enInput.substring(0, 1).toUpperCase() + enInput.substring(1).toLowerCase());
        wordDAO.getWordByEn(en, new DictionaryCrawl.TranslateCallback() {
            @Override
            public void success(Word word) {
                setResultSearch(word, addHistory);
                hideLoad();
            }

            @Override
            public void fail(String error) {
                DictionaryCrawl.instance(enInput, new DictionaryCrawl.TranslateCallback() {
                    @Override
                    public void success(Word word) {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            saveWordOffline(word);
                            upLoadFirebase(word);
                            setResultSearch(word, addHistory);
                            hideLoad();
                        });
                    }

                    @Override
                    public void fail(final String error) {
                        Word word = new Word(0, enInput, "", "", "", "");
                        upLoadFirebase(word);
                        setError(error);
                        hideLoad();
                    }
                }).translate();
            }
        });
    }

    private String validateInput(String en) {
        int lenght = en.length();
        char c = en.charAt(lenght - 1);
        if(c == ' '){
            en = en.substring(0, lenght-1);
            return validateInput(en);
        }else {
            return en;
        }
    }

    private void upLoadFirebase(Word word) {
        MyFirebase.uploadWord(word);
    }

    private void saveWordOffline(Word word) {
        int biggestId = SharePreferenceUtil.getIntPereferences(this, Constant.CURRENT_ID_WORD, 0);
        biggestId++;
        word.setId(biggestId);
        SharePreferenceUtil.saveIntPereferences(this, Constant.CURRENT_ID_WORD, biggestId);
        new Thread(() -> {
            wordDAO.addWord(word);
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == MAIN_TO_FAVOURITE){
                String en = data.getExtras().getString("en");
                search(en, true);
                drawerLayout.closeDrawer(Gravity.LEFT);
            }else if(requestCode == MAIN_TO_HISTORY){
                String en = data.getExtras().getString("en");
                search(en, true);
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
