package ma.fstt.calculatrice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

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
    List<String> historyList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultTv = findViewById(R.id.result_tv);
        solutionTv = findViewById(R.id.solution_tv);

        bracketStack = new Stack<>();
        historyList = new ArrayList<>();


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
        } else if (buttonText.equals("C")) {
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
            historyList.add(data);
            String finalResult = context.evaluateString(scriptable, data, "Javascript", 1, null).toString();
            if (finalResult.endsWith(".0")) {
                finalResult = finalResult.replace(".0", "");
            }
            return finalResult;
        } catch (Exception e) {
            e.printStackTrace();
            return "ERR";
        }
    }

    void openHistoryActivity() {
        Intent intent = new Intent(this, HistoryActivity.class);
        intent.putStringArrayListExtra("historyList", new ArrayList<>(historyList));
        startActivity(intent);
    }

}