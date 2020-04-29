package com.gigabytedevelopersinc.apps.sonshub.fragments;


import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.browser.customtabs.CustomTabsIntent;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.Status;
import com.gigabytedevelopersinc.apps.sonshub.utils.DownloadUtils;
import com.gigabytedevelopersinc.apps.sonshub.utils.misc.AnalyticsManager;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import com.gigabytedevelopersinc.apps.sonshub.BuildConfig;
import com.gigabytedevelopersinc.apps.sonshub.R;
import com.google.android.material.snackbar.Snackbar;
import saschpe.android.customtabs.CustomTabsHelper;
import saschpe.android.customtabs.WebViewFallback;
import timber.log.Timber;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Objects;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment {

    // Updater
    private ImageView updateImg;
    private TextView updateTitle, updateText;
    private Button continueButton, cancelButton;

    private boolean isDeleted;

    int downloadId;

    public AboutFragment() {
        // Required empty public constructor
    }


    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d(AboutFragment.class.getSimpleName());
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        String url = getResources().getString(R.string.developer_url);
        String faceBookUrl = getResources().getString(R.string.facebook_url);
        String sonsHubUrl = getResources().getString(R.string.website_url);
        String instagramUrl = getResources().getString(R.string.instagram_url);
        String changeLogUrl = getResources().getString(R.string.changelog_url);
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setShowTitle(true);
        CustomTabsIntent customTabsIntent = builder.build();
        builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));
        CustomTabsHelper.addKeepAliveExtra(requireContext(), customTabsIntent.intent);

        LinearLayout developer = view.findViewById(R.id.developedBy);
        developer.setOnClickListener(view12 -> CustomTabsHelper.openCustomTab(getContext(), customTabsIntent,
                Uri.parse(url),
                new WebViewFallback()));

        TextView versionNumber = view.findViewById(R.id.version_number);
        versionNumber.append(BuildConfig.VERSION_NAME);

        LinearLayout facebook = view.findViewById(R.id.facebook);
        facebook.setOnClickListener(view12 -> CustomTabsHelper.openCustomTab(getContext(), customTabsIntent,
                Uri.parse(faceBookUrl),
                new WebViewFallback()));
        LinearLayout youtube = view.findViewById(R.id.youtube);
        youtube.setOnClickListener(view1 -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.youtube_url)))));

        LinearLayout website = view.findViewById(R.id.web);
        website.setOnClickListener(view12 -> CustomTabsHelper.openCustomTab(getContext(), customTabsIntent,
                Uri.parse(sonsHubUrl),
                new WebViewFallback()));
        LinearLayout instagram = view.findViewById(R.id.instagram);
        instagram.setOnClickListener(view12 -> CustomTabsHelper.openCustomTab(getContext(), customTabsIntent,
                Uri.parse(instagramUrl),
                new WebViewFallback()));
        LinearLayout changelog = view.findViewById(R.id.changeLog);
        changelog.setOnClickListener(view12 -> CustomTabsHelper.openCustomTab(getContext(), customTabsIntent,
                Uri.parse(changeLogUrl),
                new WebViewFallback()));
        LinearLayout openSource = view.findViewById(R.id.open_source_license);
        openSource.setOnClickListener(view1 ->{

            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
            View openSourceView = getLayoutInflater().inflate(R.layout.open_source_bottom_sheet, null);
            openSourceView.findViewById(R.id.license_1).setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.license_1_url)))));
            openSourceView.findViewById(R.id.license_2).setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.license_2_url)))));
            openSourceView.findViewById(R.id.license_3).setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.license_3_url)))));
            openSourceView.findViewById(R.id.license_4).setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.license_4_url)))));
            openSourceView.findViewById(R.id.license_5).setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.license_5_url)))));
            openSourceView.findViewById(R.id.license_6).setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.license_6_url)))));
            openSourceView.findViewById(R.id.license_7).setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.license_7_url)))));
            openSourceView.findViewById(R.id.license_8).setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.license_8_url)))));
            openSourceView.findViewById(R.id.license_9).setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.license_9_url)))));

            (openSourceView.findViewById(R.id.continue_btn)).setOnClickListener(v -> bottomSheetDialog.dismiss());
            bottomSheetDialog.setCancelable(false);
            bottomSheetDialog.setContentView(openSourceView);
            bottomSheetDialog.show();

        });
        LinearLayout privacyPolicy = view.findViewById(R.id.privacy_policy);
        privacyPolicy.setOnClickListener(view1 -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
            View privacyPolicyView = getLayoutInflater().inflate(R.layout.privacy_policy,null);
            WebView webView = privacyPolicyView.findViewById(R.id.privacy_policy_web);
            String prompt = "";
            try {
                InputStream inputStream = getResources().openRawResource(R.raw.policy);
                byte[] buffer = new byte[inputStream.available()];
                inputStream.read(buffer);
                prompt = new String(buffer);
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            webView.loadData(prompt,"text/html","utf-8");
            /*try {
                AssetManager assetManager = requireContext().getAssets();
                InputStream stream = assetManager.open("www/policy.html");
                BufferedReader r = new BufferedReader(new InputStreamReader(stream));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    total.append(line).append("\n");
                }
                webView.loadDataWithBaseURL(null, total.toString(), "text/html", "UTF-8", null);
            } catch (Exception xxx) {
                Timber.e(xxx, "Error loading www/policy.html");
            }*/
            (privacyPolicyView.findViewById(R.id.privacy_policy_continue)).setOnClickListener(v -> bottomSheetDialog.dismiss());
            bottomSheetDialog.setCancelable(false);
            bottomSheetDialog.setContentView(privacyPolicyView);
            bottomSheetDialog.show();
        });

        TextView copyright = view.findViewById(R.id.copyright);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        String currentYear = Integer.toString(year);
        String text = String.format(getString(R.string.copyright_desc), currentYear);
        copyright.setText(text);

        LinearLayout about = view.findViewById(R.id.aboutSonsHub);
        about.setOnLongClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
            View appUpdateView = getLayoutInflater().inflate(R.layout.general_notice,null);

            updateImg = appUpdateView.findViewById(R.id.warningImg);
            updateTitle = appUpdateView.findViewById(R.id.warning);
            updateText = appUpdateView.findViewById(R.id.generalNoticeText);
            continueButton = appUpdateView.findViewById(R.id.continueButton);
            cancelButton = appUpdateView.findViewById(R.id.cancelButton);

            updateImg.setImageResource(R.drawable.ic_system_update_white_24dp);
            updateImg.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
            updateTitle.setText(getString(R.string.update_check_title));
            updateText.setText("If for some reason you have not automatically been asked to update your version of SonsHub Mobile to the latest, then it most likely means that you are already running the latest version of SonsHub Mobile.\n\nDo you still want to check for new SonsHub Mobile app update?");
            continueButton.setText(getString(R.string.update_check_proceed));
            cancelButton.setText(getString(R.string.update_check_cancel));

            continueButton.setOnClickListener(view1 -> {
                bottomSheetDialog.dismiss();
                checkForUpdate();
            });
            cancelButton.setOnClickListener(view1 -> {
                bottomSheetDialog.dismiss();
                Snackbar.make(requireActivity()
                                .findViewById(android.R.id.content),
                        getString(R.string.snackBar_update_check_cancel),
                        Snackbar.LENGTH_LONG).show();
            });
            bottomSheetDialog.setCancelable(false);
            bottomSheetDialog.setContentView(appUpdateView);
            bottomSheetDialog.show();
            return false;
        });

        LinearLayout rate = view.findViewById(R.id.rate);
        rate.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
            View updateNoticeView = getLayoutInflater().inflate(R.layout.updater_notice, null);

            TextView updateNoticeTitle = updateNoticeView.findViewById(R.id.updateNoticeTitle);
            TextView updateNoticeMarquee = updateNoticeView.findViewById(R.id.updateNoticeMarquee);
            TextView updateNoticeText = updateNoticeView.findViewById(R.id.updateNoticeText);
            Button continueButton = updateNoticeView.findViewById(R.id.update_continue);
            LinearLayout closeButton = updateNoticeView.findViewById(R.id.close);
            updateNoticeTitle.setText(getString(R.string.rate));
            updateNoticeMarquee.setVisibility(View.GONE);
            updateNoticeText.setText("Love what we do? \nWhy not support us today by giving SonsHub Mobile a 5-Star rating on Play Store. This takes only a few seconds and means a lot to us.\n\nClick on the button below to rate SonsHub Mobile on Google Play Store.");
            continueButton.setText(getString(R.string.rate_btn_desc));

            // For Rating SonsHub Mobile
            Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.gigabytedevelopersinc.apps.sonshub"));
            continueButton.setOnClickListener(v1 -> {
                startActivity(rateIntent);
                bottomSheetDialog.dismiss();
            });
            closeButton.setOnClickListener(v1 -> bottomSheetDialog.dismiss());
            bottomSheetDialog.setCancelable(false);
            bottomSheetDialog.setContentView(updateNoticeView);
            bottomSheetDialog.show();
        });

        LinearLayout share = view.findViewById(R.id.share);
        share.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
            View updateNoticeView = getLayoutInflater().inflate(R.layout.updater_notice, null);

            TextView updateNoticeTitle = updateNoticeView.findViewById(R.id.updateNoticeTitle);
            TextView updateNoticeMarquee = updateNoticeView.findViewById(R.id.updateNoticeMarquee);
            TextView updateNoticeText = updateNoticeView.findViewById(R.id.updateNoticeText);
            Button continueButton = updateNoticeView.findViewById(R.id.update_continue);
            LinearLayout closeButton = updateNoticeView.findViewById(R.id.close);
            updateNoticeTitle.setText(getString(R.string.share));
            updateNoticeMarquee.setVisibility(View.GONE);
            updateNoticeText.setText("Love what we do? We can't do this without your support.\nSupport Us today by sharing SonsHub Mobile across to your friends. This takes only a few seconds and means a lot to us.\n\nClick on the button below to share SonsHub Mobile to your friends.");
            continueButton.setText(getString(R.string.share_btn_desc));

            // For Sharing SonsHub Mobile
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareSub = "\nDownload SonsHub Mobile App for your latest Gospel Music, Sermons, Articles and all round Christian Entertainment.\n\n";
            shareSub = shareSub + "https://bit.ly/DownloadSonsHubMobile \n\n";
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out SonsHub Mobile");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareSub);
            continueButton.setOnClickListener(v1 -> {
                startActivity(Intent.createChooser(shareIntent, "Share SonsHub Mobile using"));
                bottomSheetDialog.dismiss();
            });
            closeButton.setOnClickListener(v1 -> bottomSheetDialog.dismiss());
            bottomSheetDialog.setCancelable(false);
            bottomSheetDialog.setContentView(updateNoticeView);
            bottomSheetDialog.show();
        });
        return view;
    }

    @SuppressLint("InflateParams")
    private void checkForUpdate() {
        Snackbar updateCheck = Snackbar.make(requireActivity()
                        .findViewById(android.R.id.content),
                "Hang on, Checking for new version update! \n- This takes less than a minute.",
                Snackbar.LENGTH_LONG);
        updateCheck.show();
        AppUpdaterUtils appUpdaterUtils = new AppUpdaterUtils(getContext())
                .setUpdateFrom(UpdateFrom.JSON)
                .setUpdateJSON("https://www.gigabytedevelopersinc.com/apps/sonshub/updater/update.json")
                .withListener(new AppUpdaterUtils.UpdateListener() {
                    @Override
                    public void onSuccess(Update update, Boolean isUpdateAvailable) {
                        Timber.tag("Latest Version:").d(update.getLatestVersion());
                        Timber.tag("Latest Version Code:").d(String.valueOf(update.getLatestVersionCode()));
                        Timber.tag("Release Notes:").d(update.getReleaseNotes());
                        Timber.tag("URL:").d(String.valueOf(update.getUrlToDownload()));
                        Timber.tag("Is update available?").d(Boolean.toString(isUpdateAvailable));

                        if ((update.getLatestVersionCode() > BuildConfig.VERSION_CODE)) {
                            Handler handler = new Handler();
                            Runnable r = () -> {
                                updateCheck.dismiss();
                                Toast.makeText(requireContext(), getString(R.string.toast_success_new_version), Toast.LENGTH_LONG).show();
                                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
                                final View updateNoticeView = LayoutInflater.from(requireContext()).inflate(R.layout.updater_notice, null);

                                TextView updateNoticeText = updateNoticeView.findViewById(R.id.updateNoticeText);
                                Button continueButton = updateNoticeView.findViewById(R.id.update_continue);
                                LinearLayout closeButton = updateNoticeView.findViewById(R.id.close);
                                updateNoticeText.setText(update.getReleaseNotes());
                                continueButton.setOnClickListener(v -> {
                                    bottomSheetDialog.dismiss();
                                    BottomSheetDialog bottomSheetDialogConfirm = new BottomSheetDialog(requireContext());
                                    final View generalNoticeViewConfirm = LayoutInflater.from(requireContext()).inflate(R.layout.general_notice, null);

                                    TextView generalNoticeTextConfirm = generalNoticeViewConfirm.findViewById(R.id.generalNoticeText);
                                    Button cancelButtonConfirm = generalNoticeViewConfirm.findViewById(R.id.cancelButton);
                                    Button continueButtonConfirm = generalNoticeViewConfirm.findViewById(R.id.continueButton);
                                    Button optionButtonConfirm = generalNoticeViewConfirm.findViewById(R.id.optionButton);

                                    generalNoticeTextConfirm.setText("Did you install your current version of SonsHub Mobile from the Google Play Store? If yes, use the first button to update from Play Store.\n\nDid you install your current version of SonsHub Mobile elsewhere (not from Play Store)? If yes, use the second button to manually download the latest update from our Server.");
                                    cancelButtonConfirm.setVisibility(View.GONE);
                                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                    continueButtonConfirm.setLayoutParams(layoutParams);
                                    continueButtonConfirm.setText(getString(R.string.update_server));
                                    optionButtonConfirm.setVisibility(View.VISIBLE);
                                    optionButtonConfirm.setText(getString(R.string.update_play_store));
                                    continueButtonConfirm.setOnClickListener(v1 -> {
                                        bottomSheetDialogConfirm.dismiss();
                                        Toast.makeText(requireContext(), "App Update Downloading... Please Wait", Toast.LENGTH_SHORT).show();
                                        File file = new File(Environment.getExternalStorageDirectory()
                                                + "/SonsHub" + "/AppUpdate" + "/sonshub_mobile.apk");
                                        if (file.exists()) {
                                            isDeleted = file.delete();
                                            deleteAndInstall();
                                        } else {
                                            firstTimeInstall();
                                        }
                                        bottomSheetDialog.dismiss();
                                    });
                                    optionButtonConfirm.setOnClickListener(v1 -> {
                                        Intent updateFromStore = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.gigabytedevelopersinc.apps.sonshub"));
                                        if (BuildConfig.DEBUG) {
                                            Toast.makeText(requireContext(), "Sorry, You are not permitted to update through Play Store!", Toast.LENGTH_LONG).show();
                                        } else {
                                            startActivity(updateFromStore);
                                        }
                                        bottomSheetDialog.dismiss();
                                    });
                                    bottomSheetDialogConfirm.setCancelable(true);
                                    bottomSheetDialogConfirm.setContentView(generalNoticeViewConfirm);
                                    bottomSheetDialogConfirm.show();
                                });
                                closeButton.setOnClickListener(v -> bottomSheetDialog.dismiss());
                                bottomSheetDialog.setCancelable(false);
                                bottomSheetDialog.setContentView(updateNoticeView);
                                bottomSheetDialog.show();
                            };
                            handler.postDelayed(r, 5000);
                        } else {
                            // No Update
                            Handler handler = new Handler();
                            Runnable r = () -> {
                                updateCheck.dismiss();
                                Snackbar.make(requireActivity()
                                                .findViewById(android.R.id.content),
                                        "You have the latest version of SonsHub Mobile!",
                                        Snackbar.LENGTH_LONG).show();
                            };
                            handler.postDelayed(r, 5000);
                        }
                    }

                    @Override
                    public void onFailed(AppUpdaterError appUpdaterError) {
                        Timber.tag("AppUpdater Error").d("Something went wrong");
                    }
                });
        appUpdaterUtils.start();
    }

    private void firstTimeInstall() {
        Timber.d("May be 1st Update or deleted from folder");
        downloadAndInstall();
    }

    private void deleteAndInstall() {
        if (isDeleted) {
            Timber.tag("Deleted Existed file:").d(String.valueOf(true));
            downloadAndInstall();

        } else {
            Timber.tag("NOT DELETED:").d(String.valueOf(false));
            Toast.makeText(requireContext(), "Error in Updating...Please try Later", Toast.LENGTH_LONG).show();
            //bottomSheetDialog.dismiss();
        }
    }

    private void downloadAndInstall() {
        String downloadLink;
        if (BuildConfig.DEBUG) {
            downloadLink = "https://www.gigabytedevelopersinc.com/apps/sonshub/updater/debug/sonshub_mobile.apk";
        } else {
            downloadLink = "https://www.gigabytedevelopersinc.com/apps/sonshub/updater/release/sonshub_mobile.apk";
        }

        final String rootFolder = Environment.getExternalStorageDirectory() + "/SonsHub/" + "AppUpdate/";

        BottomSheetDialog updateDownloadBottomSheet = new BottomSheetDialog(requireContext());
        final View updateDownloadView = LayoutInflater.from(requireContext()).inflate(R.layout.update_download_bottom_sheet, null);
        TextView updateDownloadTitle = updateDownloadView.findViewById(R.id.update_downloading_title);
        ProgressBar downloadProgress = updateDownloadView.findViewById(R.id.updateProgressBar);
        TextView downloadProgressText = updateDownloadView.findViewById(R.id.updateViewProgress);
        Button cancelButton = updateDownloadView.findViewById(R.id.cancelButton);
        Button pauseButton = updateDownloadView.findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener(v -> {
            if (Status.RUNNING == PRDownloader.getStatus(downloadId)) {
                PRDownloader.pause(downloadId);
                return;
            }
            //pauseButton.setEnabled(false);
            downloadProgress.setIndeterminate(true);
            downloadProgress.getIndeterminateDrawable().setColorFilter(
                    Color.parseColor("#9C27B0"), android.graphics.PorterDuff.Mode.SRC_IN);

            if (Status.PAUSED == PRDownloader.getStatus(downloadId)) {
                PRDownloader.resume(downloadId);
                return;
            }
        });
        cancelButton.setOnClickListener(v -> {
            PRDownloader.cancel(downloadId);
        });
        updateDownloadBottomSheet.setCancelable(false);
        updateDownloadBottomSheet.setContentView(updateDownloadView);
        updateDownloadBottomSheet.show();

        downloadId = PRDownloader.download(downloadLink, rootFolder, "sonshub_mobile.apk")
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {
                        downloadProgress.setIndeterminate(false);
                        pauseButton.setEnabled(true);
                        pauseButton.setText(R.string.pause);
                        cancelButton.setEnabled(true);
                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {
                        pauseButton.setText(R.string.resume);
                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {
                        Toast.makeText(getContext(), R.string.message_download_canceled, Toast.LENGTH_LONG).show();
                        downloadProgress.setProgress(0);
                        downloadProgressText.setText("");
                        downloadId = 0;
                        downloadProgress.setIndeterminate(false);
                        updateDownloadBottomSheet.dismiss();
                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                        long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                        downloadProgress.setProgress((int) progressPercent);
                        downloadProgressText.setText(DownloadUtils.getProgressDisplayLine(progress.currentBytes, progress.totalBytes));
                        downloadProgress.setIndeterminate(false);
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        Toast.makeText(getContext(), R.string.message_download_completed, Toast.LENGTH_LONG).show();
                        updateDownloadBottomSheet.dismiss();
                        installApk(new File(rootFolder + "/" + "sonshub_mobile.apk"), requireContext());
                    }

                    @Override
                    public void onError(Error error) {
                        Toast.makeText(getContext(), R.string.message_download_failed, Toast.LENGTH_LONG).show();
                        updateDownloadBottomSheet.dismiss();
                    }
                });
    }

    private static void installApk(File file, Context context) {
        String type = "application/vnd.android.package-archive";


        Uri uri = FileProvider.getUriForFile(context.getApplicationContext(), context.getApplicationContext().getPackageName() + ".provider", file);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            uri = Uri.fromFile(file);
        }

        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        intent.setDataAndType(uri, type);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        context.startActivity(intent);
    }

    @Override
    public void onResume() {
        AnalyticsManager.setCurrentScreen(getActivity(), AboutFragment.class.getSimpleName());
        super.onResume();
    }
}
