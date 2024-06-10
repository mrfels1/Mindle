package com.example.mindle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mindle.databinding.ActivityCreatePostBinding;
import com.example.mindle.models.ListAdapter;

import java.util.HashMap;
import java.util.Map;

public class CreatePostActivity extends AppCompatActivity {
    ActivityCreatePostBinding binding;
    SharedPreferences sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreatePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPref = getApplicationContext().getSharedPreferences("My_Preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String token = sharedPref.getString("token", "no token");
        binding.createpostbrn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.textET.getText().toString().isEmpty() || binding.titleET.getText().toString().isEmpty()){
                    Toast.makeText(CreatePostActivity.this, "Напишите текст и название поста", Toast.LENGTH_LONG).show();

                    return;
                }
                String url = getResources().getString(R.string.ip)+"/api/post/create";
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(CreatePostActivity.this, "Create response: " + response, Toast.LENGTH_LONG).show();
                        finish();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CreatePostActivity.this, "Create error: " + error.toString() + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Authorization", "Bearer " + token);
                        headers.put("title", binding.titleET.getText().toString());
                        headers.put("text_content",binding.textET.getText().toString());
                        Log.d("create", "getHeaders: " + binding.titleET.getText().toString() + binding.textET.getText().toString());
                        return headers;
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(
                        200000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Volley.newRequestQueue(CreatePostActivity.this).add(request);
            }
        });
    }
}