package com.msc.mscdictionary.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(resLayoutId());
        initView();
    }

    public Fragment findFragment(String tag){
        return getSupportFragmentManager().findFragmentByTag(tag);
    }

    public void replaceFragment(Fragment fragment, int id, String tag){
        getSupportFragmentManager().beginTransaction().replace(id, fragment, tag).commit();
    }

    abstract public int resLayoutId();
    abstract public void initView();
}
