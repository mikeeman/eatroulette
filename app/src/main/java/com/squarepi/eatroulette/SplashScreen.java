package com.squarepi.eatroulette;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by PC on 9/20/2016.
 */

public class SplashScreen extends AppCompatActivity implements View.OnClickListener {

    private Button bLogin, bRandomize;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splashscreen);

        initControls();
        applyFonts();
    }

    private  void initControls() {
        bLogin = (Button) findViewById(R.id.bSplashButtonLogin);
        bRandomize = (Button) findViewById(R.id.bSplashButtonRandomize);

        bLogin.setOnClickListener(this);
        bRandomize.setOnClickListener(this);
    }

    private void applyFonts() {
        //Add HelveticaNeueLT Pro 55 Roman
        Typeface tfHelvetica = Typeface.createFromAsset(getAssets(), "fonts/helvetica.otf");

        //Title TextView
        TextView tvSplashScreenTitle = (TextView) findViewById(R.id.splashTitle);
        tvSplashScreenTitle.setTypeface(tfHelvetica);

        //LinearLayout Buttons
        //Randomize
        bRandomize.setTypeface(tfHelvetica);
        //Login
        bLogin.setTypeface(tfHelvetica);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bSplashButtonLogin:
                login();
                break;
            case R.id.bSplashButtonRandomize:
                randomize();
                break;
        }
    }

    private void login() {
        Intent gotoLogin = new Intent(SplashScreen.this, Login.class);
        SplashScreen.this.startActivity(gotoLogin);
    }

    private void randomize() {
        Intent gotoRandomize = new Intent(SplashScreen.this, Slider.class);
        SplashScreen.this.startActivity(gotoRandomize);
    }
}
