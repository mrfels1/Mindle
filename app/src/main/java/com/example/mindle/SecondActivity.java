package com.example.mindle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mindle.API.Post;
import com.example.mindle.databinding.ActivitySecondBinding;
import com.example.mindle.models.ListAdapter;
import com.example.mindle.API.postParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SecondActivity extends AppCompatActivity {
    private static final String TAG = "MyApp";
    ActivitySecondBinding binding;
    SharedPreferences sharedPref;
    ListAdapter listAdapter;
    ArrayList<Post> postArrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySecondBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPref = getApplicationContext().getSharedPreferences("My_Preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Log.d(TAG, "Activity created");
        ///     GET POSTS      ///
        String url = "http://192.168.1.64:80/api/posts";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(ctxt, response, Toast.LENGTH_LONG).show();
                List<Post> posts = new ArrayList<Post>();
                try {
                    JSONArray arr = new JSONArray(response);
                    for (int i=0; i < arr.length(); i++) {
                        JSONObject jsonpost = arr.getJSONObject(i);
                        int likes;
                        if(jsonpost.has("likes")&&!jsonpost.isNull("likes")){
                            likes = jsonpost.getInt("likes");
                        }
                        else {
                            likes = 0;
                        }

                        int dislikes;
                        if(jsonpost.has("dislikes")&&!jsonpost.isNull("dislikes")){
                            dislikes = jsonpost.getInt("dislikes");
                        }
                        else {
                            dislikes = 0;
                        }

                        int comments;
                        if(jsonpost.has("comments")&&!jsonpost.isNull("comments")){
                            comments = jsonpost.getInt("comments");
                        }
                        else {
                            comments = 0;
                        }
                        posts.add(new Post(
                                jsonpost.getInt("id"),
                                jsonpost.getString("title"),
                                jsonpost.getInt("user_id"),
                                jsonpost.getString("text_content"),
                                likes,
                                dislikes,
                                comments
                                ));
                    }
                } catch (Throwable t) {
                    Log.e("API", t.toString());
                }

                //after parsing json
                Collections.sort(posts, new Comparator<Post>() {
                    @Override
                    public int compare(Post o1, Post o2) {
                        return o2.id - o1.id;
                    }
                });
                Post[] postarr = posts.toArray(new Post[0]);
                Log.d(TAG, "onResponse: " + postarr.toString());
                postArrayList.addAll(Arrays.asList(postarr));
                listAdapter = new ListAdapter(SecondActivity.this, postArrayList);
                binding.postList.setAdapter(listAdapter);
                binding.postList.setClickable(true);

                binding.postList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(SecondActivity.this, DetailedActivity.class);
                        //intent.putExtra("parameters", parameters[i]);
                        //intent.putExtra("text", text[i]);
                        //intent.putExtra("image", image1[i]);
                        startActivity(intent);
                    }
                });


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(ctxt, error.toString(), Toast.LENGTH_LONG).show();


            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                200000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(request);
        ////

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_second, menu);
        MenuItem search = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setQueryHint("Поиск поста");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }
    private static final int CONTENT_VIEW_ID = 10101010;
    author frag1;
    programm frag2;
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Switching on the item id of the menu item
        if(item.getItemId() == R.id.update) {
            updatePosts();
            return true;
        }
        if(item.getItemId() == R.id.author){
            Toast.makeText(this, "authormenu", Toast.LENGTH_SHORT).show();
            author author_fragment = new author();
            FragmentManager fm =  getSupportFragmentManager();

            fm.beginTransaction().replace(R.id.fragmentContainerView,author_fragment).commit();
            binding.closefragbtn.setVisibility(View.VISIBLE);
            binding.closefragbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.closefragbtn.setVisibility(View.GONE);
                    fm.beginTransaction().remove(author_fragment).commit();
                }
            });
            return true;
        }
        if(item.getItemId() == R.id.programm){
            Toast.makeText(this, "programm", Toast.LENGTH_SHORT).show();
            programm programm_fragment = new programm();
            FragmentManager fm =  getSupportFragmentManager();

            fm.beginTransaction().replace(R.id.fragmentContainerView,programm_fragment).commit();
            binding.closefragbtn.setVisibility(View.VISIBLE);
            binding.closefragbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.closefragbtn.setVisibility(View.GONE);
                    fm.beginTransaction().remove(programm_fragment).commit();
                }
            });
            return true;
        }
        return false;
    }


    public void updatePosts(){
        postArrayList.clear();
        binding = ActivitySecondBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPref = getApplicationContext().getSharedPreferences("My_Preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Log.d(TAG, "updatePosts");
        ///     GET POSTS      ///
        String url = "http://192.168.1.64:80/api/posts";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(ctxt, response, Toast.LENGTH_LONG).show();
                List<Post> posts = new ArrayList<Post>();
                try {
                    JSONArray arr = new JSONArray(response);
                    for (int i=0; i < arr.length(); i++) {
                        JSONObject jsonpost = arr.getJSONObject(i);
                        int likes;
                        if(jsonpost.has("likes")&&!jsonpost.isNull("likes")){
                            likes = jsonpost.getInt("likes");
                        }
                        else {
                            likes = 0;
                        }

                        int dislikes;
                        if(jsonpost.has("dislikes")&&!jsonpost.isNull("dislikes")){
                            dislikes = jsonpost.getInt("dislikes");
                        }
                        else {
                            dislikes = 0;
                        }

                        int comments;
                        if(jsonpost.has("comments")&&!jsonpost.isNull("comments")){
                            comments = jsonpost.getInt("comments");
                        }
                        else {
                            comments = 0;
                        }
                        posts.add(new Post(
                                jsonpost.getInt("id"),
                                jsonpost.getString("title"),
                                jsonpost.getInt("user_id"),
                                jsonpost.getString("text_content"),
                                likes,
                                dislikes,
                                comments
                        ));
                    }
                } catch (Throwable t) {
                    Log.e("API", t.toString());
                }

                //after parsing json
                Collections.sort(posts, new Comparator<Post>() {
                    @Override
                    public int compare(Post o1, Post o2) {
                        return o2.id - o1.id;
                    }
                });
                Post[] postarr = posts.toArray(new Post[0]);
                Log.d(TAG, "onResponse: " + postarr.toString());
                postArrayList.addAll(Arrays.asList(postarr));
                listAdapter = new ListAdapter(SecondActivity.this, postArrayList);
                binding.postList.setAdapter(listAdapter);
                binding.postList.setClickable(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(ctxt, error.toString(), Toast.LENGTH_LONG).show();


            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                200000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(request);
        ////
    }
}