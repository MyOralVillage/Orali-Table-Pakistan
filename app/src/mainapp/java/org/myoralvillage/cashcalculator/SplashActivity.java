package org.myoralvillage.cashcalculator;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.myoralvillage.cashcalculator.MainActivity;
import org.myoralvillage.cashcalculator.R;
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
        setContentView(R.layout.activity_splash);

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
                    230f,
                    getResources().getDisplayMetrics()
            );
            int height = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    140f,
                    getResources().getDisplayMetrics()
            );
            int margin = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    20f,
                    getResources().getDisplayMetrics()
            );

            for (int i = 0; i < currencies.length; i++) {
                String currency = currencies[i];
                Button button = new Button(this);
                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(width, height);
                if(i == 0){
                    params.setMargins(margin*5, 0, 0, 0);
                }else if (i + 1 == currencies.length) {
                    params.setMargins(margin, 0, margin, 0);
                }else{
                    params.setMargins(margin, 0, 0, 0);
                }

                button.setLayoutParams(params);
                button.setBackgroundResource(CurrencyService.getCurrencyResource(currency));
                button.setOnClickListener(e -> switchToMainActivity(currency));
                view.addView(button);
            }
        }));
    }

    private void switchToMainActivity(String currencyCode) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("currencyCode", currencyCode);
        intent.putExtra("numericMode", getIntent().getBooleanExtra("numericMode", false));
        startActivity(intent);
        finish();
    }

}
