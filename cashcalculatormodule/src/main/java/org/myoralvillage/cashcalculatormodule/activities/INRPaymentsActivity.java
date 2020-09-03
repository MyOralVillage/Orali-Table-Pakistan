package org.myoralvillage.cashcalculatormodule.activities;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.hover.sdk.api.Hover;
import com.hover.sdk.api.HoverParameters;
import org.myoralvillage.cashcalculatormodule.R;
import org.myoralvillage.cashcalculatormodule.config.HoverConstants;
import org.myoralvillage.cashcalculatormodule.fragments.CashCalculatorFragment;

import java.util.ArrayList;

public class INRPaymentsActivity extends AppCompatActivity {

    private ImageButton sendMoneyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_payments);

        Hover.initialize(this);
        Intent intent = getIntent();
        String money = intent.getStringExtra("10");//CashCalculatorFragment.AMOUNT);//Hardcoded
        sendMoneyButton= findViewById(R.id.finalsendmoney);

        sendMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = money;//amountEdit.getText().toString();
                String mobileNumber = "7042336881";//Hardcoded
                String remark = "TEST";//Hardcoded

                Intent i = new HoverParameters.Builder(INRPaymentsActivity.this)
                        .request(HoverConstants.ACTION_ID_SEND_MONEY_INR)
                        .extra("mobileNumber", mobileNumber)
                        .extra("amount", amount)
                        .extra("remark", remark)
                        .buildIntent();
                startActivityForResult(i, 0);
            }
        });
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            String[] sessionTextArr = data.getStringArrayExtra("session_messages");
            String uuid = data.getStringExtra("uuid");
        } else if (requestCode == 0 && resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, "Error: " + data.getStringExtra("error"), Toast.LENGTH_LONG).show();
        }
    }
}
