package com.gigabytedevelopersinc.apps.sonshub.utils;

/**
 * Project - SonsHub
 * Created with Android Studio
 * Company: Gigabyte Developers
 * User: Emmanuel Nwokoma
 * Title: Founder and CEO
 * Day: Tuesday, 05
 * Month: March
 * Year: 2019
 * Date: 05 Mar, 2019
 * Time: 10:04 PM
 * Desc: ActionListener
 **/
public interface ActionListener {
    void onPauseDownload(int id);
    void onResumeDownload(int id);
    void onRemoveDownload(int id);
    void onRetryDownload(int id);
}
