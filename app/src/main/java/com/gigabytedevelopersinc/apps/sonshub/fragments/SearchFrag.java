package com.gigabytedevelopersinc.apps.sonshub.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.gigabytedevelopersinc.apps.sonshub.App;
import com.gigabytedevelopersinc.apps.sonshub.R;
import com.gigabytedevelopersinc.apps.sonshub.activities.MainActivity;
import com.gigabytedevelopersinc.apps.sonshub.adapters.MainListAdapter;
import com.gigabytedevelopersinc.apps.sonshub.models.MainListModel;
import com.gigabytedevelopersinc.apps.sonshub.utils.TinyDb;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.gigabytedevelopersinc.apps.sonshub.activities.MainActivity.searchQuery;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFrag extends Fragment {
    public List<MainListModel> searchList;
    public MainListAdapter searchAdapter;
    public ProgressBar searchProgress, bufferingProgress;
    public RecyclerView searchRecyclerView;
    public String SEARCH_URL = "https://sonshub.com/wp-json/wp/v2/posts?search=";
    public Context context;
    public int pageNum;
    public TinyDb tinyDb;
    private String webSearch;
    private static Context appContext = App.Companion.getContext();

    public SearchFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchList = new ArrayList<>();
        searchProgress = view.findViewById(R.id.search_progress);
        searchRecyclerView = view.findViewById(R.id.search_recyclerview);
        bufferingProgress = view.findViewById(R.id.progressBar);
        tinyDb = new TinyDb(appContext);
        HomeFragment.hideStreamLayout(searchRecyclerView);
        searchAdapter = new MainListAdapter(appContext, searchList, (view1, position) -> {
        });
        getSearchResults();
    }

    private void getSearchResults() {
//        String search = searchQuery.replaceAll(" ", "%20");

        try {
            if (searchQuery != null) {
                webSearch = URLEncoder.encode(searchQuery.trim(),"utf-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NullPointerException npe) {
            Toast.makeText(appContext, "Unknown Error try again", Toast.LENGTH_LONG).show();
            Crashlytics.logException(npe);
        }
        JsonArrayRequest searchRequest = new JsonArrayRequest(SEARCH_URL + webSearch+"&per_page=10&page=1", response -> {
            searchProgress.setVisibility(View.GONE);
            try {
                for (int i =0; i < response.length(); i++) {
                    JSONObject obj = response.getJSONObject(i);
                    String title = obj.getJSONObject("title").getString("rendered");
                    String description = obj.getJSONObject("excerpt").getString("rendered");
                    String time = obj.getString("date");
                    String content = obj.getJSONObject("content").getString("rendered");

                    String movieImage = obj.getString("jetpack_featured_media_url");
                    String link = obj.getString("link");

                    updateSearchList(movieImage,title,link,description,time,content,searchQuery);
                }
            } catch (JSONException je) {
                je.printStackTrace();
            }
        }, error -> {
            HomeFragment.checkVolleyErrors(appContext,error);
            error.printStackTrace();
        });

        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(appContext));
        requestQueue.add(searchRequest);
        searchRequest.setRetryPolicy(new RetryPolicy() {
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

    private void updateSearchList(String imageUrl, String title, String link, String description, String time, String content, String searchQuery) {
        MainListModel mainListModel = new MainListModel(imageUrl,title,link ,description,time,content);
        searchList.add(mainListModel);
        searchAdapter.notifyDataSetChanged();

        searchAdapter = new MainListAdapter(appContext, searchList, (view, position) -> {
            tinyDb.putString("clicked", "music");
            tinyDb.putString("musicDetailsList", getDetails(searchList,position));

            if (!requireActivity().isFinishing()) {
                ((MainActivity) requireActivity()).fillBottomSheet(getContext());
            }
        });

        searchAdapter.setOnBottomReachedListener(position -> {
            String SEARCH_URL = "https://sonshub.com/wp-json/wp/v2/posts?search="+ searchQuery.trim()+"&per_page=10&page=" + pageNum;
            pageNum = pageNum + 1;
            bufferingProgress.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> loadMoreSearchList(SEARCH_URL), 5000);
        });

        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL,false);
        searchRecyclerView.setLayoutManager(llm);
        searchRecyclerView.setAdapter(searchAdapter);
    }

    private void loadMoreSearchList(String SEARCH_URL) {
        try {
            JsonArrayRequest africanRequest = new JsonArrayRequest(SEARCH_URL, response -> {
                System.out.println(response);
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        String title = obj.getJSONObject("title").getString("rendered");
                        String description = obj.getJSONObject("excerpt").getString("rendered");
                        String time = obj.getString("date");
                        String content = obj.getJSONObject("content").getString("rendered");
                        String newTitle = title.trim().replace("DOWNLOAD MOVIE:", "");
                        String link = obj.getString("link");
                        String movieImage = obj.getString("jetpack_featured_media_url");
                        updateLoadMoreSearchList(movieImage,newTitle,link,description,time,content);
                    }
                    bufferingProgress.setVisibility(View.GONE);
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
                            try {
                                bufferingProgress.setVisibility(View.GONE);
                                Toast.makeText(appContext, "Page End", Toast.LENGTH_LONG).show();
                            } catch (NullPointerException ignored) {
                                // Ignore this Exception
                            }
                        }
                    } catch (UnsupportedEncodingException | JSONException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    } // returned data is not JSONObject?
                } else if ((error.getMessage() == null)
                        || error.getMessage().equals("")
                        || Objects.requireNonNull(error.getCause()).getMessage() == null
                        || Objects.requireNonNull(error.getCause().getMessage()).equals("")) {
                    HomeFragment.checkVolleyErrors(appContext,error);
                    error.printStackTrace();
                } else {
                    HomeFragment.checkVolleyErrors(appContext,error);
                    error.printStackTrace();
                }
            });

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
            Toast.makeText(appContext, "Unknown Error Occurred while trying to load results", Toast.LENGTH_SHORT).show();
        }
    }

    private String getDetails(List<MainListModel> mainList, int position) {
        List<MainListModel> mainListModels = new ArrayList<>();
        mainListModels.add(mainList.get(position));

        Gson gson = new Gson();
        return gson.toJson(mainListModels);
    }

    private void updateLoadMoreSearchList(String imageUrl, String title, String link, String description, String time, String content) {
        MainListModel mainListModel = new MainListModel(imageUrl,title,link ,description,time,content);
        searchList.add(mainListModel);
    }
}