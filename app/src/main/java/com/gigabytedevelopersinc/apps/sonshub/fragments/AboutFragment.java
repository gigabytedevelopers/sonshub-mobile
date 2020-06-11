package com.gigabytedevelopersinc.apps.sonshub.fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;

import com.gigabytedevelopersinc.apps.sonshub.activities.MainActivity;
import com.gigabytedevelopersinc.apps.sonshub.utils.misc.AnalyticsManager;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.*;
import com.gigabytedevelopersinc.apps.sonshub.BuildConfig;
import com.gigabytedevelopersinc.apps.sonshub.R;
import com.google.android.material.snackbar.Snackbar;
import saschpe.android.customtabs.CustomTabsHelper;
import saschpe.android.customtabs.WebViewFallback;
import timber.log.Timber;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment {

    // Updater
    private ImageView updateImg;
    private TextView updateTitle, updateText;
    private Button continueButton, cancelButton;

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
        NestedScrollView scrollView = view.findViewById(R.id.nestedScroll);

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



        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > oldScrollY) {
                MainActivity.streamLayout.setVisibility(View.GONE);
                MainActivity.miniPlayerCollapse();
            } else if (scrollY < oldScrollY) {
                MainActivity.streamLayout.setVisibility(View.VISIBLE);
            }
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
                                    Intent playStore = new Intent(Intent.ACTION_VIEW,
                                            Uri.parse("market://details?id=com.gigabytedevelopersinc.apps.sonshub")
                                    );
                                    startActivity(playStore);
                                    bottomSheetDialog.dismiss();
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

    @Override
    public void onResume() {
        AnalyticsManager.setCurrentScreen(getActivity(), AboutFragment.class.getSimpleName());
        super.onResume();
    }
}
