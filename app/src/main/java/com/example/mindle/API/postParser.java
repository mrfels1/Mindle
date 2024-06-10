package com.example.mindle.API;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.MyApplication;
import com.example.mindle.MainActivity;
import com.example.mindle.RegisterActivity;

import java.util.HashMap;
import java.util.Map;

public class postParser {
    Post[] postarr;
    Context ctxt;
    public postParser(Context context){
        this.ctxt = context;
    }

    public interface getPostsCallback{
        void onSuccess(String response);
        void onError(String error);
    }

    public Post[] getPosts(){

        String TAG = "API";
        getRequest();

        Log.d(TAG, "getPosts: "+ postarr);
        return postarr;
    }

    private void getRequest(){
        makeRequest(new getPostsCallback() {
            @Override
            public void onSuccess(String response) {
                String TAG = "API";
                Log.d(TAG, "onSuccess: " + response);

                postarr = new Post[]{new Post(0,"Title",1,"Text content",10,12,2),new Post(1,"Title2",1,"Text content2",20,12,0)};

            }

            @Override
            public void onError(String error) {
                Toast.makeText(ctxt, error.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void makeRequest(getPostsCallback callback){
        String url = "http://192.168.1.64:80/api/posts";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(ctxt, response, Toast.LENGTH_LONG).show();

                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(ctxt, error.toString(), Toast.LENGTH_LONG).show();

                callback.onError(error.toString());
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                200000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(ctxt).add(request);
    }
}
