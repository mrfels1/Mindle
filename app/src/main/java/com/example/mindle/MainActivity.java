package com.example.mindle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mindle.databinding.ActivityMainBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.android.volley.Request;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyApp";
    ActivityMainBinding binding;
    SharedPreferences sharedPref;
    private TextInputEditText emailField;
    private TextInputEditText passwordField;
    private RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        emailField = (TextInputEditText) binding.email;
        passwordField = (TextInputEditText) binding.password;

        sharedPref = getApplicationContext().getSharedPreferences("My_Preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Log.d(TAG, "Activity created");
        binding.loginbtn.setOnClickListener(v -> onLoginClick());
        binding.email.setText(sharedPref.getString("email",""));
        binding.password.setText(sharedPref.getString("password",""));

        binding.regbtn.setOnClickListener(v -> onRegisterClick());
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void onLoginClick() {

        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Почта/Пароль не могут быть пустыми", Toast.LENGTH_LONG).show();
            return;
        }
        sharedPref = getApplicationContext().getSharedPreferences("My_Preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();


        String url = getResources().getString(R.string.ip)+"/api/token";

        int random = ThreadLocalRandom.current().nextInt(100000, 999999);

        String device = sharedPref.getString("device","None");

        if(device.equals("None")){
            device = String.valueOf(random);
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("device", device);
        headers.put("password", password);
        headers.put("email", email);

        editor.putString("device", device);
        editor.apply();
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.contains("Error\":\"No such credentials")){
                    Toast.makeText(MainActivity.this, "Неправильный логин/пароль.", Toast.LENGTH_LONG).show();
                    return;
                }
                editor.putString("token", response);
                editor.putString("email", binding.email.getText().toString());
                editor.putString("password",binding.password.getText().toString());
                editor.apply();

                Intent intent = new Intent (MainActivity.this, SecondActivity.class);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(request);
    }

    private void onRegisterClick(){
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);

    }
}//dimas9.00@mail.ru 12344321