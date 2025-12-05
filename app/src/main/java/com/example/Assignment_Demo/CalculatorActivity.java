package com.example.Assignment_Demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.mozilla.javascript.Context; // Import Rhino
import org.mozilla.javascript.Scriptable; // Import Rhino

public class CalculatorActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvResult, tvSolution;
    Button btnC, btnOpen, btnClose, btnDivide;
    Button btn7, btn8, btn9, btnMultiply;
    Button btn4, btn5, btn6, btnMinus;
    Button btn1, btn2, btn3, btnPlus;
    Button btnAC, btn0, btnDot, btnEqual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        // Header Back Button
        View btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        tvResult = findViewById(R.id.tvResult);
        tvSolution = findViewById(R.id.tvSolution);

        // Assign IDs to buttons
        assignId(btnC, R.id.btnC);
        assignId(btnOpen, R.id.btnOpen);
        assignId(btnClose, R.id.btnClose);
        assignId(btnDivide, R.id.btnDivide);
        assignId(btnMultiply, R.id.btnMultiply);
        assignId(btnPlus, R.id.btnPlus);
        assignId(btnMinus, R.id.btnMinus);
        assignId(btnEqual, R.id.btnEqual);
        assignId(btn0, R.id.btn0);
        assignId(btn1, R.id.btn1);
        assignId(btn2, R.id.btn2);
        assignId(btn3, R.id.btn3);
        assignId(btn4, R.id.btn4);
        assignId(btn5, R.id.btn5);
        assignId(btn6, R.id.btn6);
        assignId(btn7, R.id.btn7);
        assignId(btn8, R.id.btn8);
        assignId(btn9, R.id.btn9);
        assignId(btnAC, R.id.btnAC);
        assignId(btnDot, R.id.btnDot);
    }

    void assignId(Button btn, int id){
        btn = findViewById(id);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Button button = (Button) view;
        String buttonText = button.getText().toString();
        // Get the current text on the screen
        String dataToCalculate = tvSolution.getText().toString();

        if (buttonText.equals("AC")) {
            // Clear everything
            tvSolution.setText("");
            tvResult.setText("0");
            return;
        }

        if (buttonText.equals("=")) {
            // Move result to the solution text so user can continue calculating
            tvSolution.setText(tvResult.getText());
            return;
        }

        if (buttonText.equals("C")) {
            // Remove the last character (Backspace)
            if (dataToCalculate.length() > 0) {
                dataToCalculate = dataToCalculate.substring(0, dataToCalculate.length() - 1);
            }
        } else {
            // Append the new button text (e.g., add "7" or "+")
            dataToCalculate = dataToCalculate + buttonText;
        }

        // Update the top text view
        tvSolution.setText(dataToCalculate);

        if (dataToCalculate.isEmpty()) {
            tvResult.setText("0");
            return;
        }
        // calculate immediately (if possible)
        String finalResult = calculateResult(dataToCalculate);

        if (!finalResult.equals("Err")) {
            tvResult.setText(finalResult);
        }
    }

    String calculateResult(String data) {
        try {
            // 1. Enter the Rhino Context
            Context context = Context.enter();
            context.setOptimizationLevel(-1); // Must be -1 for Android

            // 2. Initialize the script
            Scriptable scriptable = context.initStandardObjects();

            // 3. Evaluate the string
            // (Since JavaScript knows how to do math, when it runs "5+10", it returns the number 15.
            // If you passed "10/2", JavaScript returns 5.
            // It even handles order of operations automatically (e.g., "2+2*2" becomes 6, not 8))
            String finalResult = context.evaluateString(scriptable, data, "Javascript", 1, null).toString();

            // 4. Remove ".0" if it's a whole number (e.g., "5.0" -> "5")
            if (finalResult.endsWith(".0")) {
                finalResult = finalResult.replace(".0", "");
            }

            return finalResult;
        } catch (Exception e) {
            // If the formula is incomplete (like "5+"), just return Err
            return "Err";
        }
    }
}