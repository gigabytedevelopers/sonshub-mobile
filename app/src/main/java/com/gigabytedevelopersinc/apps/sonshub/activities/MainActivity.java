package com.gigabytedevelopersinc.apps.sonshub.activities;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.amazon.device.ads.Ad;
import com.amazon.device.ads.AdError;
import com.amazon.device.ads.AdProperties;
import com.amazon.device.ads.DefaultAdListener;
import com.amazon.device.ads.InterstitialAd;
import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gigabytedevelopersinc.apps.sonshub.App;
import com.gigabytedevelopersinc.apps.sonshub.R;
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2.Fetch;
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2.FetchConfiguration;
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2core.Downloader;
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2okhttp.OkHttpDownloader;
import com.gigabytedevelopersinc.apps.sonshub.fragments.AboutFragment;
import com.gigabytedevelopersinc.apps.sonshub.fragments.HomeFragment;
import com.gigabytedevelopersinc.apps.sonshub.fragments.SearchFrag;
import com.gigabytedevelopersinc.apps.sonshub.fragments.downloads.DownloadingFragment;
import com.gigabytedevelopersinc.apps.sonshub.fragments.gist.GistFragment;
import com.gigabytedevelopersinc.apps.sonshub.fragments.music.MusicFragment;
import com.gigabytedevelopersinc.apps.sonshub.fragments.videos.VideosFragment;
import com.gigabytedevelopersinc.apps.sonshub.fragments.wordoffaith.WordOfFaithFragment;
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.activities.MusicMainActivity;
import com.gigabytedevelopersinc.apps.sonshub.services.notification.SonsHubDownloadNotificationManager;
import com.gigabytedevelopersinc.apps.sonshub.ui.ExpandableLayout;
import com.gigabytedevelopersinc.apps.sonshub.utils.NotificationUtil;
import com.gigabytedevelopersinc.apps.sonshub.utils.TinyDb;
import com.gigabytedevelopersinc.apps.sonshub.utils.misc.Configs;
import com.gigabytedevelopersinc.apps.sonshub.utils.misc.Data;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.leinardi.android.speeddial.SpeedDialView;
import com.startapp.sdk.adsbase.StartAppAd;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import saschpe.android.customtabs.CustomTabsHelper;
import saschpe.android.customtabs.WebViewFallback;
import timber.log.Timber;

import static com.gigabytedevelopersinc.apps.sonshub.ui.ExpandableLayout.State.COLLAPSING;
import static com.gigabytedevelopersinc.apps.sonshub.ui.ExpandableLayout.State.EXPANDING;

@SuppressLint("StaticFieldLeak")
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "SonsHub Mobile";
    private DrawerLayout drawerLayout;
    public static Toolbar toolbar;
    private boolean haveConnectedWifi = false;
    private boolean haveConnectedMobile = false;
    private static TinyDb tinyDb;
    boolean doubleBackToExitPressedOnce = false;
    public static PlayerView playerView;
    public static SimpleExoPlayer player;

    public static ImageView toggleStreamLayout;
    public static LinearLayout toggleDivider;
    public static ExpandableLayout expandableLayout;

    private static boolean isTelevision;
    private static MainActivity sonshubAppInstance;

    private BroadcastReceiver sonshubNotificationBroadcastReceiver;
    public static int currentWindow;
    public static long playBackPosition;
    public static boolean playWhenReady = false;
    public  static LinearLayout streamLayout;
    public static ImageView imageStreamArt;
    public static TextView streamTitle;
    public static boolean isPreparing;
    public static AudioManager mAudioManager;
    public static AudioManager.OnAudioFocusChangeListener afChangeListener;
    public static AudioAttributes mAudioAttributes;
    public static String searchQuery;

    // Ads
    private StartAppAd startAppAd;
    private static InterstitialAd mInterstitialAd;
    private boolean showInterstitial = true;
    private boolean showStartAppInterstitial = true;

    // Downloader
    public static Fetch fetch;
    private static Context appContext = App.Companion.getContext();
    private static final String FETCH_NAMESPACE = "Downloading";

    public MainActivity() {
    }

    @SuppressLint({"InflateParams", "HardwareIds"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        playerView = findViewById(R.id.audio_view);
        player = new SimpleExoPlayer.Builder(this).build();
        tinyDb = new TinyDb(MainActivity.this);
        playerView.setControllerHideOnTouch(false);

        // Initialize Ads
        startAppAd = new StartAppAd(this);
        mInterstitialAd = new InterstitialAd(this);

        mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        //This is a check for different android versions to handle audio focus differently
        mAudioAttributes =
                new AudioAttributes.Builder()
                        .setUsage(C.USAGE_MEDIA)
                        .setContentType(C.CONTENT_TYPE_MUSIC)
                        .build();
        player.setAudioAttributes(mAudioAttributes, true);
        initializePlayer(
                this,
                player,
                playerView,
                false,
                playBackPosition,
                currentWindow,
                tinyDb.getString("downloadLink")
        );
        
        sonshubAppInstance = this;
        checkForUpdate();
        initializeInterstitialAd();
        initStartAppInterstitialAd();

        toggleStreamLayout = findViewById(R.id.toggle);
        toggleDivider = findViewById(R.id.toggleDivider);
        toggleStreamLayout.setImageResource(R.drawable.ic_toggle_open);
        expandableLayout = findViewById(R.id.expandable_layout);

        // Start app with collapsed (hidden) mini-player
        toggleDivider.setVisibility(View.VISIBLE);
        expandableLayout.collapse();
        toggleStreamLayout.setOnClickListener(v -> {
            //expandableLayout.toggle();
            if (expandableLayout.isExpanded()) {
                //toggleStreamLayout.setImageResource(R.drawable.ic_toggle_open);
                miniPlayerCollapse();
            } else {
                //toggleStreamLayout.setImageResource(R.drawable.ic_toggle_close);
                miniPlayerExpand();
            }
        });
        expandableLayout.setOnExpansionUpdateListener((expansionFraction, state) -> {
            Timber.tag("ExpandableLayout").d("State: %s", state);
            if (state == EXPANDING) {
                toggleStreamLayout.setImageResource(R.drawable.ic_toggle_close);
                //toggleStreamLayout.setRotation(expansionFraction * 180);
            } else if (state == COLLAPSING) {
                toggleStreamLayout.setImageResource(R.drawable.ic_toggle_open);
            }
        });

        streamLayout = findViewById(R.id.stream_layout);
        imageStreamArt = findViewById(R.id.image_stream_art);
        streamTitle = findViewById(R.id.stream_title);
        String imageLink = tinyDb.getString("details_image_link");
        String titleString = tinyDb.getString("music_title");
        System.out.println("Title String " + titleString);

        Glide.with(this)
                .load(imageLink)
                .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
                .apply(RequestOptions.placeholderOf(R.drawable.placeholder))
                .into(imageStreamArt);

        streamTitle.setSelected(true);
        streamTitle.setText(Html.fromHtml(titleString), TextView.BufferType.NORMAL);

        if (haveNetworkConnection()) {
            checkNetworkState();
        } else {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
            final View generalNoticeView = LayoutInflater.from(this).inflate(R.layout.general_notice, null);

            TextView generalNoticeText = generalNoticeView.findViewById(R.id.generalNoticeText);
            Button cancelButton = generalNoticeView.findViewById(R.id.cancelButton);
            Button continueButton = generalNoticeView.findViewById(R.id.continueButton);

            generalNoticeText.setText("Hello, We've detected a lack of internet connectivity on your device.\nTo avoid some difficulties using SonsHub Mobile, kindly turn on your Internet Connection.");
            cancelButton.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            continueButton.setLayoutParams(layoutParams);
            continueButton.setText(R.string.continue_without_internet);
            (generalNoticeView.findViewById(R.id.continueButton)).setOnClickListener(v -> bottomSheetDialog.dismiss());
            bottomSheetDialog.setCancelable(false);
            bottomSheetDialog.setContentView(generalNoticeView);
            bottomSheetDialog.show();
        }

        // Register Broadcast Receiver for Custom Notifications from Firebase
        sonshubNotificationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                FirebaseMessaging.getInstance().subscribeToTopic(Configs.TOPIC_GLOBAL);
            }
        };
        //checkNetworkState();
        isTelevision = Configs.isTelevision(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        tinyDb = new TinyDb(this);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                toolbar,
                R.string.navigation_open,
                R.string.navigation_close
        );
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.parent_frame, homeFragment);
        toolbar.setTitle(R.string.nav_home);
        fragmentTransaction.commit();
        SpeedDialView contactUs = findViewById(R.id.speedDial);
        contactUs.setOnChangeListener(new SpeedDialView.OnChangeListener() {
            @Override
            public boolean onMainActionSelected() {
                // Call your main action here
                sendFeedback();
                return false; // true to keep the Speed Dial open
            }

            @Override
            public void onToggleChanged(boolean isOpen) {
                Timber.tag(TAG).d("Speed dial toggle state changed. Open = %s", isOpen);
            }
        });

        // Initialize Downloader
        final FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(sonshubAppInstance)
                .setDownloadConcurrentLimit(6)
                .setHttpDownloader(new OkHttpDownloader(Downloader.FileDownloaderType.PARALLEL))
                .setNamespace(FETCH_NAMESPACE)
                .setNotificationManager(new SonsHubDownloadNotificationManager(sonshubAppInstance) {
                    @NotNull
                    @Override
                    public Fetch getFetchInstanceForNamespace(@NotNull String namespace) {
                        return fetch;
                    }
                })
                .build();
        fetch = Fetch.Impl.getInstance(fetchConfiguration);

        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
    }

    public static void initializePlayer(Context context, SimpleExoPlayer player, PlayerView playerView, boolean playWhenReady, long playBackPosition, int currentWindow, String songLink) {

        playerView.setPlayer(player);
        isPreparing = true;
        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playBackPosition);
//        if (songLink.isEmpty()) {
//            songLink = tinyDb.getString("downloadLink");
//        }
        Uri uri = Uri.parse(songLink);
        MediaSource mediaSource = buildMediaSource(uri);
        player.prepare(mediaSource, true, false);

        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, int reason) { }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) { }

            @Override
            public void onLoadingChanged(boolean isLoading) { }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                playerView.setControllerHideOnTouch(false);
                if (isPreparing && playbackState == Player.STATE_READY) {
                    // This is accurate
                    isPreparing = false;
                }
                if (playbackState == Player.STATE_ENDED) {
                    int nexMusicPostistion = tinyDb.getInt("musicPosition") + 1;

                    String nextMusicLink = tinyDb.getListString("allMusicLinkList").get(nexMusicPostistion);
                    initializePlayer(
                            getInstance(),
                            player,
                            playerView,
                            false,
                            playBackPosition,
                            currentWindow,
                            nextMusicLink
                    );
                    /*String nextMusicLink = tinyDb.getListString("allMusicLinkList").get(tinyDb.getInt("musicPosition"));
                    isPreparing = true;
                    player.setPlayWhenReady(playWhenReady);
                    player.seekTo(currentWindow, playBackPosition);

                    Uri uri = Uri.parse(nextMusicLink);
                    MediaSource mediaSource = buildMediaSource(uri);
                    player.prepare(mediaSource, true, false);*/
                }

            }

            @Override
            public void onRepeatModeChanged(int repeatMode) { }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) { }

            @Override
            public void onPlayerError(ExoPlaybackException error) { }

            @Override
            public void onPositionDiscontinuity(int reason) { }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) { }

            @Override
            public void onSeekProcessed() { }
        });
    }

    public static boolean isPlaying() {
        return player.getPlaybackState() == Player.STATE_READY && player.getPlayWhenReady();
    }

    @SuppressLint("InflateParams")
    public void fillBottomSheet(Context context) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(Objects.requireNonNull(context));
        View detailsView = LayoutInflater.from(context).inflate(R.layout.details_bottomsheet, null);
        ImageView imageView = detailsView.findViewById(R.id.songImage);
        ImageView downloadBtn = detailsView.findViewById(R.id.download_button);
        ImageView streamBtn = detailsView.findViewById(R.id.stream_button);
        TextView songTitle = detailsView.findViewById(R.id.song_title);
        TextView songContent = detailsView.findViewById(R.id.content_view);
        Button button = detailsView.findViewById(R.id.web_button);
        LinearLayout backButton = detailsView.findViewById(R.id.back);
        songTitle.setSelected(true);

        backButton.setOnClickListener(v -> bottomSheetDialog.dismiss());
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(context.getResources().getColor(R.color.colorAccent));          // (no gradient)
        gd.setStroke(2, Color.BLACK);
        gd.setShape(GradientDrawable.OVAL);
        gd.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        gd.setGradientRadius(streamBtn.getWidth()/2);
        gd.setSize(50,50);
        streamBtn.setBackground(gd);
        downloadBtn.setBackground(gd);

        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setContentView(detailsView);
        bottomSheetDialog.show();
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setShowTitle(true);
        CustomTabsIntent customTabsIntent = builder.build();
        builder.setToolbarColor(context.getResources().getColor(R.color.colorPrimary));
        CustomTabsHelper.addKeepAliveExtra(context, customTabsIntent.intent);
        TinyDb tinyDb = new TinyDb(context);

        // This switch statement checks tiny db for each item clicked and know which specific genre was clicked
        // to handle logic properly...
        // This switch statement different categories which are movies, music, news, featureimages, featured
        final View.OnClickListener onClickListener = view -> Toast.makeText(context, "No Download Link Available at this time", Toast.LENGTH_SHORT).show();
        Pattern pattern;
        Matcher matcher;

        switch (tinyDb.getString("clicked")) {
            case "movies":
                streamBtn.setVisibility(View.GONE);
                downloadBtn.setVisibility(View.VISIBLE);
                try {
                    JSONArray jsonArray = new JSONArray(tinyDb.getString("movieDetailsList"));

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String imageUrl = obj.getString("imageUrl");
                        String title = obj.getString("title");
                        String content = obj.getString("content");
                        String link = obj.getString("link");

                        Glide.with(context)
                                .load(imageUrl)
                                .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
                                .apply(RequestOptions.placeholderOf(R.drawable.placeholder))
                                .into(imageView);
                        String newContentText = content.trim().replace("<img>", "");
//                        contentView.setText(Html.fromHtml(newContentText), TextView.BufferType.SPANNABLE);
                        songTitle.setText(Html.fromHtml(title), TextView.BufferType.NORMAL);
                        tinyDb.putString("movie_title", title);

                        button.setOnClickListener(view12 -> CustomTabsHelper.openCustomTab(context, customTabsIntent,
                                Uri.parse(link),
                                new WebViewFallback()));
                        pattern = Pattern.compile("http.*?mp4");
                        matcher = pattern.matcher(content);
                        final Matcher matcher2 = matcher;
                        if (matcher.find()) {
                            //put the download link in tinyDb for the exoplayer to pick up
                            tinyDb.putString("downloadLink", matcher.group(0));
                            downloadBtn.setOnClickListener(view -> {
                                bottomSheetDialog.dismiss();
                                downloadFile(matcher2, context);
                            });
                        } else {
                            downloadBtn.setOnClickListener(onClickListener);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            //If it was music that was clicked
            case "music":
                downloadBtn.setVisibility(View.VISIBLE);
                streamBtn.setVisibility(View.VISIBLE);
                try {
                    JSONArray jsonArray = new JSONArray(tinyDb.getString("musicDetailsList"));

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String imageUrl = obj.getString("imageUrl");
                        String title = obj.getString("title");
                        String content = obj.getString("content");
                        String link = obj.getString("link");
                        tinyDb.putString("details_image_link", imageUrl);
                        Glide.with(context)
                                .load(imageUrl)
                                .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
                                .apply(RequestOptions.placeholderOf(R.drawable.placeholder))
                                .into(imageView);

                        String newTitle = title.trim()
                                .replace("DOWNLOAD", "")
                                .replace("MP3", "")
                                .replace(":", "");
                        songTitle.setText(Html.fromHtml(newTitle), TextView.BufferType.NORMAL);
                        tinyDb.putString("music_title", newTitle);

                        button.setOnClickListener(view12 -> CustomTabsHelper.openCustomTab(context, customTabsIntent,
                                Uri.parse(link),
                                new WebViewFallback()));

                        pattern = Pattern.compile("http.*?\\.mp3");
                        matcher = pattern.matcher(content);
                        final Matcher matcher2 = matcher;
                        if (matcher.find()) {
                            //put the download link in tinyDb for the exoplayer to pick up
                            tinyDb.putString("downloadLink", matcher.group(0));
                            downloadBtn.setOnClickListener(view -> {
                                bottomSheetDialog.dismiss();
                                downloadFile(matcher2, context);
                            });

                            streamBtn.setOnClickListener(view -> {
                                bottomSheetDialog.dismiss();
                                if (player.getPlayWhenReady()) {
                                    player.stop();
                                }

                                Glide.with(context)
                                        .load(imageUrl)
                                        .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
                                        .apply(RequestOptions.placeholderOf(R.drawable.placeholder))
                                        .into(imageStreamArt);
                                streamTitle.setText(Html.fromHtml(newTitle), TextView.BufferType.NORMAL);

                                if (!isPreparing) {
                                    miniPlayerExpand();
                                    initializePlayer(
                                            getInstance(),
                                            player,
                                            playerView,
                                            false,
                                            playBackPosition,
                                            currentWindow,
                                            matcher2.group(0)
                                    );
                                }
                                player.setPlayWhenReady(true);
                            });
                        } else {
                            downloadBtn.setOnClickListener(onClickListener);
                            streamBtn.setOnClickListener(onClickListener);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "featured":
                try {
                    JSONArray jsonArray = new JSONArray(tinyDb.getString("featuredDetailsList"));

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String imageUrl = obj.getString("imageUrl");
                        String title = obj.getString("title");
                        String content = obj.getString("content");
                        String link = obj.getString("link");

                        Glide.with(context)
                                .load(imageUrl)
                                .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
                                .apply(RequestOptions.placeholderOf(R.drawable.placeholder))
                                .into(imageView);

                        String newTitle = title.trim()
                                .replace("DOWNLOAD", "")
                                .replace("MP3", "")
                                .replace(":", "");
                        songTitle.setText(Html.fromHtml(newTitle), TextView.BufferType.NORMAL);

                        button.setOnClickListener(view12 -> CustomTabsHelper.openCustomTab(context, customTabsIntent,
                                Uri.parse(link),
                                new WebViewFallback()));

                        pattern = Pattern.compile("http.*?\\.mp3");
                        matcher = pattern.matcher(content);
                        final Matcher matcher2= matcher;
                        //If the mp3 file is found meaning it's a featured music
                        if (matcher.find()) {
                            //put the download link in tinyDb for the exoplayer to pick up
                            tinyDb.putString("downloadLink", matcher.group(0));

                            downloadBtn.setOnClickListener(view -> {
                                bottomSheetDialog.dismiss();
                                downloadFile(matcher2, context);
                            });

                            streamBtn.setOnClickListener(view -> {
                                bottomSheetDialog.dismiss();
                                Toast.makeText(context, "Loading Music...", Toast.LENGTH_SHORT).show();
                                if (player.getPlayWhenReady()) {
                                    player.stop();
                                }

                                Glide.with(context)
                                        .load(imageUrl)
                                        .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
                                        .apply(RequestOptions.placeholderOf(R.drawable.placeholder))
                                        .into(imageStreamArt);
                                streamTitle.setText(Html.fromHtml(newTitle), TextView.BufferType.NORMAL);

                                if (!isPreparing) {
                                    miniPlayerExpand();
                                    initializePlayer(
                                            getInstance(),
                                            player,
                                            playerView,
                                            false,
                                            playBackPosition,
                                            currentWindow,
                                            matcher2.group(0)
                                    );
                                }
                                player.setPlayWhenReady(true);
                            });
                        } else {
                            downloadBtn.setOnClickListener(onClickListener);
                            streamBtn.setOnClickListener(onClickListener);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "news":
                streamBtn.setVisibility(View.GONE);
                downloadBtn.setVisibility(View.GONE);
                try {
                    JSONArray jsonArray = new JSONArray(tinyDb.getString("newsDetailsList"));

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String imageUrl = obj.getString("imageUrl");
                        String title = obj.getString("title");
                        String content = obj.getString("content");
                        String link = obj.getString("link");

                        Glide.with(context)
                                .load(imageUrl)
                                .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
                                .apply(RequestOptions.placeholderOf(R.drawable.placeholder))
                                .into(imageView);

                        String newContentText = content.trim().replace("<img>", "");
                        String newTitle = title.trim()
                                .replace("DOWNLOAD", "")
                                .replace("MP3", "")
                                .replace(":", "");
                        songTitle.setText(Html.fromHtml(newTitle), TextView.BufferType.NORMAL);
                        songContent.setText(Html.fromHtml(newContentText), TextView.BufferType.SPANNABLE);

                        button.setOnClickListener(view12 -> CustomTabsHelper.openCustomTab(context, customTabsIntent,
                                Uri.parse(link),
                                new WebViewFallback()));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "featuredimages":
                downloadBtn.setVisibility(View.VISIBLE);
                streamBtn.setVisibility(View.VISIBLE);
                try {
                    JSONArray jsonArray = new JSONArray(tinyDb.getString("featuredimageslist"));

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String imageUrl = obj.getString("imageUrl");
                        String title = obj.getString("title");
                        String content = obj.getString("content");
                        String link = obj.getString("link");
                        tinyDb.putString("details_image_link", imageUrl);
                        Glide.with(context)
                                .load(imageUrl)
                                .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
                                .apply(RequestOptions.placeholderOf(R.drawable.placeholder))
                                .into(imageView);

                        String newTitle = title.trim()
                                .replace("DOWNLOAD", "")
                                .replace("MP3", "")
                                .replace(":", "");
                        songTitle.setText(Html.fromHtml(newTitle), TextView.BufferType.NORMAL);
                        tinyDb.putString("music_title", newTitle);

                        button.setOnClickListener(view12 -> CustomTabsHelper.openCustomTab(context, customTabsIntent,
                                Uri.parse(link),
                                new WebViewFallback()));

                        //THis looks for a specific regex pattern (a link ending with mp3)
                        //If it is found then the user can download or stream the music
                        pattern = Pattern.compile("http.*?\\.mp3");
                        matcher = pattern.matcher(content);
                        final Matcher matcher2= matcher;
                        if (matcher.find()) {
                            //put the download link in tinDdb for the exoplayer to pick up
                            tinyDb.putString("downloadLink", matcher.group(0));

                            downloadBtn.setOnClickListener(view -> {
                                bottomSheetDialog.dismiss();
                                downloadFile(matcher2, context);
                            });

                            streamBtn.setOnClickListener(view -> {
                                bottomSheetDialog.dismiss();
                                Toast.makeText(context, "Loading Music...", Toast.LENGTH_SHORT).show();
                                if (player.getPlayWhenReady()) {
                                    player.stop();
                                }

                                Glide.with(context)
                                        .load(imageUrl)
                                        .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
                                        .apply(RequestOptions.placeholderOf(R.drawable.placeholder))
                                        .into(imageStreamArt);
                                streamTitle.setText(Html.fromHtml(newTitle), TextView.BufferType.NORMAL);

                                if (!isPreparing) {
                                    miniPlayerExpand();
                                    initializePlayer(
                                            getInstance(),
                                            player,
                                            playerView,
                                            false,
                                            playBackPosition,
                                            currentWindow,
                                            matcher2.group(0)
                                    );
                                }
                                player.setPlayWhenReady(true);
                            });
                        } else {
                            downloadBtn.setOnClickListener(onClickListener);
                            streamBtn.setOnClickListener(onClickListener);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @SuppressLint("InflateParams")
    public static void downloadFile(Matcher matcher,Context context) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        final View generalNoticeView = LayoutInflater.from(context).inflate(R.layout.general_notice, null);

        Button cancelButton = generalNoticeView.findViewById(R.id.cancelButton);
        Button continueButton = generalNoticeView.findViewById(R.id.continueButton);

        String downloadFileUrl = matcher.group(0);
        Data.sampleUrls = new String[]{downloadFileUrl};
        Timber.d(Arrays.toString(Data.sampleUrls));
        assert downloadFileUrl != null;
        String fileName = downloadFileUrl.substring(downloadFileUrl.lastIndexOf('/') + 1);
        if (need2Download(fileName)) {
            DownloadingFragment.enqueueDownload();
        } else {
            continueButton.setOnClickListener(v -> {
                File basePathMp3 = new File(Environment.getExternalStorageDirectory() + "/SonsHub" + "/Music");
                File basePathVideo = new File(Environment.getExternalStorageDirectory() + "/SonsHub" + "/Videos");

                File fullPathMp3 = new File(basePathMp3, fileName);
                File fullPathVideo = new File(basePathVideo, fileName);
                if (fileName.toLowerCase().endsWith(".mp3")) {
                    fullPathMp3.delete();
                } else if (fileName.toLowerCase().endsWith(".mp4")) {
                    fullPathVideo.delete();
                } else {
                    Toast.makeText(context, "We encountered an error ;(", Toast.LENGTH_LONG).show();
                }
                DownloadingFragment.enqueueDownload();
                bottomSheetDialog.dismiss();
            });
            cancelButton.setOnClickListener(v -> bottomSheetDialog.dismiss());
            bottomSheetDialog.setCancelable(false);
            bottomSheetDialog.setContentView(generalNoticeView);
            bottomSheetDialog.show();
        }

    }

    public static boolean need2Download(String fileName) {
        File basePathMp3 = new File(Environment.getExternalStorageDirectory() + "/SonsHub" + "/Music");
        File basePathVideo = new File(Environment.getExternalStorageDirectory() + "/SonsHub" + "/Videos");

        File fullPathMp3 = new File(basePathMp3, fileName);
        File fullPathVideo = new File(basePathVideo, fileName);

        /*if (fullPathMp3.exists() || fullPathVideo.exists()) {
            return false;
        }*/
        return !fullPathMp3.exists() && !fullPathVideo.exists();
    }


    public static MediaSource buildMediaSource(Uri uri) {
        return new ProgressiveMediaSource.Factory(
                new DefaultHttpDataSourceFactory("exoplayer-codelab")).createMediaSource(uri);
    }

    @SuppressLint("InflateParams")
    public void checkForUpdate() {
        AppUpdaterUtils appUpdaterUtils = new AppUpdaterUtils(this)
                .setUpdateFrom(UpdateFrom.JSON)
                .setUpdateJSON("https://www.gigabytedevelopersinc.com/apps/sonshub/updater/update.json")
                .withListener(new AppUpdaterUtils.UpdateListener() {
                    @Override
                    public void onSuccess(Update update, Boolean isUpdateAvailable) {
                        Timber.tag("Latest Version").d(update.getLatestVersion());
                        Timber.tag("Latest Version Code").d(String.valueOf(update.getLatestVersionCode()));
                        Timber.tag("Release notes").d(update.getReleaseNotes());
                        Timber.tag("URL").d(String.valueOf(update.getUrlToDownload()));
                        Timber.tag("Is update available?").d(Boolean.toString(isUpdateAvailable));
                        tinyDb.putString("updateDownloadURL", String.valueOf(update.getUrlToDownload()));

                        if (isUpdateAvailable) {
                            Toast.makeText(appContext, getString(R.string.toast_success_new_version), Toast.LENGTH_LONG).show();
                            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
                            final View updateNoticeView = LayoutInflater.from(MainActivity.this).inflate(R.layout.updater_notice, null);

                            TextView updateNoticeText = updateNoticeView.findViewById(R.id.updateNoticeText);
                            Button continueButton = updateNoticeView.findViewById(R.id.update_continue);
                            LinearLayout closeButton = updateNoticeView.findViewById(R.id.close);
                            updateNoticeText.setText(update.getReleaseNotes());
                            continueButton.setOnClickListener(v -> {
                                Intent playStore = new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("market://details?id=com.gigabytedevelopersinc.apps.sonshub")
                                );
                                startActivity(playStore);
                                bottomSheetDialog.dismiss();
                            });
                            closeButton.setOnClickListener(v -> bottomSheetDialog.dismiss());
                            bottomSheetDialog.setCancelable(false);
                            bottomSheetDialog.setContentView(updateNoticeView);
                            if (!MainActivity.this.isFinishing()) {
                                bottomSheetDialog.show();
                            }
                        }
                    }

                    @Override
                    public void onFailed(AppUpdaterError appUpdaterError) {
                        Timber.tag("AppUpdater Error").d("Something went wrong");
                    }
                });
        appUpdaterUtils.start();
    }

    public static void miniPlayerExpand() {
        if (!expandableLayout.isExpanded()) {
            expandableLayout.expand();
        }
    }

    public static void miniPlayerCollapse() {
        if (expandableLayout.isExpanded()) {
            expandableLayout.collapse();
        }
    }

    public void releasePlayer() {
        if (player != null) {
            playBackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            mAudioManager.abandonAudioFocus(afChangeListener);
        }
    }

    public static synchronized MainActivity getInstance() {
        return sonshubAppInstance;
    }

    public static boolean isTelevision() {
        return isTelevision;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home,menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.nav_search)
                .getActionView();
        assert searchManager != null;
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnClickListener(view -> {
            SearchFrag searchFrag = new SearchFrag();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.parent_frame, searchFrag);
            fragmentTransaction.commit();
        });
        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                if (query.length() > 1) {
                    AsyncTask.execute(() -> {
                        //TODO your background code
                        searchQuery = query;
                        SearchFrag searchFrag = new SearchFrag();
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.parent_frame, searchFrag);
                        fragmentTransaction.commitAllowingStateLoss();
                    });
                } else {
                    HomeFragment homeFragment = new HomeFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.parent_frame, homeFragment);
                    toolbar.setTitle(R.string.nav_home);
                    fragmentTransaction.commit();

                }

                return false;
            }
        });
        return true;
    }

    @SuppressLint({"InflateParams", "SetTextI18n"})
    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
        final View optionsView = LayoutInflater.from(MainActivity.this).inflate(R.layout.options_sheet, null);

        LinearLayout optionMusicPlayer = optionsView.findViewById(R.id.openPlayer);
        LinearLayout optionShare = optionsView.findViewById(R.id.shareSonsHub);
        LinearLayout optionRate = optionsView.findViewById(R.id.rateSonsHub);
        LinearLayout close = optionsView.findViewById(R.id.back);
        switch (item.getItemId()) {
            case R.id.nav_search:
                return true;

            case R.id.nav_download:
                startActivity(new Intent(MainActivity.this, DownloadActivity.class));
                showInterstitial();
                return true;

            case R.id.nav_music_play:
                player.setPlayWhenReady(false);
                startActivity(new Intent(MainActivity.this, MusicMainActivity.class));
                overridePendingTransition(R.anim.push_up_in, R.anim.hold);
                showInterstitial();
                return true;

            case R.id.nav_sub_menu:
                // For Rating SonsHub Mobile
                Intent rateIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=com.gigabytedevelopersinc.apps.sonshub")
                );

                // For Sharing SonsHub Mobile
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                String shareSub = "Download the SonsHub Mobile app for your latest Gospel Music, Sermons, Articles and all round Christian Entertainment.\n\n";
                shareSub = shareSub + "https://bit.ly/DownloadSonsHubMobile \n\n";
                share.putExtra(Intent.EXTRA_SUBJECT, "Check out SonsHub Mobile");
                share.putExtra(Intent.EXTRA_TEXT, shareSub);
                optionMusicPlayer.setOnClickListener(v -> {
                    player.setPlayWhenReady(false);
                    startActivity(new Intent(MainActivity.this, MusicMainActivity.class));
                    overridePendingTransition(R.anim.push_up_in, R.anim.hold);
                    bottomSheetDialog.dismiss();
                });
                optionShare.setOnClickListener(v -> startActivity(Intent.createChooser(share,
                        "Share SonsHub Mobile using")));
                optionRate.setOnClickListener(v -> startActivity(rateIntent));
                close.setOnClickListener(v -> bottomSheetDialog.dismiss());
                bottomSheetDialog.setCancelable(false);
                bottomSheetDialog.setContentView(optionsView);
                bottomSheetDialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            //startAppAd.showAd();
            super.onBackPressed();
            return;
        }
        doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    @SuppressLint({"InflateParams", "SetTextI18n"})
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        drawerLayout = findViewById(R.id.drawer_layout);
        if (id == R.id.nav_home) {
            miniPlayerCollapse();
            showInterstitial();
            HomeFragment homeFragment = new HomeFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.parent_frame, homeFragment);
            toolbar.setTitle(R.string.nav_home);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_music) {
            miniPlayerCollapse();
            showInterstitial();
            MusicFragment musicFragment = new MusicFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.parent_frame, musicFragment);
            toolbar.setTitle(R.string.nav_music);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_video) {
            miniPlayerCollapse();
            showInterstitial();
            VideosFragment videosFragment = new VideosFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.parent_frame, videosFragment);
            toolbar.setTitle(R.string.nav_video);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_gist) {
            miniPlayerCollapse();
            showInterstitial();
            GistFragment gistFragment = new GistFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.parent_frame, gistFragment);
            toolbar.setTitle(R.string.nav_gist);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_word_of_faith) {
            miniPlayerCollapse();
            showInterstitial();
            WordOfFaithFragment wordOfFaithFragment = new WordOfFaithFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.parent_frame, wordOfFaithFragment);
            toolbar.setTitle(R.string.nav_word_of_faith);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_sonshub_tv) {
            miniPlayerCollapse();
            showInterstitial();
            drawerLayout.closeDrawers();
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
            final View generalNoticeView = LayoutInflater.from(this).inflate(R.layout.general_notice, null);

            TextView generalNoticeText = generalNoticeView.findViewById(R.id.generalNoticeText);
            TextView generalNoticeTitle = generalNoticeView.findViewById(R.id.warning);
            ImageView generalNoticeImg = generalNoticeView.findViewById(R.id.warningImg);
            Button cancelButton = generalNoticeView.findViewById(R.id.cancelButton);
            Button continueButton = generalNoticeView.findViewById(R.id.continueButton);

            generalNoticeTitle.setText("SonsHub TV");
            generalNoticeTitle.setPadding(0,10,0,0);
            generalNoticeImg.setImageResource(R.drawable.ic_sonshub_tv);
            generalNoticeText.setText("\"Coming Soon\".\n\nMeanwhile, do subscribe to our YouTube Channel...");
            cancelButton.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            continueButton.setLayoutParams(layoutParams);
            continueButton.setText("Subscribe to our YouTube Channel");
            continueButton.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getResources().getString(R.string.youtube_url)))
                );
            });
            bottomSheetDialog.setCancelable(true);
            bottomSheetDialog.setContentView(generalNoticeView);
            bottomSheetDialog.show();
        } else if (id == R.id.nav_music_player) {
            miniPlayerCollapse();
            startActivity(new Intent(MainActivity.this, MusicMainActivity.class));
            overridePendingTransition(R.anim.push_up_in, R.anim.hold);
            showInterstitial();
            drawerLayout.closeDrawers();
        } else if (id == R.id.nav_about) {
            miniPlayerCollapse();
            showInterstitial();
            AboutFragment aboutFragment = new AboutFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.parent_frame, aboutFragment);
            toolbar.setTitle(R.string.nav_about);
            fragmentTransaction.commit();
        }
        drawerLayout.closeDrawers();
        return true;
    }

    @SuppressLint({"InflateParams", "SetTextI18n"})
    private void checkNetworkState() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo info = cm.getActiveNetworkInfo();

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        final View generalNoticeView = LayoutInflater.from(this).inflate(R.layout.general_notice, null);

        TextView generalNoticeText = generalNoticeView.findViewById(R.id.generalNoticeText);
        Button cancelButton = generalNoticeView.findViewById(R.id.cancelButton);
        Button continueButton = generalNoticeView.findViewById(R.id.continueButton);

        assert info != null;
        if (info.getType() == ConnectivityManager.TYPE_WIFI) {
            // do something
            /*generalNoticeText.setText("Hello, We have detected that your current Internet Connectivity is on WiFi and the your Internet state might become unpredictable and fluctuate often which makes your network state unreliable.\n\nContinue using SonsHub if you trust this network is reliable.");
            cancelButton.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            continueButton.setLayoutParams(layoutParams);
            continueButton.setText("Continue with WiFi Network");
            continueButton.setOnClickListener(v -> bottomSheetDialog.dismiss());
            bottomSheetDialog.setCancelable(false);
            bottomSheetDialog.setContentView(generalNoticeView);
            bottomSheetDialog.show();*/
        } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            // check NetworkInfo subtype
            if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_CDMA) {
                // Bandwidth between 14-64 kbps
            } else if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE) {
                // Bandwidth between 50-100 kbps
                generalNoticeText.setText("Hello, We have detected that your current Internet Connectivity is on EDGE (2G).\nThis means that your Internet Connection is slow and could affect your User Experience on this app.\nWe strongly advice you to switch your network to 3G, 4G or 5G (if available at your location).\n\nContinue using SonsHub if you are certain that you are unable to switch over to a better network... This could adversely affect your experience with the SonsHub Mobile app");
                cancelButton.setVisibility(View.GONE);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                continueButton.setLayoutParams(layoutParams);
                continueButton.setText(getString(R.string.continue_with_edge));
                continueButton.setOnClickListener(v -> bottomSheetDialog.dismiss());
                bottomSheetDialog.setCancelable(false);
                bottomSheetDialog.setContentView(generalNoticeView);
                bottomSheetDialog.show();
            } else if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_GPRS) {
                // Bandwidth between 100 kbps and below
            } else if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_EVDO_0) {
                // Bandwidth between 400-1000 kbps
            } else if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_EVDO_A) {
                // Bandwidth between 600-1400 kbps
            } else if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_UMTS) {
                // Bandwidth between 400-7000 kbps
            } else if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_HSPA) {
                // Bandwidth between 700-1700 kbps
            } else if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_HSPAP) {
                // Bandwidth between 700-1700 kbps
            } else if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_HSDPA) {
                // Bandwidth between 2-14 Mbps
            } else if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_HSUPA) {
                // Bandwidth between 1-23 Mbps
            } else if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_UNKNOWN) {
                // Bandwidth is Unknown
            }
            // Other list of various subtypes you can check for and their bandwidth limits
            // TelephonyManager.NETWORK_TYPE_1xRTT       ~ 50-100 kbps
            // TelephonyManager.NETWORK_TYPE_CDMA        ~ 14-64 kbps
            // TelephonyManager.NETWORK_TYPE_HSDPA       ~ 2-14 Mbps
            // TelephonyManager.NETWORK_TYPE_HSPA        ~ 700-1700 kbps
            // TelephonyManager.NETWORK_TYPE_HSUPA       ~ 1-23 Mbps
            // TelephonyManager.NETWORK_TYPE_UMTS        ~ 400-7000 kbps
            // TelephonyManager.NETWORK_TYPE_UNKNOWN     ~ Unknown

        }
    }

    private boolean haveNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }

        return haveConnectedWifi || haveConnectedMobile;
    }

    @SuppressLint({"IntentReset", "InflateParams", "SetTextI18n"})
    private void sendFeedback() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        final View generalNoticeView = LayoutInflater.from(this).inflate(R.layout.general_notice, null);

        TextView generalNoticeTitle = generalNoticeView.findViewById(R.id.warning);
        TextView generalNoticeText = generalNoticeView.findViewById(R.id.generalNoticeText);
        ImageView generalNoticeImg = generalNoticeView.findViewById(R.id.warningImg);
        Button cancelButton = generalNoticeView.findViewById(R.id.cancelButton);
        Button continueButton = generalNoticeView.findViewById(R.id.continueButton);
        Button optionButton = generalNoticeView.findViewById(R.id.optionButton);

        generalNoticeTitle.setText(getString(R.string.feedback_fab_text));
        generalNoticeImg.setImageResource(R.drawable.ic_feedback);
        generalNoticeText.setText("Would you like to contact us for promotional offers to promote your content on both our website and SonsHub Mobile? If yes, use the first button to contact us.\n\nAre you having issues using SonsHub Mobile or did you notice anything unusual with our content? If yes, use the second button to contact our technical team.");
        cancelButton.setVisibility(View.GONE);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        continueButton.setLayoutParams(layoutParams);
        continueButton.setText(getString(R.string.feedback_report_error));
        optionButton.setVisibility(View.VISIBLE);
        optionButton.setText(getString(R.string.feedback_promotion));
        continueButton.setOnClickListener(v -> {
            Intent mail = new Intent(Intent.ACTION_SENDTO);
            mail.setType("text/html");
            mail.setData(Uri.parse("mailto:"));
            mail.putExtra(Intent.EXTRA_EMAIL, new String[]{ "support@gigabytedevelopersinc.com"});
            mail.putExtra(Intent.EXTRA_SUBJECT, "Hello, Gigabyte Developers");
            startActivity(Intent.createChooser(mail, "Talk to Gigabyte Developers with"));
            bottomSheetDialog.dismiss();
        });
        optionButton.setOnClickListener(v -> {
            Intent mail = new Intent(Intent.ACTION_SENDTO);
            mail.setType("text/html");
            mail.setData(Uri.parse("mailto:"));
            mail.putExtra(Intent.EXTRA_EMAIL, new String[]{ "sonshubmobile@gmail.com"});
            mail.putExtra(Intent.EXTRA_SUBJECT, "Hello, SonsHub (Promotional Offer)");
            startActivity(Intent.createChooser(mail, "Talk to SonsHub with"));
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setContentView(generalNoticeView);
        bottomSheetDialog.show();
    }

    private void initializeInterstitialAd() {
        mInterstitialAd.setListener(new InterstitialAdsListener());
        loadInterstitialAds();
    }

    private void initStartAppInterstitialAd() {
        loadStartAppInterstitialAds();
    }

    private void loadInterstitialAds() {
        mInterstitialAd.loadAd();
    }

    private void loadStartAppInterstitialAds() {
        startAppAd.loadAd();
    }

    private class InterstitialAdsListener extends DefaultAdListener {
        /**
         * This event is called once an ad loads successfully.
         */
        @Override
        public void onAdLoaded(final Ad ad, final AdProperties adProperties) {
            Timber.i("%s Interstitial ad loaded successfully.", adProperties.getAdType().toString());
        }

        /**
         * This event is called if an ad fails to load.
         */
        @Override
        public void onAdFailedToLoad(final Ad ad, final AdError error) {
            Timber.w("Interstitial Ad failed to load. Code: " + error.getCode() + ", Message: " + error.getMessage());
            if (error.getCode().toString().equals("NO_FILL")) {
                loadStartAppInterstitialAds();
            } else {
                loadInterstitialAds();
            }
        }

        /**
         * This event is called when an interstitial ad has been dismissed by the user.
         */
        @Override
        public void onAdDismissed(final Ad ad) {
            Timber.i("Interstitial Ad has been dismissed by the user.");

            // Once the shown ad is dismissed, its lifecycle is complete and a new ad can be loaded.
            loadInterstitialAds();
        }
    }

    private void showInterstitial() {
        if (showInterstitial && null != mInterstitialAd) {
            if (mInterstitialAd.isReady()) {
                mInterstitialAd.showAd();
                if (!mInterstitialAd.showAd()) {
                    Timber.w("The ad was not shown. Check the logcat for more information.");
                    showStartAppInterstitial();
                    loadInterstitialAds();
                }
            } else {
                showStartAppInterstitial();
            }
        }
    }

    private void showStartAppInterstitial() {
        if (showStartAppInterstitial && null != startAppAd) {
            if (startAppAd.isReady()) {
                startAppAd.showAd();
                if (!startAppAd.showAd()) {
                    Timber.w("The ad was not shown. Check the logcat for more information.");
                    loadStartAppInterstitialAds();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        startAppAd.onResume();
        super.onResume();
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(sonshubNotificationBroadcastReceiver,
                new IntentFilter(Configs.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(sonshubNotificationBroadcastReceiver,
                new IntentFilter(Configs.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtil.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(sonshubNotificationBroadcastReceiver);
        super.onPause();
        startAppAd.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
        fetch.close();
    }
}