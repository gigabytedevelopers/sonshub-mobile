package com.gigabytedevelopersinc.apps.sonshub.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import com.gigabytedevelopersinc.apps.sonshub.R
import com.gigabytedevelopersinc.apps.sonshub.utils.SpUtil
import timber.log.Timber
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

/**
 * Project - SonsHub
 * Created with Android Studio
 * Company: Gigabyte Developers
 * User: Emmanuel Nwokoma
 * Title: Founder and CEO
 * Day: Monday, 18
 * Month: February
 * Year: 2019
 * Date: 18 Feb, 2019
 * Time: 4:40 AM
 * Desc: SplashActivity
 */
class SplashActivityKotlin : AppCompatActivity() {

    var listPermissionsNeeded = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen_layout)

        // Carry on with the normal flow, when permissions are granted.
        val SPLASH_TIME_OUT = 10000
        Handler().postDelayed({

            // This method will be executed once the timer is over
            // Start Main Activity
            val i = Intent(this@SplashActivityKotlin, MainActivity::class.java)
            startActivity(i)

            // Close this Splash Screen
            finish()
        }, SPLASH_TIME_OUT.toLong())
    }

    private fun checkAndRequestPermissions(): Boolean {
        if (!listPermissionsNeeded.isEmpty()) {
            openDocumentTree()
            return false
        }
        return true
    }

    private fun openDocumentTree() {
        val uriString = SpUtil.getString(SpUtil.FOLDER_URI, "")
        when {
            uriString == "" -> {
                Timber.tag(LOG_TAG).d("uri not stored")
                askPermission()
            }
            arePermissionsGranted(uriString) -> {
                makeDoc(Uri.parse(uriString))
            }
            else -> {
                Timber.tag(LOG_TAG).d( "uri permission not stored")
                askPermission()
            }
        }
    }


    // this will present the user with folder browser to select a folder for our data
    private fun askPermission() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        intent.addFlags(
            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                    or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (data != null) {
                //this is the uri user has provided us
                val treeUri: Uri? = data.data
                if (treeUri != null) {
                    Timber.tag(LOG_TAG).d( "got uri: ${treeUri.toString()}")
                    // here we should do some checks on the uri, we do not want root uri
                    // because it will not work on Android 11, or perhaps we have some specific
                    // folder name that we want, etc
                    if (Uri.decode(treeUri.toString()).endsWith(":")){
                        Toast.makeText(this,"Cannot use root folder!",Toast.LENGTH_SHORT).show()
                        // consider asking user to select another folder
                        return
                    }
                    // here we ask the content resolver to persist the permission for us
                    val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    contentResolver.takePersistableUriPermission(treeUri,
                        takeFlags)

                    // we should store the string fo further use
                    SpUtil.storeString(SpUtil.FOLDER_URI, treeUri.toString())

                    //Finally, we can do our file operations
                    //Please note, that all file IO MUST be done on a background thread. It is not so in this
                    //sample - for the sake of brevity.
                    makeDoc(treeUri)
                }
            }
        }
    }

    private fun makeDoc(dirUri: Uri) {
        val dir = DocumentFile.fromTreeUri(this, dirUri)
        if (dir == null || !dir.exists()) {
            //the folder was probably deleted
            Timber.tag(LOG_TAG).d( "no Dir")
            //according to Commonsware blog, the number of persisted uri permissions is limited
            //so we should release those we cannot use anymore
            //https://commonsware.com/blog/2020/06/13/count-your-saf-uri-permission-grants.html
            releasePermissions(dirUri)
            //ask user to choose another folder
            Toast.makeText(this,"Folder deleted, please choose another!",Toast.LENGTH_SHORT).show()
            openDocumentTree()
        } else {
            val file = dir.createFile("*/txt", "test.txt")
            if (file != null && file.canWrite()) {
                Timber.tag(LOG_TAG).d( "file.uri = ${file.uri.toString()}")
                alterDocument(file.uri)
            } else {
                Timber.tag(LOG_TAG).d( "no file or cannot write")
                //consider showing some more appropriate error message
                Toast.makeText(this,"Write error!",Toast.LENGTH_SHORT).show()

            }
        }
    }


    private fun releasePermissions(uri: Uri) {
        val flags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        contentResolver.releasePersistableUriPermission(uri,flags)
        //we should remove this uri from our shared prefs, so we can start over again next time
        SpUtil.storeString(SpUtil.FOLDER_URI, "")
    }


    //Just a test function to write something into a file, from https://developer.android.com
    //Please note, that all file IO MUST be done on a background thread. It is not so in this
    //sample - for the sake of brevity.
    private fun alterDocument(uri: Uri) {
        try {

            contentResolver.openFileDescriptor(uri, "w")?.use { parcelFileDescriptor ->
                FileOutputStream(parcelFileDescriptor.fileDescriptor).use {
                    it.write(
                        ("String written at ${System.currentTimeMillis()}\n")
                            .toByteArray()
                    )
                    Toast.makeText(this,"File Write OK!",Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    private fun arePermissionsGranted(uriString: String): Boolean {
        // list of all persisted permissions for our app
        val list = contentResolver.persistedUriPermissions
        for (i in list.indices) {
            val persistedUriString = list[i].uri.toString()
            //Log.d(LOG_TAG, "comparing $persistedUriString and $uriString")
            if (persistedUriString == uriString && list[i].isWritePermission && list[i].isReadPermission) {
                //Log.d(LOG_TAG, "permission ok")
                return true
            }
        }
        return false
    }


    /**
     * Explain why the app needs permissions
     *
     * @param okListener The OK Button Listener
     *//*
    private fun showDialogOK(okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setMessage("Service Permissions are required for this app")
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", okListener)
            .create()
            .show()
    }

    private fun explain() {
        val dialog = AlertDialog.Builder(this)
        dialog.setMessage("You need to give some mandatory permissions to continue. Do you want to go to app settings?")
            .setPositiveButton("Yes") { paramDialogInterface: DialogInterface?, paramInt: Int ->
                //  permissionsClass.requestPermission(type, code);
                startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:com.gigabytedevelopersinc.apps.sonshub")
                    )
                )
            }
            .setNegativeButton("Cancel") { paramDialogInterface: DialogInterface?, paramInt: Int -> finish() }
        dialog.show()
    }*/

    companion object {
        const val REQUEST_ID_MULTIPLE_PERMISSIONS = 1
        const val LOG_TAG = "SplashActivity"
        const val REQUEST_CODE = 12123
    }
}