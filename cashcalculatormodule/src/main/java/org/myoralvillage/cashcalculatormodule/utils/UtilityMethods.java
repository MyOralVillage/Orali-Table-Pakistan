package org.myoralvillage.cashcalculatormodule.utils;

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
        //Everything except USD, should be displayed in whole numbers (KES/KSH, ETB)
        if(df.getDecimalFormatSymbols().getCurrency().getCurrencyCode().equals("KES")
                || df.getDecimalFormatSymbols().getCurrency().getCurrencyCode().equals("ETB")) {
            df.setMaximumFractionDigits(0);
        }
        return df;
    }
}
