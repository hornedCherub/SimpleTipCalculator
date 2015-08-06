package com.example.evelina.simpletipcalculator;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    public static final String LOG_TAG = "CheckPrefs";
    EditText txtTipPercentage, txtMealCost;
    TextView textTotalTip, textTotalMealCost, historyList;
    Button btnCalculate, btnClear;

    public static final String PREFS_NAME = "MyPrefsFile";
    private SharedPreferences settings;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    private static String roundTotalMealCost, roundTotalTip;

    private static String MEAL_COST = "mealCost";
    private static String TOTAL_TIP = "totalTip";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String textForDisplay = restorePreferences(); //restore from Shared preferences

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Log.i(LOG_TAG, key);

            }
        };

        settings.registerOnSharedPreferenceChangeListener(listener);

        historyList = (TextView) findViewById(R.id.historyList);//display history of previous tips
        historyList.setText(textForDisplay);

        btnCalculate = (Button)findViewById(R.id.calculateButton);
        btnClear = (Button)findViewById(R.id.clearButton);

        txtTipPercentage = (EditText) findViewById(R.id.editText2);
        txtMealCost = (EditText) findViewById(R.id.editText1);
        textTotalTip = (TextView) findViewById(R.id.textView);
        textTotalMealCost = (TextView) findViewById(R.id.textView2);

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateTip();
                saveSharedPrefs();
                dismissKeyboard();
            }

        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearApp();
                dismissKeyboard();
            }
        });
    }

    private String restorePreferences() {
        String textForDisplay;
        settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//                String restoredText = settings.getString("text", null);
//                if (restoredText != null) {
        String mealCostPrefs = settings.getString(MEAL_COST, "No previous meal cost");
        Log.i(LOG_TAG, mealCostPrefs);
        String totalTipPrefs = settings.getString(TOTAL_TIP, "No previous tip");
        Log.i(LOG_TAG, totalTipPrefs);
        textForDisplay = mealCostPrefs.concat("\t").concat(totalTipPrefs).concat("\n");
//                }
        return textForDisplay;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private void calculateTip(){

        String txtTipPercentageString = txtTipPercentage.getText().toString();
        String txtMealCostString = txtMealCost.getText().toString();

        if ((txtTipPercentageString.isEmpty() || txtMealCostString.isEmpty())){
            CharSequence errorMessage = "Please fill in both fields";
            executeToast(errorMessage);
            return;
        }

        double tipPercentage = Double.parseDouble(txtTipPercentage.getText().toString());
        double mealCost = Double.parseDouble(txtMealCost.getText().toString());
        double totalMealCost;
        double totalTip;

        tipPercentage = tipPercentage * 0.01;
        totalTip = mealCost * tipPercentage;
        totalMealCost = mealCost + totalTip;

        roundTotalTip = String.format("%1.2f", totalTip);
        roundTotalMealCost = String.format("%1.2f", totalMealCost);

        textTotalTip.setText("Total tip: $" + roundTotalTip);
        textTotalMealCost.setText("Total meal cost: $" + roundTotalMealCost);

    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private void saveSharedPrefs() {

        settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(MEAL_COST, roundTotalMealCost);
        editor.putString(TOTAL_TIP, roundTotalTip);

        editor.commit(); //==apply();
    }


    private void executeToast(CharSequence text) {

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void clearApp(){
        txtTipPercentage.setText("");
        txtMealCost.setText("");
        textTotalTip.setText("Total tip: $0.00");
        textTotalMealCost.setText("Total meal cost: $0.00");
    }

    private void dismissKeyboard(){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txtMealCost.getWindowToken(),0);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
