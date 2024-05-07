package com.example.mindle;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mindle.databinding.ActivityDetailedBinding;

public class DetailedActivity extends AppCompatActivity {

    ActivityDetailedBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = this.getIntent();
        if (intent != null){
            String parameters = intent.getStringExtra("parameters");
            int text = intent.getIntExtra("text", R.string.about_the_program);
            int image = intent.getIntExtra("image", R.drawable.baseline_bubble_chart_24);

            binding.detailImage.setImageResource(image);
            binding.detailName.setText(parameters);
            binding.detailDesc.setText(text);
        }
    }
}