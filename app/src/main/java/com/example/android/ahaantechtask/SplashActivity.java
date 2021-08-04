package com.example.android.ahaantechtask;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.android.ahaantechtask.Utils.MyApplication;
import com.example.android.ahaantechtask.Utils.SPCsnstants;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkIsLogin();
            }
        },1000);

    }




    public void checkIsLogin() {

        String islogin = "";
        if (MyApplication.mSp.chk(SPCsnstants.IS_LOGGED_IN)) {
            islogin = MyApplication.mSp.getKey(SPCsnstants.IS_LOGGED_IN);
        }

        if (islogin.equals(SPCsnstants.YES)) {

            gotoMainScreen();

        }

        else {

            gotoLoginScreen();

        }
    }



    private void gotoLoginScreen()
    {
        Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
    private void gotoMainScreen()
    {
        Intent intent = new Intent(SplashActivity.this,ProfilePage.class);
        startActivity(intent);
        finish();
    }
}