package ma.fstt.calculatrice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView resultTv, solutionTv;
    MaterialButton buttonBrackOpen, buttonBrackClose, buttonHistory;
    MaterialButton buttonDivide, buttonMultiply, buttonMinus, buttonPlus, buttonEquals;
    MaterialButton button0, button1, button2, button3, button4, button5, button6, button7, button8, button9;
    MaterialButton buttonAC, buttonC, buttonDot;

    Stack<Character> bracketStack;
    List<String> historyOperation;

    private DatabaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultTv = findViewById(R.id.result_tv);
        solutionTv = findViewById(R.id.solution_tv);

        bracketStack = new Stack<>();
        historyOperation = new ArrayList<>();
        databaseHelper = new DatabaseHelper(this);

        Log.d("MainActivity", "History Operation List: " + historyOperation.toString());


        assignId(buttonHistory, R.id.button_history);
        assignId(buttonBrackOpen, R.id.button_open_bracket);
        assignId(buttonBrackClose, R.id.button_close_bracket);
        assignId(buttonDivide, R.id.button_divide);
        assignId(buttonMultiply, R.id.button_multiply);
        assignId(buttonPlus, R.id.button_plus);
        assignId(buttonMinus, R.id.button_minus);
        assignId(buttonEquals, R.id.button_equals);
        assignId(button0, R.id.button_0);
        assignId(button1, R.id.button_1);
        assignId(button2, R.id.button_2);
        assignId(button3, R.id.button_3);
        assignId(button4, R.id.button_4);
        assignId(button5, R.id.button_5);
        assignId(button6, R.id.button_6);
        assignId(button7, R.id.button_7);
        assignId(button8, R.id.button_8);
        assignId(button9, R.id.button_9);
        assignId(buttonAC, R.id.button_ac);
        assignId(buttonC, R.id.button_c);
        assignId(buttonDot, R.id.button_dot);

    }

    void assignId(MaterialButton btn, int id) {
        btn = findViewById(id);
        btn.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        MaterialButton button = (MaterialButton) view;
        String buttonText = button.getText().toString();
        String dataToCalculate = solutionTv.getText().toString();

        if (buttonText.equals("H")) {
            openHistoryActivity();
            return;
        }

        if (buttonText.equals("AC")) {
            solutionTv.setText("");
            resultTv.setText("0");
            bracketStack.clear();
            return;
        }


        if (buttonText.equals("=")) {
            if (!bracketStack.isEmpty()) {
                solutionTv.setText("ERR: Missing closing bracket");
                return;
            }

            String finalResult = getResult(dataToCalculate);
            if (!finalResult.equals("ERR")) {
                solutionTv.setText(finalResult);
                resultTv.setText(finalResult);
                saveOperationToHistory(dataToCalculate, finalResult);
            } else {
                solutionTv.setText("ERR");
            }
            return;
        }

        if (buttonText.equals("(")) {
            dataToCalculate += buttonText;
            bracketStack.push('(');
        } else if (buttonText.equals(")")) {
            if (!bracketStack.isEmpty() && bracketStack.peek() == '(') {
                dataToCalculate += buttonText;
                bracketStack.pop();
            } else {
                solutionTv.setText("ERR: Missing opening bracket");
                return;
            }
        } else if (buttonText.equals("DEL")) {
            if (!dataToCalculate.isEmpty()) {
                dataToCalculate = dataToCalculate.substring(0, dataToCalculate.length() - 1);
            }
        } else {
            dataToCalculate += buttonText;
        }

        solutionTv.setText(dataToCalculate);

        String finalResult = getResult(dataToCalculate);

        if (!finalResult.equals("ERR")) {
            resultTv.setText(finalResult);
        }
    }

    String getResult(String data) {
        try {
            Context context = Context.enter();
            context.setOptimizationLevel(-1);
            Scriptable scriptable = context.initSafeStandardObjects();
            String finalResult = context.evaluateString(scriptable, data, "Javascript", 1, null).toString();

            // Round the result to 5 decimal places
            double result = Double.parseDouble(finalResult);
            DecimalFormat decimalFormat = new DecimalFormat("#.#####");
            finalResult = decimalFormat.format(result);

            // Remove trailing .0 if present
            if (finalResult.endsWith(".0")) {
                finalResult = finalResult.replace(".0", "");
            }

            // Handle devision by zero
            if (Double.isInfinite(result)) {
                finalResult = "ERR: Division by zero";
            }

            String operationResult = data + " = " + finalResult;
            historyOperation.clear();
            historyOperation.add(operationResult);

            return finalResult;
        } catch (Exception e) {
            e.printStackTrace();
            return "ERR";
        }
    }

    private void saveOperationToHistory(String operation, String result) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        // Get the current number of operations in the table
        long rowCount = DatabaseUtils.queryNumEntries(db, DatabaseHelper.TABLE_NAME);

        // If the number of operations exceeds 10, delete the oldest operation
        if (rowCount >= 10) {
            String oldestOperationId = "SELECT " + DatabaseHelper.COLUMN_ID +
                    " FROM " + DatabaseHelper.TABLE_NAME +
                    " ORDER BY " + DatabaseHelper.COLUMN_ID +
                    " ASC LIMIT 1";
            db.execSQL("DELETE FROM " + DatabaseHelper.TABLE_NAME +
                    " WHERE " + DatabaseHelper.COLUMN_ID +
                    " IN (" + oldestOperationId + ")");
        }

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_OPERATION, operation + " = " + result);
        db.insert(DatabaseHelper.TABLE_NAME, null, values);
        db.close();
        Log.d("onSave", "table saved");
    }


    void openHistoryActivity() {
        List<String> historyOperation = fetchHistoryFromDatabase();
        Intent intent = new Intent(this, HistoryActivity.class);
        intent.putStringArrayListExtra("historyOperation", new ArrayList<>(historyOperation));
        startActivity(intent);
    }

    private List<String> fetchHistoryFromDatabase() {
        List<String> historyOperation = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String[] columns = {DatabaseHelper.COLUMN_OPERATION};
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);

        int columnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_OPERATION);
        if (columnIndex != -1) {
            while (cursor.moveToNext()) {
                String operation = cursor.getString(columnIndex);
                historyOperation.add(operation);
            }
        }
        cursor.close();
        db.close();
        return historyOperation;
    }


    // Persist history until emulator is off
    @Override
    protected void onResume() {
        super.onResume();
        loadHistoryList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveHistoryList();
    }

    private void saveHistoryList() {
        SharedPreferences preferences = getSharedPreferences("CalcHistory", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("HistorySize", historyOperation.size());
        for (int i = 0; i < historyOperation.size(); i++) {
            editor.putString("History_" + i, historyOperation.get(i));
        }
        editor.apply();
    }

    private void loadHistoryList() {
        SharedPreferences preferences = getSharedPreferences("CalcHistory", MODE_PRIVATE);
        int historySize = preferences.getInt("HistorySize", 0);
        historyOperation.clear();
        for (int i = 0; i < historySize; i++) {
            String history = preferences.getString("History_" + i, "");
            historyOperation.add(history);
        }
    }

}