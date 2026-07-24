package com.my.luck.features;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraModule implements Camera.PictureCallback {
    private Context context;
    private Camera camera;
    private File currentPhoto;

    public CameraModule(Context context) {
        this.context = context;
    }

    public void capturePhoto() {
        try {
            camera = Camera.open();
            if (camera != null) {
                SurfaceView dummyView = new SurfaceView(context);
                SurfaceHolder holder = dummyView.getHolder();
                camera.setPreviewDisplay(holder);
                camera.startPreview();
                camera.takePicture(null, null, this);
                Log.d("CameraModule", "Photo capture initiated");
            }
        } catch (Exception e) {
            Log.e("CameraModule", "Error capturing photo", e);
        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File dcim = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DCIM);
            currentPhoto = new File(dcim, "IMG_" + timestamp + ".jpg");
            FileOutputStream fos = new FileOutputStream(currentPhoto);
            fos.write(data);
            fos.close();
            camera.release();
            camera = null;
            Log.d("CameraModule", "Photo saved: " + currentPhoto.getAbsolutePath());
        } catch (Exception e) {
            Log.e("CameraModule", "Error saving photo", e);
        }
    }
}