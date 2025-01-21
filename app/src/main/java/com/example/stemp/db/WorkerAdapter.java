package com.example.stemp.db;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.room.Ignore;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class WorkerAdapter extends Worker {
    public WorkerAdapter(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }


    @NonNull
    @Override
    public Result doWork() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // create an URI to access database on the phone
        Uri contactUri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[] {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.LOOKUP_KEY,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER
        };

        // gotta understand better this portion
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        Cursor contacts = contentResolver.query(contactUri, projection, null, null, null);

        // 1. populate contact
        if (contacts != null){
            while (contacts.moveToNext()){
                // get current row
                int phone_id = contacts.getInt(0);
                String lookup_key = contacts.getString(1);
                String name = contacts.getString(2);
                int has_phone_number = contacts.getInt(3);

                // insert current row into database
                Contact temp = new Contact(phone_id, lookup_key, name, has_phone_number);
                PhonecallDatabase db = PhonecallDatabase.getInstance(this.getApplicationContext());
                db.phonecallDao().insertContact(temp);

            }
        }
        // 2. populate contact_number and number (only known ones)

        // 3. populate call and number (only unknown ones)

        // free cursor if it is not null, if it is null there is no need to worry about it
        if (contacts != null){
            contacts.close();
        }



        return Result.success();
    }
}
