package com.gigabytedevelopersinc.apps.sonshub.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

public class EasyFileFinders {

    private Context context;

    public EasyFileFinders(Context context) {
        this.context = context;
    }

    public ArrayList<File> getListFiles() {
        File file = new File(Environment.getExternalStorageDirectory() + "/SonsHub");

        ArrayList<File> fileArrayList = new ArrayList<>();

        File[] listOfFiles = file.listFiles();
        if (file.exists()) {
            for (File f :
                    listOfFiles) {
                if (f.getAbsolutePath().endsWith(".mp3"))
                    fileArrayList.add(f);
            }
        } else {
            file.mkdir();
        }

        return fileArrayList;
    }
}
