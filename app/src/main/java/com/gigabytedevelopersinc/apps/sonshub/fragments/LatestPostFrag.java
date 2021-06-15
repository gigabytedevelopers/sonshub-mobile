package com.gigabytedevelopersinc.apps.sonshub.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.gigabytedevelopersinc.apps.sonshub.R;
import com.gigabytedevelopersinc.apps.sonshub.activities.MainActivity;
import com.gigabytedevelopersinc.apps.sonshub.adapters.LatestPostsAdapter;
import com.gigabytedevelopersinc.apps.sonshub.models.MainListModel;
import com.gigabytedevelopersinc.apps.sonshub.utils.TinyDb;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class LatestPostFrag extends Fragment {
    private GridView gridView;
    private List<MainListModel> list;
    private LatestPostsAdapter adapter;
    private TinyDb tinyDb;
    private ProgressBar progressBar,progressBarLoading;
    int pageNum;
    int myLastVisiblePos;

    public LatestPostFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_latest_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gridView = view.findViewById(R.id.latest_posts_list);
        list = new ArrayList<>();
        progressBar = view.findViewById(R.id.progressBar);
        progressBarLoading = view.findViewById(R.id.progress_bar_loading);
        myLastVisiblePos = gridView.getFirstVisiblePosition();
        adapter = new LatestPostsAdapter(getActivity(), list, (view1, position) -> {
        });
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
//                int currentFirstVisPos = absListView.getFirstVisiblePosition();
//                if(currentFirstVisPos > myLastVisiblePos) {
//                    //scroll down
//                    MusicPlayerActivity.streamLayout.setVisibility(View.GONE);
//                }
//                if(currentFirstVisPos < myLastVisiblePos) {
//                    //scroll up
//                    MusicPlayerActivity.streamLayout.setVisibility(View.VISIBLE);
//                }
//                myLastVisiblePos = currentFirstVisPos;
            }
        });
        progressBarLoading.setVisibility(View.VISIBLE);
        tinyDb = new TinyDb(getContext());
        pageNum = 2;
        getLatestPostList();

    }


    //Method to get the first 10 items from the sonshub api
    private void getLatestPostList() {
        String LATEST_POST_URL = "https://sonshub.co/wp-json/wp/v2/posts?per_page=10&page=1";
        JsonArrayRequest africanRequest = new JsonArrayRequest(LATEST_POST_URL, response -> {
            list.clear();
            System.out.println("African response: "+response);
            progressBarLoading.setVisibility(View.GONE);
            try {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject obj = response.getJSONObject(i);
                    String title = obj.getJSONObject("title").getString("rendered");
                    String description = obj.getJSONObject("excerpt").getString("rendered");
                    String time = obj.getString("date");
                    String content = obj.getJSONObject("content").getString("rendered");
                    String link = obj.getString("link");
                    String movieImage = obj.getString("jetpack_featured_media_url");
                    updateLatestList(movieImage,title,link,description,time,content);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            try {
                progressBarLoading.setVisibility(View.GONE);
            } catch (Exception npe) {
                npe.printStackTrace();
            }
            Snackbar.make(requireActivity()
                            .findViewById(android.R.id.content),
                    "error connecting to server",
                    Snackbar.LENGTH_SHORT).show();
            error.printStackTrace();
        }) {
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                try {
                    Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
                    if (cacheEntry == null) {
                        cacheEntry = new Cache.Entry();
                    }
                    final long cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
                    final long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this cache entry expires completely
                    long now = System.currentTimeMillis();
                    final long softExpire = now + cacheHitButRefreshed;
                    final long ttl = now + cacheExpired;
                    cacheEntry.data = response.data;
                    cacheEntry.softTtl = softExpire;
                    cacheEntry.ttl = ttl;
                    String headerValue;
                    headerValue = response.headers.get("Date");
                    if (headerValue != null) {
                        cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    headerValue = response.headers.get("Last-Modified");
                    if (headerValue != null) {
                        cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    cacheEntry.responseHeaders = response.headers;
                    final String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(new JSONArray(jsonString), cacheEntry);
                } catch (UnsupportedEncodingException | JSONException e) {
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            protected void deliverResponse(JSONArray response) {
                super.deliverResponse(response);
            }

            @Override
            public void deliverError(VolleyError error) {
                super.deliverError(error);
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                return super.parseNetworkError(volleyError);
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
        requestQueue.add(africanRequest);

        africanRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) {

            }
        });
    }

    private void updateLatestList(String imageUrl, String title, String link,String description, String time,String content) {
        MainListModel mainListModel = new MainListModel(imageUrl,title,link,description,time,content);
        list.add(mainListModel);
        adapter.notifyDataSetChanged();

        adapter = new LatestPostsAdapter(getActivity(), list, (view, position) -> {
            tinyDb.putString("clicked", "music");
            tinyDb.putString("musicDetailsList", getDetails(list,position));
            System.out.println("LatestPostsList "+ tinyDb.getString("musicDetailsList"));

            MainActivity mainActivity = new MainActivity();
            mainActivity.fillBottomSheet(getContext());
        });


        gridView.setAdapter(adapter);

        adapter.setOnBottomReachedListener(position -> {
            String LATEST_URL = "https://sonshub.co/wp-json/wp/v2/posts?per_page=10&page=" + pageNum;
            pageNum = pageNum + 1;
            progressBar.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> loadMoreLatestList(LATEST_URL), 5000);


        });

    }

    private String getDetails(List<MainListModel> mainList, int position) {
        List<MainListModel> mainListModels = new ArrayList<>();
        mainListModels.add(mainList.get(position));

        Gson gson = new Gson();
        return gson.toJson(mainListModels);
    }

    //Method to load more to the list
    private void loadMoreLatestList(String LATEST_URL) {
        try {
            JsonArrayRequest africanRequest = new JsonArrayRequest(LATEST_URL, response -> {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        String title = obj.getJSONObject("title").getString("rendered");
                        String description = obj.getJSONObject("excerpt").getString("rendered");
                        String time = obj.getString("date");
                        String content = obj.getJSONObject("content").getString("rendered");
                        String link = obj.getString("link");
                        String movieImage = obj.getString("jetpack_featured_media_url");
                        updateloadMoreLatestList(movieImage,title,link,description,time,content);
                    }

                    progressBar.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        System.out.print("Error Json " + res);
                        JSONObject obj = new JSONObject(res);
                        int status = obj.getJSONObject("data").getInt("status");

                        if (status == 400) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Page End", Toast.LENGTH_LONG).show();
                        }
                    } catch (UnsupportedEncodingException | JSONException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    } // returned data is not JSONObject?

                }else {
                    System.out.println("Load More Error" + error);
                    Toast.makeText(requireActivity().getApplicationContext(), "Error connecting to server", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
            }) {

            };

            RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
            requestQueue.add(africanRequest);

            africanRequest.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 50000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 50000;
                }

                @Override
                public void retry(VolleyError error) {

                }
            });
        } catch (NullPointerException ignored) {
            Toast.makeText(getContext(), "Unknown Error Occurred while trying to load results", Toast.LENGTH_SHORT).show();

        }

    }

    private void updateloadMoreLatestList(String imageUrl, String title,String link ,String description, String time,String content) {
        MainListModel mainListModel = new MainListModel(imageUrl,title,link,description,time,content);
        list.add(mainListModel);
        adapter.notifyDataSetChanged();
    }
}
