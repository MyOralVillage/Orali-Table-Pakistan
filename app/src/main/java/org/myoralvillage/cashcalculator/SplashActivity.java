package org.myoralvillage.cashcalculator;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import org.myoralvillage.cashcalculatormodule.services.CurrencyService;
import org.myoralvillage.cashcalculatormodule.services.SettingService;

public class SplashActivity extends AppCompatActivity {

    String currencyName = null;
    boolean numericMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        flagSelectListener();
        mainActivityButtonListener();
        //Removed the Cash-Numeric switch from the Home Screen
        //modeSwitchButtonListener();
    }

    private void mainActivityButtonListener() {
        ImageView setting = findViewById(R.id.main);
        setting.setOnClickListener(e -> switchToTutorial());
    }

    private void switchToTutorial() {
        Intent intent = new Intent(this, TutorialActivity.class);
        intent.putExtra("currencyName", currencyName);
        intent.putExtra("numericMode", numericMode);
        startActivity(intent);
        finish();
    }


    private void setDefaultImage(Button setting) {
        SettingService settingService = new SettingService(getApplicationContext(), getResources());
        new CurrencyService(getApplicationContext(), settingService.getDefaultOrder()).call(
                currencies -> {
            int id = CurrencyService.getCurrencyResource(currencies[0]);
            currencyName = currencies[0];
            runOnUiThread(() -> setting.setBackgroundResource(id));
        });
    }

    private void flagSelectListener() {
        Button btnSelectCountry = findViewById(R.id.btn_select_country);

        setDefaultImage(btnSelectCountry);
        btnSelectCountry.setOnClickListener(e -> openCountrySelector());
    }

    /*private void modeSwitchButtonListener() {
        Switch modeSwitch = (Switch) findViewById(R.id.mode_switch);
        modeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                numericMode = isChecked;
            }
        });
    }*/

    private void openCountrySelector() {
        Intent intent = new Intent(this, SettingActivity.class);
        intent.putExtra("numericMode", numericMode);
        startActivity(intent);
        finish();
    }
}
