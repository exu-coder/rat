package com.my.luck.features;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileDumpModule {
    private Context context;

    public FileDumpModule(Context context) {
        this.context = context;
    }

    public List<String> getGalleryFiles() {
        List<String> images = new ArrayList<>();
        File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (dcim != null && dcim.exists()) {
            scanFolder(dcim, images);
        }
        File pictures = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (pictures != null && pictures.exists()) {
            scanFolder(pictures, images);
        }
        Log.d("FileDump", "Found " + images.size() + " images");
        return images;
    }

    public List<String> getAllFiles() {
        List<String> files = new ArrayList<>();
        File storage = Environment.getExternalStorageDirectory();
        if (storage != null && storage.exists()) {
            scanFolder(storage, files);
        }
        return files;
    }

    private void scanFolder(File folder, List<String> list) {
        if (folder == null || !folder.isDirectory()) return;
        File[] items = folder.listFiles();
        if (items == null) return;
        for (File file : items) {
            if (file.isDirectory()) {
                scanFolder(file, list);
            } else {
                list.add(file.getAbsolutePath());
            }
        }
    }
}