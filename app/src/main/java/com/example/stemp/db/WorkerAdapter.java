package com.example.stemp.db;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.telephony.TelephonyScanManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Ignore;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.List;
import java.util.HashSet; // Import the HashSet class
import java.util.Locale;

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

        // delay loading bar so it won't flash before your eyes
        try {
            Thread.sleep(1000);
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

        // delay to avoid loading screen flashing if loading time is short
        PhonecallDatabase db = PhonecallDatabase.getInstance(this.getApplicationContext());

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
                db.phonecallDao().insertContact(temp);

                // update counter
                counter++;
                int prog = counter*100/db_size;
                setProgressAsync(new Data.Builder().putInt(PROGRESS, prog).build());
            }
        }

        // 2. populate contact_number and number (only known ones)
        Log.d("STEP 2", "Creating contentResolver");
        // create an URI to access database on the phone
        Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection2 = new String[] {
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        ContentResolver contentResolver2 = getApplicationContext().getContentResolver();
        Cursor data = contentResolver.query(phoneUri, projection2, null, null, null);

        Log.d("STEP 2", "Going through all phone numbers");
        if (data != null){
            // get database size
            int data_size = data.getCount();
            // set a counter
            int count = 0;

            while (data.moveToNext()){
                count ++;
                Log.d("ITERATING", "Loop:" + count);

                Log.d("OPERATING", "getting current row");
                // get current row
                int phone_id = data.getInt(0);
                String phone_number = data.getString(2);

                // check if contact has phone number
                if (phone_number != null){
                    // check if normalized number already exists
                    String normalized_phone_number = data.getString(1);
                    if (false){
                    //if (normalized_phone_number != null){
                        phone_number = normalized_phone_number;
                    }
                    else{
                        Log.d("INFO", "no normalized phone number");
                        // get default country code
                        //TODO: ask for country code or get it from sim card or maybe both

                        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                        String code = telephonyManager.getSimCountryIso().toUpperCase();

                        Log.d("INFO", "code" + code);
                        // normalized
                        phone_number = PhoneNumberUtils.formatNumberToE164(phone_number, code);
                    }

                    Log.d("OPERATING", "phone_id:" + phone_id + "   " + phone_number + data.getString(2));
                    // Avoid duplicates
                    if (db.phonecallDao().countPhoneNumber(phone_number) == 0) {
                        Log.d("OPERATING", "Inserting to number");
                        // insert current row into number
                        Number number = new Number(phone_number, true);
                        int number_id = (int) db.phonecallDao().insertNumber(number);
                        Log.d("NUMBER ID", "id " + number_id);

                        Log.d("OPERATING", "Inserting to ContactNumber");
                        // insert contact_number
                        int local_id = db.phonecallDao().getLocalId(phone_id);
                        Log.d("OPERATING", "local_id" + local_id);
                        ContactNumber contactNumber = new ContactNumber(local_id, number_id);
                        db.phonecallDao().insertContactNumber(contactNumber);
                    }
                    else{
                        Log.d("OPERATING", "No insertion");
                    }

                }
                else{
                    Log.d("NO NUMBER", "contact " + phone_id + " does not have a number");
                }

                // update counter

            }
        }

        // 3. populate call and number (only unknown ones)


        // free cursors if not null, else no need
        if (data != null){data.close();}
        if (contacts != null){contacts.close();}

        return Result.success();
    }

    public void populate_contact(){

    }
}
