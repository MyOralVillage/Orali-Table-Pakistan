package org.myoralvillage.cashcalculatormodule.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;


import org.myoralvillage.cashcalculatormodule.R;
import org.myoralvillage.cashcalculatormodule.utils.SavedPreferences;
import org.myoralvillage.cashcalculatormodule.models.AppStateModel;
import org.myoralvillage.cashcalculatormodule.models.CurrencyModel;
import org.myoralvillage.cashcalculatormodule.models.DenominationModel;
import org.myoralvillage.cashcalculatormodule.models.MathOperationModel;
import org.myoralvillage.cashcalculatormodule.services.AnalyticsLogger;
import org.myoralvillage.cashcalculatormodule.services.AppService;
import org.myoralvillage.cashcalculatormodule.utils.UtilityMethods;
import org.myoralvillage.cashcalculatormodule.views.CountingTableView;
import org.myoralvillage.cashcalculatormodule.views.CurrencyScrollbarView;
import org.myoralvillage.cashcalculatormodule.views.NumberPadView;
import org.myoralvillage.cashcalculatormodule.views.listeners.CountingTableListener;
import org.myoralvillage.cashcalculatormodule.views.listeners.CurrencyScrollbarListener;
import org.myoralvillage.cashcalculatormodule.views.listeners.NumberPadListener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.LinkedHashMap;
import java.util.Locale;

import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

/**
 * CashCalculatorFragment creates the whole Cash Calculator application, including the Counting Table
 * and the Currency Scrollbar, packaged as one.
 *
 * @author Alexander Yang
 * @author Hamza Mahfooz
 * @author Jiaheng Li
 * @author Lingjing Zou
 * @author Peter Panagiotis Roubatsis
 * @author Yujie Wu
 * @author Zhipeng Zou
 * @author Rahul Vaish
 * @author Aman Alam
*/
public class CashCalculatorFragment extends Fragment {

    /**
     * A constant variable to store and lookup the state of the app when a new activity is started.
     * It stores the app state in the Activity's bundle so that it can be accessed by the next
     * activity.
     *
     * @see Bundle
     */
    private final String APP_STATE_KEY = "appState";

    /**
     * The view of the Cash Calculator.
     */
    private View view;

    /**
     * The service class used to perform the main operations of the Cash Calculator.
     *
     * @see AppService
     */
    AppService service;

    /**
     * The model class used to represent the type of currency as well as the set of denominations
     * available.
     *
     * @see CurrencyModel
     */
    private CurrencyModel currCurrency;

    /**
     * The view that displays images of currency to represent a number.
     *
     * @see CountingTableView
     */
    private CountingTableView countingTableView;

    /**
     * The view of the Scrollbar to monitor and render the display of the denominations in the
     * scrollbar as well as the scrollbar itself.
     *
     * @see CurrencyScrollbarView
     */
    private CurrencyScrollbarView currencyScrollbarView;

    /**
     * The view class used to monitor and render the display of the number pad.
     *
     * @see NumberPadView
     */
    private NumberPadView numberPadView;

    /**
     * Displays the number when in numeric mode.
     *
     */
    private TextView numberInputView;
    private Locale locale;

    /**
     * To export the currency displaying on CountingTable to Views.
     */
    public static String currencyOnCountingTable="";
    /**
     * Called to have the <code>CashCalculatorFragment</code> instantiate its user interface view.
     *
     * @param inflater _
     * @param parent _
     * @param savedInstanceState _
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null && extras.containsKey(APP_STATE_KEY))
            service = new AppService((AppStateModel) extras.getSerializable(APP_STATE_KEY));
        else service = new AppService();

        view = inflater.inflate(R.layout.fragment_activity, parent, false);
        numberInputView = view.findViewById(R.id.number_input_view);
        numberInputView.setVisibility(View.INVISIBLE);

        AnalyticsLogger.logEvent(getContext(), AnalyticsLogger.EVENT_CASH_SCROLLBAR_VISIBLE);

        return view;
    }


    /**
     * Gets the value of current total amount of money of the Cash Calculator.
     *
     * @return the total amount of money.
     */
    public BigDecimal getValue(){
        return service.getValue();
    }

    /**
     * Initializes the <code>CashCalculatorFragment</code> based on the currency code.
     *
     * @param currencyCode The currency code that the application is set to.
     */
    public void initialize(String currencyCode) {
        this.locale = Locale.getDefault();
        for (Locale l : Locale.getAvailableLocales()) {
            try {
                Currency currency = Currency.getInstance(l);
                if (currency == null) {
                    continue;
                }

                if (currency.getCurrencyCode().equals(currencyCode)) {
                    locale = l;
                    break;
                }
            } catch (IllegalArgumentException ignored) {}
        }

        initializeCurrencyScrollbar(currencyCode);
        initializeCountingView();
        initializeNumberPad();

        updateAll();
    }

    /**
     * Initializes the <code>CountingTableView</code>.
     *
     * @see CountingTableView
     */
    private void initializeCountingView() {

        //loading previous values of operations
        LinkedHashMap<MathOperationModel, ArrayList<MathOperationModel>> restored = deserialize();
        if(restored.size() > 0){
            service.getAppState().setRetrievedOperations(restored);
        }

        /*for(MathOperationModel result : service.getAppState().getAllHistory().keySet()){
            Log.d("RESULT ->>>>>",""+result.getValue());
            for (MathOperationModel operation:
                    service.getAppState().getAllHistory().get(result)) {
                Log.d("OPERATION>","value: "+operation.getValue()+", mode: "+operation.getMode()+", type: "+operation.getType());
            }
        }*/

        TextView sum = view.findViewById(R.id.sum_view);
        countingTableView = view.findViewById(R.id.counting_table);
        countingTableView.initialize(currCurrency, service.getAppState(), locale);
        if (service.getAppState().getAppMode() == AppStateModel.AppMode.NUMERIC) {
            sum.setVisibility(View.INVISIBLE);
            numberInputView.setVisibility(View.VISIBLE);
            AnalyticsLogger.logEvent(getContext(), AnalyticsLogger.EVENT_NUMPAD_VISIBLE);
            numberInputView.setText(formatCurrency(service.getValue()));
        }
        countingTableView.setListener(new CountingTableListener() {
            @Override
            public void onSwipeAddition() {
                AnalyticsLogger.logEventwithParams(getContext(),
                        AnalyticsLogger.EVENT_CALCULATION_PERFORMED, new Pair<String, String>(AnalyticsLogger.PARAM_CALCULATION_NAME,
                                AnalyticsLogger.VAL_CALCULATION_ADDITION));
                if (!service.isInHistorySlideshow()) {
                    service.add();
                    switchState();
                    if(null!=getActivity()) {
                        getActivity().overridePendingTransition(R.anim.activity_left_in, R.anim.activity_left_out);
                        getActivity().finish();
                    }
                }
            }

            @Override
            public void onSwipeSubtraction() {
                AnalyticsLogger.logEventwithParams(getContext(),
                        AnalyticsLogger.EVENT_CALCULATION_PERFORMED, new Pair<String, String>(AnalyticsLogger.PARAM_CALCULATION_NAME,
                                AnalyticsLogger.VAL_CALCULATION_SUBTRACTION));
                if (!service.isInHistorySlideshow()) {
                    service.subtract();
                    switchState();
                    if(null!=getActivity()) {
                        getActivity().overridePendingTransition(R.anim.activity_right_in, R.anim.activity_right_out);
                        getActivity().finish();
                    }

                }
            }

            @Override
            public void onSwipeMultiplication() {
                AnalyticsLogger.logEventwithParams(getContext(),
                        AnalyticsLogger.EVENT_CALCULATION_PERFORMED, new Pair<String, String>(AnalyticsLogger.PARAM_CALCULATION_NAME,
                                AnalyticsLogger.VAL_CALCULATION_MULTIPLY));
                // Dragging towards the top
                if (!service.isInHistorySlideshow()) {
                    service.multiply();
                    switchState();
                    if(null!=getActivity()) {
                        getActivity().overridePendingTransition(R.anim.activity_down_in, R.anim.activity_down_out);
                        getActivity().finish();
                    }

                }
            }

            @Override
            public void onDenominationChange(DenominationModel denomination, int oldCount, int newCount) {
                BigDecimal amount = denomination.getValue().multiply(new BigDecimal(oldCount - newCount));
                if (service.getValue().compareTo(BigDecimal.ZERO) >= 0) {
                    service.setValue(service.getValue().subtract(amount));
                } else {
                    service.setValue(service.getValue().add(amount));
                }
                updateAll();
            }

            @Override
            public void onTapCalculateButton() {
                service.calculate();
                switch (service.getAppState().getAppMode()) {
                    case NUMERIC:
                        if (numberInputView.getVisibility() == View.INVISIBLE) {
                            BigDecimal actual = service.getValue();
                            service.setValue(BigDecimal.ZERO);
                            countingTableView.initialize(currCurrency, service.getAppState(), locale);
                            sum.setVisibility(View.INVISIBLE);
                            numberInputView.setVisibility(View.VISIBLE);
                            numberInputView.setText(formatCurrency(actual));
                            numberPadView.setValue(actual);
                        }
                        else {
                            numberInputView.setText(formatCurrency(service.getValue()));
                            numberPadView.setValue(service.getValue());
                        }
                        break;
                }

                updateAll();
            }

            @Override
            public void onTapClearButton() {

                //ADDING TO HISTORY AND SERIALIZING
                if(!service.getAppState().isInCalculationMode()
                    && !service.getAppState().isInResultSwipingMode()
                    && !service.getAppState().isInOperationsBrowsingMode()){

                    Log.d("4Share", "Will save everything until the last result value");

                    if(service.getAppState().getOperations().size()>0){

                        Log.d("4Share", "Preparing data to save");

                        service.getAppState().addToOperationsHistory(service.getAppState().getOperations());
                        Log.d("4Share", "Saved in the array");

                        serialize(service.getAppState().getAllHistory());
                        Log.d("4Share", "Saved on disk");
                    }
                }else{
                    Log.d("4Share", "Not going to save");
                }

                if(service.getAppState().isInOperationsBrowsingMode()) {
                    service.getAppState().setInOperationsBrowsingMode(false);
                }

                switch (service.getAppState().getAppMode()) {
                    case NUMERIC:
                        sum.setVisibility(View.INVISIBLE);
                        numberInputView.setVisibility(View.VISIBLE);
                        numberInputView.setText(formatCurrency(BigDecimal.ZERO));
                        numberPadView.setValue(BigDecimal.ZERO);
                        break;
                }
                service.reset();
                updateAll();
            }

            @Override
            public void onTapEnterHistory() {
                if(service.getAppState().isInResultSwipingMode()){
                    service.getAppState().setOperations(
                            service.getAppState().getAllOperationsOfResult(
                                    service.getAppState().getOperations().get(0)));
                    service.getAppState().setInResultSwipingMode(false);
                }
                numberInputView.setVisibility(View.INVISIBLE);
                service.enterHistorySlideshow();
                service.getAppState().setInOperationsBrowsingMode(true);
                updateAll();
                sum.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTapNextHistory() {
                numberInputView.setVisibility(View.INVISIBLE);
                service.gotoNextHistorySlide();
                updateAll();
                sum.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTapPreviousHistory() {
                numberInputView.setVisibility(View.INVISIBLE);
                service.gotoPreviousHistorySlide();
                updateAll();
                sum.setVisibility(View.VISIBLE);
            }

            /**
             *
             * @param shouldGoBack true if the swipe is left > right, so that we go "bacK" in history
             *                     otherwise, false, so that we go forward in history
             */
            @Override
            public void onMemorySwipe(boolean shouldGoBack) {

                if(service.getAppState().isInCalculationMode()){
                    new UtilityMethods().vibrateDevice(getContext());
                    Log.d("4Share Log", "Not responding to two finger swipe : In Calculation Mode");
                    return;
                }

                /*if(shouldGoBack) {
                    Log.d("4Share","Go BACK in history");
                    return;
                }else{
                    Log.d("4Share","Go FORWARD in history");
                    return;
                }*/

                if(null != service.getAppState().getAllResults()
                        && service.getAppState().getAllResults().size() > 0) {
//                    && service.getAppState().getCurrentResultIndex() <= (service.getAppState().getAllResults().size() - 1)

                    ArrayList<MathOperationModel> results = new ArrayList<MathOperationModel>();

                    if(service.getAppState().isInResultSwipingMode()){
//                        && service.getAppState().getCurrentResultIndex() < service.getAppState().getAllResults().size() - 1
                        //Subsequent swipes after first one

                        AnalyticsLogger.logEvent(getContext(), AnalyticsLogger.EVENT_SUBSEQUENT_TWO_SWIPE);
                        if(shouldGoBack) {
                            if(service.getAppState().getCurrentResultIndex() < service.getAppState().getAllResults().size() - 1) {
                                service.getAppState().setCurrentResultIndex(service.getAppState().getCurrentResultIndex() + 1);
                            }else{
                                new UtilityMethods().vibrateDevice(getContext());
                                Log.d("4Share Log", "Not responding to two finger swipe : Not going back, no more history available");
                            }
                        }else{
                            //go forward in recent history
                            if(service.getAppState().getCurrentResultIndex() > 0) {
                                //Still in history mode, move one step forward in history
                                service.getAppState().setCurrentResultIndex(service.getAppState().getCurrentResultIndex() - 1);
                            }else{
                                //at the forward most point, get out of history
                                service.reset();
                                service.getAppState().setInResultSwipingMode(false);
                                updateAll();
                                return;
                            }
                        }

                        for (int i = service.getAppState().getCurrentResultIndex(); i< service.getAppState().getAllResults().size(); i++){
                            results.add(service.getAppState().getAllResults().get(i));
                        }
                    }else{
                        //First Swipe
                        /**
                         * initialize the result swiping mode only if current result index is 0, which
                         * means that the user hasn't gone through the list completely yet. Otherwise
                         * when the 'if' condition fails, this re-initializes the array and the swiping
                         * loops. We don't want that.
                         **/


                        AnalyticsLogger.logEvent(getContext(), AnalyticsLogger.EVENT_FIRST_TWO_SWIPE);

                        if(!shouldGoBack) {
                            return;
                        }

                        if(service.getAppState().getCurrentResultIndex() == 0) {
                            service.getAppState().setInResultSwipingMode(true);
                            results = service.getAppState().getAllResults();
                        }else{
                            //browsing more than last result
                            return;
                        }
                    }
                    service.getAppState().setOperations(results);
                    updateAll();
                }else{
                    new UtilityMethods().vibrateDevice(getContext());
                    Log.d("4Share Log", "Not responding to two finger swipe : No history available");
                }
            }
        });
    }

    /**
     * Called when the value displayed is changed.
     */
    private void updateCountingTable() {
        countingTableView.setAppState(service.getAppState());
    }

    /**
     * Initializes the <code>CurrencyScrollBarView</code> to the given currency code.
     *
     * @param currencyCode The currency code that the denominations being displayed is set to.
     *
     * @see CurrencyScrollbarView
     */

    private void initializeCurrencyScrollbar(String currencyCode){
        currencyScrollbarView = view.findViewById(R.id.currency_scrollbar);
        currencyScrollbarView.setCurrency(currencyCode);
        this.currCurrency = currencyScrollbarView.getCurrency();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            currencyScrollbarView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    AnalyticsLogger.logEvent(getContext(), AnalyticsLogger.EVENT_CASH_SCROLLBAR_SWIPED);
                }
            });
        }

        currencyScrollbarView.setCurrencyScrollbarListener(new CurrencyScrollbarListener() {
            @Override
            public void onTapDenomination(DenominationModel denomination) {
                AnalyticsLogger.logEvent(getContext(), AnalyticsLogger.EVENT_NOTE_ADDED);
                service.setValue(service.getValue().add(denomination.getValue()));
                updateAll();
            }

            @Override
            public void onVerticalSwipe() {
                if(service.getValue().floatValue() == 0f) {
                    switchAppMode();
                }
            }


        });
    }

    /**
     * Switches the Cash Calculator between numeric mode and image mode.
     *
     * @see AppService
     */
    public void switchAppMode() {
        service.switchAppMode();
        TextView sum = getView().findViewById(R.id.sum_view);

        switch(service.getAppState().getAppMode()) {
            case IMAGE:
                sum.setVisibility(View.VISIBLE);
                numberInputView.setVisibility(View.INVISIBLE);
                AnalyticsLogger.logEvent(getContext(), AnalyticsLogger.EVENT_CASH_SCROLLBAR_VISIBLE);
                break;
            case NUMERIC:
                sum.setVisibility(View.INVISIBLE);
                numberInputView.setVisibility(View.VISIBLE);
                AnalyticsLogger.logEvent(getContext(), AnalyticsLogger.EVENT_NUMPAD_VISIBLE);
                currencyOnCountingTable= formatCurrency(service.getValue());
                numberInputView.setText(currencyOnCountingTable);
                service.setValue(BigDecimal.ZERO);
                break;
        }

        updateAll();
    }

    public void switchToNumericMode() {
        if (service.getAppState().getAppMode() == AppStateModel.AppMode.IMAGE) {
            service.switchAppMode();
            TextView sum = getView().findViewById(R.id.sum_view);
            sum.setVisibility(View.INVISIBLE);
            numberInputView.setVisibility(View.VISIBLE);
            AnalyticsLogger.logEvent(getContext(), AnalyticsLogger.EVENT_NUMPAD_VISIBLE);
            numberInputView.setText(formatCurrency(service.getValue()));
            service.setValue(BigDecimal.ZERO);
        }
    }

    /**
     * Initializes the <code>NumberPadView</code> when the application is in numeric mode.
     *
     * @see NumberPadView
     */
    private void initializeNumberPad() {
        TextView sum = view.findViewById(R.id.sum_view);
        numberPadView = view.findViewById(R.id.number_pad_view);

        numberPadView.setListener(new NumberPadListener() {
            @Override
            public void onCheck(BigDecimal value) {
                if (numberInputView.getVisibility() == View.INVISIBLE) {
                    //do nothing
                }
                else {
                    sum.setVisibility(View.VISIBLE);
                    service.setValue(value);
                    numberInputView.setVisibility(View.INVISIBLE);
                    service.getAppState().setAppMode(AppStateModel.AppMode.IMAGE);
                    countingTableView.initialize(currCurrency, service.getAppState(), locale);
                    service.getAppState().setAppMode(AppStateModel.AppMode.NUMERIC);
                    updateAll();
                }
            }

            @Override
            public void onBack(BigDecimal value) {
                numberInputView.setText(formatCurrency(value));
                service.setValue(value);
            }

            @Override
            public void onTapNumber(BigDecimal value) {
                if (numberInputView.getVisibility() == View.INVISIBLE) {
                    service.setValue(BigDecimal.ZERO);
                    countingTableView.initialize(currCurrency, service.getAppState(), locale);
                    updateAll();
                }
                service.setValue(value);
                sum.setVisibility(View.INVISIBLE);
                numberInputView.setVisibility(View.VISIBLE);
                numberInputView.setText(formatCurrency(value));
            }

            @Override
            public void onVerticalSwipe() {
                if(service.getValue().floatValue() == 0f) {
                    switchAppMode();
                }
            }
        });
    }

    private String formatCurrency(BigDecimal value) {
        UtilityMethods utilityMethods = new UtilityMethods();
        return String.format(locale,"%s",
                utilityMethods.getAdaptedNumberFormat(locale)
                        .format(value)
        );
    }

    /**
     * Called when the application is updated.
     */
    private void updateAppMode() {
        switch(service.getAppState().getAppMode()) {
            case IMAGE:
                numberPadView.setVisibility(View.INVISIBLE);
                currencyScrollbarView.setVisibility(View.VISIBLE);
                break;
            case NUMERIC:
                numberPadView.setVisibility(View.VISIBLE);
                currencyScrollbarView.setVisibility(View.INVISIBLE);
                break;
        }
    }

    /**
     * Called whenever the application switches its mathematical operation mode.
     */
    private void switchState() {
        if(null!=getActivity()) {
            Intent intent = new Intent(getActivity(), getActivity().getClass());
            intent.putExtra(APP_STATE_KEY, service.getAppState());
            if (getActivity().getIntent().hasExtra("animationStage")) {
                intent.putExtra("animationStage", getActivity().getIntent().getIntExtra("animationStage", -2) + 1);
            }
            if (getActivity().getIntent().hasExtra("numericMode")) {
                intent.putExtra("numericMode", getActivity().getIntent().getBooleanExtra("numericMode", false));
            }
            if (getActivity().getIntent().hasExtra("currencyName")) {
                intent.putExtra("currencyName", getActivity().getIntent().getStringExtra("currencyName"));
            }
            startActivity(intent);
        }
    }

    /**
     * Called whenever a gesture is performed on the Cash Calculator and upon initialization.
     */
    private void updateAll() {
        updateCountingTable();
        updateAppMode();
    }

    public CurrencyScrollbarView getCurrencyScrollbarView() {
        return currencyScrollbarView;
    }

    public CountingTableView getCountingTableView() {
        return countingTableView;
    }

    public NumberPadView getNumberPadView() {
        return numberPadView;
    }

    /**
     * Saves the given ArrayList<> object onto disk
     * @param history LinkedHashMap representing the latest state of results and their calculations
     */
    private void serialize(LinkedHashMap<MathOperationModel, ArrayList<MathOperationModel>> history){
        try{
            String filename = "history_"+SavedPreferences.getSelectedCurrencyCode(getActivity());
            FileOutputStream fos = getContext().openFileOutput(filename,
                    Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(history);
            os.close();
            fos.close();
            Log.d("4Share","Serialized in file: "+filename);
        }catch (FileNotFoundException e) {
            Log.e("4Share",e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("4Share",e.toString());
            e.printStackTrace();
        }
    }

    /**
     * Retrieves saved state of history
     * @return
     */
    private LinkedHashMap<MathOperationModel, ArrayList<MathOperationModel>> deserialize(){
        LinkedHashMap<MathOperationModel, ArrayList<MathOperationModel>> deserializedList = new LinkedHashMap<MathOperationModel, ArrayList<MathOperationModel>>();
        try{
            String filename = "history_"+SavedPreferences.getSelectedCurrencyCode(getActivity());
            FileInputStream fis = getContext().openFileInput(filename);
            ObjectInputStream is = new ObjectInputStream(fis);
            deserializedList = (LinkedHashMap<MathOperationModel, ArrayList<MathOperationModel>>) is.readObject();
            is.close();
            fis.close();
            Log.d("4Share","Deserialized from file: "+filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("4Share",e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("4Share",e.toString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.e("4Share",e.toString());
        }
        return deserializedList;
    }
}
