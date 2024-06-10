package com.example.mindle.models;

import static android.content.Context.MODE_PRIVATE;

import static com.google.android.material.color.MaterialColors.getColor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mindle.API.Post;
import com.example.mindle.API.apiCaller;
import com.example.mindle.R;
import com.example.mindle.SecondActivity;
import com.example.mindle.databinding.ActivityMainBinding;
import com.example.mindle.databinding.ListItemBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListAdapter extends ArrayAdapter<Post> implements Filterable {
    public ListAdapter(@NonNull Context context, ArrayList<Post> postArrayList) {
        super(context, R.layout.list_item, postArrayList);
        this.list = postArrayList;
        this.context = context;
        this.ip = context.getResources().getString(R.string.ip);
    }
    String ip;
    boolean isliked;
    boolean isdisliked;
    boolean candelete;
    private final List<Post> list;
    private final Context context;
    private Filter filter;
    View innerview;
    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        Post post = getItem(position);
        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        SharedPreferences sharedPref = getContext().getSharedPreferences("My_Preferences", MODE_PRIVATE);
        String token = sharedPref.getString("token", "no token");
        /// check if post is already liked/disliked by user
        String id = Integer.toString(post.id) ;
        String url = ip+"/api/post/" + id + "/userrate";
        View finalView = view;
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();
                try {
                    JSONObject obj = new JSONObject(response);
                    isliked = obj.getString("liked").equals("true");
                    isdisliked = obj.getString("disliked").equals("true");
                    candelete = obj.getString("canDelete").equals("true");
                    //Toast.makeText(getContext(), post.title.substring(0,5) + candelete, Toast.LENGTH_LONG).show();
                    if(isliked){
                        ImageButton upvoteBtn = finalView.findViewById(R.id.upvoteBtn);
                        upvoteBtn.setBackgroundColor(Color.GREEN);
                    }else {
                        ImageButton upvoteBtn = finalView.findViewById(R.id.upvoteBtn);
                        upvoteBtn.setBackgroundColor(Color.RED);
                    }
                    if (isdisliked){
                        ImageButton downvoteBtn = finalView.findViewById(R.id.downvoteBtn);
                        downvoteBtn.setBackgroundColor(Color.GREEN);
                    }else{
                        ImageButton downvoteBtn = finalView.findViewById(R.id.downvoteBtn);
                        downvoteBtn.setBackgroundColor(Color.RED);
                    }
                    if(candelete){
                        //Toast.makeText(getContext(), "!candelete", Toast.LENGTH_LONG).show();
                        ImageButton deleteBtn = finalView.findViewById(R.id.delbtn);
                        deleteBtn.setVisibility(View.VISIBLE);
                    }else {
                        //Toast.makeText(getContext(), "candelete", Toast.LENGTH_LONG).show();
                    }
                }catch (Throwable t) {
                    Log.e("API", t.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "userrateerror: " + error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                200000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getContext()).add(request);
        ///




        TextView postAuthorTV = view.findViewById(R.id.postAuthorTV);

        ///get author name
        String author_id = Integer.toString(post.author_id) ;
        String author_url = ip+"/api/user/" + author_id;
        StringRequest request2 = new StringRequest(Request.Method.GET, author_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();
                try {
                    JSONObject obj = new JSONObject(response);
                    postAuthorTV.setText(obj.getString("name"));
                }catch (Throwable t) {
                    Log.e("API", t.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "user/id: " + error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        request2.setRetryPolicy(new DefaultRetryPolicy(
                200000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getContext()).add(request2);
        ///
        TextView postTextTV = view.findViewById(R.id.postTextTV);
        postTextTV.setText(post.text_content);

        TextView postTitleTV = view.findViewById(R.id.postTitleTV);
        postTitleTV.setText(post.title);

        TextView upvoteCounterTV = view.findViewById(R.id.upvoteCounterTV);
        upvoteCounterTV.setText(String.valueOf(post.likes - post.dislikes));
        ImageButton upvoteBtn = view.findViewById(R.id.upvoteBtn);
        ImageButton downvoteBtn = view.findViewById(R.id.downvoteBtn);
        ImageButton deleteBtn = view.findViewById(R.id.delbtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ///
                String id = Integer.toString(post.id) ;
                String url = ip+"/api/post/" + id + "/delete";
                StringRequest request = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        deleteBtn.setVisibility(View.GONE);
                        ListAdapter.this.list.remove(position);
                        ListAdapter.this.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Delete error: " + error.toString() + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Authorization", "Bearer " + token);
                        return headers;
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(
                        200000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Volley.newRequestQueue(getContext()).add(request);
                ///

            }
        });
        upvoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = Integer.toString(post.id) ;
                String url = ip+"/api/post/" + id + "/like";
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        ImageButton upvoteBtn = finalView.findViewById(R.id.upvoteBtn);
                        ImageButton downvoteBtn = finalView.findViewById(R.id.downvoteBtn);

                        ColorDrawable upvtclr = (ColorDrawable) upvoteBtn.getBackground();
                        ColorDrawable dwnvtclr = (ColorDrawable) downvoteBtn.getBackground();
                        if (upvtclr.getColor() == Color.GREEN){
                            return;

                        }
                        if(dwnvtclr.getColor() == Color.RED){
                            upvoteCounterTV.setText(String.valueOf(post.likes  - post.dislikes + 1));
                            post.likes += 1;
                            upvoteBtn.setBackgroundColor(Color.GREEN);
                            downvoteBtn.setBackgroundColor(Color.RED);
                            return;
                        }
                        if (dwnvtclr.getColor() == Color.GREEN) {
                            upvoteCounterTV.setText(String.valueOf(post.likes  - post.dislikes + 2));
                            post.likes += 1;
                            post.dislikes -=1;
                            upvoteBtn.setBackgroundColor(Color.GREEN);
                            downvoteBtn.setBackgroundColor(Color.RED);
                            return;
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Upvote error: " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Authorization", "Bearer " + token);
                        return headers;
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(
                        200000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Volley.newRequestQueue(getContext()).add(request);
            }
        });
        downvoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = Integer.toString(post.id) ;
                String url = ip+"/api/post/" + id + "/dislike";
                StringRequest request = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ImageButton upvoteBtn = finalView.findViewById(R.id.upvoteBtn);
                        ImageButton downvoteBtn = finalView.findViewById(R.id.downvoteBtn);

                        ColorDrawable upvtclr = (ColorDrawable) upvoteBtn.getBackground();
                        ColorDrawable dwnvtclr = (ColorDrawable) downvoteBtn.getBackground();
                        if (dwnvtclr.getColor() == Color.GREEN){
                            return;

                        }
                        if(upvtclr.getColor() == Color.RED){
                            upvoteCounterTV.setText(String.valueOf(post.likes  - post.dislikes - 1));
                            post.dislikes += 1;
                            upvoteBtn.setBackgroundColor(Color.RED);
                            downvoteBtn.setBackgroundColor(Color.GREEN);
                            return;
                        }
                        if (upvtclr.getColor() == Color.GREEN) {
                            upvoteCounterTV.setText(String.valueOf(post.likes  - post.dislikes - 2));
                            post.dislikes += 1;
                            post.likes -= 1;
                            upvoteBtn.setBackgroundColor(Color.RED);
                            downvoteBtn.setBackgroundColor(Color.GREEN);
                            return;
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "downvote error: " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Authorization", "Bearer " + token);
                        return headers;
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(
                        200000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Volley.newRequestQueue(getContext()).add(request);
            }
        });
        return view;

    }
    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new AppFilter<Post>(list);
        return filter;
    }
    private class AppFilter<T> extends Filter {

        private ArrayList<T> sourceObjects;

        public AppFilter(List<T> objects) {
            sourceObjects = new ArrayList<T>();
            synchronized (this) {
                sourceObjects.addAll(objects);
            }
        }

        @Override
        protected FilterResults performFiltering(CharSequence chars) {
            String filterSeq = chars.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (filterSeq != null && filterSeq.length() > 0) {
                ArrayList<T> filter = new ArrayList<T>();

                for (T object : sourceObjects) {
                    // the filtering itself:
                    Log.d("fil", "performFiltering: " + ((Post) object).title + " |||| " + filterSeq);
                    if (((Post) object).title.toLowerCase().contains(filterSeq))
                        filter.add(object);
                }
                result.count = filter.size();
                result.values = filter;
            } else {
                // add all objects
                synchronized (this) {
                    result.values = sourceObjects;
                    result.count = sourceObjects.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            // NOTE: this function is *always* called from the UI thread.
            ArrayList<T> filtered = (ArrayList<T>) results.values;
            notifyDataSetChanged();
            clear();
            for (int i = 0, l = filtered.size(); i < l; i++)
                add((Post) filtered.get(i));
            notifyDataSetInvalidated();
        }
    }
}
