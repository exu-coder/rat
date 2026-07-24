package com.my.luck.features;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactsModule {
    private Context context;

    public ContactsModule(Context context) {
        this.context = context;
    }

    public List<HashMap<String, String>> getContacts() {
        List<HashMap<String, String>> contacts = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                HashMap<String, String> contact = new HashMap<>();
                contact.put("name", cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                contact.put("number", cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                contacts.add(contact);
            } while (cursor.moveToNext());
            cursor.close();
        }
        Log.d("ContactsModule", "Found " + contacts.size() + " contacts");
        return contacts;
    }
}