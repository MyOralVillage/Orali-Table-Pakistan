package org.myoralvillage.cashcalculatormodule.services;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.LinkedHashMap;

public class AnalyticsLogger {

    private static FirebaseAnalytics mFirebaseAnalytics ;

    public static void logEvent(Context ctx, String eventName){

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(ctx);
        Bundle params = new Bundle();
        mFirebaseAnalytics.logEvent(eventName, params);
    }

    public static void logEventwithParams(Context ctx, String eventName, LinkedHashMap<String, String> keyValPairs){

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(ctx);
        Bundle params = new Bundle();
        for (String param :
                keyValPairs.keySet()) {
            params.putString(param, keyValPairs.get(param));
        }
        mFirebaseAnalytics.logEvent(eventName, params);
    }

    public static String EVENT_CASH_SCROLLBAR_VISIBLE = "cash_scrollbar_visible",
        EVENT_CASH_SCROLLBAR_SWIPED = "cash_scrollbar_swiped",
        EVENT_NUMPAD_VISIBLE = "numpad_scrollbar_visible",
        EVENT_NUMPAD_KEY_PRESSED = "numpad_key_pressed";

}
