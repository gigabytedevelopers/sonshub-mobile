package com.gigabytedevelopersinc.apps.sonshub.utils.misc;

import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import androidx.annotation.NonNull;

import com.gigabytedevelopersinc.apps.sonshub.activities.MainActivity;
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2.Priority;
import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2.Request;
import timber.log.Timber;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

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
 * Time: 9:43 PM
 * Desc: Data
 **/
public final class Data {
    public static String[] sampleUrls = new String[]{""};

    private Data() {

    }

    @NonNull
    private static List<Request> getFetchRequests() {
        final List<Request> requests = new ArrayList<>();
        for (String sampleUrl : sampleUrls) {
            final Request request = new Request(sampleUrl, getFilePath(sampleUrl));
            requests.add(request);
        }
        return requests;
    }

    @NonNull
    public static List<Request> getFetchRequestWithGroupId(final int groupId) {
        final List<Request> requests = getFetchRequests();
        for (Request request : requests) {
            request.setGroupId(groupId);
        }
        return requests;
    }

    private static String getFilePath(@NonNull final String url) {
        final Uri uri = Uri.parse(url);
        final String fileName = getNameFromUrl(url);
        final String dir = getSaveDir();
        String savePath;
        if (fileName.toLowerCase().endsWith(".mp3")) {
            savePath = dir + "/Music/" + fileName;
        } else if (fileName.toLowerCase().endsWith(".mp4")) {
            savePath = dir + "/Videos/" + fileName;
        } else {
            savePath = dir + "/" + fileName;
        }
        return savePath;
    }

    private static String getNameFromUrl(final String url) {
        int lastIndex = url.lastIndexOf("/");
        return url.substring(url.lastIndexOf('/') + 1);
    }

    @NonNull
    public static List<Request> getGameUpdates() {
        final List<Request> requests = new ArrayList<>();
        final String url = "http://speedtest.ftp.otenet.gr/files/test100k.db";
        for (int i = 0; i < 10; i++) {
            final String filePath = getSaveDir() + "/gameAssets/" + "asset_" + i + ".asset";
            final Request request = new Request(url, filePath);
            request.setPriority(Priority.HIGH);
            requests.add(request);
        }
        return requests;
    }

    @NonNull
    private static String getSaveDir() {
        return MainActivity.commonDocumentDirPath("SonsHub").toString();

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ "/SonsHub";
        } else {
            return Environment.getExternalStorageDirectory() + "/SonsHub";
        }*/
        // return Environment.getExternalStorageDirectory() + "/SonsHub";
    }

    private void moveFiles(String inputPath, String inputFile, String outputPath) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            // Create Output Directory if it doesn't exist
            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            inputStream = new FileInputStream(inputPath + inputFile);
            outputStream = new FileOutputStream(outputPath + inputFile);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            inputStream.close();
            inputStream = null;

            // Delete the Original File
            new File(inputPath + inputFile).delete();
        } catch (FileNotFoundException fileNotFound) {
            Timber.tag("FileNotFound").e(fileNotFound);
        } catch (Exception e) {
            Timber.tag("UnknownError").e(e);
        }
    }

    private void moveFile(File file, File dir) throws IOException {
        File newFile = new File(dir, file.getName());
        try (FileChannel outputChannel = new FileOutputStream(newFile).getChannel(); FileChannel inputChannel = new FileInputStream(file).getChannel()) {
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
            inputChannel.close();
            file.delete();
        }
    }
}
