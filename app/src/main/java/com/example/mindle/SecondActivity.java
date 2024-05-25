package com.example.mindle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.mindle.databinding.ActivityMainBinding;
import com.example.mindle.databinding.ActivitySecondBinding;

public class SecondActivity extends AppCompatActivity {
    private static final String TAG = "MyApp";
    ActivitySecondBinding binding;
    SharedPreferences sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySecondBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPref = getApplicationContext().getSharedPreferences("My_Preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Log.d(TAG, "Activity created");
        binding.tvMain.setText(sharedPref.getString("token","No token wtf?"));

    }
}