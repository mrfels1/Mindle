package com.example.mindle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mindle.databinding.ActivityMainBinding;
import com.example.mindle.databinding.ActivityRegisterBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "MyApp";
    ActivityRegisterBinding binding;
    SharedPreferences sharedPref;
    private TextInputEditText nameField;
    private TextInputEditText emailField;
    private TextInputEditText passwordField;
    private RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        nameField = (TextInputEditText) binding.name;
        emailField = (TextInputEditText) binding.emailReg;
        passwordField = (TextInputEditText) binding.passwordReg;

        sharedPref = getApplicationContext().getSharedPreferences("My_Preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Log.d(TAG, "Activity created");
        binding.regbtnReg.setOnClickListener(v -> onRegisterClick());
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }
    private void onRegisterClick(){
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        String name = nameField.getText().toString();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Имя/Почта/Пароль не могут быть пустыми", Toast.LENGTH_LONG).show();
            return;
        }
        String url = getResources().getString(R.string.ip)+"/api/register";


        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.contains("Error\":\"User already exists")){
                    Toast.makeText(RegisterActivity.this, "Почта/имя уже использованы", Toast.LENGTH_LONG).show();
                    binding.emailReg.setText("");
                    binding.name.setText("");
                    return;
                }
                Toast.makeText(RegisterActivity.this, "Пользователь зарегистрирован", Toast.LENGTH_LONG).show();
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegisterActivity.this, error.toString(), Toast.LENGTH_LONG).show();

                // handle the error here
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("name", name);
                headers.put("password", password);
                headers.put("email", email);
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                200000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(RegisterActivity.this).add(request);
    }
}