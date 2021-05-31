package com.alibi.mapwithcurrentlocation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.alibi.mapwithcurrentlocation.config.SharedPreferenceConfig;
import com.alibi.mapwithcurrentlocation.config.UserDetailsPrefrennce;
import com.alibi.mapwithcurrentlocation.config.listener.AppListener;
import com.alibi.mapwithcurrentlocation.databinding.ActivityLoginBinding;
import com.alibi.mapwithcurrentlocation.response.LogInResponsePage;
import com.alibi.mapwithcurrentlocation.util.MapApiController;

public class LoginActivity extends AppCompatActivity {

    UserDetailsPrefrennce userDetailsPrefrennce;
    private ActivityLoginBinding loginBinding;
    private MapApiController mapApiController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        mapApiController = new MapApiController(this);

        userDetailsPrefrennce = new UserDetailsPrefrennce(this);
        if (!userDetailsPrefrennce.getBooleanData(SharedPreferenceConfig.IS_FIRST_TIME_LAUNCH)) {
            userDetailsPrefrennce.saveBooleanData(SharedPreferenceConfig.IS_FIRST_TIME_LAUNCH, true);
        }
        UserDetailsPrefrennce userDetailsPrefrennce = UserDetailsPrefrennce.getInstance(getBaseContext());
        if (userDetailsPrefrennce.getBooleanData(SharedPreferenceConfig.IS_USER_LOGIN)) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }



        loginBinding.log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginBinding.user.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter username", Toast.LENGTH_SHORT).show();
                } else if (loginBinding.pass.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
                }
                loginBinding.progressbar.setVisibility(View.VISIBLE);
                validateLogIn(loginBinding.user.getText().toString(), loginBinding.pass.getText().toString());
                Log.d("LogInActivity", "onCheck\t" + loginBinding.user.getText().toString() + "\n" + loginBinding.pass.getText().toString());
            }
        });


    }

    public void validateLogIn(String userName, String passWord) {
        Log.d("LoginActivity", "onValidate \t" + userName + "\n" + passWord);
        mapApiController.onLoggedIn(userName, passWord, new AppListener.OnLoggedIn() {
            @Override
            public void onSuccess(LogInResponsePage logInResponsePageListener) {
                loginBinding.progressbar.setVisibility(View.GONE);
                userDetailsPrefrennce.saveStringData(SharedPreferenceConfig.USER_NAME,logInResponsePageListener.getName());
                userDetailsPrefrennce.saveStringData(SharedPreferenceConfig.TOKEN,logInResponsePageListener.getToken());
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(String message) {
                loginBinding.progressbar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}