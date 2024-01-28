package org.myoralvillage.cashcalculatormodule.utils;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class UtilityMethods {

    public NumberFormat getAdaptedNumberFormat(Locale locale) {
        DecimalFormat df = (DecimalFormat) NumberFormat.getCurrencyInstance(locale);
        DecimalFormat dfUS = (DecimalFormat) NumberFormat.getCurrencyInstance(new Locale("ENGLISH", "US"));
        DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
        DecimalFormatSymbols dfsUS = dfUS.getDecimalFormatSymbols();
        dfsUS.setInternationalCurrencySymbol(dfs.getInternationalCurrencySymbol());
        dfsUS.setCurrency(dfs.getCurrency());
        dfsUS.setCurrencySymbol(dfs.getCurrencySymbol());
        df.setDecimalFormatSymbols(dfsUS);
        switch(df.getPositivePrefix()) {
            case "Rs":
                df.setPositivePrefix("Rs. ");
        }
        switch(df.getNegativePrefix()) {
            case "-Rs":
                df.setNegativePrefix("Rs. -");
        }
        switch(df.getPositiveSuffix()) {
            case "৳":
                df.setPositiveSuffix(" ৳");
        }
        switch(df.getNegativeSuffix()) {
            case "৳":
                df.setNegativeSuffix(" ৳");
        }

        //Change as per JIRA: SHAR-46
        //Everything except USD, should be displayed in whole numbers (PKR for Pakistan build)
        if(df.getDecimalFormatSymbols().getCurrency().getCurrencyCode().equals("PKR")) {
            df.setMaximumFractionDigits(0);
        }
        return df;
    }

    public void vibrateDevice(Context context){
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        long[] vibrationPattern = {0,50, 100, 50};
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            VibrationEffect vibrationEffect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK);
            v.cancel();
            v.vibrate(vibrationEffect);
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            long[] vibrationPattern2 = {100, 100, 100, 100};
            int[] amplitudes = {255, 0,255, 0};
            v.vibrate(VibrationEffect.createWaveform(vibrationPattern2, amplitudes, -1));
        } else {
            //deprecated in API 26
            v.vibrate(vibrationPattern,-1);
        }
    }
}
