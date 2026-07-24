package com.my.luck.features;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CallLogModule {
    private Context context;

    public CallLogModule(Context context) {
        this.context = context;
    }

    public List<HashMap<String, String>> getCallLog() {
        List<HashMap<String, String>> calls = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC");
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                HashMap<String, String> call = new HashMap<>();
                call.put("name", cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME)));
                call.put("number", cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER)));
                call.put("type", getCallType(cursor.getInt(cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE))));
                call.put("duration", cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION)));
                call.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        .format(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE)))));
                calls.add(call);
            } while (cursor.moveToNext());
            cursor.close();
        }
        Log.d("CallLogModule", "Found " + calls.size() + " calls");
        return calls;
    }

    private String getCallType(int type) {
        switch (type) {
            case CallLog.Calls.INCOMING_TYPE: return "INCOMING";
            case CallLog.Calls.OUTGOING_TYPE: return "OUTGOING";
            case CallLog.Calls.MISSED_TYPE: return "MISSED";
            default: return "UNKNOWN";
        }
    }
}