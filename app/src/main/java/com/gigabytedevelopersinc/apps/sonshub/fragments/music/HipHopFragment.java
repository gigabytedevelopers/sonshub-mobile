package com.gigabytedevelopersinc.apps.sonshub.fragments.music;


import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.gigabytedevelopersinc.apps.sonshub.adapters.MainListAdapter;
import com.gigabytedevelopersinc.apps.sonshub.fragments.HomeFragment;
import com.gigabytedevelopersinc.apps.sonshub.models.MainListModel;
import com.gigabytedevelopersinc.apps.sonshub.utils.TinyDb;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

import static com.gigabytedevelopersinc.apps.sonshub.fragments.HomeFragment.checkVolleyErrors;

/**
 * A simple {@link Fragment} subclass.
 */
public class HipHopFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<MainListModel> list;
    private MainListAdapter adapter;
    private TinyDb tinyDb;
    private LinearLayoutManager manager;
    private ProgressBar progressBar,progressBarLoading;
    private WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;
    int pageNum;

    public HipHopFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_african, container, false);
        recyclerView = view.findViewById(R.id.african_list);
        list = new ArrayList<>();
        tinyDb = new TinyDb(getActivity());
        manager = new LinearLayoutManager(getActivity());
        progressBar = view.findViewById(R.id.progressBar);
        progressBarLoading = view.findViewById(R.id.progress_bar_loading);
       HomeFragment.hideStreamLayout(recyclerView);
        progressBarLoading.setVisibility(View.VISIBLE);
        adapter = new MainListAdapter(getActivity(), list, (view1, position) -> {
        });
        mWaveSwipeRefreshLayout = view.findViewById(R.id.main_swipe);
        mWaveSwipeRefreshLayout.setWaveColor(getResources().getColor(R.color.colorPrimary));
        mWaveSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        mWaveSwipeRefreshLayout.setOnRefreshListener(this::getHipHopList);
        pageNum = 2;
        getHipHopList();

        return view;
    }

    //Method to get the first 10 items from the sonshub api
    private void getHipHopList() {
        String AFRICAN_URL = "https://sonshub.co/wp-json/wp/v2/posts?categories=2208&per_page=10&page=1";
        JsonArrayRequest africanRequest = new JsonArrayRequest(AFRICAN_URL, response -> {
            mWaveSwipeRefreshLayout.setRefreshing(false);
            System.out.println("Album response: "+response);
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
                    updateHipHopList(movieImage,title,link,description,time,content);
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
            checkVolleyErrors(getContext(), error);
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

    private void updateHipHopList(String imageUrl, String title, String link,String description, String time,String content) {
        MainListModel mainListModel = new MainListModel(imageUrl,title,link,description,time,content);
        list.add(mainListModel);
        adapter.notifyDataSetChanged();

        adapter = new MainListAdapter(getActivity(), list, (view, position) -> {
            tinyDb.putString("clicked", "music");
            tinyDb.putString("musicDetailsList", getDetails(list,position));

            MainActivity mainActivity = new MainActivity();
            mainActivity.fillBottomSheet(getContext());
        });

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        adapter.setOnBottomReachedListener(position -> {
            String AFRICAN_URL = "https://sonshub.co/wp-json/wp/v2/posts?categories=2208&per_page=10&page=" + pageNum;
            pageNum = pageNum + 1;
            progressBar.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> loadMoreHipHopList(AFRICAN_URL), 5000);


        });

    }

    //Method to load more to the list
    private void loadMoreHipHopList(String AFRICAN_URL) {
        try {
            JsonArrayRequest africanRequest = new JsonArrayRequest(AFRICAN_URL, response -> {
                System.out.println(response);
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        String title = obj.getJSONObject("title").getString("rendered");
                        String description = obj.getJSONObject("excerpt").getString("rendered");
                        String time = obj.getString("date");
                        String content = obj.getJSONObject("content").getString("rendered");
                        String link = obj.getString("link");
                        String movieImage = obj.getString("jetpack_featured_media_url");
                        updateloadMoreHipHopList(movieImage,title,link,description,time,content);
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
                    checkVolleyErrors(getContext(), error);
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

    private void updateloadMoreHipHopList(String imageUrl, String title,String link ,String description, String time,String content) {
        MainListModel mainListModel = new MainListModel(imageUrl,title,link,description,time,content);
        list.add(mainListModel);
        adapter.notifyDataSetChanged();
    }

    private String getDetails(List<MainListModel> mainList, int position) {
        List<MainListModel> mainListModels = new ArrayList<>();
        mainListModels.add(mainList.get(position));

        Gson gson = new Gson();
        return gson.toJson(mainListModels);
    }
}
