package org.myoralvillage.cashcalculatormodule.models;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

/**
 *  A model class used to represent the modes that the Cash Calculator can be displayed as well as
 *  the operations each mode can execute.
 *
 * @author Peter Panagiotis Roubatsis
 * @author Hamza Mahfooz
 * @see java.lang.Object
 */
public class AppStateModel implements Serializable {
    /**
     * The current mode of the Cash Calculator. The available modes are either IMAGE or NUMERIC.
     *
     * @see AppMode
     */
    private AppMode appMode;

    /**
     * A List of operations performed by the Cash Calculator leading to the <code>appMode</code>. Each element in
     * the list contains a <code>MathOperationModel</code> class.
     *
     * @see MathOperationModel
     */
    private ArrayList<MathOperationModel> operations;

    /**
     * Used to store retrieved calculations from disk, until they are required
     */
    private ArrayList<MathOperationModel> retrievedOperations;

    /**
     * HashMap that stores all the operations history. The key are the results, the values are the
     * list of operations in the result. So that when two swipes are performed, all the keys (results)
     * can be traversed, and when the user wants to see operations under a result, the values (list of
     * operations) can be used.
     */
    private LinkedHashMap<MathOperationModel, ArrayList<MathOperationModel>> operationsHistory = new LinkedHashMap<MathOperationModel, ArrayList<MathOperationModel>>();

    /**
     * An integer used to record the index of the current operation after a list of operations are
     * performed. This integer is useful in traversing the history of the Cash Calculator module.
     */
    private int currentOperationIndex;

    private int currentResultIndex = 0;

    private boolean shouldSaveResults = false;

    private boolean isInCalculationMode = false;

    private boolean isInOperationsBrowsingMode = false;

    private boolean isInResultSwipingMode = false;

    private String currencyCode = "";

    /**
     * Constructs a new <code>AppStateModel</code> in the specified Cash Calculator mode and the list
     * of operations performed by the Cash Calculator.
     *
     * @param appMode The mode of the Cash Calculator.
     * @param operations A list of mathematical operations for the appMode.
     */
    public AppStateModel(AppMode appMode, ArrayList<MathOperationModel> operations) {
        this.appMode = appMode;
        this.operations = operations;
    }

    /**
     * Returns the mode associated with this model.
     *
     * @return The mode of this model.
     */
    public AppMode getAppMode() {
        return appMode;
    }

    /**
     * Returns the list of operations performed associated with this model.
     *
     * @return list of operations of this model.
     * @see MathOperationModel
     */
    public ArrayList<MathOperationModel> getOperations() {
        return operations;
    }

    /**
     * Sets operations list with previously saved operations
     * @param operations
     */
    public void setOperations(ArrayList<MathOperationModel> operations) {
        this.operations = operations;
    }

    public ArrayList<MathOperationModel> getRetrievedOperations() {
        return this.retrievedOperations;
    }

    public void setRetrievedOperations(LinkedHashMap<MathOperationModel, ArrayList<MathOperationModel>> retrievedOperations) {
        this.operationsHistory = retrievedOperations;
    }

    public ArrayList<MathOperationModel> getAllResults(){
        ArrayList<MathOperationModel> allResults = new ArrayList<MathOperationModel>(this.operationsHistory.keySet());
        Collections.reverse(allResults);
        return allResults;
    }

    public ArrayList<MathOperationModel> getAllOperationsOfResult(MathOperationModel hashKey) {
        return this.operationsHistory.get(hashKey);
    }

    public LinkedHashMap<MathOperationModel, ArrayList<MathOperationModel>> getAllHistory() {
        return this.operationsHistory;
    }

    public void putAllHistory(LinkedHashMap<MathOperationModel, ArrayList<MathOperationModel>> operationsHistory) {
        this.operationsHistory = operationsHistory;
    }

    /**
     * Finds the index of the RESULT type just previous to the current one
     * @param operations
     * @return
     */
    private static int findIndexOfLastResult(ArrayList<MathOperationModel> operations) {
        boolean isLastOperationResult = false;
        if (operations.get(operations.size() - 1).getType() == MathOperationModel.MathOperationMode.RESULT){
            return (operations.size() - 1);
        }else{
            int index = 0;
            for (int i = 0; i < operations.size(); i++)
                if (operations.get(i).getType() == MathOperationModel.MathOperationMode.RESULT)
                    index = i;

            return index;
        }
    }

    /**
     * Finding the last result, and saving up all the things until then, into the operations array.
     * This avoids the following condition:
     * because the user can press clear right after swiping to go into calculation mode, without making any calculation.
     * at that point, we'll try to save all the operations until the last results, abandoning this current operation which the user swiped but didn't use
     * @param operations
     */
    public void addToOperationsHistory(ArrayList<MathOperationModel> operations) {
        ArrayList<MathOperationModel> valueOperationsForThisResult = new ArrayList<MathOperationModel>();
        int lastResultIndex = findIndexOfLastResult(operations);
        if (lastResultIndex <= 0){
            return;
        }
        MathOperationModel keyResult = operations.get(lastResultIndex);

        for (int i = 0; i<=lastResultIndex; i++) {
            valueOperationsForThisResult.add(operations.get(i));
        }

        if(this.operationsHistory.keySet().size() > 49){
            this.operationsHistory.remove((MathOperationModel) this.operationsHistory.keySet().toArray()[0]);
        }
        this.operationsHistory.put(keyResult, valueOperationsForThisResult);
    }

    /**
     * Marks the mode of the Cash Calculator to the specified <code>appMode</code>.
     *
     * @param appMode the Cash Calculator mode that this model should be set.
     * @see AppMode
     */
    public void setAppMode(AppMode appMode) {
        this.appMode = appMode;
    }

    /**
     * Returns the index of the current operation from the list of operations associated with this
     * model. Usually, this method is used during the traversal of the past operations performed by
     * the Cash Calculator.
     *
     * @return the index of the list of operations (Integer value).
     */
    public int getCurrentOperationIndex() {
        return currentOperationIndex;
    }

    /**
     * Changes the index of the current operation index from the list of operations. The index is
     * changed to update which operation the model is currently visiting.
     *
     * @param currentOperationIndex The index related to the list of <code>operations</code>.
     *                              Integer value.
     */
    public void setCurrentOperationIndex(int currentOperationIndex) {
        this.currentOperationIndex = currentOperationIndex;
    }

    public int getCurrentResultIndex() {
        return currentResultIndex;
    }

    public void setCurrentResultIndex(int currentResultIndex) {
        this.currentResultIndex = currentResultIndex;
    }

    public boolean shouldSaveResults() {
        return shouldSaveResults;
    }

    public void setShouldSaveResults(boolean shouldSaveResults) {
        this.shouldSaveResults = shouldSaveResults;
    }

    public boolean isInCalculationMode() {
        return isInCalculationMode;
    }

    public void setInCalculationMode(boolean inCalculationMode) {
        isInCalculationMode = inCalculationMode;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    /**
     * Is the application currently in the history slideshow mode?
     * @return True if it is in the memory mode, return False otherwise.
     */
    public boolean isInHistorySlideshow() {
        return getCurrentOperationIndex() < (getOperations().size() - 1);
    }

    public boolean isInResultSwipingMode(){
        return this.isInResultSwipingMode;
    }

    public void setInResultSwipingMode(boolean isInResultSwipingMode){
        this.isInResultSwipingMode = isInResultSwipingMode;
    }

    public boolean isInOperationsBrowsingMode() {
        return isInOperationsBrowsingMode;
    }

    public void setInOperationsBrowsingMode(boolean inOperationsBrowsingMode) {
        isInOperationsBrowsingMode = inOperationsBrowsingMode;
    }

    /**
     * Returns the operation from the list, <code>operations</code>, based on the value of
     * the current Operation Index
     *
     * @return A mathematical operation performed by the Cash Calculator, an instance
     * of {@link MathOperationModel}.
     */
    public MathOperationModel getCurrentOperation() {
        return operations.get(currentOperationIndex);
    }

    /**
     * Creates a default Cash Calculator model. THe default <code>appMode</code> is the IMAGE mode
     * and the default <code>operations</code> is a list containing one <code>MathOperationModel</code>
     * with the STANDARD operation.
     *
     * @return A new <code>AppStateModel</code> in IMAGE mode and the standard operation.
     */
    public static AppStateModel getDefault() {
        ArrayList<MathOperationModel> operations = new ArrayList<>();
        operations.add(MathOperationModel.createStandard(new BigDecimal(0), MathOperationModel.MathOperationMode.STANDARD));

        return new AppStateModel(AppMode.IMAGE, operations);
    }

    /**
     * Indicates whether some other <code>AppStateModel</code> is equal to this one.
     *
     * @param obj an <code>AppStateModel</code> with which to compare.
     * @return true if this <code>AppStateModel</code> is the same as the obj argument; false
     * otherwise.
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof AppStateModel))
            return false;

        AppStateModel state = (AppStateModel) obj;
        return operations.equals(state.operations) &&
                appMode == state.appMode &&
                currentOperationIndex == state.currentOperationIndex;
    }

    /**
     * An enum class that highlights the mode of the <code>AppStateModel</code>.
     *
     * @see AppStateModel
     */
    public enum AppMode {
        IMAGE,
        NUMERIC
    }
}
