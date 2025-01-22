package com.example.stemp.db;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.room.Ignore;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class WorkerAdapter extends Worker {
    private static final String PROGRESS = "PROGRESS";
    public WorkerAdapter(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Set initial progress to 0
        setProgressAsync(new Data.Builder().putInt(PROGRESS, 0).build());

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

        //TODO: comment this when I don't need to test loading screen anymore
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // 1. populate contact
        if (contacts != null){
            int db_size = contacts.getCount(); // get database size
            int counter = 0; // set a counter


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

                // update counter
                counter++;
                int prog = counter*100/db_size;
                setProgressAsync(new Data.Builder().putInt(PROGRESS, prog).build());
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
