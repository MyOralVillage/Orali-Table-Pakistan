package org.myoralvillage.cashcalculator;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.myoralvillage.cashcalculator.tutorials.AdvancedVideoActivity;
import org.myoralvillage.cashcalculator.tutorials.IntroVideoActivity;
import org.myoralvillage.cashcalculator.tutorials.ThirdVideoActivity;
import org.myoralvillage.cashcalculator.tutorials.ZeroVideoActivity;
import org.myoralvillage.cashcalculatormodule.utils.SavedPreferences;
import org.myoralvillage.cashcalculatormodule.services.CurrencyService;
import org.myoralvillage.cashcalculatormodule.services.SettingService;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener {

    String currencyName = null;
    boolean numericMode = false;
    String status = "notstarted";
    int animationStage = 0 ;
    private static SettingService settingService;
    private ImageView finger, gotovideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_v2);

//        status = getIntent().getStringExtra("status");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("status")) {
                status = extras.getString("status");
                Log.d("Status", status);
            }
            if (extras.containsKey("animationStage")) {
                animationStage = extras.getInt("animationStage");
            }
        }
        Log.d("Splash", "Splash started");
//        finger = (ImageView) findViewById(R.id.finger);
        gotovideo = (ImageView) findViewById(R.id.gotovideo);
        gotovideo.setOnClickListener(this);

        settingService = new SettingService(getApplicationContext(), getResources());
        buildLayout();
        // to be uncomment
        if(animationStage == 0){
            (new Handler()).postDelayed(()-> {
                switchtozerovideo();
            }, 2000);
        }

//        startAnimation();
    }

    private void switchtozerovideo() {
        System.out.println("Test here");
        Intent intent = new Intent(this, ZeroVideoActivity.class);
        intent.putExtra("currencyName", currencyName);
        intent.putExtra("numericMode", numericMode);
        intent.putExtra("animationStage", 0);
        intent.putExtra("pageNumber", 1);

        startActivity(intent);
        finish();
    }
    public static SettingService getSettingService() {
        return settingService;
    }

    public void startAnimation() {
        (new Handler()).postDelayed(()->handAnimation(finger, 150, 500, 500), 2000);
        (new Handler()).postDelayed(()->ImageAnimation(gotovideo, 0.1f, 1.0f), 3000);
        (new Handler()).postDelayed(()-> {
            // do something
            (new Handler()).postDelayed(()->ImageAnimation(gotovideo, 0.1f, 1.0f), 3600);
            (new Handler()).postDelayed(()->ImageAnimation(gotovideo, 0.1f, 1.0f), 4000);
            (new Handler()).postDelayed(()->ImageAnimation(gotovideo, 0.1f, 1.0f), 4300);
            (new Handler()).postDelayed(()->ImageAnimation(gotovideo, 0.1f, 1.0f), 4600);
            (new Handler()).postDelayed(()->ImageAnimation(gotovideo, 0.1f, 1.0f), 4900);
        }, 3300);
    }
    public void handAnimation(ImageView code, float x, float y, int milliSecond) {
        code.animate().x(x).y(y).setDuration(milliSecond);
    }
    public void ImageAnimation(ImageView code, float x, float y) {
        Animation anim = new AlphaAnimation(x, y);
        anim.setDuration(50); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.ABSOLUTE);
        code.startAnimation(anim);
    }
    private void buildLayout() {
        new CurrencyService(getApplicationContext(), settingService.getDefaultOrder()).call(currencies
                -> runOnUiThread(() -> {
            LinearLayout view = findViewById(R.id.currencies);
            int width = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    260f,
                    getResources().getDisplayMetrics()
            );
            int height = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    130f,
                    getResources().getDisplayMetrics()
            );
            int paddingSides = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    25f,
                    getResources().getDisplayMetrics()
            );
            int paddingTopBottom = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    80f,
                    getResources().getDisplayMetrics()
            );

            for (int i = 0; i < currencies.length; i++) {
                String currency = currencies[i];
                currencyName = currency;
                ImageView button = new ImageView(this);
                button.setScaleType(ImageView.ScaleType.CENTER_CROP);

                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(width, height);

                params.setMargins(0, 0, 0, (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        10f,
                        getResources().getDisplayMetrics()));

                Log.d("PADDING", "TOP>>>> "+paddingTopBottom+" SIDES>>>>> "+paddingSides);

                button.setLayoutParams(params);
                button.setPadding(paddingSides,
                        paddingTopBottom,
                        paddingSides,
                        paddingTopBottom);
                button.setBackgroundResource(R.drawable.white_rectangle);
                button.setImageResource(CurrencyService.getCurrencyResource(currency));
                button.setOnClickListener(e -> switchToMainActivity(currency));
                view.addView(button);
            }
        }));
        findViewById(R.id.btn_privacy_policy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_privacy_policy)));
                startActivity(browserIntent);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gotovideo:
//                if(status.compareTo("two")==0)
//                    switchtoVideo2();
//                else if(status.compareTo("two")==0)
//                    switchtoVideo3();
//                else
//                    switchtoVideo1();
                switchToTutorialActivity(currencyName);
                break;
        }
    }
    private void switchToTutorialActivity(String currencyCode) {
        SavedPreferences.setSelectedCurrencyCode(this,currencyCode);
        Intent intent = new Intent(this, TutorialActivity.class);
//        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("currencyCode", currencyCode);
        intent.putExtra("numericMode", getIntent().getBooleanExtra("numericMode", false));
        intent.putExtra("firstComplete", true);
        intent.putExtra("status", status);
        startActivity(intent);
        finish();
    }
    private void switchToMainActivity(String currencyCode) {
        SavedPreferences.setSelectedCurrencyCode(this,currencyCode);
//        Intent intent = new Intent(this, TutorialActivity.class);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("currencyCode", currencyCode);
        intent.putExtra("numericMode", getIntent().getBooleanExtra("numericMode", false));
        intent.putExtra("firstComplete", true);
        intent.putExtra("status", status);
        startActivity(intent);
        finish();
    }
    private void switchtoVideo1() {
        Intent intent = new Intent(this, IntroVideoActivity.class);
        intent.putExtra("currencyName", currencyName);
        intent.putExtra("numericMode", numericMode);
        intent.putExtra("animationStage", 0);
        startActivity(intent);
        finish();
    }

    private void switchtoVideo2() {
        Intent intent = new Intent(this, AdvancedVideoActivity.class);
        intent.putExtra("currencyName", currencyName);
        intent.putExtra("numericMode", numericMode);
        intent.putExtra("animationStage", 0);
        startActivity(intent);
        finish();
    }

    private void switchtoVideo3() {
        Intent intent = new Intent(this, ThirdVideoActivity.class);
        intent.putExtra("currencyName", currencyName);
        intent.putExtra("numericMode", numericMode);
        intent.putExtra("animationStage", 0);
        startActivity(intent);
        finish();
    }

}
