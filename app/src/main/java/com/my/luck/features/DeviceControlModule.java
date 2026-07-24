package com.my.luck.features;

import android.content.Context;
import android.hardware.camera2.CameraManager;
import android.net.wifi.WifiManager;
import android.util.Log;

public class DeviceControlModule {
    private Context context;
    private WifiManager wifiManager;
    private CameraManager cameraManager;

    public DeviceControlModule(Context context) {
        this.context = context;
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    }

    public void toggleWifi(boolean enable) {
        if (wifiManager != null) {
            wifiManager.setWifiEnabled(enable);
            Log.d("DeviceControl", "WiFi toggled to: " + enable);
        }
    }

    public boolean isWifiEnabled() {
        return wifiManager != null && wifiManager.isWifiEnabled();
    }

    public void toggleFlashlight(boolean enable) {
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, enable);
            Log.d("DeviceControl", "Flashlight toggled to: " + enable);
        } catch (Exception e) {
            Log.e("DeviceControl", "Error toggling flashlight", e);
        }
    }
}