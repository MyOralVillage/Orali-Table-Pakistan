package org.myoralvillage.cashcalculatormodule.utils;

import android.content.Context;

public class SavedPreferences {

    private static final String KeyFile = "CashCalculatorPref";
    public static final String KEY_CURRENCY_CODE = "selected_currency_code";

    public static String getSelectedCurrencyCode(Context context){
        return context
                .getSharedPreferences(KeyFile, Context.MODE_PRIVATE)
                .getString(KEY_CURRENCY_CODE, "");

    }

    public static void setSelectedCurrencyCode(Context context, String currency){
        context
                .getSharedPreferences(KeyFile, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_CURRENCY_CODE, currency)
                .commit();
    }
}
