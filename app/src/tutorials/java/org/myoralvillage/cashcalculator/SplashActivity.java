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

        setDefaultImage();

        ImageView setting = findViewById(R.id.switchToTutorial);
        setting.setOnClickListener(e -> switchToTutorial());

    }

    private void switchToTutorial() {
        Intent intent = new Intent(this, TutorialActivity.class);
        intent.putExtra("currencyName", currencyName);
        intent.putExtra("numericMode", numericMode);
        startActivity(intent);
        finish();
    }

    private void setDefaultImage() {
        SettingService settingService = new SettingService(getApplicationContext(), getResources());
        new CurrencyService(getApplicationContext(), settingService.getDefaultOrder()).call(
                currencies -> {
                    int id = CurrencyService.getCurrencyResource(currencies[0]);
                    currencyName = currencies[0];
                });
    }
}
