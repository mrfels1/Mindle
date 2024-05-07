package com.example.mindle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mindle.databinding.ActivityMainBinding;
import com.google.android.material.textfield.TextInputEditText;


import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private RequestQueue mQueue;
    private static final String TAG = "MyApp";

    ActivityMainBinding binding;

    SharedPreferences sharedPref;
    private TextInputEditText emailField;
    private TextInputEditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mQueue = Volley.newRequestQueue(this);

        emailField = (TextInputEditText) binding.email;
        passwordField = (TextInputEditText) binding.password;
        sharedPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Log.d(TAG, "Activity created");
        binding.loginbtn.setOnClickListener(v -> onLoginClick());
        binding.email.setText("dimas9.00@mail.ru");
        binding.password.setText("123321");

        binding.regbtn.setOnClickListener(v -> {
            Intent intent = new Intent (MainActivity.this, ThirdActivity.class);
            startActivity(intent);
        });
    }

    private void onLoginClick() {
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Почта/Пароль не могут быть пустыми", Toast.LENGTH_LONG).show();
            return;
        }
        sharedPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        getCsrf(email, password,editor);
    }

    private void postLogin(String email, String password,String csrf,SharedPreferences.Editor editor) {
        String url = "http://192.168.1.65:8000/api_login";
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                binding.email.setText("");
                binding.password.setText("");// on below line we are displaying a success toast message.
                Toast.makeText(MainActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
                binding.responseTV.setText(response);
                editor.putString("email", email);
                editor.putString("password", password);

                /*
                try {
                    // on below line we are parsing the response
                    // to json object to extract data from it.
                    JSONObject respObj = new JSONObject(response);

                    // below are the strings which we
                    // extract from our json object.
                    String name = respObj.getString("email");
                    String pass = respObj.getString("password");

                    // on below line we are setting this string s to our text view.
                    binding.responseTV.setText("Email : " + email + "\n" + "Pass : " + password);
                } catch (JSONException e) {
                    e.printStackTrace();
                    binding.responseTV.setText(e.toString());
                }
                */
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Fail to get response = " + error.networkResponse.statusCode, Toast.LENGTH_SHORT).show();
                binding.responseTV.setText(""+error.networkResponse.allHeaders);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("User-Agent", "Mindle/1.0");
                params.put("Content-Type", "application/json");
                params.put("email", email);
                params.put("password", password);
                params.put("X-CSRF-TOKEN",csrf);

                return params;
            }
        };

        // below line is to make
        // a json object request.
        queue.add(request);
    }


    private void getCsrf(String email, String password, SharedPreferences.Editor editor) {
        String url = "http://192.168.1.65:8000/csrf";
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        Request request = new Request(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                binding.responseTV.setText(response);
                editor.putString("CSRF",response);
                editor.commit();
                postLogin(email, password, response, editor);
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
                binding.responseTV.setText(""+error.networkResponse);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

            @Override
            protected Response parseNetworkResponse(NetworkResponse response) {
                try {
                    String json = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(gson.fromJson(json, clazz),
                            HttpHeaderParser.parseCacheHeaders(response));
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Accept", "*/*");
                params.put("Host", "Mindle/1.0");
                params.put("User-Agent", "Mindle/1.0");
                params.put("Content-Type", "application/json");
                params.put("email", email);
                params.put("password", password);

                return params;
            }
        };
        queue.add(request);
    }

}//dimas9.00@mail.ru 123321