package com.gigabytedevelopersinc.apps.sonshub.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.gigabytedevelopersinc.apps.sonshub.R;
import com.gigabytedevelopersinc.apps.sonshub.activities.MainActivity;
import com.gigabytedevelopersinc.apps.sonshub.adapters.MovieAdapter;
import com.gigabytedevelopersinc.apps.sonshub.adapters.MusicAdapter;
import com.gigabytedevelopersinc.apps.sonshub.adapters.NewsAdapter;
import com.gigabytedevelopersinc.apps.sonshub.fragments.music.MusicFragment;
import com.gigabytedevelopersinc.apps.sonshub.fragments.videos.MoviesFragment;
import com.gigabytedevelopersinc.apps.sonshub.models.MovieModel;
import com.gigabytedevelopersinc.apps.sonshub.models.MusicModel;
import com.gigabytedevelopersinc.apps.sonshub.models.NewsModel;
import com.gigabytedevelopersinc.apps.sonshub.utils.TinyDb;
import com.glide.slider.library.Animations.DescriptionAnimation;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.SliderTypes.DefaultSliderView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private RecyclerView moviesRecyclerView, musicRecyclerView, gistRecyclerView;
    private List<MovieModel> movieList;
    private List<MusicModel> musicList, featuredList;
    private List<NewsModel> gistList;
    private NewsAdapter newsAdapter;
    private MovieAdapter adapter;
    private MusicAdapter musicAdapter;
    private String MOVIE_HOME_URL="https://sonshub.com/wp-json/wp/v2/posts?categories=586&per_page=10";
    private String MUSIC_HOME_URL ="https://sonshub.com/wp-json/wp/v2/posts?categories=2&per_page=10";
    private String ARTICLE_HOME_URL ="https://sonshub.com/wp-json/wp/v2/posts?per_page=10";
    private String FEATURED_AREA_URL = "https://sonshub.com/wp-json/wp/v2/posts?categories=19824&per_page=10";
    private SliderLayout mDemoSlider;
    private ProgressBar moviesProgress, musicProgress, gistProgress;
    private TinyDb tinyDb;
    private WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;

    public HomeFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        RelativeLayout movieView = view.findViewById(R.id.movies_view);
        RelativeLayout musicView = view.findViewById(R.id.music_view);
        RelativeLayout gistView = view.findViewById(R.id.gist_view);
        moviesRecyclerView = view.findViewById(R.id.movies_list);
        musicRecyclerView = view.findViewById(R.id.music_list);
        gistRecyclerView = view.findViewById(R.id.gist_list);
        movieList = new ArrayList<>();
        musicList = new ArrayList<>();
        featuredList = new ArrayList<>();
        gistList = new ArrayList<>();
        mDemoSlider = view.findViewById(R.id.slider_layout);
        moviesProgress = view.findViewById(R.id.movies_progress);
        musicProgress = view.findViewById(R.id.music_progress);
        gistProgress = view.findViewById(R.id.gist_progress);
        tinyDb = new TinyDb(getContext());
        tinyDb.putString("whichFrag","Home");
        NestedScrollView scrollView = view.findViewById(R.id.nestedScroll);
        ArrayList<String> listUrl = new ArrayList<>();

        mWaveSwipeRefreshLayout = view.findViewById(R.id.main_swipe);
        mWaveSwipeRefreshLayout.setWaveColor(getResources().getColor(R.color.colorPrimary));
        mWaveSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        mWaveSwipeRefreshLayout.setOnRefreshListener(() -> {
            getMoviePicAndTitle();
            getMusicPicAndTitle();
            getGistList();

        });

        adapter = new MovieAdapter(getActivity(), movieList, (view12, position) -> {
        });
        newsAdapter = new NewsAdapter(getContext(), gistList, (view13, position) -> {

        });
        musicAdapter = new MusicAdapter(getContext(), musicList, (view14, position) -> {

        });

        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > oldScrollY) {
                MainActivity.miniPlayerCollapse();
                MainActivity.streamLayout.setVisibility(View.GONE);
            } else if (scrollY < oldScrollY) {
                MainActivity.streamLayout.setVisibility(View.VISIBLE);
            }
        });

        moviesProgress.setVisibility(View.VISIBLE);
        musicProgress.setVisibility(View.VISIBLE);
        gistProgress.setVisibility(View.VISIBLE);

            getMoviePicAndTitle();
            getMusicPicAndTitle();
            getGistList();
            getFeaturedImages(listUrl);

        musicView.setOnClickListener(view1 ->{
            MusicFragment musicFragment = new MusicFragment();
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.parent_frame, musicFragment);
            fragmentTransaction.commit();
            fragmentTransaction.addToBackStack(null);
        });

        movieView.setOnClickListener(view1 ->{
            MoviesFragment moviesFragment = new MoviesFragment();
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.parent_frame, moviesFragment);
            fragmentTransaction.commit();
            fragmentTransaction.addToBackStack(null);
        });

        gistView.setOnClickListener(view1 ->{
            LatestPostFrag gistFragment = new LatestPostFrag();
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.parent_frame, gistFragment);
            MainActivity.toolbar.setTitle("Latest Posts");
            fragmentTransaction.commit();
            fragmentTransaction.addToBackStack(null);
        });

        return view;
    }

    public static void hideStreamLayout(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    //Scrolling Down

                    MainActivity.streamLayout.setVisibility(View.GONE);
                } else {
                    //Scrolling Up

                    MainActivity.streamLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public static void checkVolleyErrors(Context context, VolleyError error){
        try {
            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                if (error.getMessage() == null
                        || error.getMessage().equals("")
                        || Objects.requireNonNull(error.getCause()).getMessage() == null
                        || Objects.requireNonNull(error.getCause().getMessage()).equals("")) {
                    Toast.makeText(context, "Sorry, There was a Timeout Error", Toast.LENGTH_LONG).show();
                } else {
                    Crashlytics.log(0, "IGNORE: ", error.getMessage());
                }
            } else if (error instanceof AuthFailureError) {
                if (error.getMessage() == null
                        || error.getMessage().equals("")
                        || Objects.requireNonNull(error.getCause()).getMessage() == null
                        || Objects.requireNonNull(error.getCause().getMessage()).equals("")) {
                    Toast.makeText(context, "Ooops... An Authentication Error Occurred", Toast.LENGTH_LONG).show();
                } else {
                    Crashlytics.log(0, "IGNORE: ", error.getMessage());
                }
            } else if (error instanceof ServerError || error.getCause() instanceof ServerError) {
                if (error.getMessage() == null
                        || error.getMessage().equals("")
                        || Objects.requireNonNull(error.getCause()).getMessage() == null
                        || Objects.requireNonNull(error.getCause().getMessage()).equals("")) {
                    Toast.makeText(context, "Ooops... A Server Error Occurred", Toast.LENGTH_LONG).show();
                } else {
                    Crashlytics.log(0, "IGNORE: ", error.getMessage());
                }
            } else if (error instanceof NetworkError) {
                if (error.getMessage() == null
                        || error.getMessage().equals("")
                        || Objects.requireNonNull(error.getCause()).getMessage() == null
                        || Objects.requireNonNull(error.getCause().getMessage()).equals("")) {
                    Toast.makeText(context, "Error Connecting to Server. Check your Network", Toast.LENGTH_LONG).show();
                } else {
                    Crashlytics.log(0, "IGNORE: ", error.getMessage());
                }
            } else if (error instanceof ParseError) {
                if (error.getMessage() == null
                        || error.getMessage().equals("")
                        || Objects.requireNonNull(error.getCause()).getMessage() == null
                        || Objects.requireNonNull(error.getCause().getMessage()).equals("")) {
                    Toast.makeText(context, "Ooops... an Unknown Error Occurred", Toast.LENGTH_LONG).show();
                } else {
                    Crashlytics.log(0, "IGNORE: ", error.getMessage());
                    //Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e){
            if (e.getCause() != null && e.getCause() instanceof UnknownHostException) {
                Crashlytics.log(0, "IGNORE: ", error.getMessage());
            }
            e.printStackTrace();
        }
    }

    private void getFeaturedImages(List<String> list){
        JsonArrayRequest featuredRequest = new JsonArrayRequest(FEATURED_AREA_URL, response -> {
            System.out.println("Featured Image " + response);
            try {
                for (int i = 0; i < response.length(); i++){
                    JSONObject obj = response.getJSONObject(i);
                    String featuredTitle = obj.getJSONObject("title").getString("rendered");
                    String newFeaturedTitle = featuredTitle.trim().replace("DOWNLOAD MUSIC:", "");
                    String mainMovieTitle = newFeaturedTitle.trim().replace("&#8217;", "'");
                    String content = obj.getJSONObject("content").getString("rendered");
                    String featuredImage = obj.getString("jetpack_featured_media_url");
                    String link = obj.getString("link");
                    list.add(featuredImage);
                    updateFeaturedList(featuredImage,mainMovieTitle,content,link);

                }

                for (int j = 0; j < list.size(); j++) {
                    DefaultSliderView sliderView = new DefaultSliderView(getContext());
                    // if you want show image only / without description text use DefaultSliderView instead

                    // initialize SliderLayout
                    sliderView
                            .image(list.get(j))
                            .setProgressBarVisible(true)
                            .setOnSliderClickListener(baseSliderView -> {
                                tinyDb.putString("clicked", "featuredimages");
                                tinyDb.putString("featuredimageslist", getDetailsForMusic(featuredList,mDemoSlider.getCurrentPosition()));
                                MainActivity mainActivity = new MainActivity();
                                mainActivity.fillBottomSheet(getContext());
                            });

                    //add your extra information
                    sliderView.bundle(new Bundle());

                    mDemoSlider.addSlider(sliderView);
                }

                // set Slider Transition Animation
                // mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
                mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
                mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                mDemoSlider.setCustomAnimation(new DescriptionAnimation());
                mDemoSlider.setDuration(4000);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> checkVolleyErrors(getContext(), error));

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(featuredRequest);
        featuredRequest.setRetryPolicy(new RetryPolicy() {
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

    private void getMoviePicAndTitle(){
        JsonArrayRequest movieRequest = new JsonArrayRequest(MOVIE_HOME_URL, response -> {
            movieList.clear();
            moviesProgress.setVisibility(View.GONE);
            System.out.println(response);
            try {
                for (int i = 0; i < response.length(); i++){
                    JSONObject obj = response.getJSONObject(i);
                    String movieTitle = obj.getJSONObject("title").getString("rendered");
                    String newMovieTitle = movieTitle.trim().replace("DOWNLOAD MOVIE:", "");
                    String mainMovieTitle = newMovieTitle.trim().replace("&#8217;", "'");
                    String content = obj.getJSONObject("content").getString("rendered");
                    String movieImage = obj.getString("jetpack_featured_media_url");
                    String link = obj.getString("link");
                    updateMovieList(movieImage,mainMovieTitle, content,link);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            try {
                moviesProgress.setVisibility(View.GONE);
            } catch (Exception npe) {
                npe.printStackTrace();
            }

            System.out.println("Home Movie list caused crash with error " + error);
            checkVolleyErrors(getContext(), error);
       }){
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

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(movieRequest);
        movieRequest.setRetryPolicy(new RetryPolicy() {
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

    private void getMusicPicAndTitle(){
        JsonArrayRequest musicRequest = new JsonArrayRequest(MUSIC_HOME_URL, response -> {
            musicList.clear();
            mWaveSwipeRefreshLayout.setRefreshing(false);
            musicProgress.setVisibility(View.GONE);
            try {
                for (int i = 0; i < response.length(); i++){
                    JSONObject obj = response.getJSONObject(i);
                    String musicTitle = obj.getJSONObject("title").getString("rendered");
                    String newMusicTitle = musicTitle.trim().replace("MUSIC:", "");
                    String mainMusicTitle = newMusicTitle.trim().replace("&#8211;", "'");
                    String content = obj.getJSONObject("content").getString("rendered");
                    String link = obj.getString("link");
                    String movieImage = obj.getString("jetpack_featured_media_url");
                    updateMusicList(movieImage,mainMusicTitle.trim().replace("&#8220:",""), content,link);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            try {
                musicProgress.setVisibility(View.GONE);
            } catch (Exception npe) {
                npe.printStackTrace();
            }

            System.out.println("Home Music list caused crash with error " + error);
            checkVolleyErrors(getContext(), error);
            error.printStackTrace();
        }){
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

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(musicRequest);
        musicRequest.setRetryPolicy(new RetryPolicy() {
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

    private void getGistList(){
        JsonArrayRequest gistRequest = new JsonArrayRequest(ARTICLE_HOME_URL, response -> {
            gistList.clear();
            gistProgress.setVisibility(View.GONE);
            System.out.println("Gist response "+response);
            try {
                for (int i = 0; i < response.length(); i++){
                    JSONObject obj = response.getJSONObject(i);
                    String gistTitle = obj.getJSONObject("title").getString("rendered");
                    String gistDescription = obj.getJSONObject("excerpt").getString("rendered");
                    String newGistDescription = gistDescription.replace("<p>", "");
                    String newsTime = obj.getString("date");
                    String content = obj.getJSONObject("content").getString("rendered");
                    String newsImage = obj.getString("jetpack_featured_media_url");
                    String link = obj.getString("link");
                    updateGistList(gistTitle,newGistDescription,newsTime,newsImage, content,link);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            try {
                gistProgress.setVisibility(View.GONE);
            } catch (Exception npe) {
                npe.printStackTrace();
            }

            System.out.println("Home Gist list caused crash with error " + error);
            checkVolleyErrors(getContext(), error);
            error.printStackTrace();
        }){
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

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(gistRequest);

        gistRequest.setRetryPolicy(new RetryPolicy() {
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

    private void updateMovieList(String movieImage, String movieTitle, String content,String link){
        MovieModel movieModel = new MovieModel(movieImage,movieTitle, content,link);
        movieList.add(movieModel);
        adapter.notifyDataSetChanged();

        adapter = new MovieAdapter(getActivity(), movieList, (view, position) -> {
            tinyDb.putString("clicked", "movies");
            tinyDb.putString("movieDetailsList", getDetailsForMovies(movieList,position));

            MainActivity mainActivity = new MainActivity();
            mainActivity.fillBottomSheet(getContext());
        });

        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        moviesRecyclerView.setLayoutManager(llm);
        moviesRecyclerView.setAdapter(adapter);
    }

    private void updateMusicList(String musicImage, String musicTitle, String content,String link){
        MusicModel musicModel = new MusicModel(musicImage, musicTitle, content,link);
        musicList.add(musicModel);
        musicAdapter.notifyDataSetChanged();

        musicAdapter = new MusicAdapter(getActivity(), musicList, (view, position) -> {
            tinyDb.putString("clicked", "music");
            tinyDb.putString("musicDetailsList", getDetailsForMusic(musicList,position));

            MainActivity mainActivity = new MainActivity();
            mainActivity.fillBottomSheet(getContext());

        });

        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        musicRecyclerView.setLayoutManager(llm);
        musicRecyclerView.setAdapter(musicAdapter);
    }

    private void updateGistList(String newsTitle, String newsDescription, String newsTime, String newsImage, String content,String link){
        NewsModel newsModel = new NewsModel(newsTitle,newsDescription,newsTime,newsImage, content,link);
        gistList.add(newsModel);
        newsAdapter.notifyDataSetChanged();

        newsAdapter = new NewsAdapter(getActivity(), gistList, (view, position) -> {
            tinyDb.putString("clicked", "featured");
            tinyDb.putString("featuredDetailsList",getDetailsForGist(gistList, position));

            MainActivity mainActivity = new MainActivity();
            mainActivity.fillBottomSheet(getContext());
        });

        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL,false);
        gistRecyclerView.setLayoutManager(llm);
        gistRecyclerView.setAdapter(newsAdapter);
    }

    private void updateFeaturedList(String musicImage, String musicTitle, String content,String link){
        MusicModel musicModel = new MusicModel(musicImage, musicTitle, content,link);
        featuredList.add(musicModel);
    }

    private String getDetailsForMovies(List<MovieModel> movieList, int position){
        List<MovieModel> movieModels = new ArrayList<>();
        movieModels.add(movieList.get(position));

        Gson gson = new Gson();
        return gson.toJson(movieModels);
    }

    private String getDetailsForMusic(List<MusicModel> musicList, int position){
        List<MusicModel> musicModels = new ArrayList<>();
        musicModels.add(musicList.get(position));

        Gson gson = new Gson();
        return gson.toJson(musicModels);
    }

    private String getDetailsForGist(List<NewsModel> newsList, int position){
        List<NewsModel> newsModels = new ArrayList<>();
        newsModels.add(newsList.get(position));

        Gson gson = new Gson();
        return gson.toJson(newsModels);
    }

    @Override
    public void onStop() {
        super.onStop();
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

}
