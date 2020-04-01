package com.msc.mscdictionary.activity;

import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.msc.mscdictionary.R;
import com.msc.mscdictionary.base.BaseActivity;
import com.msc.mscdictionary.firebase.MyFirebase;

public class LoginActivity extends BaseActivity {
    EditText edUserName, edPassword;
    TextView btnLogin, btnRegister;
    ProgressBar loadbar;

    @Override
    public int resLayoutId() {
        return R.layout.activity_logic;
    }

    @Override
    public void intView() {
        edPassword = findViewById(R.id.edPassword);
        edUserName = findViewById(R.id.edUserName);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.tvCreateAccount);
        loadbar = findViewById(R.id.loadLogin);
        onClick();
    }

    private void onClick() {
        btnLogin.setOnClickListener(v -> {
            String userName = edUserName.getText().toString();
            String pass = edPassword.getText().toString();
            if(userName.isEmpty()){
                Toast.makeText(this, getString(R.string.username_input_emty), Toast.LENGTH_SHORT).show();
            }else if(pass.isEmpty()){
                Toast.makeText(this, getString(R.string.pass_input_emty), Toast.LENGTH_SHORT).show();
            }else {
                loginWithUserNamePass(userName, pass);
            }
        });

        btnRegister.setOnClickListener(v -> {
            btnLogin.setText(getString(R.string.register_lable));
        });
    }

    private void loginWithUserNamePass(String userName, String pass) {
        if(!userName.isEmpty() && !pass.isEmpty()){
            btnLogin.setVisibility(View.INVISIBLE);
            loadbar.setVisibility(View.VISIBLE);
            MyFirebase.checkLogin(userName, pass, new MyFirebase.TaskListener() {
                @Override
                public void fail(String error) {
                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                    loadbar.setVisibility(View.INVISIBLE);
                    btnLogin.setVisibility(View.VISIBLE);
                }

                @Override
                public void success() {
                    Toast.makeText(LoginActivity.this, "success", Toast.LENGTH_SHORT).show();
                    loadbar.setVisibility(View.INVISIBLE);
                    btnLogin.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}
