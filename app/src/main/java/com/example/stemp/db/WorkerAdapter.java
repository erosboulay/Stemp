package com.example.stemp.db;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.DateFormat;
import java.util.Objects;

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

        // create an URI to access contacts database (on phone)
        Uri contactUri = ContactsContract.Contacts.CONTENT_URI;
        String[] contactProjection = new String[] {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.LOOKUP_KEY,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER
        };
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        Cursor contacts = contentResolver.query(contactUri, contactProjection, null, null, null);

        // create an URI to access phone number database (on phone)
        Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] phoneProjection = new String[] {
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        Cursor data = contentResolver.query(phoneUri, phoneProjection, null, null, null);

        // create an URI to access call log database (on phone)
        Uri callUri = CallLog.Calls.CONTENT_URI;
        String[] callProjection = new String[]{
                CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION
        };
        Cursor call = contentResolver.query(callUri, callProjection, null, null, null);

        // get app internal database
        PhonecallDatabase db = PhonecallDatabase.getInstance(this.getApplicationContext());

        int counter = 0; // set a counter
        int total = 0;

        if (contacts != null){total += contacts.getCount();}
        if (data != null){total += data.getCount();}

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
                db.phonecallDao().insertContact(temp);

                // update counter
                counter ++;
                int prog = counter*100/total;
                setProgressAsync(new Data.Builder().putInt(PROGRESS, prog).build());
            }
        }

        // 2. populate contact_number and number (only known ones)
        if (data != null){
            // get database size
            int data_size = data.getCount();
            // set a counter
            int count = 0;

            while (data.moveToNext()){
                // get current row
                int phone_id = data.getInt(0);
                String phone_number = data.getString(2);

                // check if contact has phone number
                if (phone_number != null){

                    // check if normalized number already exists
                    String normalized_phone_number = data.getString(1);

                    if (normalized_phone_number != null){
                        phone_number = normalized_phone_number;
                    }
                    else{
                        // get default country code
                        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                        String code = telephonyManager.getSimCountryIso().toUpperCase();

                        // normalized
                        phone_number = PhoneNumberUtils.formatNumberToE164(phone_number, code);
                    }

                    // Avoid duplicates
                    if (db.phonecallDao().countPhoneNumber(phone_number) == 0) {
                        // insert current row into number
                        Number number = new Number(phone_number, true);
                        int number_id = (int) db.phonecallDao().insertNumber(number);

                        // insert contact_number
                        int local_id = db.phonecallDao().getLocalId(phone_id);
                        ContactNumber contactNumber = new ContactNumber(local_id, number_id);
                        db.phonecallDao().insertContactNumber(contactNumber);
                    }
                }

                // update counter
                counter++;
                int prog = counter*100/total;
                setProgressAsync(new Data.Builder().putInt(PROGRESS, prog).build());

            }
        }

        // 3. populate call and number (only unknown ones)
        Log.d("STEP 3", "Upload call log");
        // iterate through each call
        if (call != null) {
            int call_counter = 0;
            while (call.moveToNext()){
                // for logging
                call_counter ++;
                //Log.d("ITERATING", "counter: " + call_counter);

                String phone_number = call.getString(0);
                int type = call.getInt(1);
                long date = call.getLong(2);
                long duration = call.getLong(3);
                int number_id;

                String local_date = DateFormat.getDateInstance().format(date);


                String temp = phone_number;

                // get default country code
                TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                String code = telephonyManager.getSimCountryIso().toUpperCase();
                // normalized

                //TODO: get phone voicemail number
                if (!Objects.equals(temp, "123")) {
                    phone_number = PhoneNumberUtils.formatNumberToE164(phone_number, code);
                }

                // check if phone number is in number table
                if (db.phonecallDao().countPhoneNumber(phone_number) == 1){
                    number_id = db.phonecallDao().getNumberId(phone_number);
                    //Log.d("IS IN NUMBER", "number_id : " + number_id);
                }
                else{
                    // phone number is not in number, add it with boolean false because it isn't in contacts
                    Number unkown_number = new Number(phone_number, false);
                    number_id = (int) db.phonecallDao().insertNumber(unkown_number);
                    //Log.d("NOT IN NUMBER", "number_id : " + number_id);
                }

                // add phone call to call
                //Log.d("INSERT", "insert phone call in call");
                Call add_call = new Call(number_id, type, date, duration);
                db.phonecallDao().insertCall(add_call);
            }
        }

        // free cursors if not null, else no need
        if (data != null){data.close();}
        if (contacts != null){contacts.close();}
        if (call != null){call.close();}

        return Result.success();
    }

    public void populate_contact(){

    }
}
