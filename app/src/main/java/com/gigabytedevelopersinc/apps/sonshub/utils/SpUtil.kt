package com.gigabytedevelopersinc.apps.sonshub.utils

import com.gigabytedevelopersinc.apps.sonshub.App

/**
 * Project - SonsHub
 * Created with Android Studio
 * Company: Gigabyte Developers
 * User: Emmanuel Nwokoma
 * Title: Founder and CEO
 * Day: Tuesday, 15
 * Month: June
 * Year: 2021
 * Date: 15 Jun, 2021
 * Time: 12:26 AM
 * Desc: SpUtil
 **/
object SpUtil {

    const val PREFS_FILENAME = "com.gigabytedevelopersinc.apps.sonshub.prefs"
    const val FOLDER_URI = "folder_uri"

    @JvmStatic
    fun storeString(key: String, text: String) {
        val editor = App.instance?.getSharedPreferences(PREFS_FILENAME, 0)!!.edit()
        editor.putString(key, text)
        editor.apply()
    }

    @JvmStatic
    fun getString(key: String, def:String): String {
        val text = App.instance?.getSharedPreferences(PREFS_FILENAME, 0)?.getString(key, def) ?:""
        return text
    }


}