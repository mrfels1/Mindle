package com.example.mindle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mindle.databinding.ActivitySecondBinding;


public class SecondActivity extends AppCompatActivity {
    private static final String TAG = "MyApp";
    ActivitySecondBinding binding;
    private EditText numberInput;
    private Button addButton;
    private TextView sumDisplay;
    private float sum = 0;
    SharedPreferences sPref;
    final String SAVED_TEXT = "saved_text";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySecondBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.addButton.setOnClickListener(v -> addNumber());

        loadNum();

        binding.remButton.setOnClickListener(v -> removeNum());

        binding.nextActivity.setOnClickListener(v ->{
            Intent intent = new Intent (SecondActivity.this, FourthActivity.class);
            startActivity(intent);
        });
    }



    private void addNumber() {
//        String numberStr = numberInput.getText().toString();
//        if (!numberStr.isEmpty()) {
//            double number = Double.parseDouble(numberStr);
//            sum += number;
//            sumDisplay.setText("Сумма: " + sum);
//            numberInput.setText("");
//        }

        String numberStr = binding.numberInput.getText().toString();
        if (!numberStr.isEmpty()){
            float number = Float.parseFloat(numberStr);
            sum += number;
            binding.sumDisplay.setText("Cумма " + sum);
            binding.numberInput.setText("");
            saveNum();
        }

    }

    private void saveNum() {
        sPref = getSharedPreferences("data_sum", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(SAVED_TEXT, binding.sumDisplay.getText().toString());
        ed.commit();
    }

    private  void loadNum(){
        sPref = getSharedPreferences("data_sum", MODE_PRIVATE);
        String saveNum = sPref.getString(SAVED_TEXT, "");
        binding.sumDisplay.setText(saveNum);
    }

    private void removeNum(){
        sPref = getSharedPreferences("data_sum", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.remove(SAVED_TEXT);
        binding.sumDisplay.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveNum();
    }
}
