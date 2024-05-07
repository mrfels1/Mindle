package com.example.mindle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mindle.databinding.ActivityFourthBinding;
import com.example.mindle.models.ListAdapter;
import com.example.mindle.models.ListData;

import java.util.ArrayList;

public class FourthActivity extends AppCompatActivity {

    ListAdapter listAdapter;
    ArrayList<ListData> dataArrayList = new ArrayList<>();
    ListData listData;

    ActivityFourthBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFourthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ListView lvMain = (ListView)findViewById(binding.lv.getId());
        final TextView txt = (TextView)findViewById(binding.text.getId());


        String[] parameters = {"О программе", "Инструкция пользования", "Об авторе"};
        int[] text = {R.string.about_the_program, R.string.Users_instructions, R.string.About_the_author};
        int[] image = {R.drawable.baseline_bubble_chart_24, R.drawable.rounded_info_i_24, R.drawable.baseline_perm_identity_24};
        int[] image1 = {R.drawable.icon2, R.drawable.icon_monopolia,R.drawable.iconc1};


        for (int i =0; i < image.length; i++){
            listData = new ListData(parameters[i], image[i]);
            dataArrayList.add(listData);
        }


        listAdapter = new ListAdapter(FourthActivity.this, dataArrayList);
        binding.lv.setAdapter(listAdapter);
        binding.lv.setClickable(true);

        binding.lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(FourthActivity.this, DetailedActivity.class);
                intent.putExtra("parameters", parameters[i]);
                intent.putExtra("text", text[i]);
                intent.putExtra("image", image1[i]);
                startActivity(intent);
            }
        });

    }
}