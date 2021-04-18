package org.myoralvillage.cashcalculatormodule.services;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import androidx.core.util.Pair;

public class AnalyticsLogger {

    private static FirebaseAnalytics mFirebaseAnalytics ;

    public static void logEvent(Context ctx, String eventName){

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(ctx);
        Bundle params = new Bundle();
        mFirebaseAnalytics.logEvent(eventName, params);
    }

    public static void logEventwithParams(Context ctx, String eventName, Pair<String, String>... keyValPairs){
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(ctx);
        Bundle params = new Bundle();
        for (Pair param :
                keyValPairs) {
            params.putString(param.first.toString(), param.second.toString());
        }
        mFirebaseAnalytics.logEvent(eventName, params);
    }

    public static String EVENT_CASH_SCROLLBAR_VISIBLE = "cash_scrollbar_visible",
        EVENT_CASH_SCROLLBAR_SWIPED = "cash_scrollbar_swiped",
        EVENT_NUMPAD_VISIBLE = "numpad_scrollbar_visible",
        EVENT_NUMPAD_KEY_PRESSED = "numpad_key_pressed";

    public static String EVENT_CALCULATION_PERFORMED = "calculation_performed";
    public static String PARAM_CALCULATION_NAME = "calculation_name";
    public static String VAL_CALCULATION_ADDITION = "addition";
    public static String VAL_CALCULATION_SUBTRACTION = "subtraction";
    public static String VAL_CALCULATION_MULTIPLY = "performed";

    public static String EVENT_NOTE_ADDED = "note_added",
            EVENT_NOTE_REMOVED = "note_removed";

    public static String EVENT_FIRST_TWO_SWIPE = "first_two_swipe",
            EVENT_SUBSEQUENT_TWO_SWIPE = "subsequent_two_swipe";


}
