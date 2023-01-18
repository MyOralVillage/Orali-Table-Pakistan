package org.myoralvillage.cashcalculator;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.myoralvillage.cashcalculatormodule.utils.SavedPreferences;
import org.myoralvillage.cashcalculatormodule.services.CurrencyService;
import org.myoralvillage.cashcalculatormodule.services.SettingService;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    String currencyName = null;
    boolean numericMode = false;
    private static SettingService settingService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_v2);

        settingService = new SettingService(getApplicationContext(), getResources());
        buildLayout();

    }

    public static SettingService getSettingService() {
        return settingService;
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

    private void switchToMainActivity(String currencyCode) {
        SavedPreferences.setSelectedCurrencyCode(this,currencyCode);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("currencyCode", currencyCode);
        intent.putExtra("numericMode", getIntent().getBooleanExtra("numericMode", false));
        startActivity(intent);
        finish();
    }

}
